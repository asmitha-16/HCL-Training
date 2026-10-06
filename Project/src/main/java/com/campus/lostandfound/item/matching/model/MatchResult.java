package com.campus.lostandfound.item.matching.model;

import com.campus.lostandfound.item.model.FoundItem;
import com.campus.lostandfound.item.model.LostReport;

import java.time.Instant;

/**
 * Result of scoring a match between a LostReport and a FoundItem.
 * Employs the Builder Pattern for flexible, fluent construction.
 */
public record MatchResult(
        String lostReportId,
        String foundItemId,
        double overallScore,
        double categoryScore,
        double locationScore,
        double dateScore,
        double keywordScore,
        String confidenceLevel,
        String rationale,
        LostReport lostReport,
        FoundItem foundItem,
        Instant evaluatedAt
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String lostReportId;
        private String foundItemId;
        private double overallScore;
        private double categoryScore;
        private double locationScore;
        private double dateScore;
        private double keywordScore;
        private String confidenceLevel = "LOW";
        private String rationale = "";
        private LostReport lostReport;
        private FoundItem foundItem;
        private Instant evaluatedAt = Instant.now();

        public Builder lostReport(LostReport report) {
            this.lostReport = report;
            if (report != null) this.lostReportId = report.id();
            return this;
        }

        public Builder foundItem(FoundItem item) {
            this.foundItem = item;
            if (item != null) this.foundItemId = item.id();
            return this;
        }

        public Builder lostReportId(String id) {
            this.lostReportId = id;
            return this;
        }

        public Builder foundItemId(String id) {
            this.foundItemId = id;
            return this;
        }

        public Builder overallScore(double score) {
            this.overallScore = Math.round(score * 1000.0) / 1000.0;
            if (this.overallScore >= 0.75) {
                this.confidenceLevel = "HIGH";
            } else if (this.overallScore >= 0.50) {
                this.confidenceLevel = "MEDIUM";
            } else {
                this.confidenceLevel = "LOW";
            }
            return this;
        }

        public Builder categoryScore(double s) {
            this.categoryScore = Math.round(s * 100.0) / 100.0;
            return this;
        }

        public Builder locationScore(double s) {
            this.locationScore = Math.round(s * 100.0) / 100.0;
            return this;
        }

        public Builder dateScore(double s) {
            this.dateScore = Math.round(s * 100.0) / 100.0;
            return this;
        }

        public Builder keywordScore(double s) {
            this.keywordScore = Math.round(s * 100.0) / 100.0;
            return this;
        }

        public Builder confidenceLevel(String level) {
            this.confidenceLevel = level;
            return this;
        }

        public Builder rationale(String rationale) {
            this.rationale = rationale;
            return this;
        }

        public Builder evaluatedAt(Instant instant) {
            this.evaluatedAt = instant;
            return this;
        }

        public MatchResult build() {
            return new MatchResult(
                    lostReportId,
                    foundItemId,
                    overallScore,
                    categoryScore,
                    locationScore,
                    dateScore,
                    keywordScore,
                    confidenceLevel,
                    rationale,
                    lostReport,
                    foundItem,
                    evaluatedAt
            );
        }
    }
}
