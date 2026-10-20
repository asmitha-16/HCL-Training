/**
 * ClaimVerificationHierarchy.java
 *
 * Day 5 Hands-on & Project Deliverable: Inheritance, Polymorphism & Abstraction
 * Project: JFS-40 Campus Lost & Found Portal
 * -----------------------------------------------------------------------------
 * Demonstrates:
 * 1. Interface (VerifiableClaim): Verification contract for ownership challenges.
 * 2. Abstract Class (CampusClaim): Shared claim state and audit metadata.
 * 3. Method Overloading: approve(officerId) vs. approve(officerId, remarks).
 * 4. Method Overriding (@Override) & super calls across category claim types.
 * 5. Runtime Polymorphism: Processing diverse claims uniformly at Security Desk.
 * -----------------------------------------------------------------------------
 */
public class ClaimVerificationHierarchy {

    // 1. Interface for ownership verification
    public interface VerifiableClaim {
        boolean verifyOwnership(String secretAttribute1, String secretAttribute2);
    }

    // 2. Abstract Base Claim Class
    public static abstract class CampusClaim {
        protected final String claimId;
        protected final String foundItemId;
        protected final String claimantId;
        protected String status; // "PENDING", "APPROVED", "REJECTED"
        protected String reviewingOfficerId;
        protected String auditNotes;

        public CampusClaim(String claimId, String foundItemId, String claimantId) {
            this.claimId = claimId;
            this.foundItemId = foundItemId;
            this.claimantId = claimantId;
            this.status = "PENDING";
            this.auditNotes = "None";
        }

        public abstract String getCategoryChallengeType();

        // Overloaded approve method 1: Standard approval with officer ID
        public void approve(String officerId) {
            approve(officerId, "Standard photo ID verification completed");
        }

        // Overloaded approve method 2: Detailed approval with audit notes
        public void approve(String officerId, String notes) {
            if (!"PENDING".equals(this.status)) {
                throw new IllegalStateException("Only pending claims can be approved.");
            }
            this.status = "APPROVED";
            this.reviewingOfficerId = officerId;
            this.auditNotes = notes;
            System.out.printf("  [CLAIM APPROVED] %s for Item %s by Officer %s. Notes: %s%n",
                    claimId, foundItemId, officerId, notes);
        }

        public String getClaimId() { return claimId; }
        public String getFoundItemId() { return foundItemId; }
        public String getClaimantId() { return claimantId; }
        public String getStatus() { return status; }
    }

    // 3. Concrete Subclass 1: Electronics Claim (Serial Number & Lock Screen)
    public static class ElectronicsClaim extends CampusClaim implements VerifiableClaim {
        private final String expectedSerialPrefix;
        private final String expectedWallpaperKeyword;

        public ElectronicsClaim(String claimId, String foundItemId, String claimantId,
                                String expectedSerialPrefix, String expectedWallpaperKeyword) {
            super(claimId, foundItemId, claimantId);
            this.expectedSerialPrefix = expectedSerialPrefix;
            this.expectedWallpaperKeyword = expectedWallpaperKeyword;
        }

        @Override
        public String getCategoryChallengeType() {
            return "ELECTRONICS (Serial # & Wallpaper)";
        }

        @Override
        public boolean verifyOwnership(String serialInput, String wallpaperInput) {
            boolean serialMatch = serialInput != null && serialInput.toUpperCase().startsWith(expectedSerialPrefix.toUpperCase());
            boolean wallpaperMatch = wallpaperInput != null && wallpaperInput.toLowerCase().contains(expectedWallpaperKeyword.toLowerCase());
            return serialMatch && wallpaperMatch;
        }
    }

    // 4. Concrete Subclass 2: Wallet Claim (Cards & Color Nuance)
    public static class WalletClaim extends CampusClaim implements VerifiableClaim {
        private final String expectedCardName;

        public WalletClaim(String claimId, String foundItemId, String claimantId, String expectedCardName) {
            super(claimId, foundItemId, claimantId);
            this.expectedCardName = expectedCardName;
        }

        @Override
        public String getCategoryChallengeType() {
            return "WALLET_CARDS (Inner Card Names)";
        }

        @Override
        public boolean verifyOwnership(String cardInput, String optionalNote) {
            return cardInput != null && cardInput.toLowerCase().contains(expectedCardName.toLowerCase());
        }
    }

    // 5. Concrete Subclass 3: Keys Claim (Fob Markings & Key Count)
    public static class KeysClaim extends CampusClaim implements VerifiableClaim {
        private final String expectedKeychainColor;

        public KeysClaim(String claimId, String foundItemId, String claimantId, String expectedKeychainColor) {
            super(claimId, foundItemId, claimantId);
            this.expectedKeychainColor = expectedKeychainColor;
        }

        @Override
        public String getCategoryChallengeType() {
            return "KEYS (Keychain Color & Markings)";
        }

        @Override
        public boolean verifyOwnership(String colorInput, String unused) {
            return colorInput != null && colorInput.equalsIgnoreCase(expectedKeychainColor);
        }
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("     CAMPUS CLAIM INHERITANCE HIERARCHY & POLYMORPHISM DEMO                    ");
        System.out.println("     PROJECT: JFS-40 CAMPUS LOST AND FOUND PORTAL                              ");
        System.out.println("===============================================================================");

        // Runtime Polymorphism: Diverse category claims treated uniformly via base type
        CampusClaim[] claimQueue = {
            new ElectronicsClaim("CLAIM-101", "FND-201", "STU-8841", "C02G", "golden retriever"),
            new WalletClaim("CLAIM-102", "FND-202", "STAFF-302", "HDFC Debit Card"),
            new KeysClaim("CLAIM-103", "FND-203", "STU-9912", "Blue")
        };

        System.out.println("\n[1] PROCESSING VERIFICATION CHALLENGES VIA RUNTIME POLYMORPHISM:");
        for (CampusClaim claim : claimQueue) {
            System.out.println("  Evaluating: " + claim.getClaimId() + " [" + claim.getCategoryChallengeType() + "]");
            if (claim instanceof VerifiableClaim verifiable) {
                // Simulate correct quiz response verification
                boolean verified = verifiable.verifyOwnership("C02G8849", "Photo of my Golden Retriever dog");
                System.out.println("    Quiz Verification Result: " + (verified ? "PASSED (Answers Valid)" : "PENDING MANUAL REVIEW"));
            }
        }

        System.out.println("\n[2] DEMONSTRATING METHOD OVERLOADING (APPROVE WITH / WITHOUT NOTES):");
        claimQueue[0].approve("OFFICER-JOHN");
        claimQueue[1].approve("OFFICER-SARAH", "Verified faculty employee ID card STF-302 matched");
        System.out.println("===============================================================================");
    }
}
