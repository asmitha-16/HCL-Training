package com.campus.lostandfound.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * ============================================================================
 * Base Model: BaseEntity
 * ============================================================================
 * Abstract root entity for the Campus Lost and Found Portal domain.
 * Provides unified identity (ID) and audit timestamps across all entities.
 *
 * COLLECTIONS FRAMEWORK INTEGRATION:
 * 1. equals() and hashCode() Contract:
 *    - In java.util.HashSet and java.util.LinkedHashSet, uniqueness and bucket
 *      indexing strictly depend on hashCode() to locate the bucket and equals()
 *      to resolve collisions.
 *    - By defining equals() and hashCode() on the unique entity ID in BaseEntity,
 *      all subclasses (LostItem, FoundItem, Claim) can be safely stored in Sets
 *      and used as Map keys without hash-collision degradation or duplicate entry bugs.
 */
public abstract class BaseEntity {

    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public BaseEntity(String id) {
        this();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
