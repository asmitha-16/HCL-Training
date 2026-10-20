package com.campus.lostandfound;

import com.campus.lostandfound.auth.service.AuthService;
import com.campus.lostandfound.claim.audit.AuditTrailService;
import com.campus.lostandfound.claim.model.Claim;
import com.campus.lostandfound.claim.service.ClaimService;
import com.campus.lostandfound.claim.service.HandoverService;
import com.campus.lostandfound.claim.service.StatisticsService;
import com.campus.lostandfound.common.model.Dtos.VerificationAnswer;
import com.campus.lostandfound.item.factory.ItemFactory;
import com.campus.lostandfound.item.model.FoundItem;
import com.campus.lostandfound.item.model.LostReport;
import com.campus.lostandfound.item.service.ItemReportService;
import com.campus.lostandfound.notification.service.NotificationService;
import com.campus.lostandfound.web.ApiRouter;
import com.campus.lostandfound.web.EmbeddedWebServer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

public class LostAndFoundApplication {

    public static void main(String[] args) {
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }

        System.out.println("Initializing Campus Lost & Found Microservices Platform...");

        NotificationService notificationService = new NotificationService();
        AuthService authService = new AuthService();
        AuditTrailService auditTrail = new AuditTrailService();
        ItemReportService itemService = new ItemReportService(notificationService);
        ClaimService claimService = new ClaimService(itemService, auditTrail, notificationService);
        HandoverService handoverService = new HandoverService(claimService, itemService, auditTrail, notificationService);
        StatisticsService statsService = new StatisticsService(itemService, claimService, handoverService, auditTrail);

        // Seed realistic campus demo data
        seedInitialCampusData(itemService, claimService, handoverService, auditTrail);

        ApiRouter apiRouter = new ApiRouter(
                authService,
                itemService,
                claimService,
                handoverService,
                statsService,
                auditTrail,
                notificationService
        );

        Path staticDir = resolveStaticDir();
        EmbeddedWebServer server = new EmbeddedWebServer(port, apiRouter, staticDir);

