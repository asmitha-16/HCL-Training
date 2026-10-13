package com.campus.lostandfound.menu;

import com.campus.lostandfound.constant.Constants;
import com.campus.lostandfound.exception.BusinessRuleViolationException;
import com.campus.lostandfound.exception.CampusLostAndFoundException;
import com.campus.lostandfound.exception.ItemAlreadyClaimedException;
import com.campus.lostandfound.exception.VerificationFailedException;
import com.campus.lostandfound.model.Claim;
import com.campus.lostandfound.model.FoundItem;
import com.campus.lostandfound.model.LostItem;
import com.campus.lostandfound.service.LostAndFoundService;
import com.campus.lostandfound.strategy.ExactCategoryMatchStrategy;
import com.campus.lostandfound.strategy.MatchResult;
import com.campus.lostandfound.strategy.MatchScoringStrategy;
import com.campus.lostandfound.strategy.WeightedMultiFactorMatchStrategy;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive Console Menu supporting 1 option per Functional Requirement (FR1–FR8).
 * Robustly recovers from any exception/business rule violation without crashing.
 */
public class ConsoleMenu {

    private final LostAndFoundService service;
    private final Scanner scanner;
    private final MatchScoringStrategy defaultStrategy;
    private final MatchScoringStrategy exactStrategy;

    public ConsoleMenu(LostAndFoundService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
        this.defaultStrategy = new WeightedMultiFactorMatchStrategy();
        this.exactStrategy = new ExactCategoryMatchStrategy();
    }

    public void run() {
        printBanner();

        boolean running = true;
        while (running) {
            try {
                displayMainMenu();
                System.out.print("\nSelect Option [0-9]: ");
                String input = scanner.nextLine().trim();

                switch (input) {
                    case "1":
                        handleFR1ReportLostItem();
                        break;
                    case "2":
                        handleFR2LogFoundItem();
                        break;
                    case "3":
                        handleFR3MatchSuggestion();
                        break;
                    case "4":
                        handleFR4SubmitClaim();
                        break;
                    case "5":
                        handleFR5PhysicalHandover();
                        break;
                    case "6":
                        handleFR6ApproveClaim();
                        break;
                    case "7":
                        handleFR7ScanExpiryDonation();
                        break;
                    case "8":
                        handleFR8AnalyticsDashboard();
                        break;
                    case "9":
                        handleViewInventory();
                        break;
                    case "0":
                        running = false;
                        System.out.println("\nExiting Campus Lost & Found Platform. Goodbye!");
                        break;
                    default:
                        System.out.println("\n[INVALID INPUT] Please select an option between 0 and 9.");
                }
            } catch (ItemAlreadyClaimedException e) {
                // Business Rule 1 Recovery
                System.out.println("\n>>> [BUSINESS RULE 1 VIOLATION - CONCURRENCY GUARD] <<<");
                System.out.println("  " + e.getMessage());
                System.out.println("  The menu recovered safely. Returning to main menu.\n");
            } catch (VerificationFailedException e) {
                // Anti-fraud Challenge Recovery
                System.out.println("\n>>> [ANTI-FRAUD CHALLENGE FAILED] <<<");
                System.out.println("  " + e.getMessage());
                System.out.println("  The menu recovered safely. Returning to main menu.\n");
            } catch (BusinessRuleViolationException e) {
                // Business Rule 2 Recovery
                System.out.println("\n>>> [BUSINESS RULE 2 VIOLATION - INPUT INTEGRITY] <<<");
                System.out.println("  " + e.getMessage());
                System.out.println("  The menu recovered safely. Returning to main menu.\n");
            } catch (CampusLostAndFoundException e) {
                // General domain exception recovery
                System.out.println("\n>>> [DOMAIN ERROR] <<<");
                System.out.println("  " + e.getMessage());
                System.out.println("  The menu recovered safely. Returning to main menu.\n");
            } catch (Exception e) {
                // Generic error recovery
                System.out.println("\n>>> [SYSTEM ERROR - RECOVERED] <<<");
                System.out.println("  Unexpected input or format error: " + e.getMessage());
                System.out.println("  The menu recovered safely. Returning to main menu.\n");
            }
        }
    }

