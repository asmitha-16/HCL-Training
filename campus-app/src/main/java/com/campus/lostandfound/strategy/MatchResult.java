package com.campus.lostandfound.strategy;

import com.campus.lostandfound.model.FoundItem;

/**
 * Data holder representing a match evaluation result between a lost item and a candidate found item.
 */
public class MatchResult implements Comparable<MatchResult> {

    private final FoundItem foundItem;
    private final double score;
    private final String strategyUsed;

    public MatchResult(FoundItem foundItem, double score, String strategyUsed) {
        this.foundItem = foundItem;
        this.score = score;
        this.strategyUsed = strategyUsed;
    }

    public FoundItem getFoundItem() {
        return foundItem;
    }

    public double getScore() {
        return score;
    }

    public String getStrategyUsed() {
        return strategyUsed;
    }

    public int getConfidencePercentage() {
        return (int) Math.round(score * 100);
    }

    public String getMatchTier() {
        if (score >= 0.80) return "HIGH CONFIDENCE MATCH";
        if (score >= 0.60) return "MODERATE MATCH";
        if (score >= 0.40) return "LOW / POSSIBLE MATCH";
        return "POOR MATCH";
    }

    @Override
    public int compareTo(MatchResult other) {
        // Descending order by score
        return Double.compare(other.score, this.score);
    }

    @Override
    public String toString() {
        return String.format("[%d%% - %s] %s (Found: %s at %s, Locker: %s)",
                getConfidencePercentage(), getMatchTier(),
                foundItem.getTitle(), foundItem.getFoundDate(),
                foundItem.getLocation(), foundItem.getLockerNumber());
    }
}
