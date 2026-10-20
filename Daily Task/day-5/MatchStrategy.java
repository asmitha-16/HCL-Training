/**
 * MatchStrategy.java
 *
 * Day 5 Project Deliverable: Strategy Pattern for Item Matching (FR3)
 * Project: JFS-40 Campus Lost & Found Portal
 * -----------------------------------------------------------------------------
 * Defines the Strategy interface and two concrete implementations:
 * 1. ExactCategoryMatchStrategy (Fast O(1) binary matching)
 * 2. WeightedMultiFactorMatchStrategy (Multi-tier weighted scoring engine)
 */
public class MatchStrategy {

    // Simple item representation for match calculation
    public record ItemSnapshot(String category, String location, String date, String keywords) {}

    // 1. Common Strategy Interface
    public interface MatchScoringStrategy {
        String getStrategyName();
        double calculateScore(ItemSnapshot lost, ItemSnapshot found);
    }

    // 2. Implementation 1: Exact Category Binary Match Strategy
    public static class ExactCategoryMatchStrategy implements MatchScoringStrategy {
        @Override
        public String getStrategyName() {
            return "Exact Category Binary Strategy";
        }

        @Override
        public double calculateScore(ItemSnapshot lost, ItemSnapshot found) {
            if (lost == null || found == null) return 0.0;
            return lost.category().equalsIgnoreCase(found.category()) ? 1.0 : 0.0;
        }
    }

    // 3. Implementation 2: Weighted Multi-Factor Proximity Strategy (FR3 & Concept Hooks)
    public static class WeightedMultiFactorMatchStrategy implements MatchScoringStrategy {
        private static final double WEIGHT_CATEGORY = 0.35;
        private static final double WEIGHT_LOCATION = 0.25;
        private static final double WEIGHT_DATE     = 0.20;
        private static final double WEIGHT_KEYWORDS = 0.20;

        @Override
        public String getStrategyName() {
            return "Weighted Multi-Factor Proximity Strategy";
        }

        @Override
        public double calculateScore(ItemSnapshot lost, ItemSnapshot found) {
            if (lost == null || found == null) return 0.0;

            double score = 0.0;

            // Category matching (35%)
            if (lost.category().equalsIgnoreCase(found.category())) {
                score += WEIGHT_CATEGORY;
            }

            // Location matching (25%)
            if (lost.location().equalsIgnoreCase(found.location())) {
                score += WEIGHT_LOCATION;
            }

            // Date matching (20%) - simplified date string exactness or close check
            if (lost.date().equalsIgnoreCase(found.date())) {
                score += WEIGHT_DATE;
            } else {
                score += (WEIGHT_DATE * 0.5); // Partial credit for close dates
            }

            // Keyword token matching (20%)
            String[] lostWords = lost.keywords().toLowerCase().split("\\s+");
            String foundKeywords = found.keywords().toLowerCase();
            int matchedWords = 0;
            for (String word : lostWords) {
                if (foundKeywords.contains(word)) {
                    matchedWords++;
                }
            }
            double keywordRatio = (lostWords.length > 0) ? (double) matchedWords / lostWords.length : 0.0;
            score += (keywordRatio * WEIGHT_KEYWORDS);

            return Math.min(1.0, score);
        }
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("         STRATEGY DESIGN PATTERN — RUNTIME INTERCHANGEABILITY DEMO             ");
        System.out.println("===============================================================================");

        ItemSnapshot lost = new ItemSnapshot("ELECTRONICS", "LIB", "2026-10-18", "Apple MacBook Pro silver");
        ItemSnapshot found = new ItemSnapshot("ELECTRONICS", "LIB", "2026-10-18", "MacBook Pro laptop in grey case");

        // Client code depends on the interface, allowing dynamic strategy switching
        MatchScoringStrategy strategy1 = new ExactCategoryMatchStrategy();
        MatchScoringStrategy strategy2 = new WeightedMultiFactorMatchStrategy();

        System.out.printf("Strategy 1: %-38s | Computed Score: %.2f%n",
                strategy1.getStrategyName(), strategy1.calculateScore(lost, found));
        System.out.printf("Strategy 2: %-38s | Computed Score: %.2f%n",
                strategy2.getStrategyName(), strategy2.calculateScore(lost, found));
        System.out.println("===============================================================================");
    }
}
