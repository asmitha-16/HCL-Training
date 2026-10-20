import java.util.Objects;

/**
 * BaseEntity.java
 *
 * Day 5 Project Deliverable: Reusable Abstract Base Entity
 * Project: JFS-40 Campus Lost & Found Portal
 * -----------------------------------------------------------------------------
 * Provides common entity attributes, timestamps, and identity semantics
 * for all derived domain models in the portal.
 */
public abstract class BaseEntity {

    protected final String id;
    protected final long createdAt;
    protected long updatedAt;

    public BaseEntity(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Entity ID cannot be null or blank");
        }
        this.id = id.trim();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    public void touch() {
        this.updatedAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
