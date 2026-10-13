package com.campus.lostandfound.strategy;

import com.campus.lostandfound.model.FoundItem;
import com.campus.lostandfound.model.LostItem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Strategy Implementation 1: Exact category match baseline with keyword overlap.
 * If categories differ, score is 0.0. If categories match, yields 0.50 base + up to 0.50 keyword bonus.
 */
public class ExactCategoryMatchStrategy implements MatchScoringStrategy {

    @Override
    public double calculateScore(LostItem lostItem, FoundItem foundItem) {
        if (lostItem == null || foundItem == null) {
            return 0.0;
        }

        // Strict category requirement
        if (!lostItem.getCategory().equalsIgnoreCase(foundItem.getCategory())) {
            return 0.0;
        }

        double score = 0.50; // Category matched baseline

        // Keyword overlap bonus
        String lostText = (lostItem.getTitle() + " " + (lostItem.getBrand() != null ? lostItem.getBrand() : "") + " "
                + (lostItem.getDistinctiveFeatures() != null ? lostItem.getDistinctiveFeatures() : "")).toLowerCase();
        String foundText = (foundItem.getTitle() + " "
                + (foundItem.getSecretIdentificationFeature() != null ? foundItem.getSecretIdentificationFeature() : "")).toLowerCase();

        Set<String> lostWords = new HashSet<>(Arrays.asList(lostText.split("\\W+")));
        Set<String> foundWords = new HashSet<>(Arrays.asList(foundText.split("\\W+")));

        // Remove short trivial stop words
        lostWords.removeIf(w -> w.length() <= 2);
        foundWords.removeIf(w -> w.length() <= 2);

        if (!lostWords.isEmpty()) {
            long common = lostWords.stream().filter(foundWords::contains).count();
            double overlapRatio = (double) common / Math.min(lostWords.size(), 5);
            score += Math.min(0.50, overlapRatio * 0.50);
        }

        return Math.min(1.0, Math.round(score * 100.0) / 100.0);
    }

    @Override
    public String getStrategyName() {
        return "Exact Category & Keyword Match Strategy";
    }
}
