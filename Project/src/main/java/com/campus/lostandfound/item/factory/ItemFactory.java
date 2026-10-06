package com.campus.lostandfound.item.factory;

import com.campus.lostandfound.common.status.ItemStatus;
import com.campus.lostandfound.item.model.FoundItem;
import com.campus.lostandfound.item.model.LostReport;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Factory pattern implementation for creating and validating LostReport and FoundItem instances.
 */
public class ItemFactory {
    private static final AtomicInteger lostCounter = new AtomicInteger(100);
    private static final AtomicInteger foundCounter = new AtomicInteger(500);

    public record CreateLostReportCommand(
            String title,
            String description,
            String categoryCode,
            String locationId,
            LocalDate lostDate,
            String brand,
            String color,
            String distinctiveFeatures,
            String contactPhone,
            String contactEmail,
            String reporterUserId,
            String reporterName
    ) {}

    public record CreateFoundItemCommand(
            String title,
            String description,
            String categoryCode,
            String locationId,
            LocalDate foundDate,
            String brand,
            String color,
            String custodyLockerId,
            String loggedByUserId,
            String loggedByRole,
            String photoUrl,
            String maskedPhotoUrl,
            boolean hasSensitiveMasking,
            String secretVerificationHints
    ) {}

    public static LostReport createLostReport(CreateLostReportCommand cmd) {
        Objects.requireNonNull(cmd.title(), "Title must not be null");
        Objects.requireNonNull(cmd.categoryCode(), "Category must not be null");
        Objects.requireNonNull(cmd.locationId(), "Location must not be null");

        String id = "LOST-" + lostCounter.incrementAndGet();
        LocalDate reportDate = cmd.lostDate() != null ? cmd.lostDate() : LocalDate.now();
        ItemStatus status = new ItemStatus.Reported(Instant.now(), "Initial lost report registered by " + (cmd.reporterName() != null ? cmd.reporterName() : "User"));

        return new LostReport(
                id,
                cmd.title().trim(),
                cmd.description() != null ? cmd.description().trim() : "",
                cmd.categoryCode().trim().toUpperCase(),
                cmd.locationId().trim(),
                reportDate,
                cmd.brand() != null ? cmd.brand().trim() : "",
                cmd.color() != null ? cmd.color().trim() : "",
                cmd.distinctiveFeatures() != null ? cmd.distinctiveFeatures().trim() : "",
                cmd.contactPhone() != null ? cmd.contactPhone().trim() : "",
                cmd.contactEmail() != null ? cmd.contactEmail().trim() : "",
                cmd.reporterUserId() != null ? cmd.reporterUserId() : "ANONYMOUS",
                cmd.reporterName() != null ? cmd.reporterName().trim() : "Campus User",
                status,
                Instant.now()
        );
    }

    public static FoundItem createFoundItem(CreateFoundItemCommand cmd) {
        Objects.requireNonNull(cmd.title(), "Title must not be null");
        Objects.requireNonNull(cmd.categoryCode(), "Category must not be null");
        Objects.requireNonNull(cmd.locationId(), "Location must not be null");

        String id = "FND-" + foundCounter.incrementAndGet();
        LocalDate foundDate = cmd.foundDate() != null ? cmd.foundDate() : LocalDate.now();
        LocalDate expiryDate = foundDate.plusDays(60); // FR7: 60-day expiry rule

        String lockerId = cmd.custodyLockerId() != null && !cmd.custodyLockerId().isBlank() 
                ? cmd.custodyLockerId() 
                : "BIN-" + (100 + (foundCounter.get() % 50));
        
        String officer = cmd.loggedByUserId() != null ? cmd.loggedByUserId() : "SECURITY_DESK";
        ItemStatus status = new ItemStatus.InCustody(lockerId, officer);

        String photo = cmd.photoUrl() != null && !cmd.photoUrl().isBlank() ? cmd.photoUrl() : "/assets/placeholder-item.svg";
        String maskedPhoto = cmd.maskedPhotoUrl() != null && !cmd.maskedPhotoUrl().isBlank() ? cmd.maskedPhotoUrl() : photo;

        return new FoundItem(
                id,
                cmd.title().trim(),
                cmd.description() != null ? cmd.description().trim() : "",
                cmd.categoryCode().trim().toUpperCase(),
                cmd.locationId().trim(),
                foundDate,
                cmd.brand() != null ? cmd.brand().trim() : "",
                cmd.color() != null ? cmd.color().trim() : "",
                lockerId,
                cmd.loggedByUserId() != null ? cmd.loggedByUserId() : "SECURITY-DESK",
                cmd.loggedByRole() != null ? cmd.loggedByRole() : "SECURITY_DESK",
                photo,
                maskedPhoto,
                cmd.hasSensitiveMasking(),
                cmd.secretVerificationHints() != null ? cmd.secretVerificationHints().trim() : "",
                status,
                expiryDate,
                Instant.now()
        );
    }
}
