package com.campus.lostandfound.web;

import com.campus.lostandfound.auth.model.AppUser;
import com.campus.lostandfound.auth.model.Role;
import com.campus.lostandfound.auth.service.AuthService;
import com.campus.lostandfound.claim.audit.AuditTrailService;
import com.campus.lostandfound.claim.model.Claim;
import com.campus.lostandfound.claim.model.Handover;
import com.campus.lostandfound.claim.service.ClaimService;
import com.campus.lostandfound.claim.service.HandoverService;
import com.campus.lostandfound.claim.service.StatisticsService;
import com.campus.lostandfound.common.model.Dtos.AuditLogEntry;
import com.campus.lostandfound.common.model.Dtos.VerificationAnswer;
import com.campus.lostandfound.item.factory.ItemFactory;
import com.campus.lostandfound.item.matching.model.MatchResult;
import com.campus.lostandfound.item.matching.model.MatchSuggestion;
import com.campus.lostandfound.item.model.Category;
import com.campus.lostandfound.item.model.FoundItem;
import com.campus.lostandfound.item.model.Location;
import com.campus.lostandfound.item.model.LostReport;
import com.campus.lostandfound.item.service.ItemExpiryService;
import com.campus.lostandfound.item.service.ItemReportService;
import com.campus.lostandfound.notification.model.Notification;
import com.campus.lostandfound.notification.service.NotificationService;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * High-performance API request dispatcher connecting all microservices.
 */
public class ApiRouter {

    private final AuthService authService;
    private final ItemReportService itemService;
    private final ClaimService claimService;
    private final HandoverService handoverService;
    private final StatisticsService statsService;
    private final AuditTrailService auditTrail;
    private final NotificationService notificationService;

    public record HttpResponse(int statusCode, String contentType, String body) {}

    public ApiRouter(
            AuthService authService,
            ItemReportService itemService,
            ClaimService claimService,
            HandoverService handoverService,
            StatisticsService statsService,
            AuditTrailService auditTrail,
            NotificationService notificationService
    ) {
        this.authService = authService;
        this.itemService = itemService;
        this.claimService = claimService;
        this.handoverService = handoverService;
        this.statsService = statsService;
        this.auditTrail = auditTrail;
        this.notificationService = notificationService;
    }

