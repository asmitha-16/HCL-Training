package com.campus.lostandfound;

import com.campus.lostandfound.item.factory.ItemFactory;
import com.campus.lostandfound.item.matching.model.MatchResult;
import com.campus.lostandfound.item.matching.service.MatchingEngineService;
import com.campus.lostandfound.item.model.FoundItem;
import com.campus.lostandfound.item.model.LostReport;
import com.campus.lostandfound.notification.service.NotificationService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MatchingPerformanceTest {

    public static void run() throws Exception {
        System.out.println("--- Running MatchingPerformanceTest (NFR-P2 & CompletableFuture / Streams Sorting) ---");

        NotificationService notificationService = new NotificationService();
        MatchingEngineService engine = new MatchingEngineService(notificationService);

        LocalDate today = LocalDate.now();

        // 1. Generate 1,000 synthetic candidate items
        String[] categories = new String[]{"ELECTRONICS", "BAGS", "ID_CARDS", "KEYS", "WEARABLES", "BOOKS_STATIONERY"};
        String[] locations = new String[]{"LOC-LIB-02", "LOC-ENG-01", "LOC-STU-01", "LOC-SCI-03", "LOC-GYM-01", "LOC-ADM-01"};
        String[] brands = new String[]{"Apple", "Dell", "Lenovo", "Sony", "Samsung", "HP", "Anker", "Hydro Flask"};

        List<FoundItem> dataset = new ArrayList<>(1000);
        for (int i = 0; i < 1000; i++) {
            dataset.add(ItemFactory.createFoundItem(new ItemFactory.CreateFoundItemCommand(
                    "Synthetic Item #" + i + " " + brands[i % brands.length],
                    "Found during daily sweep of " + locations[i % locations.length],
                    categories[i % categories.length],
                    locations[i % locations.length],
                    today.minusDays(i % 45),
                    brands[i % brands.length],
                    "Color-" + (i % 8),
                    "BIN-" + (100 + i),
                    "SECURITY_DESK",
                    "SECURITY_DESK",
                    null,
                    null,
                    false,
                    null
            )));
        }

        LostReport queryReport = ItemFactory.createLostReport(new ItemFactory.CreateLostReportCommand(
                "Apple MacBook Air Space Gray",
                "Lost in library quiet study hall",
                "ELECTRONICS",
                "LOC-LIB-02",
                today.minusDays(3),
                "Apple",
                "Space Gray",
                "Stickers on lid",
                "+1-555-0100",
                "test@campus.edu",
                "USR-STU-01",
                "Student"
        ));

        // 2. Measure execution time with CompletableFuture
        long startNano = System.nanoTime();

        CompletableFuture<List<MatchResult>> future = engine.findMatchesForLostReportAsync(queryReport, dataset, 0.40);
        List<MatchResult> matches = future.get(5, TimeUnit.SECONDS);

        long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNano);

        // Assert NFR-P2: Matching < 1 s (1000 ms)
        assert elapsedMillis < 1000 : "NFR-P2 Violation: Matching 1,000 items took " + elapsedMillis + " ms (must be < 1,000 ms)";

        // Assert Streams sorting: Results must be ordered strictly by score descending
        for (int i = 0; i < matches.size() - 1; i++) {
            double current = matches.get(i).overallScore();
            double next = matches.get(i + 1).overallScore();
            assert current >= next : "Streams sorting violation: Item at " + i + " (" + current + ") is less than " + next;
        }

        System.out.println("  [PASS] MatchingPerformanceTest: Scored 1,000 items in " + elapsedMillis + " ms (< 1s NFR-P2 achieved). Found " + matches.size() + " matches, top score: " + (matches.isEmpty() ? 0 : matches.get(0).overallScore()));
    }
}