import java.util.*;

/**
 * CampusFunctionalApp.java
 *
 * Day 13 Project Deliverable: Refactoring Legacy OOP to Functional Lambdas
 * Project: JFS-40 Campus Lost & Found Portal
 * -----------------------------------------------------------------------------
 * Demonstrates:
 * 1. Replacing legacy Anonymous Inner Classes with concise Lambdas.
 * 2. Replacing verbose If-Ladders with composable Predicates and Functions.
 * 3. Expressing MatchScoringStrategy as an inline Functional Lambda.
 * 4. Utilizing Method References across sorting and printing pipelines.
 * -----------------------------------------------------------------------------
 */
public class CampusFunctionalApp {

    public record SimpleIncident(String id, String category, String title, double value) {}

    // Legacy Strategy Interface
    @FunctionalInterface
    public interface SimpleStrategy {
        boolean isHighRisk(SimpleIncident incident);
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("   REFACTORING ANONYMOUS CLASSES TO LAMBDAS — DAY 13 PROJECT ALIGNMENT         ");
        System.out.println("   PROJECT: JFS-40 CAMPUS LOST AND FOUND PORTAL                                ");
        System.out.println("===============================================================================");

        List<SimpleIncident> incidents = Arrays.asList(
            new SimpleIncident("INC-01", "ELECTRONICS", "MacBook Air", 999.00),
            new SimpleIncident("INC-02", "KEYS", "Room 402 Key", 15.00),
            new SimpleIncident("INC-03", "ELECTRONICS", "iPhone 15 Pro", 1199.00),
            new SimpleIncident("INC-04", "WALLET", "Leather Wallet", 50.00)
        );

        // BEFORE (Legacy Java 7 Anonymous Inner Class):
        SimpleStrategy legacyStrategy = new SimpleStrategy() {
            @Override
            public boolean isHighRisk(SimpleIncident incident) {
                return incident.value() > 500.00;
            }
        };

        // AFTER (Modern Java 8-21 Lambda Expression):
        SimpleStrategy modernLambdaStrategy = incident -> incident.value() > 500.00;

        System.out.println("\n[1] ANONYMOUS CLASS REPLACED BY LAMBDA:");
        for (SimpleIncident inc : incidents) {
            if (modernLambdaStrategy.isHighRisk(inc)) {
                System.out.printf("  * High-Value Asset Flagged: %s (%s) - $%.2f%n",
                        inc.id(), inc.title(), inc.value());
            }
        }

        // BEFORE: Verbose Comparator
        // Collections.sort(incidents, new Comparator<SimpleIncident>() { ... });

        // AFTER: Comparator.comparing() with method reference
        List<SimpleIncident> sortedIncidents = new ArrayList<>(incidents);
        sortedIncidents.sort(Comparator.comparing(SimpleIncident::value).reversed());

        System.out.println("\n[2] COMPARATOR CHAIN VIA METHOD REFERENCES:");
        sortedIncidents.forEach(inc ->
            System.out.printf("  - $%-8.2f : %s (%s)%n", inc.value(), inc.title(), inc.id())
        );

        System.out.println("===============================================================================");
    }
}
