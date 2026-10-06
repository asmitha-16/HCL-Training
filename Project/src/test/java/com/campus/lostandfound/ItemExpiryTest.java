package com.campus.lostandfound;

import com.campus.lostandfound.common.status.ItemStatus;
import com.campus.lostandfound.item.factory.ItemFactory;
import com.campus.lostandfound.item.model.FoundItem;
import com.campus.lostandfound.item.service.ItemExpiryService;

import java.time.LocalDate;
import java.util.List;

public class ItemExpiryTest {

    public static void run() {
        System.out.println("--- Running ItemExpiryTest (FR7: 60-Day Expiry & Donation) ---");

        ItemExpiryService expiryService = new ItemExpiryService();
        LocalDate today = LocalDate.now();

        // 1. Item found 65 days ago (eligible for donation)
        FoundItem oldItem = ItemFactory.createFoundItem(new ItemFactory.CreateFoundItemCommand(
                "Winter Scarf Red Plaid",
                "Found in lecture hall",
                "WEARABLES",
                "LOC-SCI-03",
                today.minusDays(65),
                "Unknown",
                "Red",
                "BIN-OLD-1",
                "OFFICER-1",
                "SECURITY_DESK",
                null,
                null,
                false,
                null
        ));

        // 2. Item found 15 days ago (active custody)
        FoundItem recentItem = ItemFactory.createFoundItem(new ItemFactory.CreateFoundItemCommand(
                "USB-C Charging Hub",
                "Found in engineering lab",
                "ELECTRONICS",
                "LOC-ENG-01",
                today.minusDays(15),
                "Anker",
                "White",
                "BIN-NEW-1",
                "OFFICER-1",
                "SECURITY_DESK",
                null,
                null,
                false,
                null
        ));

        ItemExpiryService.ExpiryScanResult result = expiryService.processExpiringItems(List.of(oldItem, recentItem), today);

        assert result.totalEvaluated() == 2 : "Expected 2 items evaluated";
        assert result.newlyFlaggedForDonation() == 1 : "Expected 1 item flagged for donation";
        assert result.stillActive() == 1 : "Expected 1 item still active";
        assert result.expiredItems().size() == 1 : "Expected 1 expired item in manifest";

        FoundItem expired = result.expiredItems().get(0);
        assert expired.status() instanceof ItemStatus.ExpiredForDonation : "Item status must be ExpiredForDonation";
        ItemStatus.ExpiredForDonation expStatus = (ItemStatus.ExpiredForDonation) expired.status();
        assert expStatus.daysUnclaimed() >= 60 : "Days unclaimed must be >= 60";
        assert result.donationBatchManifestId().startsWith("DONATION-BATCH-") : "Batch manifest must have valid ID";

        System.out.println("  [PASS] ItemExpiryTest: Unclaimed items past 60 days successfully flagged for donation batch " + result.donationBatchManifestId());
    }
}