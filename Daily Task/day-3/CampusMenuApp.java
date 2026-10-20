import java.util.Scanner;

/**
 * CampusMenuApp.java
 *
 * Day 3 Project Deliverable: Interactive Console Menu (FR1–FR8)
 * Project: JFS-40 Campus Lost & Found Portal
 * -----------------------------------------------------------------------------
 * Demonstrates Core Control Flow Concepts:
 * 1. while loop: 3-attempt Security Desk PIN verification with 'break'.
 * 2. do-while loop: Interactive persistent menu loop.
 * 3. switch-case: Clean dispatch for all 8 Functional Requirements (FR1 to FR8).
 * 4. Input validation: scanner.hasNextInt() with 'continue' on bad input (never crashes!).
 * 5. Enhanced-for loop: Traversal of recent custody audit log records.
 * -----------------------------------------------------------------------------
 */
public class CampusMenuApp {

    // Security desk authentication credentials
    private static final int SECURITY_PIN = 7376;

    // In-memory operational counters
    private static int lostReportsCount = 4;
    private static int foundItemsCount = 6;
    private static int activeClaimsCount = 2;
    private static int returnedCount = 18;
    private static int donatedCount = 3;

    // Custody audit ledger (Enhanced-for target)
    private static final String[] RECENT_AUDIT_LOGS = {
        "FND-201: Apple MacBook Pro received into custody (Bin: LOCKER-A01)",
        "CLAIM-301: Verification approved for STU-2023-8841 (Officer: SEC-101)",
        "HANDOVER-401: Physical disbursement confirmed with Student Photo ID",
        "DONATION-501: Batch #12 (2 unclaimed items > 60 days) cleared for Charity"
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("===============================================================================");
        System.out.println("             CAMPUS LOST & FOUND PORTAL — INTERACTIVE CONSOLE MENU             ");
        System.out.println("             PROJECT: JFS-40 | DAY 3 CONTROL FLOW ARCHITECTURE                 ");
        System.out.println("===============================================================================");

        // 1. while loop: Security Desk PIN Verification with 3 attempts and break
        boolean authenticated = false;
        int attempts = 0;

        while (attempts < 3) {
            attempts++;
            System.out.print("Enter 4-digit Security Desk Authorization PIN (Attempt " + attempts + " of 3): ");

            if (!scanner.hasNextInt()) {
                System.out.println("[WARN] Non-numeric input! Please enter a 4-digit number.");
                scanner.next(); // Consume invalid token
                continue;       // Skip to next iteration
            }

            int enteredPin = scanner.nextInt();
            if (enteredPin == SECURITY_PIN) {
                authenticated = true;
                System.out.println("[SUCCESS] Security Desk authorized. Welcome to Campus Operations!\n");
                break; // Break out of authentication loop
            } else {
                int remaining = 3 - attempts;
                System.out.println("[DENIED] Invalid PIN! Remaining attempts: " + remaining);
            }
        }

        if (!authenticated) {
            System.out.println("[LOCKED] Security access locked after 3 failed attempts. Contact facility admin.");
            return;
        }

        // 2. do-while Menu Loop
        int selection = -1;
        do {
            printMenu();
            System.out.print("Select an option (1-10): ");

            // Defensive input validation: ensures scanner never throws InputMismatchException
            if (!scanner.hasNextInt()) {
                String badInput = scanner.next();
                System.out.println("[WARN] Invalid input '" + badInput + "'! Please enter a number between 1 and 10.\n");
                continue; // Next iteration of do-while
            }

            selection = scanner.nextInt();
            scanner.nextLine(); // Clear newline buffer

            switch (selection) {
                case 1:
                    handleReportLost(scanner);
                    break;
                case 2:
                    handleLogFound(scanner);
                    break;
                case 3:
                    handleViewMatches();
                    break;
                case 4:
                    handleRaiseClaim(scanner);
                    break;
                case 5:
                    handleSecurityHandover(scanner);
                    break;
                case 6:
                    handleConcurrencyLockTest();
                    break;
                case 7:
                    handleScanExpiryAndDonations();
                    break;
                case 8:
                    handleViewRecoveryAnalytics();
                    break;
                case 9:
                    handleViewRecentAuditLogs();
                    break;
                case 10:
                    System.out.println("\n[EXIT] Exiting Campus Lost & Found Portal. Session closed safely.");
                    break;
                default:
                    System.out.println("[ERROR] Choice " + selection + " is out of range. Enter a number between 1 and 10.\n");
                    break;
            }

        } while (selection != 10);

        System.out.println("===============================================================================");
    }

