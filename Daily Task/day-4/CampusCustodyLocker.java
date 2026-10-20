import java.util.Objects;

/**
 * CampusCustodyLocker.java
 *
 * Day 4 OOP Concepts & Debugging — Aligned to Campus Lost and Found (JFS-40)
 * -----------------------------------------------------------------------------
 * Demonstrates:
 * 1. Encapsulation: Strict private fields with validated state mutators.
 * 2. Static vs. Instance: Static intake sequence counter shared across all locker banks.
 * 3. Constructor Chaining: 3 overloaded constructors chained via this().
 * 4. Business Invariants: Validated item assignment, custody release, and donation.
 * 5. equals() and hashCode() Contract: Implemented on immutable locker ID.
 * 6. IDE Debugging Guide: Target for conditional breakpoint and hot code replace.
 * -----------------------------------------------------------------------------
 */
public class CampusCustodyLocker {

    // Static sequence counter for auto-generating unique custody log IDs
    private static long custodySequence = 1000L;

    // Encapsulated private fields
    private final String custodyId;
    private final String lockerBinCode;
    private String assignedItemId;
    private String itemCategory;
    private String custodyStatus; // "AVAILABLE", "OCCUPIED", "RELEASED", "EXPIRED_FOR_DONATION"
    private String securityOfficerId;

    // Constructor 1: Default constructor chaining to Constructor 2
    public CampusCustodyLocker(String lockerBinCode) {
        this(lockerBinCode, "GENERAL", "SEC-DESK-DEFAULT");
    }

    // Constructor 2: Chaining with bin code and category to Master Constructor
    public CampusCustodyLocker(String lockerBinCode, String itemCategory) {
        this(lockerBinCode, itemCategory, "SEC-DESK-DEFAULT");
    }

    // Constructor 3: Master constructor initializing all fields with validation
    public CampusCustodyLocker(String lockerBinCode, String itemCategory, String securityOfficerId) {
        if (lockerBinCode == null || lockerBinCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Locker bin code cannot be blank");
        }
        this.custodyId = "CUSTODY-" + (++custodySequence);
        this.lockerBinCode = lockerBinCode.trim().toUpperCase();
        this.itemCategory = (itemCategory != null) ? itemCategory.trim().toUpperCase() : "GENERAL";
        this.securityOfficerId = (securityOfficerId != null) ? securityOfficerId.trim() : "SEC-DESK-DEFAULT";
        this.custodyStatus = "AVAILABLE";
        this.assignedItemId = "NONE";
    }

    // Business Methods with Input Validation
    public void assignItem(String itemId, String category) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new IllegalArgumentException("Item ID cannot be blank");
        }
        if (!"AVAILABLE".equals(this.custodyStatus)) {
            throw new IllegalStateException("Locker " + lockerBinCode + " is already occupied by item " + assignedItemId);
        }
        this.assignedItemId = itemId.trim();
        this.itemCategory = (category != null) ? category.trim().toUpperCase() : "GENERAL";
        this.custodyStatus = "OCCUPIED";
    }

    public boolean releaseToOwner(String verifiedClaimId) {
        if (verifiedClaimId == null || verifiedClaimId.trim().isEmpty()) {
            throw new IllegalArgumentException("Claim ID cannot be blank");
        }

        // Fixed logic: ensures locker is currently occupied before releasing
        if (!"OCCUPIED".equals(this.custodyStatus)) {
            return false; // Cannot release an empty or already released locker
        }

        this.custodyStatus = "RELEASED";
        this.assignedItemId = "RELEASED-TO-" + verifiedClaimId.trim();
        return true;
    }

    public void flagForDonation() {
        if ("RELEASED".equals(this.custodyStatus)) {
            throw new IllegalStateException("Cannot donate an item that was already released to verified owner.");
        }
        this.custodyStatus = "EXPIRED_FOR_DONATION";
    }

    // Encapsulated Getters
    public String getCustodyId() { return custodyId; }
    public String getLockerBinCode() { return lockerBinCode; }
    public String getAssignedItemId() { return assignedItemId; }
    public String getItemCategory() { return itemCategory; }
    public String getCustodyStatus() { return custodyStatus; }
    public String getSecurityOfficerId() { return securityOfficerId; }

    public static long getTotalCustodyIntakes() {
        return custodySequence - 1000L;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CampusCustodyLocker that)) return false;
        return Objects.equals(custodyId, that.custodyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(custodyId);
    }

    @Override
    public String toString() {
        return String.format("CampusCustodyLocker[ID=%s, Bin=%s, Item=%s, Status=%s, Officer=%s]",
                custodyId, lockerBinCode, assignedItemId, custodyStatus, securityOfficerId);
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("          CAMPUS CUSTODY LOCKER OOP & CONSTRUCTOR CHAINING DEMO                ");
        System.out.println("          PROJECT: JFS-40 CAMPUS LOST AND FOUND PORTAL                         ");
        System.out.println("===============================================================================");

        // Demonstrating constructor chaining
        CampusCustodyLocker locker1 = new CampusCustodyLocker("BIN-A01");
        CampusCustodyLocker locker2 = new CampusCustodyLocker("BIN-B12", "ELECTRONICS");
        CampusCustodyLocker locker3 = new CampusCustodyLocker("BIN-C05", "WALLET_CARDS", "SEC-OFFICER-77");

        System.out.println("Locker 1 (1-arg chained): " + locker1);
        System.out.println("Locker 2 (2-arg chained): " + locker2);
        System.out.println("Locker 3 (Master chained): " + locker3);

        // Demonstrating validated business operations
        locker2.assignItem("FND-201", "ELECTRONICS");
        System.out.println("\nAfter assigning MacBook (FND-201) to Locker 2: " + locker2);

        boolean released = locker2.releaseToOwner("CLAIM-882");
        System.out.println("Locker 2 released to owner: " + released + " | New Status: " + locker2.getCustodyStatus());

        System.out.println("\nTotal Custody Intakes via static counter: " + CampusCustodyLocker.getTotalCustodyIntakes());
        System.out.println("===============================================================================");
    }
}
