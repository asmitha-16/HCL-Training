import java.util.Objects;

/**
 * Claim.java
 *
 * Day 4 Project Deliverable: Encapsulated Domain Model 3
 * Project: JFS-40 Campus Lost & Found Portal
 * -----------------------------------------------------------------------------
 * Encapsulates ownership claim and challenge responses (FR4, FR5, FR6).
 */
public class Claim {

    private final String id;
    private final String foundItemId;
    private final String claimantId;
    private final String challengeAnswer1;
    private final String challengeAnswer2;
    private String status; // "PENDING", "APPROVED", "REJECTED"
    private String reviewingOfficerId;

    public Claim(String id, String foundItemId, String claimantId, String challengeAnswer1, String challengeAnswer2) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Claim ID cannot be blank");
        if (foundItemId == null || foundItemId.isBlank()) throw new IllegalArgumentException("Found Item ID required");
        if (claimantId == null || claimantId.isBlank()) throw new IllegalArgumentException("Claimant ID required");

        this.id = id.trim();
        this.foundItemId = foundItemId.trim();
        this.claimantId = claimantId.trim();
        this.challengeAnswer1 = (challengeAnswer1 != null) ? challengeAnswer1.trim() : "";
        this.challengeAnswer2 = (challengeAnswer2 != null) ? challengeAnswer2.trim() : "";
        this.status = "PENDING";
    }

    public void approve(String officerId) {
        if (!"PENDING".equals(this.status)) {
            throw new IllegalStateException("Only pending claims can be approved. Current status: " + this.status);
        }
        this.status = "APPROVED";
        this.reviewingOfficerId = officerId;
    }

    public void reject(String officerId) {
        if (!"PENDING".equals(this.status)) {
            throw new IllegalStateException("Only pending claims can be rejected.");
        }
        this.status = "REJECTED";
        this.reviewingOfficerId = officerId;
    }

    public String getId() { return id; }
    public String getFoundItemId() { return foundItemId; }
    public String getClaimantId() { return claimantId; }
    public String getChallengeAnswer1() { return challengeAnswer1; }
    public String getChallengeAnswer2() { return challengeAnswer2; }
    public String getStatus() { return status; }
    public String getReviewingOfficerId() { return reviewingOfficerId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Claim claim)) return false;
        return Objects.equals(id, claim.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Claim[ID=%s, Item=%s, Claimant=%s, Status=%s]",
                id, foundItemId, claimantId, status);
    }
}
