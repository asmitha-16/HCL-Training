/**
 * CampusConstants.java
 *
 * Day 2 Deliverable: Centralized Business Rule Constants
 * Project: JFS-40 Campus Lost & Found Portal
 * -----------------------------------------------------------------------------
 * Centralizes domain business rules, scoring weights, temporal thresholds,
 * and security constraints to prevent magic numbers across the application.
 */
public final class CampusConstants {

    private CampusConstants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    // -------------------------------------------------------------------------
    // 1. Custody & Expiry Rules (FR7: 60-Day Item Expiry & Charitable Donation)
    // -------------------------------------------------------------------------
    public static final int ITEM_EXPIRY_THRESHOLD_DAYS = 60;
    public static final String STATUS_REPORTED_LOST     = "LOST";
    public static final String STATUS_FOUND_IN_CUSTODY  = "FOUND";
    public static final String STATUS_CLAIMED           = "CLAIMED";
    public static final String STATUS_RETURNED_TO_OWNER = "RETURNED";
    public static final String STATUS_EXPIRED_DONATION  = "EXPIRED_FOR_DONATION";
    public static final int DEFAULT_DONATION_BATCH_SIZE = 50;

    // -------------------------------------------------------------------------
    // 2. Multi-Factor Match Scoring Weights (FR3 & Concept Hooks)
    // -------------------------------------------------------------------------
    public static final double WEIGHT_CATEGORY           = 0.35; // 35% weight
    public static final double WEIGHT_LOCATION           = 0.25; // 25% weight
    public static final double WEIGHT_DATE_DELTA         = 0.20; // 20% weight
    public static final double WEIGHT_KEYWORD_SIMILARITY = 0.20; // 20% weight

    public static final double MATCH_MIN_THRESHOLD       = 0.50; // 50% minimum match cutoff
    public static final double MATCH_HIGH_CONFIDENCE     = 0.75; // 75% high probability tier

    // -------------------------------------------------------------------------
    // 3. Claims & Concurrency Control (FR6: Anti-Duplicate Claim Lock)
    // -------------------------------------------------------------------------
    public static final int MAX_PENDING_CLAIMS_PER_ITEM  = 5;
    public static final long CLAIM_LOCK_TIMEOUT_SECONDS  = 30L;

    // -------------------------------------------------------------------------
    // 4. Security Desk Handover & Photo Verification Policies (FR5 & NFR-P1)
    // -------------------------------------------------------------------------
    public static final String[] AUTHORIZED_ID_TYPES = {
        "CAMPUS_STUDENT_ID",
        "FACULTY_STAFF_ID",
        "GOVERNMENT_PHOTO_ID",
        "DRIVING_LICENSE"
    };

    public static final String[] SENSITIVE_CATEGORIES = {
        "ELECTRONICS",
        "WALLET_CARDS",
        "IDENTITY_DOCUMENTS",
        "KEYS"
    };

    // -------------------------------------------------------------------------
    // 5. Performance & SLA Benchmarks (NFR-P2 & NFR-04)
    // -------------------------------------------------------------------------
    public static final long MAX_MATCHING_LATENCY_MS = 1000L; // NFR-P2 (< 1 s)
    public static final int DEFAULT_PAGE_SIZE        = 20;    // NFR-04
    public static final int MAX_PAGE_SIZE            = 100;   // NFR-04
}
