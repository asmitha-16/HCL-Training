package com.campus.lostandfound.item.service;

import com.campus.lostandfound.common.feign.ItemServiceClient;
import com.campus.lostandfound.common.status.ItemStatus;
import com.campus.lostandfound.item.factory.ItemFactory;
import com.campus.lostandfound.item.matching.model.MatchResult;
import com.campus.lostandfound.item.matching.service.MatchingEngineService;
import com.campus.lostandfound.item.model.Category;
import com.campus.lostandfound.item.model.FoundItem;
import com.campus.lostandfound.item.model.Location;
import com.campus.lostandfound.item.model.LostReport;
import com.campus.lostandfound.item.privacy.PhotoPrivacyService;
import com.campus.lostandfound.notification.service.NotificationService;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service managing lost reports, found items, categories, and locations.
 * Implements ItemServiceClient to fulfill the Feign claim -> item inter-service contract.
 */
public class ItemReportService implements ItemServiceClient {

    private final Map<String, LostReport> lostReports = new ConcurrentHashMap<>();
    private final Map<String, FoundItem> foundItems = new ConcurrentHashMap<>();
    private final Map<String, Category> categories = new ConcurrentHashMap<>();
    private final Map<String, Location> locations = new ConcurrentHashMap<>();

    private final MatchingEngineService matchingEngine;
    private final ItemExpiryService expiryService;
    private final PhotoPrivacyService privacyService;
    private final NotificationService notificationService;

    public ItemReportService(NotificationService notificationService) {
        this.notificationService = notificationService;
        this.matchingEngine = new MatchingEngineService(notificationService);
        this.expiryService = new ItemExpiryService();
        this.privacyService = new PhotoPrivacyService();

        // Load reference data
        for (Category c : Category.defaultCategories()) {
            categories.put(c.code(), c);
        }
        for (Location l : Location.defaultLocations()) {
            locations.put(l.id(), l);
        }
    }

    // FR1: User can report lost item with details
    public LostReport reportLostItem(ItemFactory.CreateLostReportCommand cmd) {
        LostReport report = ItemFactory.createLostReport(cmd);
        lostReports.put(report.id(), report);

        // Run auto-match asynchronously using CompletableFuture (NFR-P2)
        matchingEngine.findMatchesForLostReportAsync(report, new ArrayList<>(foundItems.values()), 0.40);

        return report;
    }

    // FR2: User or security can log found item with photo & privacy masking (NFR-P1)
    public FoundItem logFoundItem(ItemFactory.CreateFoundItemCommand cmd) {
        // Apply privacy filter
        PhotoPrivacyService.PrivacyMaskResult privacyResult = privacyService.applyPrivacyFilter(
                cmd.photoUrl(),
                cmd.categoryCode(),
                cmd.hasSensitiveMasking() ? List.of("Serial Number", "Card Digits") : List.of()
        );

        ItemFactory.CreateFoundItemCommand securedCmd = new ItemFactory.CreateFoundItemCommand(
                cmd.title(),
                cmd.description(),
                cmd.categoryCode(),
                cmd.locationId(),
                cmd.foundDate(),
                cmd.brand(),
                cmd.color(),
                cmd.custodyLockerId(),
                cmd.loggedByUserId(),
                cmd.loggedByRole(),
                privacyResult.originalUrl(),
                privacyResult.maskedUrl(),
                privacyResult.isMasked(),
                cmd.secretVerificationHints()
        );

        FoundItem item = ItemFactory.createFoundItem(securedCmd);
        foundItems.put(item.id(), item);

        // Trigger asynchronous match against open lost reports (FR3 / NFR-P2)
        matchingEngine.findMatchesForFoundItemAsync(item, new ArrayList<>(lostReports.values()), 0.40);

        return item;
    }

    public Optional<LostReport> getLostReport(String id) {
        return Optional.ofNullable(lostReports.get(id));
    }

    public Optional<FoundItem> getFoundItem(String id) {
        return Optional.ofNullable(foundItems.get(id));
    }

