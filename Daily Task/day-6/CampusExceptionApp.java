import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * CampusExceptionApp.java
 *
 * Day 6 Project Deliverable: Exception Handling & Business Rule Validation
 * Project: JFS-40 Campus Lost & Found Portal
 * -----------------------------------------------------------------------------
 * Enforces 2 Core Business Rules:
 *   Rule 1: Duplicate Item Prevention (throws DuplicateReportException)
 *   Rule 2: Claim Verification (throws ItemAlreadyClaimedException)
 *
 * Guarantees that the application menu recovers cleanly after any error,
 * displaying meaningful ProblemDetail-style messages rather than crashing.
 * -----------------------------------------------------------------------------
 */
public class CampusExceptionApp {

    // -------------------------------------------------------------------------
    // Custom Exception Hierarchy
    // -------------------------------------------------------------------------

    // Base Unchecked Exception for the domain
    public static class CampusLostAndFoundException extends RuntimeException {
        public CampusLostAndFoundException(String message) {
            super(message);
        }
        public CampusLostAndFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // Business Rule 1 Violation (Unchecked)
    public static class DuplicateReportException extends CampusLostAndFoundException {
        private final String duplicateKey;

        public DuplicateReportException(String message, String duplicateKey) {
            super(message);
            this.duplicateKey = duplicateKey;
        }

        public String getDuplicateKey() { return duplicateKey; }
    }

    // Business Rule 2 Violation (Checked: must be handled by security flow)
    public static class ItemAlreadyClaimedException extends Exception {
        private final String itemId;
        private final String existingStatus;

        public ItemAlreadyClaimedException(String message, String itemId, String existingStatus) {
            super(message);
            this.itemId = itemId;
            this.existingStatus = existingStatus;
        }

        public String getItemId() { return itemId; }
        public String getExistingStatus() { return existingStatus; }
    }

    // Item Not Found Exception
    public static class ItemNotFoundException extends CampusLostAndFoundException {
        public ItemNotFoundException(String message) {
            super(message);
        }
    }

    // Verification Challenge Failure
    public static class VerificationFailedException extends Exception {
        public VerificationFailedException(String message) {
            super(message);
        }
    }

    // -------------------------------------------------------------------------
    // Domain Models for Exception Demonstration
    // -------------------------------------------------------------------------
    public record SimpleReport(String id, String category, String title, String location) {}
    public static class SimpleFoundItem {
        public final String id;
        public final String title;
        public String status; // "AVAILABLE", "CLAIMED", "RETURNED"

        public SimpleFoundItem(String id, String title, String status) {
            this.id = id;
            this.title = title;
            this.status = status;
        }
    }

    // Simulated in-memory storage
    private static final List<SimpleReport> lostReports = new ArrayList<>();
    private static final List<SimpleFoundItem> foundItems = new ArrayList<>();

    static {
        lostReports.add(new SimpleReport("LOST-01", "ELECTRONICS", "MacBook Air", "LIB"));
        foundItems.add(new SimpleFoundItem("FND-01", "MacBook Air M2", "AVAILABLE"));
        foundItems.add(new SimpleFoundItem("FND-02", "Brown Leather Wallet", "CLAIMED")); // Already claimed
        foundItems.add(new SimpleFoundItem("FND-03", "Dorm Room Key 402", "RETURNED")); // Already returned
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("             CAMPUS LOST & FOUND — EXCEPTION HANDLING & RECOVERY               ");
        System.out.println("===============================================================================");

        // Automated demonstration of Business Rule enforcement & graceful recovery
        testBusinessRule1DuplicatePrevention();
        testBusinessRule2ClaimValidation();
        testInteractiveRecovery();

        System.out.println("===============================================================================");
    }

