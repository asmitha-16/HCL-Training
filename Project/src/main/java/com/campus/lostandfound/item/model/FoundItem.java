package com.campus.lostandfound.item.model;

import com.campus.lostandfound.common.status.ItemStatus;

import java.time.Instant;
import java.time.LocalDate;

public record FoundItem(
        String id,
        String title,
        String description,
        String categoryCode,
        String locationId,
        LocalDate foundDate,
        String brand,
        String color,
        String custodyLockerId,
        String loggedByUserId,
        String loggedByRole,
        String originalPhotoUrl,
        String maskedPhotoUrl,
        boolean hasSensitiveMasking,
        String secretVerificationHints,
        ItemStatus status,
        LocalDate expiryDate,
        Instant createdAt
) {
    public FoundItem withStatus(ItemStatus newStatus) {
        return new FoundItem(id, title, description, categoryCode, locationId, foundDate, brand, color, custodyLockerId, loggedByUserId, loggedByRole, originalPhotoUrl, maskedPhotoUrl, hasSensitiveMasking, secretVerificationHints, newStatus, expiryDate, createdAt);
    }
}
