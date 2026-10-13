package com.campus.lostandfound.service;

import com.campus.lostandfound.constant.Constants;
import com.campus.lostandfound.exception.BusinessRuleViolationException;
import com.campus.lostandfound.exception.ItemAlreadyClaimedException;
import com.campus.lostandfound.exception.ItemNotFoundException;
import com.campus.lostandfound.exception.VerificationFailedException;
import com.campus.lostandfound.model.Claim;
import com.campus.lostandfound.model.FoundItem;
import com.campus.lostandfound.model.LostItem;
import com.campus.lostandfound.model.role.Admin;
import com.campus.lostandfound.model.role.SecurityDesk;
import com.campus.lostandfound.model.role.Staff;
import com.campus.lostandfound.model.role.Student;
import com.campus.lostandfound.store.CampusDataStore;
import com.campus.lostandfound.strategy.MatchResult;
import com.campus.lostandfound.strategy.MatchScoringStrategy;
import com.campus.lostandfound.strategy.WeightedMultiFactorMatchStrategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Core business service implementing FR1 through FR8, enforcing business rules:
 * - BR-1: Atomic Anti-Duplicate Claim Lock (FR6)
 * - BR-2: Incident Date & Valuation Bounds Validation (FR1/FR2)
 * - BR-3: Anti-Fraud Secret Challenge Quiz Verification (FR4/FR6)
 */
public class LostAndFoundService {

    private final CampusDataStore dataStore;
    private final AtomicInteger lostIdCounter = new AtomicInteger(1008);
    private final AtomicInteger foundIdCounter = new AtomicInteger(2008);
    private final AtomicInteger claimIdCounter = new AtomicInteger(3001);

    public LostAndFoundService(CampusDataStore dataStore) {
        this.dataStore = dataStore;
        initializeSampleData();
    }

    /**
     * Seeds the system with the 1-week preloaded sample data in arrays.
     */
    public void initializeSampleData() {
        // 1. Seed Sample Users
        for (String[] u : Constants.SAMPLE_USERS_DATA) {
            String role = u[4];
            if ("STUDENT".equalsIgnoreCase(role)) {
                dataStore.registerUser(new Student(u[0], u[1], u[2], u[3], "555-0100", "7376231CD108", u[5], 3));
            } else if ("STAFF".equalsIgnoreCase(role)) {
                dataStore.registerUser(new Staff(u[0], u[1], u[2], u[3], "555-0200", "EMP-902", u[5], "Assistant Professor"));
            } else if ("SECURITY_DESK".equalsIgnoreCase(role)) {
                dataStore.registerUser(new SecurityDesk(u[0], u[1], u[2], u[3], "555-0300", "SEC-BDG-42", u[5]));
            } else if ("ADMIN".equalsIgnoreCase(role)) {
                dataStore.registerUser(new Admin(u[0], u[1], u[2], u[3], "555-0400", u[5], 5));
            }
        }

        // 2. Seed Sample Lost Items (List)
        for (String[] row : Constants.SAMPLE_LOST_ITEMS_DATA) {
            LostItem item = new LostItem(
                    row[0], row[1], row[2], row[3],
                    LocalDate.parse(row[4]), row[5], row[6], row[7],
                    Double.parseDouble(row[8]), row[9]
            );
            dataStore.addLostItem(item);
        }

        // 3. Seed Sample Found Items (Set)
        for (String[] row : Constants.SAMPLE_FOUND_ITEMS_DATA) {
            FoundItem item = new FoundItem(
                    row[0], row[1], row[2], row[3],
                    LocalDate.parse(row[4]), row[5], row[6], row[7]
            );
            item.setStatus(row[8]);
            dataStore.addFoundItem(item);
        }

        // 4. Seed an active claim into Queue for FOUND-2004
        Claim sampleClaim = new Claim(
                "CLM-3000", "FOUND-2004", "kavya_student",
                "College ID card #7376231CD108 inside photo slot",
                "Lost my leather wallet during lab hours. Has my college card."
        );
        dataStore.enqueueClaim(sampleClaim);
    }

    // =========================================================================
    // FR1: Report Lost Item
    // =========================================================================
    public LostItem reportLostItem(String title, String category, String location, LocalDate lostDate,
                                   String brand, String color, String distinctiveFeatures,
                                   double estimatedValue, String reporterUsername) {
        // Enforce Business Rule 2 (Validation & Date Integrity)
        LostItem.validate(title, category, location, lostDate, estimatedValue);

        String id = "LOST-" + lostIdCounter.getAndIncrement();
        LostItem item = new LostItem(id, title, category, location, lostDate, brand, color,
                distinctiveFeatures, estimatedValue, reporterUsername);

        dataStore.addLostItem(item);
        return item;
    }

