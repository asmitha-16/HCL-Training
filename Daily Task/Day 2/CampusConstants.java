package com.campus.lostandfound.common;

import java.util.List;

/**
 * CampusConstants.java
 *
 * Day 2 Deliverable: Centralized Business Rule Constants
 * Centralizes all domain business rules, scoring parameters, temporal expiry
 * policies, and security thresholds for the Campus Lost & Found Platform.
 */
public final class CampusConstants {

    // Suppress default constructor for utility class
    private CampusConstants() {
        throw novelUnsupportedOperationException();
    }

    private static UnsupportedOperationException novelUnsupportedOperationException() {
        return new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // -------------------------------------------------------------
    // 1. Custody & Expiry Rules (FR7: 60-Day Item Expiry & Donation)
    // -------------------------------------------------------------
    public static final int ITEM_EXPIRY_THRESHOLD_DAYS = 60;
    public static final String DEFAULT_EXPIRY_STATUS = "EXPIRED_FOR_DONATION";
    public static final int DONATION_BATCH_DEFAULT_SIZE = 50;

    // -------------------------------------------------------------
    // 2. Algorithmic Match Weights (FR3: Strategy Pattern Scoring)
    // -------------------------------------------------------------
    public static final double WEIGHT_CATEGORY = 0.35;        // 35% weight
    public static final double WEIGHT_LOCATION = 0.25;        // 25% weight
    public static final double WEIGHT_DATE_DELTA = 0.20;      // 20% weight
    public static final double WEIGHT_KEYWORD_SIMILARITY = 0.20; // 20% weight

    public static final double MATCH_CANDIDATE_MIN_SCORE = 0.50; // 50% cutoff
    public static final double MATCH_HIGH_CONFIDENCE_SCORE = 0.75; // 75% tier

    // -------------------------------------------------------------
    // 3. Concurrency & Claim Rules (FR6: Anti-Duplicate Claim Lock)
    // -------------------------------------------------------------
    public static final int MAX_ACTIVE_CLAIMS_PER_ITEM = 5;
    public static final long CLAIM_LOCK_TIMEOUT_SECONDS = 30L;

    // -------------------------------------------------------------
    // 4. Security Desk Handover Policies (FR5: In-Person Verification)
    // -------------------------------------------------------------
    public static final List<String> AUTHORIZED_ID_TYPES = List.of(
            "CAMPUS_STUDENT_ID",
            "FACULTY_STAFF_ID",
            "GOVERNMENT_PHOTO_ID",
            "DRIVING_LICENSE"
    );

    // -------------------------------------------------------------
    // 5. Privacy Protection Standards (NFR-P1: Image Masking)
    // -------------------------------------------------------------
    public static final int PRIVACY_BLUR_RADIUS_PIXELS = 15;
    public static final List<String> SENSITIVE_CATEGORIES = List.of(
            "ELECTRONICS",
            "WALLET_CARDS",
            "IDENTITY_DOCUMENTS",
            "KEYS"
    );

    // -------------------------------------------------------------
    // 6. SLA & Performance Benchmarks (NFR-P2: Sub-Second Latency)
    // -------------------------------------------------------------
    public static final long MAX_MATCHING_LATENCY_MS = 1000L;
}