    public List<LostReport> getAllLostReports() {
        return new ArrayList<>(lostReports.values());
    }

    public List<FoundItem> getAllFoundItems() {
        return new ArrayList<>(foundItems.values());
    }

    public List<Category> getAllCategories() {
        return new ArrayList<>(categories.values());
    }

    public List<Location> getAllLocations() {
        return new ArrayList<>(locations.values());
    }

    public MatchingEngineService getMatchingEngine() {
        return matchingEngine;
    }

    public ItemExpiryService getExpiryService() {
        return expiryService;
    }

    public PhotoPrivacyService getPrivacyService() {
        return privacyService;
    }

    // FR7: 60-day expiry scanner
    public ItemExpiryService.ExpiryScanResult trigger60DayExpiryScan(LocalDate currentDate) {
        ItemExpiryService.ExpiryScanResult result = expiryService.processExpiringItems(new ArrayList<>(foundItems.values()), currentDate);
        for (FoundItem expired : result.expiredItems()) {
            foundItems.put(expired.id(), expired);
            notificationService.sendNotification("USR-ADM-01", "Item Donated After 60 Days: " + expired.title(),
                    "Item #" + expired.id() + " reached 60 days unclaimed and was moved to donation batch " + result.donationBatchManifestId(),
                    "EXPIRY_ALERT", expired.id());
        }
        return result;
    }

    // Feign Client Implementation (claim -> item)
    @Override
    public Optional<ItemSummary> getItemSummary(String itemId) {
        FoundItem f = foundItems.get(itemId);
        if (f != null) {
            return Optional.of(new ItemSummary(f.id(), f.title(), f.categoryCode(), f.locationId(), f.status().code(), f.custodyLockerId(), f.loggedByUserId()));
        }
        LostReport l = lostReports.get(itemId);
        if (l != null) {
            return Optional.of(new ItemSummary(l.id(), l.title(), l.categoryCode(), l.locationId(), l.status().code(), "N/A", l.reporterUserId()));
        }
        return Optional.empty();
    }

    @Override
    public synchronized LockResponse lockItemForClaim(LockRequest request) {
        FoundItem item = foundItems.get(request.itemId());
        if (item == null) {
            return new LockResponse(false, "Item not found: " + request.itemId(), "UNKNOWN");
        }

        String currentStatus = item.status().code();
        if ("HANDED_OVER".equals(currentStatus) || "CLAIM_APPROVED".equals(currentStatus)) {
            return new LockResponse(false, "Item is already resolved or claimed: " + currentStatus, currentStatus);
        }

        FoundItem updated = item.withStatus(new ItemStatus.ClaimPending(request.claimId(), request.requesterId()));
        foundItems.put(item.id(), updated);
        return new LockResponse(true, "Item locked for claim review", "CLAIM_PENDING");
    }

    @Override
    public synchronized StatusUpdateResponse updateItemStatus(StatusUpdateRequest request) {
        FoundItem item = foundItems.get(request.itemId());
        if (item != null) {
            ItemStatus newStatus = ItemStatus.fromCode(request.newStatusCode());
            foundItems.put(item.id(), item.withStatus(newStatus));
            return new StatusUpdateResponse(true, newStatus.code(), "Found item status updated to " + newStatus.displayName());
        }
        LostReport report = lostReports.get(request.itemId());
        if (report != null) {
            ItemStatus newStatus = ItemStatus.fromCode(request.newStatusCode());
            lostReports.put(report.id(), report.withStatus(newStatus));
            return new StatusUpdateResponse(true, newStatus.code(), "Lost report status updated to " + newStatus.displayName());
        }
        return new StatusUpdateResponse(false, "UNKNOWN", "Item ID not found");
    }

    @Override
    public boolean verifyItemAvailableForClaim(String itemId) {
        FoundItem item = foundItems.get(itemId);
        if (item == null) return false;
        String code = item.status().code();
        return !"CLAIM_APPROVED".equals(code) && !"HANDED_OVER".equals(code) && !"EXPIRED_FOR_DONATION".equals(code);
    }
}