    // =========================================================================
    // FR2: Log Found Item
    // =========================================================================
    public FoundItem logFoundItem(String title, String category, String location, LocalDate foundDate,
                                  String lockerNumber, String secretFeature, String finderUsername) {
        // Enforce Business Rule 2 (Validation & Date Integrity)
        FoundItem.validate(title, category, location, foundDate, lockerNumber);

        String id = "FOUND-" + foundIdCounter.getAndIncrement();
        FoundItem item = new FoundItem(id, title, category, location, foundDate, lockerNumber,
                secretFeature, finderUsername);

        dataStore.addFoundItem(item);
        return item;
    }

    // =========================================================================
    // FR3: Match Suggestion Engine (Strategy Pattern)
    // =========================================================================
    public List<MatchResult> findMatches(String lostItemId, MatchScoringStrategy strategy) {
        LostItem lostItem = dataStore.findLostItemById(lostItemId);
        if (lostItem == null) {
            throw new ItemNotFoundException("Lost item report not found with ID: " + lostItemId);
        }

        List<MatchResult> matches = new ArrayList<>();
        for (FoundItem foundItem : dataStore.getFoundItems()) {
            // Ignore items already handed over or donated
            if (Constants.STATUS_HANDED_OVER.equals(foundItem.getStatus())
                    || Constants.STATUS_BATCHED_FOR_DONATION.equals(foundItem.getStatus())) {
                continue;
            }

            double score = strategy.calculateScore(lostItem, foundItem);
            if (score >= Constants.MIN_SIMILARITY_MATCH_SCORE) {
                matches.add(new MatchResult(foundItem, score, strategy.getStrategyName()));
            }
        }

        // Sort descending by score
        Collections.sort(matches);

        // Update lost item status if matches exist
        if (!matches.isEmpty()) {
            lostItem.setStatus(Constants.STATUS_MATCHED);
        }

        return matches;
    }

    // =========================================================================
    // FR4: Submit Ownership Claim with Anti-Fraud Quiz Answer
    // =========================================================================
    public Claim submitClaim(String foundItemId, String claimantUsername,
                             String quizAnswerSecret, String proofDescription) {
        FoundItem item = dataStore.findFoundItemById(foundItemId);
        if (item == null) {
            throw new ItemNotFoundException("Found item not found with ID: " + foundItemId);
        }

        // Prevent claims if already locked
        if (item.isLockedForClaims()) {
            throw new ItemAlreadyClaimedException(foundItemId,
                    "Item " + foundItemId + " is currently in status '" + item.getStatus()
                            + "' and is not open for new claims.");
        }

        Claim.validate(foundItemId, claimantUsername, quizAnswerSecret);

        String claimId = "CLM-" + claimIdCounter.getAndIncrement();
        Claim claim = new Claim(claimId, foundItemId, claimantUsername, quizAnswerSecret, proofDescription);

        dataStore.enqueueClaim(claim);
        item.setStatus(Constants.STATUS_CLAIM_PENDING);

        return claim;
    }

    // =========================================================================
    // FR6: Approve Claim with Atomic Concurrency Guard & Anti-Fraud Verification
    // =========================================================================
    public synchronized Claim approveClaim(String claimId, String reviewerUsername, String remarks) {
        Claim claim = dataStore.getAllClaimsById().get(claimId);
        if (claim == null) {
            throw new ItemNotFoundException("Claim record not found with ID: " + claimId);
        }

        FoundItem item = dataStore.findFoundItemById(claim.getFoundItemId());
        if (item == null) {
            throw new ItemNotFoundException("Found item referenced by claim was not found: " + claim.getFoundItemId());
        }

        // ENFORCE BUSINESS RULE 1: Anti-Duplicate Approval Concurrency Guard (FR6)
        if (item.isLockedForClaims() || item.getApprovedClaimId() != null) {
            throw new ItemAlreadyClaimedException(item.getId(),
                    "Business Rule Violation [BR-01-LOCK]: Found item " + item.getId()
                            + " has already been approved for claim (" + item.getApprovedClaimId()
                            + "). Multiple approved claims on the same item are strictly prohibited.");
        }

        // ENFORCE ANTI-FRAUD VERIFICATION RULE: Challenge quiz answer check
        if (!isQuizAnswerSufficientlyMatched(claim.getQuizAnswerSecret(), item.getSecretIdentificationFeature())) {
            claim.setStatus(Constants.STATUS_CLAIM_REJECTED);
            claim.setReviewedBy(reviewerUsername);
            claim.setReviewRemarks("Rejected: Verification quiz failed. Answer did not match secret feature.");
            claim.setReviewedDate(LocalDate.now());
            throw new VerificationFailedException(
                    "Business Rule Violation [BR-03-VERIFICATION]: Anti-fraud verification challenge failed! "
                            + "Claimant's answer did not match confidential custody records.");
        }

        // Approve and atomically lock item
        claim.setStatus(Constants.STATUS_CLAIM_APPROVED);
        claim.setReviewedBy(reviewerUsername);
        claim.setReviewRemarks(remarks != null ? remarks : "Approved by security desk officer.");
        claim.setReviewedDate(LocalDate.now());

        item.setStatus(Constants.STATUS_CLAIM_APPROVED);
        item.setApprovedClaimId(claim.getId());

        return claim;
    }

