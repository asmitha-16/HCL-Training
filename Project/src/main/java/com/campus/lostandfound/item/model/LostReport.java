package com.campus.lostandfound.item.model;

import com.campus.lostandfound.common.status.ItemStatus;

import java.time.Instant;
import java.time.LocalDate;

public record LostReport(
        String id,
        String title,
        String description,
        String categoryCode,
        String locationId,
        LocalDate lostDate,
        String brand,
        String color,
        String distinctiveFeatures,
        String contactPhone,
        String contactEmail,
        String reporterUserId,
        String reporterName,
        ItemStatus status,
        Instant createdAt
) {
    public LostReport withStatus(ItemStatus newStatus) {
        return new LostReport(id, title, description, categoryCode, locationId, lostDate, brand, color, distinctiveFeatures, contactPhone, contactEmail, reporterUserId, reporterName, newStatus, createdAt);
    }
}
