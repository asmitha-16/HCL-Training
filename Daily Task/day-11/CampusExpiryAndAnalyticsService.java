import java.util.*;

/**
 * CampusExpiryAndAnalyticsService.java
 *
 * Day 11 Project Deliverable: FR7 (60-Day Expiry) & FR8 (Recovery Rate Analytics)
 * Project: JFS-40 Campus Lost & Found Portal
 * -----------------------------------------------------------------------------
 * FR7: Scans items older than 60 days in custody and flags them for charity donation.
 * FR8: Computes overall campus recovery rate and category statistics.
 */
public class CampusExpiryAndAnalyticsService {

    public record CustodyItemRecord(String id, String title, String category, int daysInCustody, String status) {}

    private final List<CustodyItemRecord> inventory = new ArrayList<>();
    private final HandoverRepository handoverRepo;

    public CampusExpiryAndAnalyticsService(HandoverRepository handoverRepo) {
        this.handoverRepo = handoverRepo;
    }

    public void addCustodyItem(CustodyItemRecord item) {
        inventory.add(item);
    }

    // FR7: 60-Day Expiry & Charitable Donation
    public List<CustodyItemRecord> sweepExpiredItemsForDonation() {
        List<CustodyItemRecord> donationBatch = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            CustodyItemRecord item = inventory.get(i);
            if (item.daysInCustody() >= 60 && "FOUND_IN_CUSTODY".equals(item.status())) {
                CustodyItemRecord expiredItem = new CustodyItemRecord(
                    item.id(), item.title(), item.category(), item.daysInCustody(), "EXPIRED_FOR_DONATION"
                );
                inventory.set(i, expiredItem);
                donationBatch.add(expiredItem);
            }
        }
        return Collections.unmodifiableList(donationBatch);
    }

    // FR8: Campus Recovery Rate Analytics
    public double calculateOverallRecoveryRate() {
        int successfulHandovers = handoverRepo.count();
        int donated = 0;
        for (CustodyItemRecord item : inventory) {
            if ("EXPIRED_FOR_DONATION".equals(item.status())) {
                donated++;
            }
        }
        int totalTerminal = successfulHandovers + donated;
        if (totalTerminal == 0) return 0.0;
        return ((double) successfulHandovers / totalTerminal) * 100.0;
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("     CAMPUS LOST & FOUND FR7 & FR8 SERVICE — DAY 11 DELIVERABLE                ");
        System.out.println("     PROJECT: JFS-40 CAMPUS LOST AND FOUND PORTAL                              ");
        System.out.println("===============================================================================");

        HandoverRepository repo = new HandoverRepository();
        repo.save(new HandoverReceipt("RCPT-01", "FND-201", "CLM-01", "STU-8841", "SEC-01", "STUDENT_ID"));
        repo.save(new HandoverReceipt("RCPT-02", "FND-202", "CLM-02", "STAFF-02", "SEC-01", "EMPLOYEE_ID"));
        repo.save(new HandoverReceipt("RCPT-03", "FND-203", "CLM-03", "STU-9921", "SEC-02", "STUDENT_ID"));

        CampusExpiryAndAnalyticsService service = new CampusExpiryAndAnalyticsService(repo);
        service.addCustodyItem(new CustodyItemRecord("FND-204", "Rain Jacket", "CLOTHING", 68, "FOUND_IN_CUSTODY"));
        service.addCustodyItem(new CustodyItemRecord("FND-205", "Water Bottle", "OTHER", 62, "FOUND_IN_CUSTODY"));
        service.addCustodyItem(new CustodyItemRecord("FND-206", "Scientific Calculator", "ELECTRONICS", 15, "FOUND_IN_CUSTODY"));

        System.out.println("\n[1] EXECUTING FR7 60-DAY ITEM EXPIRY SWEEP:");
        List<CustodyItemRecord> donated = service.sweepExpiredItemsForDonation();
        for (CustodyItemRecord item : donated) {
            System.out.printf("  * Flagged for Charity Donation: %s - %s (%d days in custody)%n",
                    item.id(), item.title(), item.daysInCustody());
        }

        System.out.println("\n[2] COMPUTING FR8 CAMPUS RECOVERY RATE ANALYTICS:");
        System.out.println("  - Completed Handovers : " + repo.count());
        System.out.println("  - Expired / Donated   : " + donated.size());
        System.out.printf("  - CAMPUS RECOVERY RATE: %.2f%%%n", service.calculateOverallRecoveryRate());
        System.out.println("===============================================================================");
    }
}