    private boolean isQuizAnswerSufficientlyMatched(String claimantAnswer, String secretFeature) {
        if (claimantAnswer == null || secretFeature == null) return false;
        String ans = claimantAnswer.toLowerCase();
        String sec = secretFeature.toLowerCase();
        // Check for meaningful overlapping keywords
        for (String word : ans.split("\\W+")) {
            if (word.length() > 3 && sec.contains(word)) {
                return true;
            }
        }
        return false;
    }

    // =========================================================================
    // FR5: Physical Handover & Custody Signature
    // =========================================================================
    public synchronized FoundItem recordHandover(String foundItemId, String recipientName,
                                                 String photoIdNumber, String officerUsername) {
        FoundItem item = dataStore.findFoundItemById(foundItemId);
        if (item == null) {
            throw new ItemNotFoundException("Found item not found with ID: " + foundItemId);
        }

        if (!Constants.STATUS_CLAIM_APPROVED.equals(item.getStatus())) {
            throw new BusinessRuleViolationException("BR-05-HANDOVER",
                    "Cannot execute physical handover: Item " + foundItemId
                            + " must have an APPROVED claim first. Current status: " + item.getStatus());
        }

        if (photoIdNumber == null || photoIdNumber.trim().isEmpty()) {
            throw new BusinessRuleViolationException("BR-05-HANDOVER",
                    "Recipient physical photo ID verification number is mandatory for custody release.");
        }

        item.setStatus(Constants.STATUS_HANDED_OVER);
        return item;
    }

    // =========================================================================
    // FR7: Scan 60-Day Expiry & Donation Batch
    // =========================================================================
    public List<FoundItem> scanAndBatchExpiredItems() {
        List<FoundItem> batched = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (FoundItem item : dataStore.getFoundItems()) {
            // If item has been in custody longer than 60 days and not claimed/handed over
            if (Constants.STATUS_IN_CUSTODY.equals(item.getStatus()) && item.isExpired()) {
                item.setStatus(Constants.STATUS_BATCHED_FOR_DONATION);
                batched.add(item);
            }
        }
        return batched;
    }

    // =========================================================================
    // FR8: Real-Time Recovery Rate & Analytics Dashboard
    // =========================================================================
    public AnalyticsReport generateAnalyticsReport() {
        int totalFound = dataStore.getFoundItems().size();
        int totalLost = dataStore.getLostItems().size();
        int handedOver = 0;
        int inCustody = 0;
        int claimsApproved = 0;
        int batchedDonated = 0;
        int highValueItems = 0;

        for (FoundItem item : dataStore.getFoundItems()) {
            if (Constants.STATUS_HANDED_OVER.equals(item.getStatus())) handedOver++;
            else if (Constants.STATUS_IN_CUSTODY.equals(item.getStatus())) inCustody++;
            else if (Constants.STATUS_CLAIM_APPROVED.equals(item.getStatus())) claimsApproved++;
            else if (Constants.STATUS_BATCHED_FOR_DONATION.equals(item.getStatus())) batchedDonated++;
        }

        for (LostItem item : dataStore.getLostItems()) {
            if (item.isHighValue()) highValueItems++;
        }

        double recoveryRate = totalFound == 0 ? 0.0 : ((double) handedOver / totalFound) * 100.0;
        int pendingClaimsCount = dataStore.getPendingClaims().size();

        return new AnalyticsReport(totalLost, totalFound, inCustody, claimsApproved,
                handedOver, batchedDonated, pendingClaimsCount, highValueItems, recoveryRate);
    }

    public CampusDataStore getDataStore() {
        return dataStore;
    }

    /**
     * Immutable DTO representing FR8 recovery telemetry.
     */
    public static class AnalyticsReport {
        public final int totalLostReports;
        public final int totalFoundItems;
        public final int itemsInCustody;
        public final int claimsApproved;
        public final int itemsHandedOver;
        public final int itemsBatchedForDonation;
        public final int pendingClaimsInQueue;
        public final int highValueItemsReported;
        public final double recoveryRatePercentage;

        public AnalyticsReport(int totalLostReports, int totalFoundItems, int itemsInCustody,
                               int claimsApproved, int itemsHandedOver, int itemsBatchedForDonation,
                               int pendingClaimsInQueue, int highValueItemsReported,
                               double recoveryRatePercentage) {
            this.totalLostReports = totalLostReports;
            this.totalFoundItems = totalFoundItems;
            this.itemsInCustody = itemsInCustody;
            this.claimsApproved = claimsApproved;
            this.itemsHandedOver = itemsHandedOver;
            this.itemsBatchedForDonation = itemsBatchedForDonation;
            this.pendingClaimsInQueue = pendingClaimsInQueue;
            this.highValueItemsReported = highValueItemsReported;
            this.recoveryRatePercentage = recoveryRatePercentage;
        }
    }
}
