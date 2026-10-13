package com.campus.lostandfound;

import com.campus.lostandfound.constant.Constants;
import com.campus.lostandfound.exception.BusinessRuleViolationException;
import com.campus.lostandfound.exception.ItemAlreadyClaimedException;
import com.campus.lostandfound.exception.VerificationFailedException;
import com.campus.lostandfound.model.Claim;
import com.campus.lostandfound.model.FoundItem;
import com.campus.lostandfound.model.LostItem;
import com.campus.lostandfound.service.LostAndFoundService;
import com.campus.lostandfound.store.CampusDataStore;
import com.campus.lostandfound.strategy.ExactCategoryMatchStrategy;
import com.campus.lostandfound.strategy.MatchResult;
import com.campus.lostandfound.strategy.MatchScoringStrategy;
import com.campus.lostandfound.strategy.WeightedMultiFactorMatchStrategy;

import java.time.LocalDate;
import java.util.List;

/**
 * Standalone verification test runner (zero external dependencies).
 * Execute with: java -ea -cp campus-app/target/classes;campus-app/target/test-classes com.campus.lostandfound.TestRunner
 */
public class TestRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("         CAMPUS LOST & FOUND PLATFORM — TEST SUITE VERIFICATION               ");
        System.out.println("===============================================================================");

        testCollectionsInitialization();
        testFR1ReportLostItem();
        testFR2LogFoundItem();
        testFR3MatchEngine();
        testBusinessRule1AntiDuplicateApprovalLock();
        testBusinessRule2FutureDateGuard();
        testExactCategoryMatchStrategy();
        testFR7AndFR8Telemetry();

        System.out.println("===============================================================================");
        System.out.println("  TOTAL TESTS: " + (passed + failed) + " | PASSED: " + passed + " | FAILED: " + failed);
        System.out.println("===============================================================================");
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void testCollectionsInitialization() {
        try {
            CampusDataStore store = new CampusDataStore();
            LostAndFoundService service = new LostAndFoundService(store);
            if (store.getLostItems().size() != 7) throw new AssertionError("List<LostItem> expected 7 items");
            if (store.getFoundItems().size() != 7) throw new AssertionError("Set<FoundItem> expected 7 items");
            if (store.getPendingClaims().size() != 1) throw new AssertionError("Queue<Claim> expected 1 item");
            pass("Collections Store (List, Set, Queue) correctly initialized with 1 week of data");
        } catch (Throwable t) {
            fail("Collections Store Initialization", t);
        }
    }

    private static void testFR1ReportLostItem() {
        try {
            CampusDataStore store = new CampusDataStore();
            LostAndFoundService service = new LostAndFoundService(store);
            int countBefore = store.getLostItems().size();
            LostItem item = service.reportLostItem(
                    "Scientific Calculator FX-991EX", "ELECTRONICS",
                    "Science Block - Lab 302", LocalDate.now().minusDays(1),
                    "Casio", "Black", "Sticker 'CS-2024' on the back",
                    30.00, "asmitha_student"
            );
            if (item.getId() == null || store.getLostItems().size() != countBefore + 1) {
                throw new AssertionError("Lost item not appended to List<LostItem>");
            }
            pass("FR1: Report lost item correctly stored in List<LostItem> with validation");
        } catch (Throwable t) {
            fail("FR1 Report Lost Item", t);
        }
    }

    private static void testFR2LogFoundItem() {
        try {
            CampusDataStore store = new CampusDataStore();
            LostAndFoundService service = new LostAndFoundService(store);
            int countBefore = store.getFoundItems().size();
            FoundItem item = service.logFoundItem(
                    "Casio Calculator in Black Cover", "ELECTRONICS",
                    "Science Block - Lab 302", LocalDate.now(),
                    "LOCKER-SEC-A01", "CS-2024 sticker on cover",
                    "officer_murthy"
            );
            if (item.getId() == null || store.getFoundItems().size() != countBefore + 1) {
                throw new AssertionError("Found item not stored in Set<FoundItem>");
            }
            if (!item.getExpiryDate().equals(LocalDate.now().plusDays(Constants.EXPIRY_DAYS_THRESHOLD))) {
                throw new AssertionError("60-day expiry date mismatch");
            }
            pass("FR2: Log found item correctly stored in Set<FoundItem> with 60-day expiry computed");
        } catch (Throwable t) {
            fail("FR2 Log Found Item", t);
        }
    }

    private static void testFR3MatchEngine() {
        try {
            CampusDataStore store = new CampusDataStore();
            LostAndFoundService service = new LostAndFoundService(store);
            MatchScoringStrategy weightedStrategy = new WeightedMultiFactorMatchStrategy();
            List<MatchResult> matches = service.findMatches("LOST-1001", weightedStrategy);
            if (matches.isEmpty()) throw new AssertionError("Expected candidate matches for MacBook Air");
            MatchResult top = matches.get(0);
            if (!"FOUND-2001".equals(top.getFoundItem().getId())) throw new AssertionError("Expected top match FOUND-2001");
            if (top.getScore() < 0.70) throw new AssertionError("Expected high match score >= 0.70, got: " + top.getScore());
            pass("FR3: Strategy Pattern matching correctly ranked candidate with " + top.getConfidencePercentage() + "% confidence");
        } catch (Throwable t) {
            fail("FR3 Match Engine", t);
        }
    }

    private static void testBusinessRule1AntiDuplicateApprovalLock() {
        try {
            CampusDataStore store = new CampusDataStore();
            LostAndFoundService service = new LostAndFoundService(store);
            // Approve initial claim CLM-3000 on FOUND-2004
            service.approveClaim("CLM-3000", "officer_murthy", "Verified with college ID card");
            boolean exceptionThrown = false;
            try {
                service.submitClaim("FOUND-2004", "another_student", "Secret tag", "Proof details");
            } catch (ItemAlreadyClaimedException e) {
                exceptionThrown = true;
            }
            if (!exceptionThrown) throw new AssertionError("Expected ItemAlreadyClaimedException");
            pass("Business Rule 1: ItemAlreadyClaimedException enforced on duplicate approval attempt");
        } catch (Throwable t) {
            fail("Business Rule 1 Concurrency Lock", t);
        }
    }

    private static void testBusinessRule2FutureDateGuard() {
        try {
            CampusDataStore store = new CampusDataStore();
            LostAndFoundService service = new LostAndFoundService(store);
            boolean exceptionThrown = false;
            try {
                service.reportLostItem(
                        "Future Watch", "CLOTHING_ACCESSORIES",
                        "Engineering Quad - Open Lawn", LocalDate.now().plusDays(5),
                        "Rolex", "Gold", "None", 200.0, "student"
                );
            } catch (BusinessRuleViolationException e) {
                exceptionThrown = true;
            }
            if (!exceptionThrown) throw new AssertionError("Expected BusinessRuleViolationException for future date");
            pass("Business Rule 2: BusinessRuleViolationException enforced for invalid future date");
        } catch (Throwable t) {
            fail("Business Rule 2 Date Guard", t);
        }
    }

    private static void testExactCategoryMatchStrategy() {
        try {
            CampusDataStore store = new CampusDataStore();
            LostAndFoundService service = new LostAndFoundService(store);
            MatchScoringStrategy strategy = new ExactCategoryMatchStrategy();
            LostItem lost = store.findLostItemById("LOST-1001");
            FoundItem same = store.findFoundItemById("FOUND-2001");
            FoundItem diff = store.findFoundItemById("FOUND-2003");
            double s1 = strategy.calculateScore(lost, same);
            double s2 = strategy.calculateScore(lost, diff);
            if (s1 < 0.50 || s2 != 0.0) throw new AssertionError("Exact category match score calculation failed");
            pass("Strategy Pattern: ExactCategoryMatchStrategy verified (same cat=" + s1 + ", diff cat=" + s2 + ")");
        } catch (Throwable t) {
            fail("Exact Category Strategy", t);
        }
    }

    private static void testFR7AndFR8Telemetry() {
        try {
            CampusDataStore store = new CampusDataStore();
            LostAndFoundService service = new LostAndFoundService(store);
            List<FoundItem> expired = service.scanAndBatchExpiredItems();
            if (expired.isEmpty() || !"FOUND-2007".equals(expired.get(0).getId())) {
                throw new AssertionError("Expected FOUND-2007 to be batched for donation");
            }
            LostAndFoundService.AnalyticsReport r = service.generateAnalyticsReport();
            if (r.itemsBatchedForDonation != 1) throw new AssertionError("Expected 1 batched item in analytics report");
            pass("FR7 & FR8: 60-day expiry scan batched FOUND-2007 and telemetry report generated");
        } catch (Throwable t) {
            fail("FR7 & FR8 Telemetry", t);
        }
    }

    private static void pass(String msg) {
        passed++;
        System.out.println("  [PASS] " + msg);
    }

    private static void fail(String testName, Throwable t) {
        failed++;
        System.out.println("  [FAIL] " + testName + " — " + t.getMessage());
    }
}