    public HttpResponse route(String method, String path, String query, String body, String clientIp, String authHeader) {
        try {
            // Check health
            if ("GET".equalsIgnoreCase(method) && "/api/health".equals(path)) {
                return jsonResponse(200, "{\"status\":\"UP\",\"platform\":\"Campus Lost and Found\",\"services\":[\"auth-service\",\"item-service\",\"claim-service\",\"matching-engine\"]}");
            }

            // M1: Auth
            if ("GET".equalsIgnoreCase(method) && "/api/auth/users".equals(path)) {
                return jsonResponse(200, serializeUsers(authService.getAllUsers()));
            }

            if ("POST".equalsIgnoreCase(method) && "/api/auth/login".equals(path)) {
                Map<String, String> data = JsonUtil.parseSimpleJson(body);
                String username = data.get("username");
                if (username == null || username.isBlank()) {
                    return jsonResponse(400, "{\"error\":\"Username required\"}");
                }
                String token = authService.login(username);
                AppUser user = authService.getUserByUsername(username).orElseThrow();
                return jsonResponse(200, "{\"success\":true,\"token\":\"" + token + "\",\"userId\":\"" + user.id() + "\",\"role\":\"" + user.role().name() + "\",\"name\":\"" + JsonUtil.escape(user.fullName()) + "\"}");
            }

            // Categories & Locations
            if ("GET".equalsIgnoreCase(method) && "/api/categories".equals(path)) {
                return jsonResponse(200, serializeCategories(itemService.getAllCategories()));
            }

            if ("GET".equalsIgnoreCase(method) && "/api/locations".equals(path)) {
                return jsonResponse(200, serializeLocations(itemService.getAllLocations()));
            }

            // M2: Lost Reports (FR1)
            if ("GET".equalsIgnoreCase(method) && "/api/items/lost".equals(path)) {
                return jsonResponse(200, serializeLostReports(itemService.getAllLostReports()));
            }

            if ("POST".equalsIgnoreCase(method) && "/api/items/lost".equals(path)) {
                Map<String, String> data = JsonUtil.parseSimpleJson(body);
                LocalDate lostDate = data.containsKey("lostDate") && !data.get("lostDate").isBlank()
                        ? LocalDate.parse(data.get("lostDate"))
                        : LocalDate.now();

                ItemFactory.CreateLostReportCommand cmd = new ItemFactory.CreateLostReportCommand(
                        data.get("title"),
                        data.get("description"),
                        data.get("categoryCode"),
                        data.get("locationId"),
                        lostDate,
                        data.get("brand"),
                        data.get("color"),
                        data.get("distinctiveFeatures"),
                        data.get("contactPhone"),
                        data.get("contactEmail"),
                        data.getOrDefault("reporterUserId", "USR-STU-01"),
                        data.getOrDefault("reporterName", "Student/Staff")
                );

                LostReport report = itemService.reportLostItem(cmd);

                auditTrail.recordEvent("LOST_REPORTED", "LostReport", report.id(), report.reporterUserId(), "STUDENT_STAFF", clientIp,
                        "Reported lost item: " + report.title(), "NONE", "REPORTED");

                return jsonResponse(201, serializeLostReport(report));
            }

            // M2: Found Items (FR2 & NFR-P1)
            if ("GET".equalsIgnoreCase(method) && "/api/items/found".equals(path)) {
                // If security desk, allow viewing original photo, else masked photo
                boolean isSecurity = authHeader != null && (authHeader.contains("SECURITY") || authHeader.contains("ADMIN"));
                return jsonResponse(200, serializeFoundItems(itemService.getAllFoundItems(), isSecurity));
            }

            if ("POST".equalsIgnoreCase(method) && "/api/items/found".equals(path)) {
                Map<String, String> data = JsonUtil.parseSimpleJson(body);
                LocalDate foundDate = data.containsKey("foundDate") && !data.get("foundDate").isBlank()
                        ? LocalDate.parse(data.get("foundDate"))
                        : LocalDate.now();

                boolean hasSensitive = "true".equalsIgnoreCase(data.get("hasSensitiveMasking")) || "true".equalsIgnoreCase(data.get("maskSensitive"));

                ItemFactory.CreateFoundItemCommand cmd = new ItemFactory.CreateFoundItemCommand(
                        data.get("title"),
                        data.get("description"),
                        data.get("categoryCode"),
                        data.get("locationId"),
                        foundDate,
                        data.get("brand"),
                        data.get("color"),
                        data.get("custodyLockerId"),
                        data.getOrDefault("loggedByUserId", "USR-SEC-01"),
                        data.getOrDefault("loggedByRole", "SECURITY_DESK"),
                        data.get("photoUrl"),
                        null,
                        hasSensitive,
                        data.get("secretVerificationHints")
                );

                FoundItem item = itemService.logFoundItem(cmd);

                auditTrail.recordEvent("FOUND_LOGGED", "FoundItem", item.id(), item.loggedByUserId(), item.loggedByRole(), clientIp,
                        "Logged found item: " + item.title() + " in locker " + item.custodyLockerId() + (item.hasSensitiveMasking() ? " [PHOTO MASKED]" : ""),
                        "NONE", "IN_CUSTODY");

                return jsonResponse(201, serializeFoundItem(item, true));
            }

            // M3: Matching Engine (FR3 / NFR-P2)
            if ("GET".equalsIgnoreCase(method) && "/api/items/matches".equals(path)) {
                String reportId = extractQueryParam(query, "reportId");
                if (reportId == null || reportId.isBlank()) {
                    // Return all suggestions
                    return jsonResponse(200, serializeSuggestions(itemService.getMatchingEngine().getAllSuggestions()));
                }

                Optional<LostReport> repOpt = itemService.getLostReport(reportId);
                if (repOpt.isEmpty()) {
                    return jsonResponse(404, "{\"error\":\"Report not found: " + reportId + "\"}");
                }

                // Asynchronous execution via CompletableFuture
                long t0 = System.currentTimeMillis();
                List<MatchResult> matches = itemService.getMatchingEngine()
                        .findMatchesForLostReportAsync(repOpt.get(), itemService.getAllFoundItems(), 0.30)
                        .get();
                long durationMs = System.currentTimeMillis() - t0;

                return jsonResponse(200, serializeMatchResults(matches, durationMs));
            }

            // M4: Claims (FR4 & FR6)
            if ("GET".equalsIgnoreCase(method) && "/api/claims".equals(path)) {
                String userId = extractQueryParam(query, "userId");
                List<Claim> list = (userId != null && !userId.isBlank())
                        ? claimService.getClaimsForUser(userId)
                        : claimService.getAllClaims();
                return jsonResponse(200, serializeClaims(list));
            }

            if ("POST".equalsIgnoreCase(method) && "/api/claims".equals(path)) {
                Map<String, String> data = JsonUtil.parseSimpleJson(body);

                // Parse questions / answers
                List<VerificationAnswer> answers = parseAnswers(data);

                ClaimService.SubmitClaimRequest req = new ClaimService.SubmitClaimRequest(
                        data.get("foundItemId"),
                        data.get("lostReportId"),
                        data.getOrDefault("claimantUserId", "USR-STU-01"),
                        data.getOrDefault("claimantName", "Student Claimant"),
                        data.get("claimantContact"),
                        answers,
                        data.get("proofAttachmentUrl"),
                        clientIp
                );

                Claim claim = claimService.submitClaim(req);
                return jsonResponse(201, serializeClaim(claim));
            }

            if ("POST".equalsIgnoreCase(method) && "/api/claims/approve".equals(path)) {
                Map<String, String> data = JsonUtil.parseSimpleJson(body);
                String claimId = data.get("claimId");
                String officerId = data.getOrDefault("officerId", "USR-SEC-01");
                String notes = data.getOrDefault("officerNotes", "Ownership verified at desk");

                // FR6: Concurrency and duplicate approved claims prevention
                Claim approved = claimService.approveClaim(claimId, officerId, notes, clientIp);
                return jsonResponse(200, serializeClaim(approved));
            }

            if ("POST".equalsIgnoreCase(method) && "/api/claims/reject".equals(path)) {
                Map<String, String> data = JsonUtil.parseSimpleJson(body);
                String claimId = data.get("claimId");
                String officerId = data.getOrDefault("officerId", "USR-SEC-01");
                String reason = data.getOrDefault("reason", "Verification answers do not match physical item attributes");

                Claim rejected = claimService.rejectClaim(claimId, officerId, reason, clientIp);
                return jsonResponse(200, serializeClaim(rejected));
            }

            // Handover (FR5)
            if ("GET".equalsIgnoreCase(method) && "/api/handovers".equals(path)) {
                return jsonResponse(200, serializeHandovers(handoverService.getAllHandovers()));
            }

            if ("POST".equalsIgnoreCase(method) && "/api/handover".equals(path)) {
                Map<String, String> data = JsonUtil.parseSimpleJson(body);

                HandoverService.RecordHandoverCommand cmd = new HandoverService.RecordHandoverCommand(
                        data.get("claimId"),
                        data.get("recipientName"),
                        data.get("recipientIdNumber"),
                        data.getOrDefault("recipientSignature", "SIGNATURE_VERIFIED"),
                        data.getOrDefault("officerId", "USR-SEC-01"),
                        data.getOrDefault("officerName", "Officer Dan"),
                        data.get("lockerIdReleased"),
                        data.getOrDefault("verificationNotes", "Physical possession handed over, ID checked"),
                        clientIp
                );

                Handover handover = handoverService.recordHandover(cmd);
                return jsonResponse(201, serializeHandover(handover));
            }

            // FR7: 60-day expiry scan
            if ("POST".equalsIgnoreCase(method) && "/api/expiry/scan".equals(path)) {
                Map<String, String> data = JsonUtil.parseSimpleJson(body);
                LocalDate scanDate = data.containsKey("date") && !data.get("date").isBlank()
                        ? LocalDate.parse(data.get("date"))
                        : LocalDate.now();

                ItemExpiryService.ExpiryScanResult result = itemService.trigger60DayExpiryScan(scanDate);
                return jsonResponse(200, "{\"success\":true,\"totalEvaluated\":" + result.totalEvaluated() +
                        ",\"newlyFlaggedForDonation\":" + result.newlyFlaggedForDonation() +
                        ",\"alreadyExpired\":" + result.alreadyExpired() +
                        ",\"stillActive\":" + result.stillActive() +
                        ",\"batchId\":\"" + result.donationBatchManifestId() + "\"}");
            }

            // M5: Stats & Recovery Rate (FR8)
            if ("GET".equalsIgnoreCase(method) && "/api/stats".equals(path)) {
                StatisticsService.RecoveryRateMetrics metrics = statsService.calculateMetrics();
                return jsonResponse(200, serializeStats(metrics));
            }

            // NFR-P3: Audit Trail
            if ("GET".equalsIgnoreCase(method) && "/api/audit".equals(path)) {
                return jsonResponse(200, serializeAudit(auditTrail.getAllEntries()));
            }

            // Notifications
            if ("GET".equalsIgnoreCase(method) && "/api/notifications".equals(path)) {
                String userId = extractQueryParam(query, "userId");
                List<Notification> list = (userId != null && !userId.isBlank())
                        ? notificationService.getNotificationsForUser(userId)
                        : notificationService.getAllNotifications();
                return jsonResponse(200, serializeNotifications(list));
            }

            return jsonResponse(404, "{\"error\":\"Endpoint not found: " + path + "\"}");

        } catch (IllegalArgumentException e) {
            return jsonResponse(400, "{\"error\":\"" + JsonUtil.escape(e.getMessage()) + "\"}");
        } catch (IllegalStateException e) {
            return jsonResponse(409, "{\"error\":\"" + JsonUtil.escape(e.getMessage()) + "\"}");
        } catch (NoSuchElementException e) {
            return jsonResponse(404, "{\"error\":\"" + JsonUtil.escape(e.getMessage()) + "\"}");
        } catch (Exception e) {
            return jsonResponse(500, "{\"error\":\"Server error: " + JsonUtil.escape(e.getMessage()) + "\"}");
        }
    }

