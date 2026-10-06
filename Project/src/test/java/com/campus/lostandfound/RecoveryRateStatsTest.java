package com.campus.lostandfound;

import com.campus.lostandfound.claim.audit.AuditTrailService;
import com.campus.lostandfound.claim.model.Claim;
import com.campus.lostandfound.claim.service.ClaimService;
import com.campus.lostandfound.claim.service.HandoverService;
import com.campus.lostandfound.claim.service.StatisticsService;
import com.campus.lostandfound.common.model.Dtos.VerificationAnswer;
import com.campus.lostandfound.item.factory.ItemFactory;
import com.campus.lostandfound.item.model.FoundItem;
import com.campus.lostandfound.item.service.ItemReportService;
import com.campus.lostandfound.notification.service.NotificationService;

import java.time.LocalDate;
import java.util.List;

public class RecoveryRateStatsTest {

    public static void run() {
        System.out.println("--- Running RecoveryRateStatsTest (FR8: Admin Views Recovery Rate) ---");

        NotificationService notif = new NotificationService();
        AuditTrailService audit = new AuditTrailService();
        ItemReportService itemService = new ItemReportService(notif);
        ClaimService claimService = new ClaimService(itemService, audit, notif);
        HandoverService handoverService = new HandoverService(claimService, itemService, audit, notif);
        StatisticsService statsService = new StatisticsService(itemService, claimService, handoverService, audit);

        LocalDate today = LocalDate.now();

        // Log 4 found items
        FoundItem f1 = itemService.logFoundItem(new ItemFactory.CreateFoundItemCommand("Item 1", "Desc", "ELECTRONICS", "LOC-LIB-02", today, "Apple", "White", "BIN-1", "OFFICER-1", "SECURITY_DESK", null, null, false, null));
        FoundItem f2 = itemService.logFoundItem(new ItemFactory.CreateFoundItemCommand("Item 2", "Desc", "ELECTRONICS", "LOC-LIB-02", today, "Dell", "Black", "BIN-2", "OFFICER-1", "SECURITY_DESK", null, null, false, null));
        FoundItem f3 = itemService.logFoundItem(new ItemFactory.CreateFoundItemCommand("Item 3", "Desc", "KEYS", "LOC-GYM-01", today, "Honda", "Blue", "BIN-3", "OFFICER-1", "SECURITY_DESK", null, null, false, null));
        FoundItem f4 = itemService.logFoundItem(new ItemFactory.CreateFoundItemCommand("Item 4", "Desc", "BAGS", "LOC-STU-01", today, "Nike", "Gray", "BIN-4", "OFFICER-1", "SECURITY_DESK", null, null, false, null));

        // Hand over 2 of the 4 items
        Claim c1 = claimService.submitClaim(new ClaimService.SubmitClaimRequest(f1.id(), "", "USR-1", "User One", "u1@campus.edu", List.of(new VerificationAnswer("q1", "Serial", "A1", false)), null, "127.0.0.1"));
        claimService.approveClaim(c1.id(), "OFFICER-1", "Approved", "127.0.0.1");
        handoverService.recordHandover(new HandoverService.RecordHandoverCommand(c1.id(), "User One", "ID-111", "SIG1", "OFFICER-1", "Officer Dan", "BIN-1", "Verified", "127.0.0.1"));

        Claim c2 = claimService.submitClaim(new ClaimService.SubmitClaimRequest(f2.id(), "", "USR-2", "User Two", "u2@campus.edu", List.of(new VerificationAnswer("q1", "Serial", "A2", false)), null, "127.0.0.1"));
        claimService.approveClaim(c2.id(), "OFFICER-1", "Approved", "127.0.0.1");
        handoverService.recordHandover(new HandoverService.RecordHandoverCommand(c2.id(), "User Two", "ID-222", "SIG2", "OFFICER-1", "Officer Dan", "BIN-2", "Verified", "127.0.0.1"));

        StatisticsService.RecoveryRateMetrics metrics = statsService.calculateMetrics();

        // 2 handovers out of 4 found items = 50.0% recovery rate
        assert metrics.totalFoundItems() == 4 : "Expected 4 found items, got " + metrics.totalFoundItems();
        assert metrics.totalHandovers() == 2 : "Expected 2 handovers, got " + metrics.totalHandovers();
        assert Math.abs(metrics.recoveryRatePercentage() - 50.0) < 0.1 : "Expected 50.0% recovery rate, got " + metrics.recoveryRatePercentage();

        assert metrics.foundByCategory().get("ELECTRONICS") == 2 : "Expected 2 electronics found";
        assert metrics.foundByCategory().get("KEYS") == 1 : "Expected 1 key found";
        assert metrics.itemsByLocation().get("LOC-LIB-02") == 2 : "Expected 2 items at library";

        System.out.println("  [PASS] RecoveryRateStatsTest: Recovery rate formula (2/4 = " + metrics.recoveryRatePercentage() + "%) and aggregations verified successfully.");
    }
}