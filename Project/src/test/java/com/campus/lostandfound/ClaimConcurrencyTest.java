package com.campus.lostandfound;

import com.campus.lostandfound.claim.audit.AuditTrailService;
import com.campus.lostandfound.claim.model.Claim;
import com.campus.lostandfound.claim.service.ClaimService;
import com.campus.lostandfound.common.model.Dtos.VerificationAnswer;
import com.campus.lostandfound.item.factory.ItemFactory;
import com.campus.lostandfound.item.model.FoundItem;
import com.campus.lostandfound.item.service.ItemReportService;
import com.campus.lostandfound.notification.service.NotificationService;

import java.time.LocalDate;
import java.util.List;

public class ClaimConcurrencyTest {

    public static void run() {
        System.out.println("--- Running ClaimConcurrencyTest (FR6: Prevent Multiple Approved Claims) ---");

        NotificationService notificationService = new NotificationService();
        AuditTrailService auditTrail = new AuditTrailService();
        ItemReportService itemService = new ItemReportService(notificationService);
        ClaimService claimService = new ClaimService(itemService, auditTrail, notificationService);

        // 1. Create a found item
        FoundItem item = itemService.logFoundItem(new ItemFactory.CreateFoundItemCommand(
                "Sony WH-1000XM4 Headphones",
                "Black over-ear headphones found in Library Carrel #8",
                "ELECTRONICS",
                "LOC-LIB-02",
                LocalDate.now(),
                "Sony",
                "Black",
                "BIN-XM4",
                "USR-SEC-01",
                "SECURITY_DESK",
                null,
                null,
                false,
                "Device bluetooth name is 'Alex-Sony'"
        ));

        // 2. Submit Claim 1 by Alice
        Claim claim1 = claimService.submitClaim(new ClaimService.SubmitClaimRequest(
                item.id(),
                "",
                "USR-STU-ALICE",
                "Alice Walker",
                "alice@campus.edu",
                List.of(new VerificationAnswer("q1", "Bluetooth Device Name", "Alex-Sony", false)),
                null,
                "192.168.1.10"
        ));

        // 3. Submit Claim 2 by Bob (concurrent / conflicting claimant)
        Claim claim2 = claimService.submitClaim(new ClaimService.SubmitClaimRequest(
                item.id(),
                "",
                "USR-STU-BOB",
                "Bob Jenkins",
                "bob@campus.edu",
                List.of(new VerificationAnswer("q1", "Bluetooth Device Name", "Bob's Headphones", false)),
                null,
                "192.168.1.11"
        ));

        // 4. Officer approves Claim 1
        Claim approved1 = claimService.approveClaim(claim1.id(), "USR-SEC-01", "Bluetooth name matched device log", "192.168.1.50");
        assert "APPROVED".equals(approved1.status()) : "Claim 1 must be APPROVED";

        // 5. Officer attempts to approve Claim 2 for the same item -> FR6 guard must block this!
        boolean blocked = false;
        try {
            claimService.approveClaim(claim2.id(), "USR-SEC-01", "Second review attempt", "192.168.1.50");
        } catch (IllegalStateException e) {
            blocked = true;
            assert e.getMessage().contains("ALREADY has an approved claim") || e.getMessage().contains("FR6 Guard")
                    : "Exception should cite duplicate approval prevention: " + e.getMessage();
        }

        assert blocked : "FR6 Violation: System failed to prevent multiple approved claims for the same item!";

        // 6. Verify conflicting claim status is REJECTED
        Claim refreshedClaim2 = claimService.getClaim(claim2.id()).orElseThrow();
        assert "REJECTED".equals(refreshedClaim2.status()) : "Conflicting claim must be auto-rejected or rejected";

        // 7. Verify Audit Trail logged the attempt
        boolean foundBlockedAudit = auditTrail.getAllEntries().stream()
                .anyMatch(e -> "FRAUD_PREVENTION_BLOCKED".equals(e.eventType()) || e.actionSummary().contains("Auto-rejected conflicting claim"));
        assert foundBlockedAudit : "Audit trail must record anti-fraud duplicate prevention action";

        System.out.println("  [PASS] ClaimConcurrencyTest: FR6 anti-duplicate approval guard and fraud audit trail verified successfully.");
    }
}