        try {
            server.start();
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Path resolveStaticDir() {
        Path p = Paths.get("src/main/resources/static");
        if (new File(p.toString()).exists()) {
            return p.toAbsolutePath();
        }
        p = Paths.get("lost-and-found-platform/src/main/resources/static");
        if (new File(p.toString()).exists()) {
            return p.toAbsolutePath();
        }
        return Paths.get(".").toAbsolutePath();
    }

    private static void seedInitialCampusData(
            ItemReportService itemService,
            ClaimService claimService,
            HandoverService handoverService,
            AuditTrailService auditTrail) {

        System.out.println("Seeding campus lost & found records...");

        LocalDate today = LocalDate.now();

        // 1. Lost Reports (FR1)
        LostReport rep1 = itemService.reportLostItem(new ItemFactory.CreateLostReportCommand(
                "MacBook Air 13\" (Space Gray)",
                "Left on desk near the window. Has a GitHub Octocat and Rust sticker on the top cover.",
                "ELECTRONICS",
                "LOC-LIB-02",
                today.minusDays(2),
                "Apple",
                "Space Gray",
                "Stickers on top lid, scratch near right USB-C port",
                "+1-555-0189",
                "asmitha.b@student.campus.edu",
                "USR-STU-01",
                "Asmitha B."
        ));

        LostReport rep2 = itemService.reportLostItem(new ItemFactory.CreateLostReportCommand(
                "Scientific Calculator Casio fx-991EX",
                "Left after Physics exam in Science Hall auditorium.",
                "ELECTRONICS",
                "LOC-SCI-03",
                today.minusDays(4),
                "Casio",
                "Black/White",
                "Initials 'RK' etched into battery cover with pencil",
                "+1-555-0111",
                "kumar.r@faculty.campus.edu",
                "USR-STF-01",
                "Prof. Rajesh Kumar"
        ));

        LostReport rep3 = itemService.reportLostItem(new ItemFactory.CreateLostReportCommand(
                "Car Keys with Blue Campus Lanyard",
                "Honda car key with two brass house keys and campus gym access tag.",
                "KEYS",
                "LOC-GYM-01",
                today.minusDays(1),
                "Honda",
                "Silver/Blue",
                "Blue woven lanyard with Campus Athletics emblem",
                "+1-555-0192",
                "rahul.s@student.campus.edu",
                "USR-STU-02",
                "Rahul Sharma"
        ));

        // 2. Found Items (FR2 & NFR-P1 sensitive masking)
        FoundItem fnd1 = itemService.logFoundItem(new ItemFactory.CreateFoundItemCommand(
                "Apple MacBook Air Space Gray 13\"",
                "Found on 2nd floor library carrel #14. Device is locked. Privacy mask applied.",
                "ELECTRONICS",
                "LOC-LIB-02",
                today.minusDays(1),
                "Apple",
                "Space Gray",
                "LOCKER-E10",
                "USR-SEC-02",
                "SECURITY_DESK",
                "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='400' height='300' viewBox='0 0 400 300'><rect width='400' height='300' fill='%23e8f4ee'/><rect x='100' y='70' width='200' height='130' rx='8' fill='%2318533d'/><polygon points='70,200 330,200 310,225 90,225' fill='%2394a3b8'/><rect x='130' y='110' width='140' height='50' rx='6' fill='%23dc2626' opacity='0.9'/><text x='200' y='140' fill='white' font-family='sans-serif' font-size='12' font-weight='bold' text-anchor='middle'>SHIELDED · SENSITIVE</text></svg>",
                null,
                true,
                "Has GitHub sticker and minor scratch on right port"
        ));

        FoundItem fnd2 = itemService.logFoundItem(new ItemFactory.CreateFoundItemCommand(
                "Leather Wallet with Student ID Card",
                "Brown leather tri-fold wallet found in Student Activity Center food court. Photo masked for student ID protection.",
                "ID_CARDS",
                "LOC-STU-01",
                today.minusDays(3),
                "Fossil",
                "Brown",
                "LOCKER-W04",
                "USR-SEC-01",
                "SECURITY_DESK",
                "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='400' height='300' viewBox='0 0 400 300'><rect width='400' height='300' fill='%23fef3c7'/><rect x='110' y='60' width='180' height='150' rx='10' fill='%2378350f'/><rect x='130' y='90' width='140' height='90' rx='6' fill='%23fef08a'/><rect x='140' y='110' width='120' height='50' rx='4' fill='%23dc2626' opacity='0.9'/><text x='200' y='140' fill='white' font-family='sans-serif' font-size='11' font-weight='bold' text-anchor='middle'>ID MASKED (NFR-P1)</text></svg>",
                null,
                true,
                "Contains student ID card and bus card"
        ));

        // 3. Completed Item Handover (historical benchmark for FR8 recovery rate)
        FoundItem fnd3 = itemService.logFoundItem(new ItemFactory.CreateFoundItemCommand(
                "Blue North Face Water Bottle",
                "32oz insulated bottle found in Gym locker room.",
                "BAGS",
                "LOC-GYM-01",
                today.minusDays(10),
                "Hydro Flask",
                "Blue",
                "BIN-302",
                "USR-SEC-01",
                "SECURITY_DESK",
                "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='400' height='300' viewBox='0 0 400 300'><rect width='400' height='300' fill='%23e0f2fe'/><rect x='160' y='70' width='80' height='170' rx='20' fill='%230284c7'/><rect x='180' y='45' width='40' height='25' rx='6' fill='%230369a1'/><text x='200' y='160' fill='white' font-family='sans-serif' font-size='12' font-weight='bold' text-anchor='middle'>HYDRO FLASK</text></svg>",
                null,
                false,
                "Dent on bottom base"
        ));

        // Create completed claim & handover for fnd3
        Claim histClaim = claimService.submitClaim(new ClaimService.SubmitClaimRequest(
                fnd3.id(),
                "",
                "USR-STU-02",
                "Rahul Sharma",
                "+1-555-0192",
                List.of(new VerificationAnswer("q1", "What brand and color?", "Hydro Flask Blue 32oz with dent on bottom", false)),
                "",
                "127.0.0.1"
        ));

        claimService.approveClaim(histClaim.id(), "USR-SEC-01", "Verified claimant describes bottom dent accurately", "127.0.0.1");

        handoverService.recordHandover(new HandoverService.RecordHandoverCommand(
                histClaim.id(),
                "Rahul Sharma",
                "STU-992144",
                "SIG_RAHUL_S",
                "USR-SEC-01",
                "Officer Daniel Hayes",
                "BIN-302",
                "ID verified in person, item released to student",
                "127.0.0.1"
        ));

        // 4. Old Unclaimed Item for FR7 60-day expiry demonstration
        FoundItem oldItem = itemService.logFoundItem(new ItemFactory.CreateFoundItemCommand(
                "Black Umbro Windbreaker Jacket (Size L)",
                "Found in Sports complex bleachers over 2 months ago. Unclaimed.",
                "WEARABLES",
                "LOC-GYM-01",
                today.minusDays(68),
                "Umbro",
                "Black",
                "DONATION-BOX-1",
                "USR-SEC-01",
                "SECURITY_DESK",
                "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='400' height='300' viewBox='0 0 400 300'><rect width='400' height='300' fill='%23f1f5f9'/><path d='M130,80 L200,110 L270,80 L300,135 L260,150 L260,230 L140,230 L140,150 L100,135 Z' fill='%231e293b'/><text x='200' y='180' fill='white' font-family='sans-serif' font-size='12' font-weight='bold' text-anchor='middle'>UMBRO JACKET (L)</text></svg>",
                null,
                false,
                "No identifying nametag"
        ));

        System.out.println("Campus initial dataset seeded successfully.");
    }
}
