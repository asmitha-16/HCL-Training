package com.campus.lostandfound.item.service;

import com.campus.lostandfound.common.status.ItemStatus;
import com.campus.lostandfound.item.model.FoundItem;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service enforcing FR7: Unclaimed items flagged after 60 days for donation/disposal.
 */
public class ItemExpiryService {

    public record ExpiryScanResult(
            int totalEvaluated,
            int newlyFlaggedForDonation,
            int alreadyExpired,
            int stillActive,
            List<FoundItem> expiredItems,
            String donationBatchManifestId,
            LocalDate scanDate
    ) {}

    private final AtomicInteger batchCounter = new AtomicInteger(100);

    public ExpiryScanResult processExpiringItems(List<FoundItem> items, LocalDate currentDate) {
        if (items == null) {
            return new ExpiryScanResult(0, 0, 0, 0, List.of(), "NONE", currentDate);
        }

        int total = items.size();
        int newlyFlagged = 0;
        int alreadyExpired = 0;
        int active = 0;
        List<FoundItem> flaggedList = new ArrayList<>();

        String batchId = "DONATION-BATCH-" + batchCounter.incrementAndGet();

        for (FoundItem item : items) {
            if (item.status() instanceof ItemStatus.ExpiredForDonation) {
                alreadyExpired++;
                continue;
            }

            // Check if item is already handed over or rejected
            if (item.status() instanceof ItemStatus.HandedOver || item.status() instanceof ItemStatus.Rejected) {
                continue;
            }

            long daysHeld = ChronoUnit.DAYS.between(item.foundDate(), currentDate);
            if (daysHeld >= 60) {
                newlyFlagged++;
                FoundItem expired = item.withStatus(new ItemStatus.ExpiredForDonation(daysHeld, batchId));
                flaggedList.add(expired);
            } else {
                active++;
            }
        }

        return new ExpiryScanResult(total, newlyFlagged, alreadyExpired, active, flaggedList, batchId, currentDate);
    }
}