    private void printBanner() {
        System.out.println("===============================================================================");
        System.out.println("          CAMPUS LOST & FOUND ENTERPRISE CONSOLE APPLICATION                   ");
        System.out.println("===============================================================================");
        System.out.println("  Repository Architecture: Maven Modular (Parent POM ready)");
        System.out.println("  Collections: List (Reports), Set (Unique Inventory), Queue (Claims)");
        System.out.println("  Business Rules Active: BR-1 (Anti-Duplicate Lock), BR-2 (Date & Valuation)");
        System.out.println("  Preloaded: 7 Days of Sample Data in Arrays (Day 1 - Day 7)");
        System.out.println("===============================================================================");
    }

    private void displayMainMenu() {
        System.out.println("\n----------------------------- CONSOLE MENU ----------------------------------");
        System.out.println("  [1] FR1: Report a Lost Item (Stored in List<LostItem>)");
        System.out.println("  [2] FR2: Log a Found Item & Assign Locker (Stored in Set<FoundItem>)");
        System.out.println("  [3] FR3: Search & Suggest Algorithmic Matches (Strategy Pattern)");
        System.out.println("  [4] FR4: Submit Ownership Claim with Anti-Fraud Quiz (Enqueued to Queue<Claim>)");
        System.out.println("  [5] FR5: Physical Handover & Custody Signature");
        System.out.println("  [6] FR6: Approve Claim with Atomic Concurrency Guard (Enforces BR-1)");
        System.out.println("  [7] FR7: Scan 60-Day Expiry & Batch Unclaimed Items for Donation");
        System.out.println("  [8] FR8: View Real-Time Recovery Rate & Analytics KPI Dashboard");
        System.out.println("  [9] DATA: View 7-Day Preloaded Sample Data (List, Set, Queue)");
        System.out.println("  [0] Exit Application");
        System.out.println("-----------------------------------------------------------------------------");
    }

    // =========================================================================
    // FR1 Implementation
    // =========================================================================
    private void handleFR1ReportLostItem() {
        System.out.println("\n--- [FR1] Report Lost Item ---");
        System.out.print("Item Title: ");
        String title = scanner.nextLine().trim();

        System.out.println("Available Categories: " + Constants.CATEGORIES);
        System.out.print("Category: ");
        String category = scanner.nextLine().trim().toUpperCase();

        System.out.println("Available Locations: " + Constants.LOCATIONS);
        System.out.print("Location lost: ");
        String location = scanner.nextLine().trim();

        System.out.print("Incident Date (YYYY-MM-DD, e.g., " + LocalDate.now() + "): ");
        String dateStr = scanner.nextLine().trim();
        LocalDate lostDate;
        try {
            lostDate = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new BusinessRuleViolationException("BR-02-DATE",
                    "Invalid date format: '" + dateStr + "'. Must be YYYY-MM-DD.");
        }

        System.out.print("Brand / Manufacturer: ");
        String brand = scanner.nextLine().trim();

        System.out.print("Color: ");
        String color = scanner.nextLine().trim();

        System.out.print("Distinctive Features / Markings: ");
        String features = scanner.nextLine().trim();

        System.out.print("Estimated Monetary Value ($): ");
        String valStr = scanner.nextLine().trim();
        double value;
        try {
            value = Double.parseDouble(valStr);
        } catch (NumberFormatException e) {
            throw new BusinessRuleViolationException("BR-02-VAL",
                    "Invalid monetary value: '" + valStr + "'. Must be a valid number.");
        }

        System.out.print("Your Campus Username: ");
        String reporter = scanner.nextLine().trim();
        if (reporter.isEmpty()) reporter = "student_user";

        LostItem item = service.reportLostItem(title, category, location, lostDate,
                brand, color, features, value, reporter);

        System.out.println("\n[SUCCESS] Lost item reported successfully!");
        System.out.println("  Assigned Tracking ID: " + item.getId());
        System.out.println("  Stored in: List<LostItem> (Current count: " + service.getDataStore().getLostItems().size() + ")");
        System.out.println("  High-Value Flag: " + (item.isHighValue() ? "YES (Priority Escalation)" : "NO"));
    }

