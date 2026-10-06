package com.campus.lostandfound;

import com.campus.lostandfound.item.factory.ItemFactory;
import com.campus.lostandfound.item.matching.model.MatchResult;
import com.campus.lostandfound.item.matching.strategy.MatchScoringStrategy;
import com.campus.lostandfound.item.matching.strategy.WeightedMatchScoringStrategy;
import com.campus.lostandfound.item.model.FoundItem;
import com.campus.lostandfound.item.model.LostReport;

import java.time.LocalDate;

public class MatchScoringStrategyTest {

    public static void run() {
        System.out.println("--- Running MatchScoringStrategyTest ---");

        MatchScoringStrategy strategy = new WeightedMatchScoringStrategy();

        LocalDate date = LocalDate.now();

        LostReport report = ItemFactory.createLostReport(new ItemFactory.CreateLostReportCommand(
                "Black Dell XPS 15 Laptop",
                "Has university sticker on left side of touchpad",
                "ELECTRONICS",
                "LOC-LIB-02",
                date,
                "Dell",
                "Black",
                "University sticker",
                "+1-555-0101",
                "user@campus.edu",
                "USR-1",
                "Alice"
        ));

        FoundItem itemMatch = ItemFactory.createFoundItem(new ItemFactory.CreateFoundItemCommand(
                "Dell XPS Laptop Black",
                "Found on 2nd floor library desk with laptop sleeve",
                "ELECTRONICS",
                "LOC-LIB-02",
                date.plusDays(1),
                "Dell",
                "Black",
                "BIN-101",
                "OFFICER-1",
                "SECURITY_DESK",
                null,
                null,
                false,
                "University sticker present"
        ));

        FoundItem itemDiff = ItemFactory.createFoundItem(new ItemFactory.CreateFoundItemCommand(
                "Silver Water Bottle",
                "Found in sports complex gym",
                "BAGS",
                "LOC-GYM-01",
                date.plusDays(10),
                "Hydro Flask",
                "Silver",
                "BIN-202",
                "OFFICER-1",
                "SECURITY_DESK",
                null,
                null,
                false,
                null
        ));

        MatchResult highMatch = strategy.calculateScore(report, itemMatch);
        MatchResult lowMatch = strategy.calculateScore(report, itemDiff);

        // Assertions
        assert highMatch.overallScore() >= 0.75 : "Expected high match score >= 0.75, got " + highMatch.overallScore();
        assert "HIGH".equals(highMatch.confidenceLevel()) : "Expected HIGH confidence level, got " + highMatch.confidenceLevel();
        assert highMatch.categoryScore() == 1.0 : "Expected exact category match = 1.0";
        assert highMatch.locationScore() == 1.0 : "Expected exact location match = 1.0";

        assert lowMatch.overallScore() < 0.40 : "Expected low match score < 0.40, got " + lowMatch.overallScore();
        assert "LOW".equals(lowMatch.confidenceLevel()) : "Expected LOW confidence level";

        // Builder pattern test
        MatchResult custom = MatchResult.builder()
                .lostReport(report)
                .foundItem(itemMatch)
                .overallScore(0.88)
                .categoryScore(1.0)
                .locationScore(0.8)
                .dateScore(0.9)
                .keywordScore(0.8)
                .rationale("Manual test build")
                .build();

        assert custom.overallScore() == 0.88 : "Builder overall score mismatch";
        assert "HIGH".equals(custom.confidenceLevel()) : "Builder confidence level mismatch";
        assert "Manual test build".equals(custom.rationale()) : "Builder rationale mismatch";

        System.out.println("  [PASS] MatchScoringStrategyTest: High match score=" + highMatch.overallScore() + " (" + highMatch.confidenceLevel() + "), Low match score=" + lowMatch.overallScore());
    }
}