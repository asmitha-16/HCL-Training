import java.util.Objects;

/**
 * LostItem.java
 *
 * Day 4 Project Deliverable: Encapsulated Domain Model 1
 * Project: JFS-40 Campus Lost & Found Portal
 * -----------------------------------------------------------------------------
 * Encapsulates lost report details submitted by students or staff (FR1).
 */
public class LostItem {

    private final String id;
    private final String reporterId;
    private final String category;
    private final String title;
    private final String lastKnownLocation;
    private final String lostDate; // YYYY-MM-DD
    private final double estimatedValue;
    private String status; // "ACTIVE", "MATCHED", "CLOSED"

    public LostItem(String id, String reporterId, String category, String title,
                    String lastKnownLocation, String lostDate, double estimatedValue) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Lost item ID cannot be blank");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title cannot be blank");
        if (category == null || category.isBlank()) throw new IllegalArgumentException("Category cannot be blank");

        this.id = id.trim();
        this.reporterId = (reporterId != null) ? reporterId.trim() : "ANONYMOUS";
        this.category = category.trim().toUpperCase();
        this.title = title.trim();
        this.lastKnownLocation = (lastKnownLocation != null) ? lastKnownLocation.trim() : "UNKNOWN";
        this.lostDate = (lostDate != null) ? lostDate.trim() : "2026-10-20";
        this.estimatedValue = Math.max(0.0, estimatedValue);
        this.status = "ACTIVE";
    }

    // Business Methods
    public void markAsMatched() {
        this.status = "MATCHED";
    }

    public void closeReport() {
        this.status = "CLOSED";
    }

    // Encapsulated Getters
    public String getId() { return id; }
    public String getReporterId() { return reporterId; }
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getLastKnownLocation() { return lastKnownLocation; }
    public String getLostDate() { return lostDate; }
    public double getEstimatedValue() { return estimatedValue; }
    public String getStatus() { return status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LostItem that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("LostItem[ID=%s, Title='%s', Category=%s, Location='%s', Status=%s]",
                id, title, category, lastKnownLocation, status);
    }
}
