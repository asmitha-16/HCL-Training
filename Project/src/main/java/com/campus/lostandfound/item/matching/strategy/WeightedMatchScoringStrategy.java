package com.campus.lostandfound.item.matching.strategy;

import com.campus.lostandfound.item.matching.model.MatchResult;
import com.campus.lostandfound.item.model.FoundItem;
import com.campus.lostandfound.item.model.LostReport;

import java.time.temporal.ChronoUnit;
import java.util.*;

public class WeightedMatchScoringStrategy implements MatchScoringStrategy {

    private final double categoryWeight;
    private final double locationWeight;
    private final double dateWeight;
    private final double keywordWeight;

    public WeightedMatchScoringStrategy() {
        this(0.35, 0.25, 0.20, 0.20);
    }

    public WeightedMatchScoringStrategy(double categoryWeight, double locationWeight, double dateWeight, double keywordWeight) {
        this.categoryWeight = categoryWeight;
        this.locationWeight = locationWeight;
        this.dateWeight = dateWeight;
        this.keywordWeight = keywordWeight;
    }

    @Override
    public String strategyName() {
        return "WeightedMultiFactorScoring (Cat: 35%, Loc: 25%, Date: 20%, Keywords: 20%)";
    }

    @Override
    public MatchResult calculateScore(LostReport report, FoundItem item) {
        if (report == null || item == null) {
            return MatchResult.builder().overallScore(0.0).rationale("Missing report or item").build();
        }

        double catScore = scoreCategory(report, item);
        double locScore = scoreLocation(report, item);
        double dtScore = scoreDate(report, item);
        double kwScore = scoreKeywordsAndAttributes(report, item);

        double total = (catScore * categoryWeight)
                + (locScore * locationWeight)
                + (dtScore * dateWeight)
                + (kwScore * keywordWeight);

        StringBuilder rationale = new StringBuilder();
        rationale.append("Category match: ").append((int) (catScore * 100)).append("%");
        rationale.append(" | Location match: ").append((int) (locScore * 100)).append("%");
        rationale.append(" | Date proximity: ").append((int) (dtScore * 100)).append("%");
        rationale.append(" | Keyword/Attribute similarity: ").append((int) (kwScore * 100)).append("%");

        return MatchResult.builder()
                .lostReport(report)
                .foundItem(item)
                .categoryScore(catScore)
                .locationScore(locScore)
                .dateScore(dtScore)
                .keywordScore(kwScore)
                .overallScore(total)
                .rationale(rationale.toString())
                .build();
    }

    private double scoreCategory(LostReport report, FoundItem item) {
        if (report.categoryCode().equalsIgnoreCase(item.categoryCode())) {
            return 1.0;
        }
        return 0.0;
    }

    private double scoreLocation(LostReport report, FoundItem item) {
        if (report.locationId().equalsIgnoreCase(item.locationId())) {
            return 1.0;
        }
        String repBuilding = extractBuildingPrefix(report.locationId());
        String itemBuilding = extractBuildingPrefix(item.locationId());
        if (!repBuilding.isEmpty() && repBuilding.equalsIgnoreCase(itemBuilding)) {
            return 0.80; // Same building, different floor/room
        }
        return 0.15;
    }

    private String extractBuildingPrefix(String locId) {
        if (locId == null) return "";
        String[] parts = locId.split("-");
        if (parts.length >= 2) {
            return parts[0] + "-" + parts[1];
        }
        return locId;
    }

    private double scoreDate(LostReport report, FoundItem item) {
        if (report.lostDate() == null || item.foundDate() == null) {
            return 0.5;
        }
        long daysDiff = ChronoUnit.DAYS.between(report.lostDate(), item.foundDate());
        if (daysDiff == 0) return 1.0;
        if (daysDiff > 0) {
            if (daysDiff <= 2) return 0.95;
            if (daysDiff <= 5) return 0.85;
            if (daysDiff <= 10) return 0.70;
            if (daysDiff <= 20) return 0.50;
            if (daysDiff <= 40) return 0.30;
            return 0.15;
        } else {
            // Found date is before reported lost date
            if (daysDiff == -1) return 0.55; // Next morning reporting
            if (daysDiff >= -3) return 0.30;
            return 0.05;
        }
    }

    private double scoreKeywordsAndAttributes(LostReport report, FoundItem item) {
        double score = 0.0;
        int factors = 0;

        // Brand match
        if (!report.brand().isBlank() && !item.brand().isBlank()) {
            factors++;
            if (report.brand().equalsIgnoreCase(item.brand()) ||
                item.description().toLowerCase().contains(report.brand().toLowerCase())) {
                score += 1.0;
            } else if (levenshteinDistance(report.brand().toLowerCase(), item.brand().toLowerCase()) <= 2) {
                score += 0.8;
            }
        }

        // Color match
        if (!report.color().isBlank() && !item.color().isBlank()) {
            factors++;
            if (report.color().equalsIgnoreCase(item.color()) ||
                item.description().toLowerCase().contains(report.color().toLowerCase())) {
                score += 1.0;
            }
        }

        // Title and description token Jaccard similarity
        Set<String> reportTokens = tokenize(report.title() + " " + report.description() + " " + report.distinctiveFeatures());
        Set<String> itemTokens = tokenize(item.title() + " " + item.description());

        double jaccard = computeJaccard(reportTokens, itemTokens);
        score += jaccard;
        factors++;

        return factors > 0 ? (score / factors) : 0.4;
    }

    private Set<String> tokenize(String text) {
        if (text == null) return Collections.emptySet();
        Set<String> tokens = new HashSet<>();
        String[] words = text.toLowerCase().split("[^a-zA-Z0-9]+");
        Set<String> stopWords = Set.of("the", "a", "an", "in", "on", "at", "and", "or", "of", "with", "my", "is", "for", "to");
        for (String w : words) {
            if (w.length() > 2 && !stopWords.contains(w)) {
                tokens.add(w);
            }
        }
        return tokens;
    }

    private double computeJaccard(Set<String> set1, Set<String> set2) {
        if (set1.isEmpty() || set2.isEmpty()) return 0.0;
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        return (double) intersection.size() / union.size();
    }

    private int levenshteinDistance(String a, String b) {
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++) costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
}
