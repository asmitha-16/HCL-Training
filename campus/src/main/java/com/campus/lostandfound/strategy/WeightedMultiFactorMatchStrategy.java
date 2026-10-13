package com.campus.lostandfound.strategy;

import com.campus.lostandfound.model.FoundItem;
import com.campus.lostandfound.model.LostItem;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Strategy Implementation 2: Weighted Multi-Factor Matching Algorithm.
 * Evaluates:
 * - Category Match (35% weight)
 * - Location Proximity (25% weight)
 * - Temporal Date Proximity (20% weight)
 * - Textual Attributes Similarity (20% weight)
 */
public class WeightedMultiFactorMatchStrategy implements MatchScoringStrategy {

    private static final double WEIGHT_CATEGORY = 0.35;
    private static final double WEIGHT_LOCATION = 0.25;
    private static final double WEIGHT_DATE = 0.20;
    private static final double WEIGHT_ATTRIBUTES = 0.20;

    @Override
    public double calculateScore(LostItem lostItem, FoundItem foundItem) {
        if (lostItem == null || foundItem == null) {
            return 0.0;
        }

        // 1. Category Score (35%)
        double categoryScore = 0.0;
        if (lostItem.getCategory().equalsIgnoreCase(foundItem.getCategory())) {
            categoryScore = 1.0;
        }

        // 2. Location Score (25%)
        double locationScore = 0.0;
        String lostLoc = lostItem.getLocation().toLowerCase();
        String foundLoc = foundItem.getLocation().toLowerCase();
        if (lostLoc.equals(foundLoc)) {
            locationScore = 1.0;
        } else {
            // Check if same building (e.g. "Library", "Science", "Quad", "Cafeteria", "Sports")
            String[] majorLandmarks = { "library", "science", "quad", "cafeteria", "sports", "hostel", "admin" };
            for (String mark : majorLandmarks) {
                if (lostLoc.contains(mark) && foundLoc.contains(mark)) {
                    locationScore = 0.60;
                    break;
                }
            }
        }

        // 3. Date Proximity Score (20%)
        double dateScore = 0.0;
        if (lostItem.getLostDate() != null && foundItem.getFoundDate() != null) {
            long daysDifference = Math.abs(ChronoUnit.DAYS.between(lostItem.getLostDate(), foundItem.getFoundDate()));
            if (daysDifference <= 2) {
                dateScore = 1.0;
            } else if (daysDifference <= 5) {
                dateScore = 0.75;
            } else if (daysDifference <= 10) {
                dateScore = 0.40;
            } else if (daysDifference <= 20) {
                dateScore = 0.15;
            }
        }

        // 4. Attribute / Keyword Overlap (20%)
        double attributeScore = 0.0;
        String lostText = buildSearchableText(lostItem);
        String foundText = (foundItem.getTitle() + " " + foundItem.getSecretIdentificationFeature()).toLowerCase();

        Set<String> lostWords = extractKeywords(lostText);
        Set<String> foundWords = extractKeywords(foundText);

        if (!lostWords.isEmpty()) {
            long matches = lostWords.stream().filter(foundWords::contains).count();
            attributeScore = Math.min(1.0, (double) matches / Math.max(1, Math.min(lostWords.size(), 4)));
        }

        // Weighted total
        double total = (categoryScore * WEIGHT_CATEGORY)
                     + (locationScore * WEIGHT_LOCATION)
                     + (dateScore * WEIGHT_DATE)
                     + (attributeScore * WEIGHT_ATTRIBUTES);

        return Math.min(1.0, Math.round(total * 1000.0) / 1000.0);
    }

    private String buildSearchableText(LostItem item) {
        StringBuilder sb = new StringBuilder();
        if (item.getTitle() != null) sb.append(item.getTitle()).append(" ");
        if (item.getBrand() != null) sb.append(item.getBrand()).append(" ");
        if (item.getColor() != null) sb.append(item.getColor()).append(" ");
        if (item.getDistinctiveFeatures() != null) sb.append(item.getDistinctiveFeatures()).append(" ");
        return sb.toString().toLowerCase();
    }

    private Set<String> extractKeywords(String text) {
        Set<String> words = new HashSet<>();
        if (text == null) return words;
        for (String w : text.split("\\W+")) {
            if (w.length() > 2) {
                words.add(w.toLowerCase());
            }
        }
        return words;
    }

    @Override
    public String getStrategyName() {
        return "Weighted Multi-Factor Scoring Strategy (Category 35%, Location 25%, Date 20%, Keywords 20%)";
    }
}
