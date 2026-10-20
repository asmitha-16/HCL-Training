package com.campus.lostandfound.app;

import com.campus.lostandfound.benchmark.CollectionBenchmark;
import com.campus.lostandfound.model.*;
import com.campus.lostandfound.service.CampusLostAndFoundService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * ============================================================================
 * Application: CampusConsoleApp
 * ============================================================================
 * Interactive Console Application demonstrating Day 7: Collections Framework Part 1
 * for the Campus Lost and Found Portal:
 *
 * - FR1: Report Lost Item (stored in ArrayList) & Fast Indexed Browsing
 * - FR2: Log Found Item (stored in LinkedHashSet for uniqueness & custody sequence)
 * - FR3: Claim Processing & Multi-Tier Waitlist:
 *        * ArrayDeque for standard student FIFO queue
 *        * PriorityQueue for faculty-first expedited review
 *        * LinkedHashSet de-duplication preventing duplicate claims
 * - Purging Overdue Unclaimed Items & Expired Claims using Iterator.remove()
 * - ConcurrentModificationException vs Iterator.remove() demonstration
 * - ListIterator bidirectional traversal demonstration
 * - ArrayList vs LinkedList Performance Benchmark and Timing Table
 * ============================================================================
 */
public class CampusConsoleApp {

    private final CampusLostAndFoundService service;
    private final Scanner scanner;