    // =========================================================================
    // FR2 Implementation
    // =========================================================================
    private void handleFR2LogFoundItem() {
        System.out.println("\n--- [FR2] Log Found Item & Assign Custody Locker ---");
        System.out.print("Item Title: ");
        String title = scanner.nextLine().trim();

        System.out.println("Available Categories: " + Constants.CATEGORIES);
        System.out.print("Category: ");
        String category = scanner.nextLine().trim().toUpperCase();

        System.out.println("Available Locations: " + Constants.LOCATIONS);
        System.out.print("Discovery Location: ");
        String location = scanner.nextLine().trim();

        System.out.print("Discovery Date (YYYY-MM-DD, e.g., " + LocalDate.now() + "): ");
        String dateStr = scanner.nextLine().trim();
        LocalDate foundDate;
        try {
            foundDate = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new BusinessRuleViolationException("BR-02-DATE",
                    "Invalid date format: '" + dateStr + "'. Must be YYYY-MM-DD.");
        }

        System.out.println("Available Lockers: " + Constants.CUSTODY_LOCKERS);
        System.out.print("Custody Locker ID: ");
        String locker = scanner.nextLine().trim().toUpperCase();

        System.out.print("Confidential Secret Feature (Anti-Fraud Challenge Key): ");
        String secret = scanner.nextLine().trim();

        System.out.print("Finder / Officer Username: ");
        String finder = scanner.nextLine().trim();
        if (finder.isEmpty()) finder = "officer_murthy";

        FoundItem item = service.logFoundItem(title, category, location, foundDate, locker, secret, finder);

        System.out.println("\n[SUCCESS] Found item logged into secure custody!");
        System.out.println("  Assigned Inventory ID: " + item.getId());
        System.out.println("  Locker: " + item.getLockerNumber());
        System.out.println("  Calculated 60-Day Expiry Date: " + item.getExpiryDate());
        System.out.println("  Stored in: Set<FoundItem> (Current unique items: " + service.getDataStore().getFoundItems().size() + ")");
    }

    // =========================================================================
    // FR3 Implementation
    // =========================================================================
    private void handleFR3MatchSuggestion() {
        System.out.println("\n--- [FR3] Search & Suggest Algorithmic Matches ---");
        System.out.println("Recently Reported Lost Items:");
        List<LostItem> lostItems = service.getDataStore().getLostItems();
        for (int i = Math.max(0, lostItems.size() - 5); i < lostItems.size(); i++) {
            LostItem li = lostItems.get(i);
            System.out.println("  - " + li.getId() + ": " + li.getTitle() + " (" + li.getCategory() + ")");
        }

        System.out.print("\nEnter Lost Item ID to find matches for (e.g. LOST-1001, LOST-1004): ");
        String lostId = scanner.nextLine().trim().toUpperCase();

        System.out.println("\nSelect Scoring Strategy:");
        System.out.println("  [1] Weighted Multi-Factor Strategy (Category 35%, Location 25%, Date 20%, Keywords 20%)");
        System.out.println("  [2] Exact Category & Keyword Match Strategy");
        System.out.print("Choice [1-2, default 1]: ");
        String stratChoice = scanner.nextLine().trim();

        MatchScoringStrategy strategy = "2".equals(stratChoice) ? exactStrategy : defaultStrategy;

        System.out.println("\nRunning " + strategy.getStrategyName() + " against Set<FoundItem>...");
        List<MatchResult> matches = service.findMatches(lostId, strategy);

        if (matches.isEmpty()) {
            System.out.println("\n[NO MATCHES FOUND] No found items scored above the "
                    + (int)(Constants.MIN_SIMILARITY_MATCH_SCORE * 100) + "% confidence threshold.");
        } else {
            System.out.println("\n[SUCCESS] Found " + matches.size() + " candidate match(es) ranked by confidence score:");
            for (int i = 0; i < matches.size(); i++) {
                MatchResult mr = matches.get(i);
                System.out.printf("  #%d [%d%% Confidence] [%s]\n", (i + 1), mr.getConfidencePercentage(), mr.getMatchTier());
                System.out.println("     Found ID: " + mr.getFoundItem().getId() + " - " + mr.getFoundItem().getTitle());
                System.out.println("     Category: " + mr.getFoundItem().getCategory() + " | Location: " + mr.getFoundItem().getLocation());
                System.out.println("     Date Found: " + mr.getFoundItem().getFoundDate() + " | Custody Locker: " + mr.getFoundItem().getLockerNumber());
            }
        }
    }

