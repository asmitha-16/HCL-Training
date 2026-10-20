import java.util.Objects;

/**
 * FoundItem.java
 *
 * Day 4 Project Deliverable: Encapsulated Domain Model 2
 * Project: JFS-40 Campus Lost & Found Portal
 * -----------------------------------------------------------------------------
 * Encapsulates property held in university custody (FR2, FR6, FR7).
 */
public class FoundItem {

    private final String id;
    private final String finderId;
    private final String category;
    private final String title;
    private final String foundLocation;
    private final String foundDate; // YYYY-MM-DD
    private String custodyLockerId;
    private boolean requiresPrivacyBlur;
    private String status; // "FOUND", "CLAIM_PENDING", "RETURNED", "EXPIRED_FOR_DONATION"
    private long version; // Optimistic concurrency lock support (FR6)

    public FoundItem(String id, String finderId, String category, String title,
                     String foundLocation, String foundDate, String custodyLockerId, boolean requiresPrivacyBlur) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Found item ID cannot be blank");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title cannot be blank");
        if (category == null || category.isBlank()) throw new IllegalArgumentException("Category cannot be blank");

        this.id = id.trim();
        this.finderId = (finderId != null) ? finderId.trim() : "SECURITY_STAFF";
        this.category = category.trim().toUpperCase();
        this.title = title.trim();
        this.foundLocation = (foundLocation != null) ? foundLocation.trim() : "CAMPUS_GROUNDS";
        this.foundDate = (foundDate != null) ? foundDate.trim() : "2026-10-20";
        this.custodyLockerId = (custodyLockerId != null) ? custodyLockerId.trim() : "LOCKER-GENERAL";
        this.requiresPrivacyBlur = requiresPrivacyBlur;
        this.status = "FOUND";
        this.version = 1L;
    }

    // Business Methods
    public void flagForDonation() {
        if ("RETURNED".equals(this.status)) {
            throw new IllegalStateException("Cannot donate an item that was already returned to owner");
        }
        this.status = "EXPIRED_FOR_DONATION";
    }

    public void markAsReturned() {
        this.status = "RETURNED";
        this.custodyLockerId = "RELEASED";
    }

    public void incrementVersion() {
        this.version++;
    }

    // Encapsulated Getters and Setters
    public String getId() { return id; }
    public String getFinderId() { return finderId; }
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getFoundLocation() { return foundLocation; }
    public String getFoundDate() { return foundDate; }
    public String getCustodyLockerId() { return custodyLockerId; }
    public boolean isRequiresPrivacyBlur() { return requiresPrivacyBlur; }
    public String getStatus() { return status; }
    public long getVersion() { return version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FoundItem that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("FoundItem[ID=%s, Title='%s', Category=%s, Locker=%s, Status=%s, Ver=%d]",
                id, title, category, custodyLockerId, status, version);
    }
}
