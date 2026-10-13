package com.campus.lostandfound.constant;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Constants defining system business rules, thresholds, categories,
 * locations, and a full week of pre-seeded sample data.
 */
public final class Constants {

    private Constants() {
        // Prevent instantiation
    }

    // ==========================================
    // Business Rules Constants
    // ==========================================
    public static final int EXPIRY_DAYS_THRESHOLD = 60;
    public static final double MIN_SIMILARITY_MATCH_SCORE = 0.50; // 50% minimum match
    public static final double HIGH_SIMILARITY_MATCH_SCORE = 0.75; // 75% high confidence
    public static final double HIGH_VALUE_THRESHOLD = 500.00;
    public static final int MAX_REPORT_AGE_DAYS = 365;

    // Item Statuses
    public static final String STATUS_REPORTED = "REPORTED";
    public static final String STATUS_IN_CUSTODY = "IN_CUSTODY";
    public static final String STATUS_MATCHED = "MATCHED";
    public static final String STATUS_CLAIM_PENDING = "CLAIM_PENDING";
    public static final String STATUS_CLAIM_APPROVED = "CLAIM_APPROVED";
    public static final String STATUS_CLAIM_REJECTED = "CLAIM_REJECTED";
    public static final String STATUS_HANDED_OVER = "HANDED_OVER";
    public static final String STATUS_BATCHED_FOR_DONATION = "BATCHED_FOR_DONATION";

    // Allowed Standard Categories
    public static final List<String> CATEGORIES = Collections.unmodifiableList(Arrays.asList(
            "ELECTRONICS",
            "BOOKS_STATIONERY",
            "CLOTHING_ACCESSORIES",
            "ID_CARDS_WALLETS",
            "KEYS",
            "SPORTS_EQUIPMENT",
            "OTHER"
    ));

    // Valid Campus Locations
    public static final List<String> LOCATIONS = Collections.unmodifiableList(Arrays.asList(
            "Main Library - 1st Floor Reading Room",
            "Science Block - Lab 302",
            "Engineering Quad - Open Lawn",
            "Student Cafeteria - Food Court",
            "Indoor Sports Complex - Badminton Court",
            "Hostel Block A - Lobby",
            "Admin Building - Ground Floor Atrium"
    ));

    // Storage Custody Lockers
    public static final List<String> CUSTODY_LOCKERS = Collections.unmodifiableList(Arrays.asList(
            "LOCKER-SEC-A01",
            "LOCKER-SEC-A02",
            "LOCKER-SEC-B01",
            "LOCKER-SEC-B02",
            "LOCKER-SEC-C01",
            "LOCKER-SEC-C02",
            "LOCKER-SEC-D01"
    ));

    // ==========================================
    // 1 Week of Sample Data in Arrays (Day 1 - Day 7)
    // ==========================================
    public static final LocalDate TODAY = LocalDate.now();

    public static final String[] SAMPLE_DATES = new String[] {
            TODAY.minusDays(6).toString(), // Day 1
            TODAY.minusDays(5).toString(), // Day 2
            TODAY.minusDays(4).toString(), // Day 3
            TODAY.minusDays(3).toString(), // Day 4
            TODAY.minusDays(2).toString(), // Day 5
            TODAY.minusDays(1).toString(), // Day 6
            TODAY.toString()               // Day 7
    };

    /**
     * Preloaded Lost Items (Day 1 to Day 7)
     * Format: {ID, Title, Category, Location, Date, Brand, Color, Features, Value, ReporterUsername}
     */
    public static final String[][] SAMPLE_LOST_ITEMS_DATA = new String[][] {
            // Day 1
            { "LOST-1001", "MacBook Air M2 Space Gray", "ELECTRONICS",
              "Main Library - 1st Floor Reading Room", TODAY.minusDays(6).toString(),
              "Apple", "Space Gray", "Sticker of Docker on lid, minor scratch on bezel", "1199.00", "asmitha_student" },
            // Day 2
            { "LOST-1002", "Sony WH-1000XM4 Noise Canceling Headphones", "ELECTRONICS",
              "Engineering Quad - Open Lawn", TODAY.minusDays(5).toString(),
              "Sony", "Black", "Custom red headband foam, small dent on right cup", "348.00", "rahul_student" },
            // Day 3
            { "LOST-1003", "Data Structures & Algorithms in Java (Sedgewick)", "BOOKS_STATIONERY",
              "Student Cafeteria - Food Court", TODAY.minusDays(4).toString(),
              "Pearson", "Blue/White", "Highlighted chapters 3 and 4 with green marker", "75.00", "priya_student" },
            // Day 4
            { "LOST-1004", "Brown Leather Fossil Wallet", "ID_CARDS_WALLETS",
              "Science Block - Lab 302", TODAY.minusDays(3).toString(),
              "Fossil", "Brown", "Contains College Student ID ending with 108 and bus pass", "45.00", "kavya_student" },
            // Day 5
            { "LOST-1005", "Titan Silver Stainless Steel Wristwatch", "CLOTHING_ACCESSORIES",
              "Indoor Sports Complex - Badminton Court", TODAY.minusDays(2).toString(),
              "Titan", "Silver", "Metal chain with clasp engraving 'B.A. 2024'", "120.00", "arun_staff" },
            // Day 6
            { "LOST-1006", "Set of 3 Honda Bike Keys with Red Carabiner", "KEYS",
              "Hostel Block A - Lobby", TODAY.minusDays(1).toString(),
              "Honda", "Silver/Red", "Red aluminum carabiner with small metallic guitar charm", "25.00", "vikram_student" },
            // Day 7
            { "LOST-1007", "Yonex Astrox 88D Pro Badminton Racket", "SPORTS_EQUIPMENT",
              "Indoor Sports Complex - Badminton Court", TODAY.toString(),
              "Yonex", "Ruby Red / Black", "Black BG65 string at 26lbs tension, yellow grip tape", "180.00", "sneha_student" }
    };