    // =========================================================================
    // FR4 Implementation
    // =========================================================================
    private void handleFR4SubmitClaim() {
        System.out.println("\n--- [FR4] Submit Ownership Claim (Verification Quiz) ---");
        System.out.print("Target Found Item ID (e.g. FOUND-2001, FOUND-2004): ");
        String foundId = scanner.nextLine().trim().toUpperCase();

        System.out.print("Claimant Username: ");
        String claimant = scanner.nextLine().trim();

        System.out.print("Verification Quiz Answer (Describe confidential unique markings/passwords): ");
        String secretAnswer = scanner.nextLine().trim();

        System.out.print("Proof of Purchase or Supporting Circumstantial Description: ");
        String proof = scanner.nextLine().trim();

        Claim claim = service.submitClaim(foundId, claimant, secretAnswer, proof);

        System.out.println("\n[SUCCESS] Claim registered and queued for officer review!");
        System.out.println("  Claim ID: " + claim.getId());
        System.out.println("  Status: " + claim.getStatus());
        System.out.println("  Stored in: Queue<Claim> (Pending claims in queue: "
                + service.getDataStore().getPendingClaims().size() + ")");
    }

    // =========================================================================
    // FR5 Implementation
    // =========================================================================
    private void handleFR5PhysicalHandover() {
        System.out.println("\n--- [FR5] Physical Custody Handover & Verification Signature ---");
        System.out.print("Found Item ID to Release: ");
        String foundId = scanner.nextLine().trim().toUpperCase();

        System.out.print("Recipient Legal Name: ");
        String recipient = scanner.nextLine().trim();

        System.out.print("Recipient Physical Photo ID Number (Govt/Student ID): ");
        String photoId = scanner.nextLine().trim();

        System.out.print("Security Officer Username: ");
        String officer = scanner.nextLine().trim();
        if (officer.isEmpty()) officer = "officer_murthy";

        FoundItem item = service.recordHandover(foundId, recipient, photoId, officer);

        System.out.println("\n[SUCCESS] Custody released and item handed over to verified owner!");
        System.out.println("  Item ID: " + item.getId() + " - " + item.getTitle());
        System.out.println("  Updated Status: " + item.getStatus());
        System.out.println("  Custody Locker " + item.getLockerNumber() + " is now vacated and sanitized.");
    }

    // =========================================================================
    // FR6 Implementation
    // =========================================================================
    private void handleFR6ApproveClaim() {
        System.out.println("\n--- [FR6] Approve Claim with Atomic Concurrency Guard (BR-1) ---");
        System.out.println("Pending Claims in FIFO Review Queue:");
        if (service.getDataStore().getPendingClaims().isEmpty()) {
            System.out.println("  (No claims currently in the pending queue)");
        } else {
            for (Claim c : service.getDataStore().getPendingClaims()) {
                System.out.println("  - " + c.getId() + " on Item " + c.getFoundItemId() + " by " + c.getClaimantUsername());
            }
        }

        System.out.print("\nEnter Claim ID to Approve (e.g. CLM-3000): ");
        String claimId = scanner.nextLine().trim().toUpperCase();

        System.out.print("Reviewing Security Desk Officer Username: ");
        String reviewer = scanner.nextLine().trim();
        if (reviewer.isEmpty()) reviewer = "officer_murthy";

        System.out.print("Officer Remarks: ");
        String remarks = scanner.nextLine().trim();

        Claim approved = service.approveClaim(claimId, reviewer, remarks);

        System.out.println("\n[SUCCESS] Claim approved successfully and item atomically locked!");
        System.out.println("  Claim ID: " + approved.getId() + " | Status: " + approved.getStatus());
        System.out.println("  Locked Item: " + approved.getFoundItemId());
        System.out.println("  Note: Any subsequent approval attempts on " + approved.getFoundItemId()
                + " will now trigger ItemAlreadyClaimedException (BR-1).");
    }