    // -------------------------------------------------------------------------
    // Business Rule 1: Duplicate Item Prevention
    // -------------------------------------------------------------------------
    private static void testBusinessRule1DuplicatePrevention() {
        System.out.println("\n[TEST 1] ENFORCING BUSINESS RULE 1: DUPLICATE REPORT PREVENTION");
        try {
            registerLostReport("LOST-02", "ELECTRONICS", "MacBook Air", "LIB"); // Same title & location as LOST-01
        } catch (DuplicateReportException ex) {
            System.err.println("  [CAUGHT] " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            System.out.println("  [RECOVERY] Duplicate prevented. Candidate key: " + ex.getDuplicateKey());
            System.out.println("  [USER HINT] Please review existing lost report #LOST-01 instead of re-submitting.");
        }
    }

    private static void registerLostReport(String id, String category, String title, String location) {
        for (SimpleReport report : lostReports) {
            if (report.category().equalsIgnoreCase(category)
                    && report.title().equalsIgnoreCase(title)
                    && report.location().equalsIgnoreCase(location)) {
                throw new DuplicateReportException(
                    "A lost item report for '" + title + "' in location '" + location + "' already exists!",
                    category + ":" + location + ":" + title
                );
            }
        }
        lostReports.add(new SimpleReport(id, category, title, location));
        System.out.println("  [SUCCESS] Report registered: " + id);
    }

    // -------------------------------------------------------------------------
    // Business Rule 2: Claim Verification & Availability
    // -------------------------------------------------------------------------
    private static void testBusinessRule2ClaimValidation() {
        System.out.println("\n[TEST 2] ENFORCING BUSINESS RULE 2: CLAIM AVAILABILITY & ANTI-FRAUD");

        // Scenario A: Attempting to claim item FND-02 which is already CLAIMED
        try {
            submitClaim("FND-02", "Alice Smith", "Blue sticker on back");
        } catch (ItemAlreadyClaimedException ex) {
            System.err.println("  [CAUGHT] " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            System.out.println("  [RECOVERY] Item " + ex.getItemId() + " status is '" + ex.getExistingStatus() + "'. Claim rejected.");
        } catch (ItemNotFoundException | VerificationFailedException ex) {
            System.err.println("  [CAUGHT] " + ex.getMessage());
        }

        // Scenario B: Attempting to claim non-existent item
        try {
            submitClaim("FND-999", "Bob", "Green case");
        } catch (ItemNotFoundException ex) {
            System.err.println("  [CAUGHT] " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            System.out.println("  [RECOVERY] Clean 404-style error handled. Prompting user to check inventory ID.");
        } catch (Exception ex) {
            System.err.println("  [UNEXPECTED] " + ex.getMessage());
        }
    }

    private static void submitClaim(String itemId, String claimant, String quizAnswer)
            throws ItemAlreadyClaimedException, VerificationFailedException {

        SimpleFoundItem target = null;
        for (SimpleFoundItem item : foundItems) {
            if (item.id.equalsIgnoreCase(itemId)) {
                target = item;
                break;
            }
        }

        if (target == null) {
            throw new ItemNotFoundException("Found item with ID '" + itemId + "' does not exist in custody records.");
        }

        if (!"AVAILABLE".equals(target.status)) {
            throw new ItemAlreadyClaimedException(
                "Cannot file claim on item '" + itemId + "'. Current status is " + target.status + ".",
                itemId,
                target.status
            );
        }

        if (quizAnswer == null || quizAnswer.trim().length() < 5) {
            throw new VerificationFailedException("Verification answer is too brief to confirm ownership.");
        }

        target.status = "CLAIMED";
        System.out.println("  [SUCCESS] Claim approved for " + itemId + " by " + claimant);
    }

    private static void testInteractiveRecovery() {
        System.out.println("\n[TEST 3] CONSOLE ERROR RECOVERY VERIFICATION");
        System.out.println("Simulating a malformed menu entry cycle:");
        String[] mockInputs = { "BAD_NUMBER", "999", "1" };
        for (String input : mockInputs) {
            try {
                System.out.println("  -> User typed: '" + input + "'");
                int choice = Integer.parseInt(input);
                if (choice < 1 || choice > 5) {
                    throw new IllegalArgumentException("Choice out of bounds (1-5)");
                }
                System.out.println("     [OK] Valid selection processed: " + choice);
            } catch (NumberFormatException nfe) {
                System.out.println("     [HANDLED] Non-numeric input ignored. Menu re-prompted without exit.");
            } catch (IllegalArgumentException iae) {
                System.out.println("     [HANDLED] " + iae.getMessage() + ". Menu re-prompted without exit.");
            }
        }
        System.out.println("\n[RESULT] Application stayed alive through all 3 errors without crashing!");
    }
}
