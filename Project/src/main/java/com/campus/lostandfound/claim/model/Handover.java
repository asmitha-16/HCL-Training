package com.campus.lostandfound.claim.model;

import java.time.Instant;

public record Handover(
        String id,
        String claimId,
        String foundItemId,
        String lostReportId,
        String recipientName,
        String recipientIdNumber,
        String recipientSignature,
        String officerId,
        String officerName,
        String lockerIdReleased,
        String verificationNotes,
        Instant handoverTimestamp
) {}
