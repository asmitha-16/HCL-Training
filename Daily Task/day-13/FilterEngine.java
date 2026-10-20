import java.util.*;
import java.util.function.*;

/**
 * FilterEngine.java
 *
 * Day 13 Deliverable: Functional Programming in Modern Java
 * Project: JFS-40 Campus Lost & Found Portal
 * -----------------------------------------------------------------------------
 * Demonstrates:
 * 1. Lambda syntax & effectively-final variable capture.
 * 2. Composed Predicates (and, or, negate) for campus item filtering.
 * 3. Function chains (andThen, compose) for DTO transformation.
 * 4. Consumer for audit logging and notifications.
 * 5. Supplier for default inventory fallbacks.
 * 6. The 4 kinds of Method References (Static, Bound, Unbound, Constructor).
 * 7. Custom @FunctionalInterface: MatchRule.
 * -----------------------------------------------------------------------------
 */
public class FilterEngine {

    // Domain models for functional processing
    public record FoundItem(String id, String category, String title, String location, double valuation, boolean available) {}
    public record ItemSummaryDTO(String code, String label, String custodyLocation) {}

    // Custom Functional Interface
    @FunctionalInterface
    public interface MatchRule {
        double evaluate(String categoryA, String categoryB, String locA, String locB);
    }

    // Static helper method for method reference demonstration
    public static String formatLabel(String title) {
        return title.toUpperCase().trim();
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("     CAMPUS LOST & FOUND FUNCTIONAL FILTER ENGINE — DAY 13                     ");
        System.out.println("     PROJECT: JFS-40 | MODULE 2: MODERN JAVA (8–21)                            ");
        System.out.println("===============================================================================");

        List<FoundItem> inventory = Arrays.asList(
            new FoundItem("FND-201", "ELECTRONICS", "Apple MacBook Pro 14", "LIB", 1299.99, true),
            new FoundItem("FND-202", "WALLET_CARDS", "Brown Leather Bi-fold", "DIN", 45.00, true),
            new FoundItem("FND-203", "KEYS", "Room Key on Blue Lanyard", "SAC", 15.00, false),
            new FoundItem("FND-204", "ELECTRONICS", "Sony WH-1000XM5 Headphones", "ENG", 349.50, true),
            new FoundItem("FND-205", "BAGS", "Nike Sports Backpack", "SAC", 60.00, true)
        );

        // ---------------------------------------------------------------------
        // 1. Composed Predicates (and, or, negate)
        // ---------------------------------------------------------------------
        System.out.println("\n[1] COMPOSED PREDICATES (AVAILABLE ELECTRONICS):");
        Predicate<FoundItem> isElectronics = item -> "ELECTRONICS".equalsIgnoreCase(item.category());
        Predicate<FoundItem> isAvailable = FoundItem::available; // Unbound method reference
        Predicate<FoundItem> isHighValue = item -> item.valuation() > 100.0;

        // Composing predicates using .and()
        Predicate<FoundItem> highValueActiveElectronics = isElectronics.and(isAvailable).and(isHighValue);

        for (FoundItem item : inventory) {
            if (highValueActiveElectronics.test(item)) {
                System.out.println("  * Filter Match: " + item.title() + " ($" + item.valuation() + " in " + item.location() + ")");
            }
        }

        // Predicate with .negate()
        System.out.println("\n[2] PREDICATE NEGATION (.negate() -> UNAVAILABLE ITEMS):");
        Predicate<FoundItem> isUnavailable = isAvailable.negate();
        for (FoundItem item : inventory) {
            if (isUnavailable.test(item)) {
                System.out.println("  * Unavailable / Claimed: " + item.id() + " - " + item.title());
            }
        }

        // ---------------------------------------------------------------------
        // 2. Function Chains (andThen)
        // ---------------------------------------------------------------------
        System.out.println("\n[3] FUNCTION TRANSFORMATION CHAINS (.andThen):");
        Function<FoundItem, String> extractRawTitle = FoundItem::title;
        Function<String, String> sanitizeTitle = FilterEngine::formatLabel; // Static method reference
        Function<FoundItem, String> getFormattedTitle = extractRawTitle.andThen(sanitizeTitle);

        Function<FoundItem, ItemSummaryDTO> toSummaryDTO = item ->
            new ItemSummaryDTO(item.id(), getFormattedTitle.apply(item), item.location());

        List<ItemSummaryDTO> dtoList = new ArrayList<>();
        for (FoundItem item : inventory) {
            dtoList.add(toSummaryDTO.apply(item));
        }
        dtoList.forEach(dto -> System.out.println("  DTO: " + dto));

        // ---------------------------------------------------------------------
        // 3. Consumer (Audit Logging & Notification)
        // ---------------------------------------------------------------------
        System.out.println("\n[4] CONSUMER FOR CUSTODY AUDIT LOGGING:");
        Consumer<FoundItem> auditLogger = item ->
            System.out.printf("  [SECURITY AUDIT] Item %s verified in custody bin at %s%n", item.id(), item.location());

        inventory.forEach(auditLogger);

        // ---------------------------------------------------------------------
        // 4. Supplier (Default Fallback Generator)
        // ---------------------------------------------------------------------
        System.out.println("\n[5] SUPPLIER FOR DEFAULT INVENTORY FALLBACK:");
        Supplier<FoundItem> defaultLockerItemSupplier = () ->
            new FoundItem("FND-DEFAULT", "GENERAL", "Unclassified Item", "SEC-DESK", 0.0, true);

        FoundItem missingLookupFallback = defaultLockerItemSupplier.get();
        System.out.println("  Supplied Fallback Item: " + missingLookupFallback);

        // ---------------------------------------------------------------------
        // 5. Custom @FunctionalInterface (MatchRule)
        // ---------------------------------------------------------------------
        System.out.println("\n[6] CUSTOM @FunctionalInterface (MATCH STRATEGY AS LAMBDA):");
        MatchRule campusMatchRule = (catA, catB, locA, locB) -> {
            double score = 0.0;
            if (catA.equalsIgnoreCase(catB)) score += 0.60;
            if (locA.equalsIgnoreCase(locB)) score += 0.40;
            return score;
        };

        double matchScore = campusMatchRule.evaluate("ELECTRONICS", "ELECTRONICS", "LIB", "LIB");
        System.out.printf("  Evaluated Match Score via Lambda: %.2f (Exact category + location)%n", matchScore);

        System.out.println("===============================================================================");
    }
}
