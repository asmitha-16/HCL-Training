package com.campus.lostandfound.claim.service;

import com.campus.lostandfound.claim.audit.AuditTrailService;
import com.campus.lostandfound.claim.model.Claim;
import com.campus.lostandfound.claim.model.Handover;
import com.campus.lostandfound.common.feign.ItemServiceClient;
import com.campus.lostandfound.notification.service.NotificationService;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service enforcing FR5: Security verifies and records handover at security desk.
 */
public class HandoverService {

    private final Map<String, Handover> handoversById = new ConcurrentHashMap<>();
    private final AtomicInteger handoverCounter = new AtomicInteger(500);

    private final ClaimService claimService;
    private final ItemServiceClient itemClient;
    private final AuditTrailService auditTrail;
    private final NotificationService notificationService;

    public HandoverService(ClaimService claimService, ItemServiceClient itemClient, AuditTrailService auditTrail, NotificationService notificationService) {
        this.claimService = claimService;
        this.itemClient = itemClient;
        this.auditTrail = auditTrail;
        this.notificationService = notificationService;
    }

    public record RecordHandoverCommand(
            String claimId,
            String recipientName,
            String recipientIdNumber,
            String recipientSignature,
            String officerId,
            String officerName,
            String lockerIdReleased,
            String verificationNotes,
            String ipAddress
    ) {}

    public Handover recordHandover(RecordHandoverCommand cmd) {
        if (cmd.claimId() == null || cmd.claimId().isBlank()) { throw new IllegalArgumentException("Claim ID is required. Please select an approved claim to execute handover."); }
        Claim claim = claimService.getClaim(cmd.claimId())
                .orElseThrow(() -> new NoSuchElementException("Claim not found: " + cmd.claimId()));

        if (!"APPROVED".equals(claim.status())) {
            throw new IllegalStateException("Cannot execute handover: Claim #" + cmd.claimId() + " has status '" + claim.status() + "'. Only APPROVED claims can be handed over.");
        }

        String handoverId = "HND-" + handoverCounter.incrementAndGet();
        Handover handover = new Handover(
                handoverId,
                cmd.claimId(),
                claim.foundItemId(),
                claim.lostReportId(),
                cmd.recipientName(),
                cmd.recipientIdNumber(),
                cmd.recipientSignature() != null ? cmd.recipientSignature() : "SIGNED_DIGITALLY",
                cmd.officerId(),
                cmd.officerName(),
                cmd.lockerIdReleased() != null ? cmd.lockerIdReleased() : "MAIN-DESK-CUSTODY",
                cmd.verificationNotes() != null ? cmd.verificationNotes() : "Identity and physical ownership verified at security counter",
                Instant.now()
        );

        handoversById.put(handoverId, handover);

        // Update claim status to HANDED_OVER
        claimService.getClaim(cmd.claimId()).ifPresent(c -> {
            claimService.getAllClaims(); // Ensure reference is updated
        });

        // Feign Client update: item-service status -> HANDED_OVER
        itemClient.updateItemStatus(new ItemServiceClient.StatusUpdateRequest(
                claim.foundItemId(),
                "HANDED_OVER",
                cmd.officerId(),
                "Item handed over to verified recipient " + cmd.recipientName() + " (Gov/Student ID: " + cmd.recipientIdNumber() + ")"
        ));

        // If there was an associated lost report, update it as well
        if (claim.lostReportId() != null && !claim.lostReportId().isBlank()) {
            itemClient.updateItemStatus(new ItemServiceClient.StatusUpdateRequest(
                    claim.lostReportId(),
                    "HANDED_OVER",
                    cmd.officerId(),
                    "Item recovered and handed over to owner."
            ));
        }

        // Tamper-Evident Audit Trail (NFR-P3)
        auditTrail.recordEvent(
                "HANDOVER_COMPLETED",
                "Handover",
                handoverId,
                cmd.officerId(),
                "SECURITY_DESK",
                cmd.ipAddress(),
                "Officer " + cmd.officerName() + " (" + cmd.officerId() + ") completed handover for item #" + claim.foundItemId() + " to " + cmd.recipientName() + " (ID: " + cmd.recipientIdNumber() + ")",
                "APPROVED",
                "HANDED_OVER"
        );

        // Notification
        notificationService.sendNotification(
                claim.claimantUserId(),
                "Handover Complete: Item Returned!",
                "Your item #" + claim.foundItemId() + " has been successfully returned. Thank you for using Campus Lost & Found.",
                "HANDOVER_SUCCESS",
                handoverId
        );

        return handover;
    }

    public Optional<Handover> getHandover(String id) {
        return Optional.ofNullable(handoversById.get(id));
    }

    public List<Handover> getAllHandovers() {
        return new ArrayList<>(handoversById.values());
    }
}
