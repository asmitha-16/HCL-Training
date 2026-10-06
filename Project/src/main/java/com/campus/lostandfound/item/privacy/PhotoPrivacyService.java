package com.campus.lostandfound.item.privacy;

import java.util.List;

/**
 * Service enforcing NFR-P1: Found item photos hide sensitive details
 * (such as ID numbers, credit card digits, student numbers, phone lock screen messages).
 */
public class PhotoPrivacyService {

    public record MaskRegion(int x, int y, int width, int height, String sensitiveType) {}

    public record PrivacyMaskResult(
            String originalUrl,
            String maskedUrl,
            boolean isMasked,
            List<String> protectedAttributes,
            String securityNotice
    ) {}

    public PrivacyMaskResult applyPrivacyFilter(String photoUrl, String categoryCode, List<String> sensitiveAttributes) {
        if (photoUrl == null || photoUrl.isBlank()) {
            return new PrivacyMaskResult(
                    "/assets/placeholder-item.svg",
                    "/assets/placeholder-item.svg",
                    false,
                    List.of(),
                    "No image provided"
            );
        }

        // Generate high-contrast masked SVG representation if photoUrl is a data URI or placeholder
        boolean requiresMask = isSensitiveCategory(categoryCode) || (sensitiveAttributes != null && !sensitiveAttributes.isEmpty());

        String maskedUrl = photoUrl;
        if (requiresMask) {
            maskedUrl = generateMaskedPhotoUrl(photoUrl, sensitiveAttributes != null ? sensitiveAttributes : List.of("Serial/Card Number", "Owner Identifier"));
        }

        return new PrivacyMaskResult(
                photoUrl,
                maskedUrl,
                requiresMask,
                sensitiveAttributes != null ? sensitiveAttributes : List.of(),
                "Sensitive details obscured to protect owner privacy (NFR-P1). Verify at Security Desk."
        );
    }

    private boolean isSensitiveCategory(String categoryCode) {
        if (categoryCode == null) return false;
        String code = categoryCode.toUpperCase();
        return code.contains("CARD") || code.contains("ID") || code.contains("ELEC") || code.contains("WALLET");
    }

    public String generateMaskedPhotoUrl(String originalUrl, List<String> maskedLabels) {
        String labelStr = String.join(", ", maskedLabels);
        // Create an SVG badge/overlay representing the masked privacy shield
        return "data:image/svg+xml;utf8," +
                "<svg xmlns='http://www.w3.org/2000/svg' width='400' height='300' viewBox='0 0 400 300'>" +
                "<rect width='400' height='300' fill='%231e293b'/>" +
                "<rect x='30' y='30' width='340' height='240' rx='12' fill='%23334155' stroke='%2338bdf8' stroke-width='2' stroke-dasharray='6 6'/>" +
                "<circle cx='200' cy='110' r='36' fill='%230f172a' stroke='%2338bdf8' stroke-width='2'/>" +
                "<path d='M192 105 L200 115 L215 95' stroke='%2338bdf8' stroke-width='4' fill='none' stroke-linecap='round'/>" +
                "<text x='200' y='175' font-family='system-ui, sans-serif' font-size='15' font-weight='bold' fill='%23f8fafc' text-anchor='middle'>SENSITIVE DETAILS PROTECTED</text>" +
                "<text x='200' y='200' font-family='system-ui, sans-serif' font-size='12' fill='%2394a3b8' text-anchor='middle'>NFR-P1 Privacy Shield Active</text>" +
                "<rect x='50' y='220' width='300' height='28' rx='6' fill='%23e11d48'/>" +
                "<text x='200' y='239' font-family='system-ui, sans-serif' font-size='11' font-weight='bold' fill='%23ffffff' text-anchor='middle'>BLURRED: " + labelStr.toUpperCase() + "</text>" +
                "</svg>";
    }
}
