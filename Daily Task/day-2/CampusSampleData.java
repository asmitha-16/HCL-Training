/**
 * CampusSampleData.java
 *
 * Day 2 Deliverable: Week of Sample Data in Arrays
 * Project: JFS-40 Campus Lost & Found Portal
 * -----------------------------------------------------------------------------
 * Establishes structured baseline sample data representing a 7-day operational
 * cycle (Monday to Sunday) across campus buildings, lost reports, found items,
 * and ownership verification questions.
 */
public class CampusSampleData {

    // Simple POJO data containers for Day 2 array representation
    public static class CampusBuilding {
        public final String code;
        public final String name;
        public final String lockerBank;

        public CampusBuilding(String code, String name, String lockerBank) {
            this.code = code;
            this.name = name;
            this.lockerBank = lockerBank;
        }
    }

    public static class SampleReport {
        public final String id;
        public final String category;
        public final String title;
        public final String locationCode;
        public final String date;
        public final double estimatedValue;

        public SampleReport(String id, String category, String title, String locationCode, String date, double estimatedValue) {
            this.id = id;
            this.category = category;
            this.title = title;
            this.locationCode = locationCode;
            this.date = date;
            this.estimatedValue = estimatedValue;
        }
    }

    // 1. Campus Facility Locations
    public static final CampusBuilding[] BUILDINGS = {
        new CampusBuilding("LIB", "Main University Library", "LOCKER-BANK-A"),
        new CampusBuilding("SAC", "Student Activity Center", "LOCKER-BANK-B"),
        new CampusBuilding("ENG", "Engineering & Computing Block", "LOCKER-BANK-C"),
        new CampusBuilding("SCI", "Science Laboratories Complex", "LOCKER-BANK-D"),
        new CampusBuilding("DIN", "Central Dining Commons", "LOCKER-BANK-E")
    };

    // 2. 7-Day Cycle of Lost Item Reports (Day 1 - Monday to Day 7 - Sunday)
    public static final SampleReport[] WEEK_LOST_REPORTS = {
        new SampleReport("LOST-101", "ELECTRONICS", "Silver Apple MacBook Pro 14", "LIB", "2026-10-12", 1299.99),
        new SampleReport("LOST-102", "WALLET_CARDS", "Brown Tommy Hilfiger Leather Wallet", "DIN", "2026-10-13", 45.00),
        new SampleReport("LOST-103", "KEYS", "Brass Dorm Key on Blue Lanyard", "SAC", "2026-10-14", 15.00),
        new SampleReport("LOST-104", "ELECTRONICS", "Sony WH-1000XM5 Black Headphones", "ENG", "2026-10-15", 349.50),
        new SampleReport("LOST-105", "BAGS", "Nike Air Backpack with Gym Gear", "SAC", "2026-10-16", 60.00),
        new SampleReport("LOST-106", "ACADEMIC_ID", "Student Smartcard - Reg #7376231CD108", "SCI", "2026-10-17", 10.00),
        new SampleReport("LOST-107", "ELECTRONICS", "iPad Air 5th Gen with Apple Pencil", "LIB", "2026-10-18", 599.00)
    };

    // 3. Corresponding 7-Day Cycle of Found Property Turned In
    public static final SampleReport[] WEEK_FOUND_ITEMS = {
        new SampleReport("FND-201", "ELECTRONICS", "Apple MacBook Pro Silver in Felt Case", "LIB", "2026-10-12", 1299.99),
        new SampleReport("FND-202", "WALLET_CARDS", "Brown Bi-Fold Leather Wallet with ID", "DIN", "2026-10-13", 45.00),
        new SampleReport("FND-203", "KEYS", "Room Key with Blue Campus Lanyard", "SAC", "2026-10-14", 15.00),
        new SampleReport("FND-204", "ELECTRONICS", "Black Over-Ear Bluetooth Headphones", "ENG", "2026-10-15", 349.50),
        new SampleReport("FND-205", "BAGS", "Black/Grey Sports Backpack", "SAC", "2026-10-16", 60.00),
        new SampleReport("FND-206", "CLOTHING", "Navy Blue Windbreaker Jacket (Size M)", "DIN", "2026-08-10", 35.00) // 69 days (FR7)
    };

    // 4. Verification Challenge Matrix by Category (FR4)
    public static final String[][] VERIFICATION_QUESTIONS = {
        { "ELECTRONICS", "State lock screen wallpaper details or serial number." },
        { "WALLET_CARDS", "Name two specific bank/ID cards located in the inner slots." },
        { "KEYS", "Describe the key tag color, ring markings, or brand." },
        { "BAGS", "List three items stored inside the secondary zip compartment." },
        { "ACADEMIC_ID", "Confirm student full name and registered department." }
    };

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("          CAMPUS LOST & FOUND — DAY 2 SAMPLE DATA VERIFICATION                 ");
        System.out.println("===============================================================================");
        System.out.println("  - Campus Buildings Configured : " + BUILDINGS.length);
        System.out.println("  - 7-Day Week Lost Reports     : " + WEEK_LOST_REPORTS.length);
        System.out.println("  - 7-Day Week Found Items      : " + WEEK_FOUND_ITEMS.length);
        System.out.println("  - Category Challenge Sets     : " + VERIFICATION_QUESTIONS.length);

        double totalLostValuation = 0.0;
        for (SampleReport r : WEEK_LOST_REPORTS) {
            totalLostValuation += r.estimatedValue;
        }
        System.out.printf("  - 7-Day Lost Assets Valuation : $%.2f%n", totalLostValuation);
        System.out.println("===============================================================================");
    }
}