    private HttpResponse jsonResponse(int code, String body) {
        return new HttpResponse(code, "application/json; charset=utf-8", body);
    }

    private String extractQueryParam(String query, String param) {
        if (query == null || query.isBlank()) return null;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2 && kv[0].equalsIgnoreCase(param)) {
                return kv[1];
            }
        }
        return null;
    }

    private List<VerificationAnswer> parseAnswers(Map<String, String> data) {
        List<VerificationAnswer> list = new ArrayList<>();
        int i = 1;
        while (data.containsKey("q" + i) || data.containsKey("q" + i + "_text")) {
            String text = data.getOrDefault("q" + i + "_text", "Verification Question #" + i);
            String ans = data.getOrDefault("q" + i, "");
            list.add(new VerificationAnswer("q" + i, text, ans, false));
            i++;
        }
        if (list.isEmpty() && data.containsKey("answersText")) {
            list.add(new VerificationAnswer("q1", "Ownership Details", data.get("answersText"), false));
        }
        return list;
    }

    // Serializers
    private String serializeUsers(List<AppUser> users) {
        return "[" + users.stream().map(u -> "{\"id\":\"" + u.id() + "\",\"username\":\"" + u.username() + "\",\"name\":\"" + JsonUtil.escape(u.fullName()) + "\",\"role\":\"" + u.role().name() + "\",\"department\":\"" + JsonUtil.escape(u.department()) + "\"}").collect(Collectors.joining(",")) + "]";
    }

    private String serializeCategories(List<Category> cats) {
        return "[" + cats.stream().map(c -> "{\"id\":\"" + c.id() + "\",\"code\":\"" + c.code() + "\",\"name\":\"" + JsonUtil.escape(c.displayName()) + "\",\"icon\":\"" + c.icon() + "\",\"questions\":[" + c.typicalVerificationQuestions().stream().map(q -> "\"" + JsonUtil.escape(q) + "\"").collect(Collectors.joining(",")) + "]}").collect(Collectors.joining(",")) + "]";
    }

    private String serializeLocations(List<Location> locs) {
        return "[" + locs.stream().map(l -> "{\"id\":\"" + l.id() + "\",\"building\":\"" + JsonUtil.escape(l.building()) + "\",\"floor\":\"" + JsonUtil.escape(l.floor()) + "\",\"room\":\"" + JsonUtil.escape(l.roomOrZone()) + "\",\"deskContact\":\"" + JsonUtil.escape(l.securityDeskContact()) + "\"}").collect(Collectors.joining(",")) + "]";
    }

    private String serializeLostReports(List<LostReport> reports) {
        return "[" + reports.stream().map(this::serializeLostReport).collect(Collectors.joining(",")) + "]";
    }

    private String serializeLostReport(LostReport r) {
        return "{\"id\":\"" + r.id() + "\",\"title\":\"" + JsonUtil.escape(r.title()) + "\",\"description\":\"" + JsonUtil.escape(r.description()) + "\",\"categoryCode\":\"" + r.categoryCode() + "\",\"locationId\":\"" + r.locationId() + "\",\"lostDate\":\"" + r.lostDate() + "\",\"brand\":\"" + JsonUtil.escape(r.brand()) + "\",\"color\":\"" + JsonUtil.escape(r.color()) + "\",\"distinctiveFeatures\":\"" + JsonUtil.escape(r.distinctiveFeatures()) + "\",\"reporterName\":\"" + JsonUtil.escape(r.reporterName()) + "\",\"statusCode\":\"" + r.status().code() + "\",\"statusName\":\"" + JsonUtil.escape(r.status().displayName()) + "\"}";
    }

    private String serializeFoundItems(List<FoundItem> items, boolean isSecurity) {
        return "[" + items.stream().map(i -> serializeFoundItem(i, isSecurity)).collect(Collectors.joining(",")) + "]";
    }

    private String serializeFoundItem(FoundItem i, boolean isSecurity) {
        // NFR-P1: Hide sensitive photos for non-security users
        String photoToShow = isSecurity ? i.originalPhotoUrl() : i.maskedPhotoUrl();
        return "{\"id\":\"" + i.id() + "\",\"title\":\"" + JsonUtil.escape(i.title()) + "\",\"description\":\"" + JsonUtil.escape(i.description()) + "\",\"categoryCode\":\"" + i.categoryCode() + "\",\"locationId\":\"" + i.locationId() + "\",\"foundDate\":\"" + i.foundDate() + "\",\"brand\":\"" + JsonUtil.escape(i.brand()) + "\",\"color\":\"" + JsonUtil.escape(i.color()) + "\",\"custodyLockerId\":\"" + i.custodyLockerId() + "\",\"photoUrl\":\"" + JsonUtil.escape(photoToShow) + "\",\"hasSensitiveMasking\":" + i.hasSensitiveMasking() + ",\"statusCode\":\"" + i.status().code() + "\",\"statusName\":\"" + JsonUtil.escape(i.status().displayName()) + "\",\"expiryDate\":\"" + i.expiryDate() + "\"}";
    }

    private String serializeMatchResults(List<MatchResult> matches, long durationMs) {
        String array = matches.stream().map(m -> "{\"foundItemId\":\"" + m.foundItemId() + "\",\"lostReportId\":\"" + m.lostReportId() + "\",\"overallScore\":" + m.overallScore() + ",\"confidenceLevel\":\"" + m.confidenceLevel() + "\",\"categoryScore\":" + m.categoryScore() + ",\"locationScore\":" + m.locationScore() + ",\"dateScore\":" + m.dateScore() + ",\"keywordScore\":" + m.keywordScore() + ",\"rationale\":\"" + JsonUtil.escape(m.rationale()) + "\",\"foundItem\":" + (m.foundItem() != null ? serializeFoundItem(m.foundItem(), false) : "null") + "}").collect(Collectors.joining(","));
        return "{\"executionTimeMs\":" + durationMs + ",\"matches\":[" + array + "]}";
    }

    private String serializeSuggestions(List<MatchSuggestion> list) {
        return "[" + list.stream().map(s -> "{\"id\":\"" + s.id() + "\",\"lostReportId\":\"" + s.lostReportId() + "\",\"foundItemId\":\"" + s.foundItemId() + "\",\"matchScore\":" + s.matchScore() + ",\"confidenceLevel\":\"" + s.confidenceLevel() + "\",\"rationale\":\"" + JsonUtil.escape(s.rationale()) + "\"}").collect(Collectors.joining(",")) + "]";
    }

    private String serializeClaims(List<Claim> claims) {
        return "[" + claims.stream().map(this::serializeClaim).collect(Collectors.joining(",")) + "]";
    }

    private String serializeClaim(Claim c) {
        String ansJson = "[" + c.answers().stream().map(a -> "{\"q\":\"" + JsonUtil.escape(a.questionText()) + "\",\"a\":\"" + JsonUtil.escape(a.answer()) + "\"}").collect(Collectors.joining(",")) + "]";
        return "{\"id\":\"" + c.id() + "\",\"foundItemId\":\"" + c.foundItemId() + "\",\"lostReportId\":\"" + c.lostReportId() + "\",\"claimantUserId\":\"" + c.claimantUserId() + "\",\"claimantName\":\"" + JsonUtil.escape(c.claimantName()) + "\",\"claimantContact\":\"" + JsonUtil.escape(c.claimantContact()) + "\",\"status\":\"" + c.status() + "\",\"officerReviewNotes\":\"" + JsonUtil.escape(c.officerReviewNotes()) + "\",\"submittedAt\":\"" + c.submittedAt() + "\",\"answers\":" + ansJson + "}";
    }

    private String serializeHandovers(List<Handover> list) {
        return "[" + list.stream().map(this::serializeHandover).collect(Collectors.joining(",")) + "]";
    }

    private String serializeHandover(Handover h) {
        return "{\"id\":\"" + h.id() + "\",\"claimId\":\"" + h.claimId() + "\",\"foundItemId\":\"" + h.foundItemId() + "\",\"lostReportId\":\"" + h.lostReportId() + "\",\"recipientName\":\"" + JsonUtil.escape(h.recipientName()) + "\",\"recipientIdNumber\":\"" + JsonUtil.escape(h.recipientIdNumber()) + "\",\"officerName\":\"" + JsonUtil.escape(h.officerName()) + "\",\"lockerIdReleased\":\"" + JsonUtil.escape(h.lockerIdReleased()) + "\",\"notes\":\"" + JsonUtil.escape(h.verificationNotes()) + "\",\"timestamp\":\"" + h.handoverTimestamp() + "\"}";
    }

    private String serializeStats(StatisticsService.RecoveryRateMetrics m) {
        String catJson = "{" + m.foundByCategory().entrySet().stream().map(e -> "\"" + e.getKey() + "\":" + e.getValue()).collect(Collectors.joining(",")) + "}";
        String locJson = "{" + m.itemsByLocation().entrySet().stream().map(e -> "\"" + e.getKey() + "\":" + e.getValue()).collect(Collectors.joining(",")) + "}";

        return "{\"totalLostReports\":" + m.totalLostReports() +
                ",\"totalFoundItems\":" + m.totalFoundItems() +
                ",\"totalHandovers\":" + m.totalHandovers() +
                ",\"activeClaimsPending\":" + m.activeClaimsPending() +
                ",\"totalApprovedClaims\":" + m.totalApprovedClaims() +
                ",\"itemsFlaggedForDonation\":" + m.itemsFlaggedForDonation() +
                ",\"recoveryRatePercentage\":" + m.recoveryRatePercentage() +
                ",\"resolutionRatePercentage\":" + m.resolutionRatePercentage() +
                ",\"averageTurnaroundDays\":" + m.averageTurnaroundDays() +
                ",\"foundByCategory\":" + catJson +
                ",\"itemsByLocation\":" + locJson +
                ",\"totalAuditEvents\":" + m.totalAuditEvents() + "}";
    }

    private String serializeAudit(List<AuditLogEntry> entries) {
        return "[" + entries.stream().map(e -> "{\"id\":\"" + e.id() + "\",\"eventType\":\"" + e.eventType() + "\",\"entityType\":\"" + e.entityType() + "\",\"entityId\":\"" + e.entityId() + "\",\"actorId\":\"" + e.actorId() + "\",\"actorRole\":\"" + e.actorRole() + "\",\"action\":\"" + JsonUtil.escape(e.actionSummary()) + "\",\"previousState\":\"" + e.previousState() + "\",\"newState\":\"" + e.newState() + "\",\"checksum\":\"" + e.checksumHash() + "\",\"timestamp\":\"" + e.timestamp() + "\"}").collect(Collectors.joining(",")) + "]";
    }

    private String serializeNotifications(List<Notification> notifs) {
        return "[" + notifs.stream().map(n -> "{\"id\":\"" + n.id() + "\",\"title\":\"" + JsonUtil.escape(n.title()) + "\",\"message\":\"" + JsonUtil.escape(n.message()) + "\",\"type\":\"" + n.type() + "\",\"referenceId\":\"" + n.referenceId() + "\",\"isRead\":" + n.isRead() + ",\"timestamp\":\"" + n.timestamp() + "\"}").collect(Collectors.joining(",")) + "]";
    }
}
