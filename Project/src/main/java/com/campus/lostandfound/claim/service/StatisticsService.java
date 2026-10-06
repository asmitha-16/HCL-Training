package com.campus.lostandfound.claim.service;

import com.campus.lostandfound.claim.audit.AuditTrailService;
import com.campus.lostandfound.claim.model.Claim;
import com.campus.lostandfound.claim.model.Handover;
import com.campus.lostandfound.item.model.FoundItem;
import com.campus.lostandfound.item.model.LostReport;
import com.campus.lostandfound.item.service.ItemReportService;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Statistics and analytics service meeting FR8: Admin views recovery rate and campus operational metrics.
 */
public class StatisticsService {

    private final ItemReportService itemService;
    private final ClaimService claimService;
    private final HandoverService handoverService;
    private final AuditTrailService auditTrail;

    public StatisticsService(ItemReportService itemService, ClaimService claimService, HandoverService handoverService, AuditTrailService auditTrail) {
        this.itemService = itemService;
        this.claimService = claimService;
        this.handoverService = handoverService;
        this.auditTrail = auditTrail;
    }

    public record RecoveryRateMetrics(
            int totalLostReports,
            int totalFoundItems,
            int totalHandovers,
            int activeClaimsPending,
            int totalApprovedClaims,
            int itemsFlaggedForDonation,
            double recoveryRatePercentage,
            double resolutionRatePercentage,
            double averageTurnaroundDays,
            Map<String, Integer> foundByCategory,
            Map<String, Integer> lostByCategory,
            Map<String, Integer> itemsByLocation,
            int totalAuditEvents
    ) {}

    public RecoveryRateMetrics calculateMetrics() {
        List<LostReport> lostReports = itemService.getAllLostReports();
        List<FoundItem> foundItems = itemService.getAllFoundItems();
        List<Claim> claims = claimService.getAllClaims();
        List<Handover> handovers = handoverService.getAllHandovers();

        int totalLost = lostReports.size();
        int totalFound = foundItems.size();
        int totalHandedOver = handovers.size();

        int pendingClaims = (int) claims.stream().filter(c -> "PENDING_VERIFICATION".equals(c.status())).count();
        int approvedClaims = (int) claims.stream().filter(c -> "APPROVED".equals(c.status())).count();
        int donatedItems = (int) foundItems.stream().filter(f -> "EXPIRED_FOR_DONATION".equals(f.status().code())).count();

        // Recovery Rate = (Total Handed Over / Total Found Items) * 100
        double recoveryRate = totalFound > 0
                ? Math.round(((double) totalHandedOver / totalFound) * 1000.0) / 10.0
                : 0.0;

        // Resolution Rate = (Total Handed Over / Total Lost Reports) * 100
        double resolutionRate = totalLost > 0
                ? Math.round(((double) totalHandedOver / totalLost) * 1000.0) / 10.0
                : 0.0;

        // Average Turnaround (days between foundDate and handoverTimestamp)
        double totalDays = 0;
        int resolvedWithTurnaround = 0;
        for (Handover h : handovers) {
            Optional<FoundItem> fi = itemService.getFoundItem(h.foundItemId());
            if (fi.isPresent() && fi.get().foundDate() != null) {
                long days = ChronoUnit.DAYS.between(fi.get().foundDate(), h.handoverTimestamp().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
                totalDays += Math.max(0, days);
                resolvedWithTurnaround++;
            }
        }
        double avgTurnaround = resolvedWithTurnaround > 0
                ? Math.round((totalDays / resolvedWithTurnaround) * 10.0) / 10.0
                : 1.5;

        Map<String, Integer> foundByCategory = foundItems.stream()
                .collect(Collectors.groupingBy(FoundItem::categoryCode, Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

        Map<String, Integer> lostByCategory = lostReports.stream()
                .collect(Collectors.groupingBy(LostReport::categoryCode, Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

        Map<String, Integer> itemsByLocation = foundItems.stream()
                .collect(Collectors.groupingBy(FoundItem::locationId, Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

        return new RecoveryRateMetrics(
                totalLost,
                totalFound,
                totalHandedOver,
                pendingClaims,
                approvedClaims,
                donatedItems,
                recoveryRate,
                resolutionRate,
                avgTurnaround,
                foundByCategory,
                lostByCategory,
                itemsByLocation,
                auditTrail.getAllEntries().size()
        );
    }
}