    public CampusConsoleApp() {
        this.service = new CampusLostAndFoundService();
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        boolean exit = false;
        while (!exit) {
            printMainMenu();
            System.out.print("Enter your choice (0-9): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    handleFR1BrowseLostItems();
                    break;
                case "2":
                    handleFR1ReportLostItem();
                    break;
                case "3":
                    handleFR2BrowseFoundInventory();
                    break;
                case "4":
                    handleFR2LogFoundItem();
                    break;
                case "5":
                    handleFR3ViewClaimQueues();
                    break;
                case "6":
                    handleFR3SubmitClaim();
                    break;
                case "7":
                    handleFR3ProcessNextClaim();
                    break;
                case "8":
                    handlePurgeOverdueItems();
                    break;
                case "9":
                    handleEducationalDemos();
                    break;
                case "10":
                case "b":
                case "B":
                    handleRunBenchmark();
                    break;
                case "11":
                case "j":
                case "J":
                    printCollectionJustificationMatrix();
                    break;
                case "0":
                    exit = true;
                    System.out.println("\nExiting Campus Lost and Found Collections Portal. Goodbye!");
                    break;
                default:
                    System.out.println("\n[ERROR] Invalid option. Please enter a valid number from the menu.\n");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n====================================================================================");
        System.out.println("       CAMPUS LOST AND FOUND PORTAL -- COLLECTIONS FRAMEWORK CONSOLE (DAY 7)");
        System.out.println("====================================================================================");
        System.out.println(" [FR1 - ArrayList Catalogue]");
        System.out.println("   1. Browse Reported Lost Items (ArrayList O(1) indexed lookup)");
        System.out.println("   2. Report a New Lost Item (ArrayList append)");
        System.out.println(" [FR2 - LinkedHashSet Custody Registry & De-dup]");
        System.out.println("   3. Browse Found Items in Custody (LinkedHashSet unique order & TreeSet sorted)");
        System.out.println("   4. Log Found Item Intake (LinkedHashSet distinctness)");
        System.out.println(" [FR3 - Multi-Tier Claim Queues (ArrayDeque & PriorityQueue)]");
        System.out.println("   5. Inspect Active Claim Queues (ArrayDeque FIFO & PriorityQueue Faculty-First)");
        System.out.println("   6. Submit Ownership Claim (LinkedHashSet de-dup + Multi-tier routing)");
        System.out.println("   7. Process Next Claim (PriorityQueue Top -> ArrayDeque Head)");
        System.out.println(" [Maintenance & Iterator Patterns]");
        System.out.println("   8. Purge Overdue Items & Expired Claims (Safe Iterator.remove)");
        System.out.println("   9. Run Iteration Demos (ConcurrentModificationException & ListIterator)");
        System.out.println(" [Performance Benchmark]");
        System.out.println("  10. Run ArrayList vs LinkedList Benchmark & Timing Table (Press 10 or 'B')");
        System.out.println("  11. View Collections Justifications & Decision Matrix (Press 11 or 'J')");
        System.out.println("   0. Exit");
        System.out.println("====================================================================================");
    }

    // -------------------------------------------------------------------------
    // FR1 HANDLERS
    // -------------------------------------------------------------------------
    private void handleFR1BrowseLostItems() {
        System.out.println("\n--- [FR1] REPORTED LOST ITEMS (java.util.ArrayList) ---");
        List<LostItem> catalogue = service.getLostItemsCatalogue();
        if (catalogue.isEmpty()) {
            System.out.println("No lost items currently reported.");
            return;
        }

        System.out.println("Collection backing: ArrayList<LostItem> (Contiguous Array, O(1) get(i))");
        System.out.printf("%-11s | %-24s | %-14s | %-12s | %-12s | %-10s\n",
                "ID", "Title", "Category", "Lost Date", "Value", "Reporter");
        System.out.println("----------------------------------------------------------------------------------------------");
        for (int i = 0; i < catalogue.size(); i++) {
            LostItem item = catalogue.get(i); // O(1) indexed access
            System.out.printf("%-11s | %-24s | %-14s | %-12s | $%-11.2f | %-10s\n",
                    item.getId(), item.getTitle(), item.getCategory(),
                    item.getLostDate(), item.getEstimatedValue(), item.getReporterUsername());
        }
        System.out.println("Total items: " + catalogue.size());
    }

    private void handleFR1ReportLostItem() {
        System.out.println("\n--- [FR1] REPORT A NEW LOST ITEM (ArrayList.add) ---");
        System.out.print("Item Title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("[ERROR] Title cannot be empty.");
            return;
        }

        System.out.print("Category (Electronics, Personal, Documents, Clothing): ");
        String category = scanner.nextLine().trim();

        System.out.print("Last Seen Location: ");
        String location = scanner.nextLine().trim();

        System.out.print("Brand / Manufacturer: ");
        String brand = scanner.nextLine().trim();

        System.out.print("Color: ");
        String color = scanner.nextLine().trim();

        System.out.print("Distinctive Features / Markings: ");
        String distinctiveFeatures = scanner.nextLine().trim();

        System.out.print("Estimated Value ($): ");
        double value = 25.0;
        try {
            value = Double.parseDouble(scanner.nextLine().trim());
        } catch (Exception ignored) {}

        System.out.print("Reporter Username (e.g. alice_student, alan_turing): ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) username = "student_reporter";

        LostItem item = service.reportLostItem(title, category, location, LocalDate.now(),
                brand, color, distinctiveFeatures, value, username);
        System.out.println("\n[SUCCESS] Lost item recorded into ArrayList catalogue: " + item.getId());
    }

    // -------------------------------------------------------------------------
    // FR2 HANDLERS
    // -------------------------------------------------------------------------
    private void handleFR2BrowseFoundInventory() {
        System.out.println("\n--- [FR2] FOUND ITEMS IN CUSTODY (java.util.LinkedHashSet) ---");
        Set<FoundItem> inventory = service.getFoundItemsInventory();
        if (inventory.isEmpty()) {
            System.out.println("Custody inventory is currently empty.");
            return;
        }

        System.out.println("Collection backing: LinkedHashSet<FoundItem> (Enforces uniqueness, preserves intake order)");
        System.out.printf("%-12s | %-24s | %-14s | %-11s | %-14s | %-12s\n",
                "ID", "Title", "Category", "Locker", "Status", "Expiry Date");
        System.out.println("-------------------------------------------------------------------------------------------------");
        for (FoundItem item : inventory) {
            System.out.printf("%-12s | %-24s | %-14s | %-11s | %-14s | %-12s\n",
                    item.getId(), item.getTitle(), item.getCategory(),
                    item.getLockerNumber(), item.getStatus(), item.getExpiryDate());
        }

        System.out.println("\nDistinct Campus Locations in registry (HashSet<String>): " + service.getDistinctLocations());
    }

    private void handleFR2LogFoundItem() {
        System.out.println("\n--- [FR2] LOG FOUND ITEM INTO CUSTODY (LinkedHashSet.add) ---");
        System.out.print("Item Title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("[ERROR] Title cannot be empty.");
            return;
        }

        System.out.print("Category: ");
        String category = scanner.nextLine().trim();

        System.out.print("Found Location: ");
        String location = scanner.nextLine().trim();

        System.out.print("Storage Locker Number (e.g. Locker-15): ");
        String locker = scanner.nextLine().trim();

        System.out.print("Secret Identification Feature (Confidential ownership quiz secret): ");
        String secret = scanner.nextLine().trim();

        System.out.print("Finder Username (Staff/Security): ");
        String finder = scanner.nextLine().trim();
        if (finder.isEmpty()) finder = "john_staff";

        FoundItem item = service.logFoundItem(title, category, location, LocalDate.now(), locker, secret, finder);
        System.out.println("\n[SUCCESS] Found item stored into LinkedHashSet inventory: " + item.getId());
    }

    // -------------------------------------------------------------------------
    // FR3 HANDLERS
    // -------------------------------------------------------------------------
    private void handleFR3ViewClaimQueues() {
        System.out.println("\n--- [FR3] MULTI-TIER CLAIM REVIEW QUEUES ---");

        System.out.println("\n1. FACULTY-FIRST EXPEDITED QUEUE (java.util.PriorityQueue - Min-Heap):");
        System.out.println("   Rule: Lower priority level number wins (Faculty=1, Staff=2, Student=3), ties broken by claim date.");
        List<Claim> facultyQueue = service.getFacultyFirstClaimsQueueSnapshot();
        if (facultyQueue.isEmpty()) {
            System.out.println("   (Empty)");
        } else {
            for (int i = 0; i < facultyQueue.size(); i++) {
                Claim c = facultyQueue.get(i);
                System.out.printf("   [%d] %s | Item: %s | Claimant: %s (%s, Pri: %d) | Date: %s\n",
                        i + 1, c.getId(), c.getFoundItemId(), c.getClaimantUsername(),
                        c.getClaimantRole(), c.getClaimantRole().getPriorityLevel(), c.getClaimDate());
            }
        }

        System.out.println("\n2. STANDARD STUDENT & VISITOR QUEUE (java.util.ArrayDeque - FIFO):");
        System.out.println("   Rule: Pure First-In-First-Out circular array queue.");
        List<Claim> standardQueue = service.getStandardClaimsQueueSnapshot();
        if (standardQueue.isEmpty()) {
            System.out.println("   (Empty)");
        } else {
            for (int i = 0; i < standardQueue.size(); i++) {
                Claim c = standardQueue.get(i);
                System.out.printf("   [%d] %s | Item: %s | Claimant: %s (%s) | Date: %s\n",
                        i + 1, c.getId(), c.getFoundItemId(), c.getClaimantUsername(),
                        c.getClaimantRole(), c.getClaimDate());
            }
        }
    }

    private void handleFR3SubmitClaim() {
        System.out.println("\n--- [FR3] SUBMIT OWNERSHIP CLAIM (LinkedHashSet De-dup + Multi-Tier Queue) ---");
        System.out.print("Enter Target Found Item ID (e.g. FOUND-2001, FOUND-2002): ");
        String foundId = scanner.nextLine().trim();

        System.out.print("Claimant Username (e.g. alan_turing [Faculty], alice_student [Student], bob_student [Student]): ");
        String username = scanner.nextLine().trim();

        System.out.print("Secret Identification Feature Answer (e.g. 'HCL sticker on top lid'): ");
        String secret = scanner.nextLine().trim();

        System.out.print("Proof / Circumstances Description: ");
        String proof = scanner.nextLine().trim();

        CampusLostAndFoundService.ClaimSubmissionResult result =
                service.submitClaim(foundId, username, secret, proof);

        if (result.success) {
            System.out.println("\n[SUCCESS] " + result.message);
        } else {
            System.out.println("\n[FAILED] " + result.message);
        }
    }

    private void handleFR3ProcessNextClaim() {
        System.out.println("\n--- [FR3] PROCESS NEXT HIGHEST PRIORITY CLAIM ---");
        System.out.println("Attempting extraction: PriorityQueue (Faculty) -> ArrayDeque (Standard FIFO)...");

        CampusLostAndFoundService.ClaimProcessingResult result =
                service.processNextClaim("security_desk_officer");

        if (result.success) {
            System.out.println("\n[SUCCESS] " + result.message);
        } else {
            System.out.println("\n[INFO] " + result.message);
        }
    }

    // -------------------------------------------------------------------------
    // MAINTENANCE & ITERATION PATTERNS
    // -------------------------------------------------------------------------
    private void handlePurgeOverdueItems() {
        System.out.println("\n--- [MAINTENANCE] PURGE OVERDUE ITEMS & CLAIMS (Iterator.remove) ---");
        System.out.println("Purging items older than 30-day retention threshold using java.util.Iterator.remove()...");

        CampusLostAndFoundService.PurgeResult result =
                service.purgeOverdueItemsAndClaims(LocalDate.now());

        System.out.println("\n1. Found Items Purged from LinkedHashSet:");
        if (result.purgedFoundItems.isEmpty()) {
            System.out.println("   No overdue found items detected.");
        } else {
            for (FoundItem fi : result.purgedFoundItems) {
                System.out.printf("   - Evicted: [%s] \"%s\" (Found: %s, Expired: %s)\n",
                        fi.getId(), fi.getTitle(), fi.getFoundDate(), fi.getExpiryDate());
            }
        }

        System.out.println("\n2. Expired Claims Purged from Queues:");
        if (result.purgedClaims.isEmpty()) {
            System.out.println("   No expired claims detected.");
        } else {
            for (Claim c : result.purgedClaims) {
                System.out.printf("   - Purged: [%s] on Item %s by %s (Expired: %s)\n",
                        c.getId(), c.getFoundItemId(), c.getClaimantUsername(), c.getExpiryDate());
            }
        }
    }

    private void handleEducationalDemos() {
        System.out.println("\n" + service.demonstrateConcurrentModificationExceptionDemo());
        System.out.println("\n" + service.demonstrateListIteratorDemo());
    }

    // -------------------------------------------------------------------------
    // BENCHMARK & JUSTIFICATION
    // -------------------------------------------------------------------------
    private void handleRunBenchmark() {
        System.out.println("\n====================================================================================");
        System.out.println("  RUNNING COLLECTIONS FRAMEWORK BENCHMARK: ARRAYLIST VS LINKEDLIST");
        System.out.println("====================================================================================");
        System.out.println("Benchmarking operations across N = 10,000 and N = 50,000 elements...");
        int[] sizes = {10000, 50000};
        List<CollectionBenchmark.BenchmarkResult> results = CollectionBenchmark.runBenchmarkSuite(sizes);
        CollectionBenchmark.printConsoleTable(results);
    }

    private void printCollectionJustificationMatrix() {
        System.out.println("\n========================================================================================================================");
        System.out.println("                       JAVA COLLECTIONS FRAMEWORK -- SELECTION DECISION MATRIX");
        System.out.println("========================================================================================================================");
        System.out.printf("%-16s | %-14s | %-18s | %-12s | %-44s\n",
                "Collection", "Interface", "Internal Structure", "Ordering", "Campus Lost & Found Project Usage");
        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-16s | %-14s | %-18s | %-12s | %-44s\n",
                "ArrayList", "List", "Dynamic Array", "Index / Insertion", "FR1: Lost Items Catalogue (Fast O(1) get(i))");
        System.out.printf("%-16s | %-14s | %-18s | %-12s | %-44s\n",
                "LinkedList", "List / Deque", "Doubly-Linked Nodes", "Sequential", "Evaluated in Benchmark (Head inserts)");
        System.out.printf("%-16s | %-14s | %-18s | %-12s | %-44s\n",
                "LinkedHashSet", "Set", "Hash Table + Links", "Insertion Order", "FR2: Custody Registry & Claim De-duplication");
        System.out.printf("%-16s | %-14s | %-18s | %-12s | %-44s\n",
                "HashSet", "Set", "Hash Table", "None", "Fast O(1) distinct categories & locations");
        System.out.printf("%-16s | %-14s | %-18s | %-12s | %-44s\n",
                "TreeSet", "NavigableSet", "Red-Black Tree", "Sorted (Tree)", "Alphabetical and Chronological sorted views");
        System.out.printf("%-16s | %-14s | %-18s | %-12s | %-44s\n",
                "ArrayDeque", "Queue / Deque", "Circular Array", "FIFO", "FR3: Standard student & visitor claims queue");
        System.out.printf("%-16s | %-14s | %-18s | %-12s | %-44s\n",
                "PriorityQueue", "Queue", "Binary Min-Heap", "Priority (Heap)", "FR3: Faculty-First expedited claim reviews");
        System.out.println("========================================================================================================================");
    }

    public static void main(String[] args) {
        if (args.length > 0 && "--benchmark".equalsIgnoreCase(args[0])) {
            CollectionBenchmark.main(args);
            return;
        }
        CampusConsoleApp app = new CampusConsoleApp();
        app.run();
    }
}
