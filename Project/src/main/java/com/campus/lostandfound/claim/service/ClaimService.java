package com.campus.lostandfound.claim.service;

import com.campus.lostandfound.claim.audit.AuditTrailService;
import com.campus.lostandfound.claim.model.Claim;
import com.campus.lostandfound.common.feign.ItemServiceClient;
import com.campus.lostandfound.common.model.Dtos.VerificationAnswer;
import com.campus.lostandfound.notification.service.NotificationService;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service managing ownership claims (FR4) and enforcing anti-fraud concurrency rules (FR6).
 * Communicates with item-service using the ItemServiceClient (Feign: claim -> item).
 */
public class ClaimService {

    private final Map<String, Claim> claimsById = new ConcurrentHashMap<>();
    private final Map<String, String> approvedClaimByItemId = new ConcurrentHashMap<>();
    private final Map<String, Object> itemLocks = new ConcurrentHashMap<>();
    private final AtomicInteger claimCounter = new AtomicInteger(1000);

    private final ItemServiceClient itemClient;
    private final AuditTrailService auditTrail;
    private final NotificationService notificationService;

    public ClaimService(ItemServiceClient itemClient, AuditTrailService auditTrail, NotificationService notificationService) {
        this.itemClient = itemClient;
        this.auditTrail = auditTrail;
        this.notificationService = notificationService;
    }

    private Object getLock(String itemId) {
        return itemLocks.computeIfAbsent(itemId, k -> new Object());
    }

    public record SubmitClaimRequest(
            String foundItemId,
            String lostReportId,
            String claimantUserId,
            String claimantName,
            String claimantContact,
            List<VerificationAnswer> answers,
            String proofAttachmentUrl,
            String ipAddress
    ) {}

    // FR4: Owner can raise claim answering verification questions
    public Claim submitClaim(SubmitClaimRequest req) {
        Objects.requireNonNull(req.foundItemId(), "Found Item ID must not be null");
        Objects.requireNonNull(req.claimantUserId(), "Claimant User ID must not be null");

        // Verify item is available
        boolean available = itemClient.verifyItemAvailableForClaim(req.foundItemId());
        if (!available) {
            throw new IllegalStateException("Found item #" + req.foundItemId() + " is currently not available for new claims.");
        }

        String claimId = "CLM-" + claimCounter.incrementAndGet();
        Claim claim = new Claim(
                claimId,
                req.foundItemId(),
                req.lostReportId() != null ? req.lostReportId() : "",
                req.claimantUserId(),
                req.claimantName() != null ? req.claimantName() : "Claimant",
                req.claimantContact() != null ? req.claimantContact() : "",
                req.answers() != null ? req.answers() : List.of(),
                req.proofAttachmentUrl() != null ? req.proofAttachmentUrl() : "",
                "PENDING_VERIFICATION",
                "Awaiting security desk review",
                null,
                Instant.now(),
                null
        );

        claimsById.put(claimId, claim);

        // Lock item via Feign client
        itemClient.lockItemForClaim(new ItemServiceClient.LockRequest(req.foundItemId(), claimId, req.claimantUserId()));

        // Audit Trail (NFR-P3)
        auditTrail.recordEvent(
                "CLAIM_SUBMITTED",
                "Claim",
                claimId,
                req.claimantUserId(),
                "STUDENT_STAFF",
                req.ipAddress(),
                "User " + req.claimantName() + " submitted ownership claim for item #" + req.foundItemId() + " answering " + (req.answers() != null ? req.answers().size() : 0) + " verification questions",
                "NONE",
                "PENDING_VERIFICATION"
        );

        // Notify Security Desk
        notificationService.sendNotification(
                "USR-SEC-01",
                "New Claim Submitted: #" + claimId,
                "Claimant " + req.claimantName() + " answered verification questions for item #" + req.foundItemId() + ". Ready for desk review.",
                "CLAIM_UPDATE",
                claimId
        );

        return claim;
    }