    // =========================================================================
    // FR7 Implementation
    // =========================================================================
    private void handleFR7ScanExpiryDonation() {
        System.out.println("\n--- [FR7] Scan 60-Day Expiry & Donation Batch ---");
        System.out.println("Scanning Set<FoundItem> for items in custody past "
                + Constants.EXPIRY_DAYS_THRESHOLD + " days...");

        List<FoundItem> expiredItems = service.scanAndBatchExpiredItems();

        if (expiredItems.isEmpty()) {
            System.out.println("[INFO] No unclaimed items currently exceed the 60-day threshold.");
        } else {
            System.out.println("[SUCCESS] Identified " + expiredItems.size() + " expired item(s). Batched for donation/charity:");
            for (FoundItem item : expiredItems) {
                System.out.println("  - " + item.getId() + ": " + item.getTitle()
                        + " (Found: " + item.getFoundDate() + ", Locker: " + item.getLockerNumber()
                        + ", Status: " + item.getStatus() + ")");
            }
        }
    }

    // =========================================================================
    // FR8 Implementation
    // =========================================================================
    private void handleFR8AnalyticsDashboard() {
        System.out.println("\n--- [FR8] Real-Time Recovery Rate & Analytics Dashboard ---");
        LostAndFoundService.AnalyticsReport r = service.generateAnalyticsReport();

        System.out.println("===============================================================================");
        System.out.println("               CAMPUS ASSET RECOVERY TELEMETRY DASHBOARD                      ");
        System.out.println("===============================================================================");
        System.out.printf("  Total Lost Reports Logged    : %d\n", r.totalLostReports);
        System.out.printf("  Total Found Items in Registry : %d\n", r.totalFoundItems);
        System.out.printf("  Items Currently in Custody   : %d\n", r.itemsInCustody);
        System.out.printf("  Claims Approved (Locked)     : %d\n", r.claimsApproved);
        System.out.printf("  Items Handed Over (Resolved) : %d\n", r.itemsHandedOver);
        System.out.printf("  Items Batched for Donation   : %d\n", r.itemsBatchedForDonation);
        System.out.printf("  Claims Pending in FIFO Queue : %d\n", r.pendingClaimsInQueue);
        System.out.printf("  High-Value Items Flagged     : %d\n", r.highValueItemsReported);
        System.out.println("-------------------------------------------------------------------------------");
        System.out.printf("  OVERALL CAMPUS RECOVERY RATE : %.1f%%\n", r.recoveryRatePercentage);
        System.out.println("  Formula: (Handed Over Items / Total Found Items) * 100");
        System.out.println("===============================================================================");
    }

    // =========================================================================
    // DATA: View 7-Day Preloaded Sample Inventory
    // =========================================================================
    private void handleViewInventory() {
        System.out.println("\n=== 7-DAY PRELOADED SAMPLE INVENTORY ===");

        System.out.println("\n1. LIST STORE: Reported Lost Items (" + service.getDataStore().getLostItems().size() + " items):");
        for (LostItem item : service.getDataStore().getLostItems()) {
            System.out.println("   " + item);
        }

        System.out.println("\n2. SET STORE: Found Items in Custody (" + service.getDataStore().getFoundItems().size() + " items):");
        for (FoundItem item : service.getDataStore().getFoundItems()) {
            System.out.println("   " + item);
        }

        System.out.println("\n3. QUEUE STORE: Pending Claims Queue (" + service.getDataStore().getPendingClaims().size() + " items):");
        for (Claim claim : service.getDataStore().getPendingClaims()) {
            System.out.println("   " + claim);
        }
    }
}
