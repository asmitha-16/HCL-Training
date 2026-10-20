/**
 * CampusIncidentAnalyser.java
 *
 * Day 2 Hands-on & Project Alignment: Language Fundamentals & Git Fundamentals
 * Project: JFS-40 Campus Lost & Found Portal
 * -----------------------------------------------------------------------------
 * Demonstrates:
 * 1. 8 primitive types applied to campus asset recovery tracking.
 * 2. 1-D Array: 12-month lost item report incident statistics.
 * 3. 2-D Array: Campus building incident matrix (3 major zones x 12 months).
 * 4. Slab constants for recovery efficiency rating (TIER_A, TIER_B, TIER_C).
 * 5. Arithmetic aggregation: Total reports, monthly average with double cast, min/max months.
 * 6. Ternary operator for campus recovery grade.
 * 7. Numeric overflow safety using long accumulator for cumulative valuations.
 * -----------------------------------------------------------------------------
 */
public class CampusIncidentAnalyser {

    // Recovery efficiency tier thresholds (Lost items successfully processed)
    public static final int SLAB_TIER_LOW    = 150; // Incidents
    public static final int SLAB_TIER_MEDIUM = 300;
    public static final int SLAB_TIER_HIGH   = 500;

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("     CAMPUS LOST & FOUND INCIDENT ANALYSER — DAY 2 LANGUAGE FUNDAMENTALS       ");
        System.out.println("     PROJECT: JFS-40 CAMPUS LOST AND FOUND PORTAL                              ");
        System.out.println("===============================================================================");

        // 1. 1-D Array: Campus-wide lost item incidents across 12 months
        int[] monthlyIncidents = { 120, 145, 210, 310, 450, 520, 480, 430, 290, 205, 160, 130 };
        String[] monthNames = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

        long totalAnnualIncidents = 0L;
        int minIncidents = monthlyIncidents[0];
        int maxIncidents = monthlyIncidents[0];
        int minMonthIdx = 0;
        int maxMonthIdx = 0;

        for (int i = 0; i < monthlyIncidents.length; i++) {
            totalAnnualIncidents += monthlyIncidents[i];
            if (monthlyIncidents[i] < minIncidents) {
                minIncidents = monthlyIncidents[i];
                minMonthIdx = i;
            }
            if (monthlyIncidents[i] > maxIncidents) {
                maxIncidents = monthlyIncidents[i];
                maxMonthIdx = i;
            }
        }

        // Explicit widening cast to double for accurate decimal average
        double averageMonthlyIncidents = (double) totalAnnualIncidents / monthlyIncidents.length;

        // Ternary operator for campus operational intensity grade
        char operationalGrade = (averageMonthlyIncidents <= SLAB_TIER_LOW) ? 'A'
                              : (averageMonthlyIncidents <= SLAB_TIER_MEDIUM) ? 'B'
                              : (averageMonthlyIncidents <= SLAB_TIER_HIGH) ? 'C' : 'D';

        System.out.println("\n[1] 12-MONTH CAMPUS LOST & FOUND METRICS (1-D ARRAY)");
        System.out.println("  - Total Annual Incidents   : " + totalAnnualIncidents + " items");
        System.out.printf("  - Monthly Average Incident : %.2f items%n", averageMonthlyIncidents);
        System.out.println("  - Peak Month (Highest)     : " + monthNames[maxMonthIdx] + " (" + maxIncidents + " items reported)");
        System.out.println("  - Lowest Month (Minimum)   : " + monthNames[minMonthIdx] + " (" + minIncidents + " items reported)");
        System.out.println("  - Operational Load Grade   : Tier " + operationalGrade);

        // 2. 2-D Array: Incidents across 3 major campus zones x 12 months
        int[][] buildingIncidents = {
            { 120, 145, 210, 310, 450, 520, 480, 430, 290, 205, 160, 130 }, // Zone 1: Main University Library
            { 310, 340, 390, 450, 600, 680, 640, 590, 410, 350, 320, 305 }, // Zone 2: Student Activity Center & Canteen
            {  90, 105, 130, 180, 240, 290, 270, 230, 160, 120, 100,  95 }  // Zone 3: Engineering & Science Labs
        };
        String[] buildingNames = { "Main University Library", "Student Center & Canteen", "Engineering & Science Labs" };

        System.out.println("\n[2] CAMPUS ZONE INCIDENT MATRIX (2-D ARRAY)");
        System.out.printf("  %-32s %-15s %-15s %-10s%n", "Campus Zone", "Annual Total", "Monthly Mean", "Grade");
        System.out.println("  -----------------------------------------------------------------------------");

        for (int b = 0; b < buildingIncidents.length; b++) {
            long zoneTotal = 0L;
            for (int m = 0; m < buildingIncidents[b].length; m++) {
                zoneTotal += buildingIncidents[b][m];
            }
            double zoneAvg = (double) zoneTotal / buildingIncidents[b].length;
            char grade = (zoneAvg <= SLAB_TIER_LOW) ? 'A'
                       : (zoneAvg <= SLAB_TIER_MEDIUM) ? 'B'
                       : (zoneAvg <= SLAB_TIER_HIGH) ? 'C' : 'D';

            System.out.printf("  %-32s %-15d %-15.2f Tier %s%n", buildingNames[b], zoneTotal, zoneAvg, grade);
        }

        // 3. Precision & Overflow Safety in Campus Asset Valuation
        System.out.println("\n[3] NUMERIC PRECISION & OVERFLOW SAFETY IN ASSET VALUATION");
        int largeAssetValue = Integer.MAX_VALUE; // $2,147,483,647 cents
        int corruptedValue = largeAssetValue + 1000; // Overflows to negative
        long safeAssetValue = (long) largeAssetValue + 1000L; // Safe widened long

        System.out.println("  - Max Integer Boundary     : " + largeAssetValue);
        System.out.println("  - Unchecked (int) overflow : " + corruptedValue + " (CORRUPTED NEGATIVE VALUATION)");
        System.out.println("  - Protected (long) widened : " + safeAssetValue + " (SAFE PRESERVED ASSET VALUE)");
        System.out.println("===============================================================================");
    }
}