    // FR6: System prevents multiple approved claims per item
    public Claim approveClaim(String claimId, String officerId, String officerNotes, String ipAddress) {
        Claim claim = claimsById.get(claimId);
        if (claim == null) {
            throw new NoSuchElementException("Claim not found: " + claimId);
        }

        synchronized (getLock(claim.foundItemId())) {
            // Guard: check if this item already has an approved claim
            String existingApproved = approvedClaimByItemId.get(claim.foundItemId());
            if (existingApproved != null && !existingApproved.equals(claimId)) {
                // Strict FR6 enforcement
                auditTrail.recordEvent(
                        "FRAUD_PREVENTION_BLOCKED",
                        "Claim",
                        claimId,
                        officerId,
                        "SECURITY_DESK",
                        ipAddress,
                        "BLOCKED: Attempted duplicate claim approval on item #" + claim.foundItemId() + ". Already approved for claim #" + existingApproved,
                        claim.status(),
                        "APPROVAL_REJECTED_DUPLICATE"
                );
                throw new IllegalStateException("FR6 Guard Activated: Found item #" + claim.foundItemId() + " ALREADY has an approved claim (#" + existingApproved + "). Multiple approved claims per item are strictly forbidden.");
            }

            // Register approved claim
            approvedClaimByItemId.put(claim.foundItemId(), claimId);
            Claim approved = claim.withStatus("APPROVED", officerNotes != null ? officerNotes : "Verified by security desk", officerId);
            claimsById.put(claimId, approved);

            // Feign Client update: item-service status -> CLAIM_APPROVED
            itemClient.updateItemStatus(new ItemServiceClient.StatusUpdateRequest(
                    claim.foundItemId(),
                    "CLAIM_APPROVED",
                    officerId,
                    "Ownership verified by officer " + officerId + " (Claim #" + claimId + ")"
            ));

            // Audit Trail (NFR-P3)
            auditTrail.recordEvent(
                    "CLAIM_APPROVED",
                    "Claim",
                    claimId,
                    officerId,
                    "SECURITY_DESK",
                    ipAddress,
                    "Officer " + officerId + " approved ownership claim #" + claimId + " for item #" + claim.foundItemId(),
                    claim.status(),
                    "APPROVED"
            );

            // Auto-reject any conflicting pending claims for the same item
            for (Claim other : claimsById.values()) {
                if (other.foundItemId().equals(claim.foundItemId()) && !other.id().equals(claimId) && "PENDING_VERIFICATION".equals(other.status())) {
                    Claim autoRejected = other.withStatus("REJECTED", "Item verified and approved for another claimant (#" + claimId + ")", "SYSTEM_GUARD");
                    claimsById.put(other.id(), autoRejected);
                    auditTrail.recordEvent(
                            "CLAIM_AUTO_REJECTED",
                            "Claim",
                            other.id(),
                            "SYSTEM",
                            "SYSTEM",
                            "127.0.0.1",
                            "Auto-rejected conflicting claim #" + other.id() + " after claim #" + claimId + " was approved",
                            "PENDING_VERIFICATION",
                            "REJECTED"
                    );
                }
            }

            // Notify Claimant
            notificationService.sendNotification(
                    claim.claimantUserId(),
                    "Claim Approved! Collect Item at Security Desk",
                    "Your claim #" + claimId + " has been approved! Please visit the Security Desk with your ID for physical handover.",
                    "HANDOVER_READY",
                    claimId
            );

            return approved;
        }
    }

    public Claim rejectClaim(String claimId, String officerId, String reason, String ipAddress) {
        Claim claim = claimsById.get(claimId);
        if (claim == null) throw new NoSuchElementException("Claim not found: " + claimId);

        Claim rejected = claim.withStatus("REJECTED", reason, officerId);
        claimsById.put(claimId, rejected);

        auditTrail.recordEvent(
                "CLAIM_REJECTED",
                "Claim",
                claimId,
                officerId,
                "SECURITY_DESK",
                ipAddress,
                "Officer " + officerId + " rejected claim #" + claimId + ". Reason: " + reason,
                claim.status(),
                "REJECTED"
        );

        notificationService.sendNotification(
                claim.claimantUserId(),
                "Claim Update: Additional Verification Required",
                "Your claim #" + claimId + " was rejected or requires further proof: " + reason,
                "CLAIM_UPDATE",
                claimId
        );

        return rejected;
    }

    public Optional<Claim> getClaim(String claimId) {
        return Optional.ofNullable(claimsById.get(claimId));
    }

    public List<Claim> getAllClaims() {
        return new ArrayList<>(claimsById.values());
    }

    public List<Claim> getClaimsForUser(String userId) {
        return claimsById.values().stream()
                .filter(c -> c.claimantUserId().equalsIgnoreCase(userId))
                .sorted(Comparator.comparing(Claim::submittedAt).reversed())
                .toList();
    }

    public List<Claim> getClaimsForItem(String itemId) {
        return claimsById.values().stream()
                .filter(c -> c.foundItemId().equalsIgnoreCase(itemId))
                .sorted(Comparator.comparing(Claim::submittedAt).reversed())
                .toList();
    }
}
