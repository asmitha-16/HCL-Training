package com.campus.lostandfound;

import com.campus.lostandfound.common.feign.ItemServiceClient;
import com.campus.lostandfound.item.factory.ItemFactory;
import com.campus.lostandfound.item.model.FoundItem;
import com.campus.lostandfound.item.service.ItemReportService;
import com.campus.lostandfound.notification.service.NotificationService;

import java.time.LocalDate;
import java.util.Optional;

public class FeignClientTest {

    public static void run() {
        System.out.println("--- Running FeignClientTest (Feign: claim -> item contract) ---");

        NotificationService notif = new NotificationService();
        ItemServiceClient client = new ItemReportService(notif);

        // 1. Log an item directly through underlying service
        ItemReportService service = (ItemReportService) client;
        FoundItem item = service.logFoundItem(new ItemFactory.CreateFoundItemCommand(
                "iPad Pro 11-inch",
                "Found in study lounge",
                "ELECTRONICS",
                "LOC-LIB-02",
                LocalDate.now(),
                "Apple",
                "Space Gray",
                "BIN-IPAD",
                "USR-SEC-01",
                "SECURITY_DESK",
                null,
                null,
                false,
                null
        ));

        // 2. Feign call: getItemSummary
        Optional<ItemServiceClient.ItemSummary> summaryOpt = client.getItemSummary(item.id());
        assert summaryOpt.isPresent() : "Feign client should find item summary";
        ItemServiceClient.ItemSummary summary = summaryOpt.get();
        assert "ELECTRONICS".equals(summary.category()) : "Feign summary category mismatch";
        assert "IN_CUSTODY".equals(summary.statusCode()) : "Feign summary status mismatch";

        // 3. Feign call: verifyItemAvailableForClaim
        boolean available = client.verifyItemAvailableForClaim(item.id());
        assert available : "Newly found item must be available for claim";

        // 4. Feign call: lockItemForClaim
        ItemServiceClient.LockResponse lockResp = client.lockItemForClaim(new ItemServiceClient.LockRequest(item.id(), "CLM-999", "USR-STU-01"));
        assert lockResp.success() : "Lock request must succeed";
        assert "CLAIM_PENDING".equals(lockResp.currentStatus()) : "Status after lock must be CLAIM_PENDING";

        // 5. Feign call: updateItemStatus
        ItemServiceClient.StatusUpdateResponse updateResp = client.updateItemStatus(
                new ItemServiceClient.StatusUpdateRequest(item.id(), "CLAIM_APPROVED", "USR-SEC-01", "Claim approved")
        );
        assert updateResp.success() : "Status update should succeed";
        assert "CLAIM_APPROVED".equals(updateResp.updatedStatus()) : "Status should be CLAIM_APPROVED";

        // Now item should no longer be available for a second claim
        assert !client.verifyItemAvailableForClaim(item.id()) : "Item with CLAIM_APPROVED must not be available for new claims";

        System.out.println("  [PASS] FeignClientTest: claim -> item inter-service Feign contract, locking, and status updates verified successfully.");
    }
}