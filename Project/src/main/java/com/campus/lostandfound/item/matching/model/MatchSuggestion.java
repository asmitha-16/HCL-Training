package com.campus.lostandfound.item.matching.model;

import java.time.Instant;

public record MatchSuggestion(
        String id,
        String lostReportId,
        String foundItemId,
        double matchScore,
        String confidenceLevel,
        String rationale,
        boolean notifiedReporter,
        boolean acknowledgedByClaimant,
        Instant suggestedAt
) {
    public MatchSuggestion withNotification(boolean notified) {
        return new MatchSuggestion(id, lostReportId, foundItemId, matchScore, confidenceLevel, rationale, notified, acknowledgedByClaimant, suggestedAt);
    }
}
