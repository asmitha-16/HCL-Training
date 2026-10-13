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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CampusLostAndFoundTest {

    private CampusDataStore store;
    private LostAndFoundService service;

    @BeforeEach
    void setUp() {
        store = new CampusDataStore();
        service = new LostAndFoundService(store);
    }

    @Test
    @DisplayName("Verify Collections Store initialization: List, Set, Queue")
    void testCollectionsInitialization() {
        // List should contain 7 sample lost items
        assertEquals(7, store.getLostItems().size());
        // Set should contain 7 sample found items
        assertEquals(7, store.getFoundItems().size());
        // Queue should contain 1 initial claim
        assertEquals(1, store.getPendingClaims().size());
    }

    @Test
    @DisplayName("FR1: Report lost item correctly stores in List<LostItem>")
    void testFR1ReportLostItem() {
        int initialSize = store.getLostItems().size();
        LostItem item = service.reportLostItem(
                "Scientific Calculator FX-991EX", "ELECTRONICS",
                "Science Block - Lab 302", LocalDate.now().minusDays(1),
                "Casio", "Black", "Sticker 'CS-2024' on the back",
                30.00, "asmitha_student"
        );

        assertNotNull(item.getId());
        assertEquals(initialSize + 1, store.getLostItems().size());
        assertEquals("Casio", item.getBrand());
    }

    @Test
    @DisplayName("FR2: Log found item correctly stores in Set<FoundItem>")
    void testFR2LogFoundItem() {
        int initialSize = store.getFoundItems().size();
        FoundItem item = service.logFoundItem(
                "Casio Calculator in Black Cover", "ELECTRONICS",
                "Science Block - Lab 302", LocalDate.now(),
                "LOCKER-SEC-A01", "CS-2024 sticker on cover",
                "officer_murthy"
        );

        assertNotNull(item.getId());
        assertEquals(initialSize + 1, store.getFoundItems().size());
        assertEquals(LocalDate.now().plusDays(Constants.EXPIRY_DAYS_THRESHOLD), item.getExpiryDate());
    }

    @Test
    @DisplayName("FR3: Algorithmic match engine correctly matches MacBook report with high confidence")
    void testFR3MatchEngine() {
        MatchScoringStrategy weightedStrategy = new WeightedMultiFactorMatchStrategy();
        List<MatchResult> matches = service.findMatches("LOST-1001", weightedStrategy);

        assertFalse(matches.isEmpty());
        MatchResult topMatch = matches.get(0);
        assertEquals("FOUND-2001", topMatch.getFoundItem().getId());
        assertTrue(topMatch.getScore() >= 0.70, "MacBook match score should be high confidence >= 0.70");
    }

    @Test
    @DisplayName("Business Rule 1: ItemAlreadyClaimedException thrown on duplicate approval attempt")
    void testBusinessRule1AntiDuplicateApprovalLock() {
        // FOUND-2004 has claim CLM-3000
        service.approveClaim("CLM-3000", "officer_murthy", "Verified with college ID card");

        // Attempting to submit or approve another claim on FOUND-2004 must throw ItemAlreadyClaimedException
        assertThrows(ItemAlreadyClaimedException.class, () -> {
            service.submitClaim("FOUND-2004", "another_student", "Secret tag", "Proof details");
        });
    }

    @Test
    @DisplayName("Business Rule 2: BusinessRuleViolationException thrown on future incident date")
    void testBusinessRule2FutureDateGuard() {
        assertThrows(BusinessRuleViolationException.class, () -> {
            service.reportLostItem(
                    "Future Watch", "CLOTHING_ACCESSORIES",
                    "Engineering Quad - Open Lawn", LocalDate.now().plusDays(5),
                    "Rolex", "Gold", "None", 200.0, "student"
            );
        });
    }

    @Test
    @DisplayName("Strategy Pattern: Verify ExactCategoryMatchStrategy scoring")
    void testExactCategoryMatchStrategy() {
        MatchScoringStrategy strategy = new ExactCategoryMatchStrategy();
        LostItem lost = store.findLostItemById("LOST-1001");
        FoundItem foundSameCat = store.findFoundItemById("FOUND-2001");
        FoundItem foundDiffCat = store.findFoundItemById("FOUND-2003"); // Books

        double sameScore = strategy.calculateScore(lost, foundSameCat);
        double diffScore = strategy.calculateScore(lost, foundDiffCat);

        assertTrue(sameScore >= 0.50);
        assertEquals(0.0, diffScore);
    }

    @Test
    @DisplayName("FR7 & FR8: Expiry scan batches 65-day item and updates analytics")
    void testFR7AndFR8Telemetry() {
        List<FoundItem> expired = service.scanAndBatchExpiredItems();
        assertEquals(1, expired.size(), "FOUND-2007 is 65 days old and should be batched");
        assertEquals("FOUND-2007", expired.get(0).getId());

        LostAndFoundService.AnalyticsReport report = service.generateAnalyticsReport();
        assertEquals(1, report.itemsBatchedForDonation);
        assertTrue(report.totalFoundItems >= 7);
    }
}
