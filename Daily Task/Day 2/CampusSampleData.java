package com.campus.lostandfound.common;

/**
 * CampusSampleData.java
 *
 * Day 2 Deliverable: Initial Domain Sample Data
 * Establishes baseline sample records using Java arrays and records to represent
 * initial campus inventory, locations, reports, claims, and verification rules
 * prior to database integration.
 */
public class CampusSampleData {

    // Domain records for sample data representation
    public record CampusBuilding(String code, String name, String custodyLockerPrefix) {}
    public record CategoryItem(String id, String name, boolean requiresPrivacyBlur) {}
    public record SampleItem(String id, String category, String title, String locationCode, String date) {}

    // 1. Initial Campus Locations & Custody Locker Banks
    public static final CampusBuilding[] BUILDINGS = {
        new CampusBuilding("LIB", "Main University Library", "LOCKER-A"),
        new CampusBuilding("SAC", "Student Activity Center", "LOCKER-B"),
        new CampusBuilding("ENG", "Engineering & Computing Block", "LOCKER-C"),
        new CampusBuilding("SCI", "Science Laboratories Complex", "LOCKER-D"),
        new CampusBuilding("DIN", "Central Dining Commons", "LOCKER-E")
    };

    // 2. Initial Category Taxonomies
    public static final CategoryItem[] CATEGORIES = {
        new CategoryItem("ELEC", "Electronics (Laptops, Phones, Tablets)", true),
        new CategoryItem("WALL", "Wallets, Purses & ID Cards", true),
        new CategoryItem("KEYS", "Hostel & Vehicle Keys", false),
        new CategoryItem("BOOK", "Textbooks & Laboratory Notebooks", false),
        new CategoryItem("CLOTH", "Jackets, Bags & Personal Wearables", false)
    };

    // 3. Sample Lost Item Reports (FR1)
    public static final SampleItem[] SAMPLE_LOST_REPORTS = {
        new SampleItem("LOST-101", "ELEC", "Silver MacBook Air M2 13-inch", "LIB", "2026-10-01"),
        new SampleItem("LOST-102", "WALL", "Brown Tommy Hilfiger Leather Wallet", "DIN", "2026-10-02"),
        new SampleItem("LOST-103", "KEYS", "Room 402 Key with Blue Carabiner", "SAC", "2026-10-03"),
        new SampleItem("LOST-104", "BOOK", "Data Structures & Algorithms 3rd Ed", "ENG", "2026-10-04")
    };

    // 4. Sample Found Inventory in Custody Lockers (FR2)
    public static final SampleItem[] SAMPLE_FOUND_ITEMS = {
        new SampleItem("FND-201", "ELEC", "Apple MacBook Air Silver in Grey Sleeve", "LIB", "2026-10-01"),
        new SampleItem("FND-202", "WALL", "Brown Bi-Fold Leather Wallet with Campus ID", "DIN", "2026-10-02"),
        new SampleItem("FND-203", "KEYS", "Silver Dorm Key with Carabiner Clip", "SAC", "2026-10-03"),
        new SampleItem("FND-204", "CLOTH", "Black North Face Rain Jacket (Size L)", "SCI", "2026-08-01") // > 60 days (FR7)
    };

    // 5. Sample Ownership Challenge Questions (FR4)
    public static final String[][] VERIFICATION_CHALLENGES = {
        {"ELEC", "Provide lock screen wallpaper description and serial number prefix."},
        {"WALL", "Name two specific cards or IDs stored inside the inner sleeve."},
        {"KEYS", "Specify the exact key brand or distinctive keychain markings."},
        {"BOOK", "State the student roll number or written inscription on page 1."}
    };

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("          CAMPUS LOST & FOUND — DAY 2 SAMPLE DATA VERIFIED     ");
        System.out.println("===============================================================");
        System.out.println("  - Registered Buildings : " + BUILDINGS.length);
        System.out.println("  - Active Categories    : " + CATEGORIES.length);
        System.out.println("  - Sample Lost Reports  : " + SAMPLE_LOST_REPORTS.length);
        System.out.println("  - Sample Found Items   : " + SAMPLE_FOUND_ITEMS.length);
        System.out.println("  - Challenge Rule Sets  : " + VERIFICATION_CHALLENGES.length);
        System.out.println("===============================================================");
    }
}
