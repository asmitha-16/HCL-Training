package com.campus.lostandfound.common.model;

import java.time.Instant;

/**
 * Immutable records for common DTOs.
 */
public final class Dtos {
    private Dtos() {}

    public record UserDto(String id, String name, String email, String role, String department) {}

    public record CategoryDto(String id, String code, String displayName, String icon, String typicalAttributes) {}

    public record LocationDto(String id, String building, String floor, String roomOrZone, String description) {}

    public record VerificationAnswer(
            String questionKey,
            String questionText,
            String answer,
            boolean isSecretQuestion
    ) {}

    public record ScoringBreakdown(
            double overallScore,
            double categoryScore,
            double locationScore,
            double dateScore,
            double keywordScore,
            String rationale
    ) {}

    public record AuditLogEntry(
            String id,
            String eventType,
            String entityType,
            String entityId,
            String actorId,
            String actorRole,
            String ipAddress,
            String actionSummary,
            String previousState,
            String newState,
            String checksumHash,
            Instant timestamp
    ) {}

    public record HandoverRecord(
            String id,
            String claimId,
            String foundItemId,
            String lostReportId,
            String recipientName,
            String recipientIdNumber,
            String recipientSignatureHash,
            String officerId,
            String officerName,
            String custodyLockerId,
            String verificationNotes,
            Instant handoverTimestamp
    ) {}
}
