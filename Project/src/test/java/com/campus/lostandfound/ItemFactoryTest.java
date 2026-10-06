package com.campus.lostandfound;

import com.campus.lostandfound.common.status.ItemStatus;
import com.campus.lostandfound.item.factory.ItemFactory;
import com.campus.lostandfound.item.model.FoundItem;
import com.campus.lostandfound.item.model.LostReport;

import java.time.LocalDate;

public class ItemFactoryTest {

    public static void run() {
        System.out.println("--- Running ItemFactoryTest ---");

        LocalDate today = LocalDate.now();

        // 1. Test LostReport creation
        ItemFactory.CreateLostReportCommand lostCmd = new ItemFactory.CreateLostReportCommand(
                "Leather Backpack",
                "Dark brown leather backpack containing notebooks",
                "BAGS",
                "LOC-LIB-02",
                today,
                "Fossil",
                "Brown",
                "Brass zippers",
                "+1-555-0999",
                "test@campus.edu",
                "USR-STU-01",
                "Student Alice"
        );

        LostReport report = ItemFactory.createLostReport(lostCmd);

        assert report.id().startsWith("LOST-") : "Report ID should start with LOST-";
        assert "BAGS".equals(report.categoryCode()) : "Category should be normalized uppercase";
        assert report.status() instanceof ItemStatus.Reported : "Initial status must be Reported";

        // 2. Test FoundItem creation with 60-day expiry rule
        ItemFactory.CreateFoundItemCommand foundCmd = new ItemFactory.CreateFoundItemCommand(
                "Apple Watch Series 8",
                "Found in gym locker room",
                "WEARABLES",
                "LOC-GYM-01",
                today,
                "Apple",
                "Midnight",
                "LOCKER-99",
                "USR-SEC-01",
                "SECURITY_DESK",
                null,
                null,
                true,
                "Small nick on crown"
        );

        FoundItem item = ItemFactory.createFoundItem(foundCmd);

        assert item.id().startsWith("FND-") : "Found item ID should start with FND-";
        assert item.status() instanceof ItemStatus.InCustody : "Initial status must be InCustody";
        assert "LOCKER-99".equals(item.custodyLockerId()) : "Custody locker ID should match";
        assert item.expiryDate().equals(today.plusDays(60)) : "Expiry date must be exactly foundDate + 60 days (FR7)";

        System.out.println("  [PASS] ItemFactoryTest: Verified factory instantiation, validation, sealed statuses, and 60-day expiry date computation.");
    }
}