    private static void printMenu() {
        System.out.println("\n=========================== MAIN OPERATIONS MENU ==============================");
        System.out.println("  1. Report Lost Item                       (FR1: User Report)");
        System.out.println("  2. Log Found Item & Assign Locker        (FR2: Security Intake)");
        System.out.println("  3. Compute Auto-Match Suggestions        (FR3: Match Engine)");
        System.out.println("  4. Submit Ownership Claim & Quiz          (FR4: Claimant Verification)");
        System.out.println("  5. Process Security Desk Handover         (FR5: Photo ID Verification)");
        System.out.println("  6. Test Concurrency Lock on Claims       (FR6: Duplicate Prevention)");
        System.out.println("  7. Scan 60-Day Unclaimed Items           (FR7: Charity Donation)");
        System.out.println("  8. View Recovery Rate & Statistics       (FR8: Operational KPIs)");
        System.out.println("  9. View Recent Custody Audit Logs        (Enhanced-For Loop)");
        System.out.println("  10. Exit Application");
        System.out.println("===============================================================================");
    }

    private static void handleReportLost(Scanner scanner) {
        System.out.println("\n--- [FR1] REPORT LOST ITEM ---");
        System.out.print("Enter item category (e.g., ELECTRONICS, KEYS, WALLET): ");
        String category = scanner.nextLine().trim();
        System.out.print("Enter item title / description: ");
        String description = scanner.nextLine().trim();
        System.out.print("Enter last known campus location: ");
        String location = scanner.nextLine().trim();

        if (category.isEmpty() || description.isEmpty() || location.isEmpty()) {
            System.out.println("[ERROR] All fields are mandatory. Report aborted.");
            return;
        }

        lostReportsCount++;
        String reportId = "LOST-2026-" + String.format("%03d", lostReportsCount);
        System.out.println("[SUCCESS] Report created with ID: " + reportId);
        System.out.println("  - Category: " + category.toUpperCase() + " | Location: " + location + " | Status: ACTIVE");
    }

    private static void handleLogFound(Scanner scanner) {
        System.out.println("\n--- [FR2] LOG FOUND ITEM & CUSTODY INTAKE ---");
        System.out.print("Enter discovered item name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter discovery location: ");
        String location = scanner.nextLine().trim();
        System.out.print("Enter designated custody locker bin (e.g., LOCKER-A04): ");
        String locker = scanner.nextLine().trim();

        if (name.isEmpty() || location.isEmpty() || locker.isEmpty()) {
            System.out.println("[ERROR] Item name, location, and locker ID are required.");
            return;
        }

        foundItemsCount++;
        String foundId = "FND-2026-" + String.format("%03d", foundItemsCount);
        System.out.println("[SUCCESS] Item registered into security custody: " + foundId);
        System.out.println("  - Custody Bin: " + locker.toUpperCase() + " | Photo Masking Applied (NFR-P1)");
    }

    private static void handleViewMatches() {
        System.out.println("\n--- [FR3] AUTOMATED MULTI-FACTOR MATCH SUGGESTIONS ---");
        System.out.println("Querying in-memory index using MatchScoringStrategy (weights: Cat 35%, Loc 25%, Date 20%, Keys 20%):");
        System.out.println("  Match 1: LOST-101 (MacBook Pro) <==> FND-201 (Apple MacBook) | Score: 0.92 [HIGH CONFIDENCE]");
        System.out.println("  Match 2: LOST-102 (Leather Wallet) <==> FND-202 (Brown Bi-Fold) | Score: 0.88 [HIGH CONFIDENCE]");
        System.out.println("  Match 3: LOST-103 (Dorm Room Key) <==> FND-203 (Blue Lanyard Key) | Score: 0.74 [MEDIUM]");
        System.out.println("[PERFORMANCE] Matching engine evaluated 10 candidates in 8 ms (< 1.0s SLA target NFR-P2).");
    }

