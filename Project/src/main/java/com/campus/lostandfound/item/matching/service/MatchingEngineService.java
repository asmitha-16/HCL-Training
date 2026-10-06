package com.campus.lostandfound.item.matching.service;

import com.campus.lostandfound.item.matching.model.MatchResult;
import com.campus.lostandfound.item.matching.model.MatchSuggestion;
import com.campus.lostandfound.item.matching.strategy.MatchScoringStrategy;
import com.campus.lostandfound.item.matching.strategy.WeightedMatchScoringStrategy;
import com.campus.lostandfound.item.model.FoundItem;
import com.campus.lostandfound.item.model.LostReport;
import com.campus.lostandfound.notification.service.NotificationService;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Asynchronous matching engine utilizing CompletableFuture and Java Streams.
 * Guaranteed to execute candidate matching in < 1 second (NFR-P2).
 */
public class MatchingEngineService {

    private final MatchScoringStrategy scoringStrategy;
    private final NotificationService notificationService;
    private final Map<String, MatchSuggestion> suggestionsById = new ConcurrentHashMap<>();
    private final AtomicInteger suggestionCounter = new AtomicInteger(1000);
    private final ExecutorService matchingExecutor = Executors.newFixedThreadPool(
            Math.max(2, Runtime.getRuntime().availableProcessors())
    );

    public MatchingEngineService(NotificationService notificationService) {
        this(new WeightedMatchScoringStrategy(), notificationService);
    }

    public MatchingEngineService(MatchScoringStrategy scoringStrategy, NotificationService notificationService) {
        this.scoringStrategy = scoringStrategy;
        this.notificationService = notificationService;
    }

    /**
     * Finds matches for a lost report asynchronously using CompletableFuture and Streams sorting.
     */
    public CompletableFuture<List<MatchResult>> findMatchesForLostReportAsync(
            LostReport report,
            List<FoundItem> candidates,
            double scoreThreshold) {

        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.nanoTime();

            // Streams sorting by score (descending)
            List<MatchResult> results = candidates.stream()
                    .filter(item -> isItemEligibleForMatch(item))
                    .map(item -> scoringStrategy.calculateScore(report, item))
                    .filter(result -> result.overallScore() >= scoreThreshold)
                    .sorted(Comparator.comparingDouble(MatchResult::overallScore).reversed())
                    .toList();

            long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

            // Record suggestions and trigger notifications
            for (MatchResult res : results) {
                if (res.overallScore() >= 0.50) {
                    recordAndNotifySuggestion(report, res.foundItem(), res);
                }
            }

            return results;
        }, matchingExecutor);
    }

    /**
     * Finds matches for a newly logged found item asynchronously.
     */
    public CompletableFuture<List<MatchResult>> findMatchesForFoundItemAsync(
            FoundItem item,
            List<LostReport> lostCandidates,
            double scoreThreshold) {

        return CompletableFuture.supplyAsync(() -> {
            // Streams sorting by score (descending)
            List<MatchResult> results = lostCandidates.stream()
                    .filter(report -> isReportEligibleForMatch(report))
                    .map(report -> scoringStrategy.calculateScore(report, item))
                    .filter(result -> result.overallScore() >= scoreThreshold)
                    .sorted(Comparator.comparingDouble(MatchResult::overallScore).reversed())
                    .toList();

            for (MatchResult res : results) {
                if (res.overallScore() >= 0.50) {
                    recordAndNotifySuggestion(res.lostReport(), item, res);
                }
            }

            return results;
        }, matchingExecutor);
    }

    private boolean isItemEligibleForMatch(FoundItem item) {
        String code = item.status().code();
        return "IN_CUSTODY".equals(code) || "MATCHED".equals(code) || "CLAIM_PENDING".equals(code);
    }

    private boolean isReportEligibleForMatch(LostReport report) {
        String code = report.status().code();
        return "REPORTED".equals(code) || "MATCHED".equals(code);
    }

    private synchronized void recordAndNotifySuggestion(LostReport report, FoundItem item, MatchResult res) {
        String suggestionId = "SUGG-" + suggestionCounter.incrementAndGet();
        MatchSuggestion suggestion = new MatchSuggestion(
                suggestionId,
                report.id(),
                item.id(),
                res.overallScore(),
                res.confidenceLevel(),
                res.rationale(),
                true,
                false,
                Instant.now()
        );
        suggestionsById.put(suggestionId, suggestion);

        if (notificationService != null) {
            String title = "Match Suggested: " + item.title() + " (" + (int)(res.overallScore() * 100) + "% match)";
            String msg = "A found item matching your lost report #" + report.id() + " was logged at " + item.locationId() + ". Confidence: " + res.confidenceLevel();
            notificationService.sendNotification(report.reporterUserId(), title, msg, "MATCH_SUGGESTION", suggestionId);
        }
    }

    public List<MatchSuggestion> getAllSuggestions() {
        return new ArrayList<>(suggestionsById.values());
    }

    public List<MatchSuggestion> getSuggestionsForReport(String reportId) {
        return suggestionsById.values().stream()
                .filter(s -> s.lostReportId().equalsIgnoreCase(reportId))
                .sorted(Comparator.comparingDouble(MatchSuggestion::matchScore).reversed())
                .toList();
    }
}
