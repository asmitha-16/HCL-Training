import java.util.*;

/**
 * CampusAiReviewDrill.java
 *
 * Day 11 Hands-on Task: AI Integration (Core Java) & Code Review Drill
 * Project: JFS-40 Campus Lost & Found Portal
 * -----------------------------------------------------------------------------
 * Demonstrates 5 critical defects caught and corrected from AI Copilot output
 * specifically in the Campus Lost & Found domain:
 *   [Defect 1 Fixed] Public fields in LostReport replaced with private encapsulated fields.
 *   [Defect 2 Fixed] Returning internal mutable list replaced with unmodifiableList.
 *   [Defect 3 Fixed] Missing equals/hashCode implemented on unique report ID.
 *   [Defect 4 Fixed] Empty swallowed catch replaced with domain exception propagation.
 *   [Defect 5 Fixed] Inefficient O(N) linear search upgraded to O(1) Map lookup.
 * -----------------------------------------------------------------------------
 */
public class CampusAiReviewDrill {

    // Production-Grade Corrected LostReport Entity
    public static class LostReport {
        private final String id;
        private final String reporterEmail;
        private final String category;
        private final String title;
        private final String location;

        public LostReport(String id, String reporterEmail, String category, String title, String location) {
            if (id == null || id.isBlank()) throw new IllegalArgumentException("Report ID cannot be blank");
            if (reporterEmail == null || !reporterEmail.contains("@")) throw new IllegalArgumentException("Valid campus email required");
            if (category == null || category.isBlank()) throw new IllegalArgumentException("Category required");
            if (title == null || title.isBlank()) throw new IllegalArgumentException("Title required");

            this.id = id.trim();
            this.reporterEmail = reporterEmail.trim();
            this.category = category.trim().toUpperCase();
            this.title = title.trim();
            this.location = (location != null) ? location.trim() : "CAMPUS_UNKNOWN";
        }

        public String getId() { return id; }
        public String getReporterEmail() { return reporterEmail; }
        public String getCategory() { return category; }
        public String getTitle() { return title; }
        public String getLocation() { return location; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof LostReport that)) return false;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return String.format("LostReport[ID=%s, Title='%s', Cat=%s, Loc='%s']", id, title, category, location);
        }
    }

    // Production-Grade Corrected LostItemService
    public static class LostItemService {
        private final Map<String, LostReport> reportRegistry = new HashMap<>();

        public void submitReport(LostReport report) {
            if (report == null) throw new IllegalArgumentException("Report cannot be null");
            if (reportRegistry.containsKey(report.getId())) {
                throw new IllegalStateException("Duplicate lost report ID: " + report.getId());
            }
            reportRegistry.put(report.getId(), report);
        }

        public Optional<LostReport> findById(String id) {
            if (id == null || id.isBlank()) return Optional.empty();
            return Optional.ofNullable(reportRegistry.get(id.trim()));
        }

        // Defect 2 Fix: Defensive copying prevents external mutation of internal collection
        public List<LostReport> getAllReports() {
            return Collections.unmodifiableList(new ArrayList<>(reportRegistry.values()));
        }
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("     CAMPUS LOST & FOUND AI CODE REVIEW DRILL — DAY 11 CORE JAVA               ");
        System.out.println("     PROJECT: JFS-40 CAMPUS LOST AND FOUND PORTAL                              ");
        System.out.println("===============================================================================");

        LostItemService service = new LostItemService();
        service.submitReport(new LostReport("LOST-101", "student@campus.edu", "ELECTRONICS", "Silver MacBook Pro 14", "LIB"));
        service.submitReport(new LostReport("LOST-102", "staff@campus.edu", "WALLET_CARDS", "Brown Leather Wallet", "DIN"));

        System.out.println("\n[1] VERIFYING REFACTORED AI CODE:");
        System.out.println("  Registered Report: " + service.findById("LOST-101").orElse(null));

        System.out.println("\n[2] TESTING DEFECT 2 FIX (IMMUTABILITY DEFENSE):");
        List<LostReport> reports = service.getAllReports();
        try {
            reports.add(new LostReport("LOST-999", "hacker@evil.com", "KEYS", "Master Key", "ADMIN"));
        } catch (UnsupportedOperationException uoe) {
            System.out.println("  [PROTECTED] Attempt to mutate internal repository rejected with UnsupportedOperationException!");
        }

        System.out.println("\n[3] TESTING DEFECT 4 FIX (NO SWALLOWED EXCEPTIONS):");
        try {
            service.submitReport(new LostReport("LOST-101", "duplicate@campus.edu", "ELECTRONICS", "MacBook Air", "LIB"));
        } catch (IllegalStateException ise) {
            System.out.println("  [CAUGHT EXPECTED EXCEPTION] " + ise.getMessage());
        }

        System.out.println("===============================================================================");
    }
}