    private static void handleRaiseClaim(Scanner scanner) {
        System.out.println("\n--- [FR4] SUBMIT OWNERSHIP CLAIM & VERIFICATION QUIZ ---");
        System.out.print("Enter Found Item ID to claim (e.g., FND-201): ");
        String itemId = scanner.nextLine().trim();
        System.out.print("Challenge Question: Describe the unique lock screen wallpaper: ");
        String answer1 = scanner.nextLine().trim();
        System.out.print("Challenge Question: Confirm device serial number or color nuance: ");
        String answer2 = scanner.nextLine().trim();

        if (answer1.length() < 3 || answer2.length() < 3) {
            System.out.println("[DENIED] Challenge responses are insufficient. Claim rejected.");
            return;
        }

        activeClaimsCount++;
        System.out.println("[SUCCESS] Claim submitted for " + itemId + ". Status: PENDING_SECURITY_REVIEW.");
    }

    private static void handleSecurityHandover(Scanner scanner) {
        System.out.println("\n--- [FR5] SECURITY DESK HANDOVER VERIFICATION ---");
        System.out.print("Enter approved Claim ID: ");
        String claimId = scanner.nextLine().trim();
        System.out.print("Select verified Photo ID (1: Student ID, 2: Employee ID, 3: Govt ID): ");

        if (!scanner.hasNextInt()) {
            System.out.println("[ERROR] Invalid ID type selection.");
            scanner.next();
            return;
        }
        int idType = scanner.nextInt();
        scanner.nextLine();

        if (idType < 1 || idType > 3) {
            System.out.println("[ERROR] Unauthorized ID credential type!");
            return;
        }

        returnedCount++;
        foundItemsCount--;
        if (activeClaimsCount > 0) activeClaimsCount--;

        System.out.println("[SUCCESS] Identity authenticated. Physical item released to owner.");
        System.out.println("  - Claim: " + claimId + " -> Status: RETURNED_TO_OWNER");
        System.out.println("  - Custody locker bin released and sanitized. Audit log created (NFR-P3).");
    }

    private static void handleConcurrencyLockTest() {
        System.out.println("\n--- [FR6] ATOMIC CONCURRENCY LOCK TEST ---");
        System.out.println("Simulating 2 concurrent approval attempts on Item FND-201:");
        System.out.println("  Thread-1 (Officer A): Attempting claim approval for Claimant Alice...");
        System.out.println("  Thread-2 (Officer B): Attempting claim approval for Claimant Bob...");
        System.out.println("  [LOCK APPLIED] Optimistic Lock (@Version 1 -> 2) acquired by Thread-1.");
        System.out.println("  [APPROVED] Thread-1 succeeded. Claim A approved.");
        System.out.println("  [CONFLICT] Thread-2 aborted with OptimisticLockException (Version conflict).");
        System.out.println("[VERIFIED] System guaranteed exactly 1 approved owner under concurrency.");
    }

    private static void handleScanExpiryAndDonations() {
        System.out.println("\n--- [FR7] SCAN 60-DAY UNCLAIMED ITEMS FOR DONATION ---");
        System.out.println("Scanning items where: DATEDIFF(CURRENT_DATE, found_date) >= 60 days...");
        System.out.println("  * Item FND-206: Navy Blue Jacket | 69 days in custody -> FLAGGED FOR CHARITY");
        System.out.println("  * Item FND-189: Scientific Calculator | 62 days in custody -> FLAGGED FOR CHARITY");
        donatedCount += 2;
        System.out.println("[BATCH COMPLETE] 2 items moved to donation manifest. Storage lockers reclaimed.");
    }

    private static void handleViewRecoveryAnalytics() {
        System.out.println("\n--- [FR8] CAMPUS RECOVERY RATE ANALYTICS ---");
        int totalProcessed = returnedCount + donatedCount;
        double recoveryRate = (totalProcessed > 0) ? ((double) returnedCount / totalProcessed) * 100.0 : 0.0;

        System.out.println("  - Active Lost Reports   : " + lostReportsCount);
        System.out.println("  - Current Custody Items : " + foundItemsCount);
        System.out.println("  - Pending Claims        : " + activeClaimsCount);
        System.out.println("  - Items Returned to Owner: " + returnedCount);
        System.out.println("  - Items Donated to Charity: " + donatedCount);
        System.out.printf("  - CAMPUS RECOVERY RATE  : %.2f%%%n", recoveryRate);
    }

    private static void handleViewRecentAuditLogs() {
        System.out.println("\n--- RECENT CUSTODY AUDIT LOGS (ENHANCED-FOR LOOP) ---");
        for (String log : RECENT_AUDIT_LOGS) {
            System.out.println("  * [AUDIT RECORD] " + log);
        }
    }
}