    /**
     * Preloaded Found Items (Day 1 to Day 7)
     * Format: {ID, Title, Category, Location, Date, LockerNumber, SecretFeature, FinderUsername, InitialStatus}
     */
    public static final String[][] SAMPLE_FOUND_ITEMS_DATA = new String[][] {
            // Day 1
            { "FOUND-2001", "Apple MacBook Air 13-inch", "ELECTRONICS",
              "Main Library - 1st Floor Reading Room", TODAY.minusDays(6).toString(),
              "LOCKER-SEC-A01", "Docker whale sticker on the aluminum top casing", "officer_murthy", "IN_CUSTODY" },
            // Day 2
            { "FOUND-2002", "Black Wireless Over-Ear Headphones", "ELECTRONICS",
              "Engineering Quad - Open Lawn", TODAY.minusDays(5).toString(),
              "LOCKER-SEC-A02", "Sony brand, red padding modification on headband", "cleaner_raju", "IN_CUSTODY" },
            // Day 3
            { "FOUND-2003", "Textbook: Algorithms 4th Edition", "BOOKS_STATIONERY",
              "Student Cafeteria - Food Court", TODAY.minusDays(4).toString(),
              "LOCKER-SEC-B01", "Sedgewick author, inside page has handwritten name 'P. Swetha'", "staff_deepa", "IN_CUSTODY" },
            // Day 4
            { "FOUND-2004", "Men's Brown Bi-fold Leather Wallet", "ID_CARDS_WALLETS",
              "Science Block - Lab 302", TODAY.minusDays(3).toString(),
              "LOCKER-SEC-B02", "College ID card #7376231CD108 inside photo slot", "officer_murthy", "CLAIM_PENDING" },
            // Day 5
            { "FOUND-2005", "Silver Dial Analog Watch with Metal Strap", "CLOTHING_ACCESSORIES",
              "Indoor Sports Complex - Badminton Court", TODAY.minusDays(2).toString(),
              "LOCKER-SEC-C01", "Rear casing inscribed with initials 'B.A. 2024'", "coach_kumar", "IN_CUSTODY" },
            // Day 6
            { "FOUND-2006", "Motorcycle Keys with Decorative Carabiner", "KEYS",
              "Hostel Block A - Lobby", TODAY.minusDays(1).toString(),
              "LOCKER-SEC-C02", "Red clip carabiner with silver miniature guitar pendant", "warden_shanti", "IN_CUSTODY" },
            // Day 7 (Older expired item for FR7 testing - reported 65 days ago)
            { "FOUND-2007", "Blue Umbrella with Wooden Handle", "OTHER",
              "Admin Building - Ground Floor Atrium", TODAY.minusDays(65).toString(),
              "LOCKER-SEC-D01", "Initials 'K.R.' on wooden curved handle", "officer_murthy", "IN_CUSTODY" }
    };

    /**
     * Preloaded Sample Users
     * Format: {ID, Username, FullName, Email, Role, DepartmentOrMajor}
     */
    public static final String[][] SAMPLE_USERS_DATA = new String[][] {
            { "USR-001", "asmitha_student", "Asmitha B", "asmitha@campus.edu", "STUDENT", "Computer Science" },
            { "USR-002", "rahul_student", "Rahul Sharma", "rahul@campus.edu", "STUDENT", "Electronics & Comm" },
            { "USR-003", "arun_staff", "Dr. Arun Kumar", "arun.kumar@campus.edu", "STAFF", "Department of Physics" },
            { "USR-004", "officer_murthy", "Officer K. Murthy", "security.murthy@campus.edu", "SECURITY_DESK", "Main Gate & Library Post" },
            { "USR-005", "admin_sharma", "Dean R. Sharma", "admin.sharma@campus.edu", "ADMIN", "Campus Administration & Safety" }
    };
}
