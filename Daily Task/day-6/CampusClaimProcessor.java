/**
 * CampusClaimProcessor.java
 *
 * Day 6 Hands-on & Project Alignment: Exception Handling & Multi-Catch
 * Project: JFS-40 Campus Lost & Found Portal
 * -----------------------------------------------------------------------------
 * Demonstrates:
 * 1. Custom Checked Exception: ItemAlreadyClaimedException (extends Exception).
 * 2. Custom Unchecked Exception: InvalidClaimDataException (extends RuntimeException).
 * 3. Exception Chaining: Preserving root cause through new ClaimProcessingException(msg, cause).
 * 4. Multi-Catch Block: Catching related exceptions in a single unified handler.
 * 5. Finally Block Semantics: Always-executed security audit log entry.
 * -----------------------------------------------------------------------------
 */
public class CampusClaimProcessor {

    // 1. Custom Checked Exception (Recoverable business failure)
    public static class ItemAlreadyClaimedException extends Exception {
        public ItemAlreadyClaimedException(String message) {
            super(message);
        }
        public ItemAlreadyClaimedException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // 2. Custom Unchecked Exception (Input validation failure)
    public static class InvalidClaimDataException extends RuntimeException {
        public InvalidClaimDataException(String message) {
            super(message);
        }
        public InvalidClaimDataException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // 3. High-level Wrapper Exception (Exception Chaining)
    public static class ClaimProcessingException extends Exception {
        public ClaimProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // In-memory item status
    private String itemStatus = "AVAILABLE"; // "AVAILABLE", "CLAIMED", "RETURNED"

    public void processClaim(String claimId, String claimantId, String quizAnswers) throws ClaimProcessingException {
        System.out.println("\n[START] Processing Claim: " + claimId + " for Claimant: " + claimantId);

        try {
            validateClaimData(claimantId, quizAnswers);
            verifyAndReserveItem(claimId);
            System.out.println("  [SUCCESS] Claim " + claimId + " verified. Status set to CLAIMED.");

        } catch (InvalidClaimDataException | ItemAlreadyClaimedException ex) {
            // Multi-catch handling specific domain failures
            System.err.println("  [CAUGHT SPECIFIC EXCEPTION] " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            // Exception chaining: Preserving root cause
            throw new ClaimProcessingException("Failed to process claim " + claimId + " due to custody rules", ex);

        } finally {
            // finally block always executes regardless of outcome
            System.out.println("  [FINALLY AUDIT] Recorded custody audit entry for Claim: " + claimId + " (Officer: SEC-DESK)");
        }
    }

    private void validateClaimData(String claimantId, String quizAnswers) {
        if (claimantId == null || claimantId.trim().isEmpty()) {
            throw new InvalidClaimDataException("Claimant ID is required and cannot be blank.");
        }
        if (quizAnswers == null || quizAnswers.trim().length() < 5) {
            throw new InvalidClaimDataException("Verification quiz responses are too short to authenticate ownership.");
        }
    }

    private void verifyAndReserveItem(String claimId) throws ItemAlreadyClaimedException {
        if (!"AVAILABLE".equals(this.itemStatus)) {
            throw new ItemAlreadyClaimedException(
                "Custody item is already marked as '" + this.itemStatus + "' and cannot be claimed."
            );
        }
        this.itemStatus = "CLAIMED";
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("        CAMPUS CLAIM PROCESSOR EXCEPTION HANDLING & RECOVERY DEMO              ");
        System.out.println("        PROJECT: JFS-40 CAMPUS LOST AND FOUND PORTAL                           ");
        System.out.println("===============================================================================");

        CampusClaimProcessor processor = new CampusClaimProcessor();

        // Case 1: Valid Claim (Normal Path)
        try {
            processor.processClaim("CLAIM-101", "STU-8841", "Silver MacBook with blue cat sticker on top lid");
        } catch (ClaimProcessingException e) {
            System.err.println("Handler caught: " + e.getMessage());
        }

        // Case 2: Invalid Data (Triggers Unchecked InvalidClaimDataException chained)
        try {
            processor.processClaim("CLAIM-102", "", "short");
        } catch (ClaimProcessingException e) {
            System.out.println("[HANDLED IN CALLER] " + e.getMessage());
            System.out.println("  Root Cause: " + e.getCause().getClass().getName() + " -> " + e.getCause().getMessage());
        }

        // Case 3: Already Claimed (Triggers Checked ItemAlreadyClaimedException chained)
        try {
            processor.processClaim("CLAIM-103", "STU-9922", "Valid description of MacBook Pro M2");
        } catch (ClaimProcessingException e) {
            System.out.println("[HANDLED IN CALLER] " + e.getMessage());
            System.out.println("  Root Cause: " + e.getCause().getClass().getName() + " -> " + e.getCause().getMessage());
        }

        System.out.println("\n[MENU RECOVERY] Main operations resumed successfully without crash. System active.");
        System.out.println("===============================================================================");
    }
}
