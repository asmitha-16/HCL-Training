package com.campus.lostandfound.notification.model;

import java.time.Instant;

public record Notification(
        String id,
        String recipientUserId,
        String title,
        String message,
        String type, // MATCH_SUGGESTION, CLAIM_UPDATE, HANDOVER_READY, EXPIRY_ALERT
        String referenceId,
        boolean isRead,
        Instant timestamp
) {
    public Notification withRead(boolean read) {
        return new Notification(id, recipientUserId, title, message, type, referenceId, read, timestamp);
    }
}
