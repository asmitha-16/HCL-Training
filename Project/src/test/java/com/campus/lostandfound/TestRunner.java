package com.campus.lostandfound;

public class TestRunner {

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("  CAMPUS LOST & FOUND ENTERPRISE PLATFORM — AUTOMATED TEST SUITE");
        System.out.println("===============================================================================");

        int passed = 0;
        int failed = 0;

        try {
            MatchScoringStrategyTest.run();
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] MatchScoringStrategyTest: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        try {
            ItemFactoryTest.run();
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] ItemFactoryTest: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        try {
            ItemStatusSealedTest.run();
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] ItemStatusSealedTest: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        try {
            MatchingPerformanceTest.run();
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] MatchingPerformanceTest: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        try {
            ClaimConcurrencyTest.run();
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] ClaimConcurrencyTest: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        try {
            ItemExpiryTest.run();
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] ItemExpiryTest: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        try {
            RecoveryRateStatsTest.run();
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] RecoveryRateStatsTest: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        try {
            AuditTrailTest.run();
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] AuditTrailTest: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        try {
            FeignClientTest.run();
            passed++;
        } catch (Throwable t) {
            System.err.println("  [FAIL] FeignClientTest: " + t.getMessage());
            t.printStackTrace();
            failed++;
        }

        System.out.println("===============================================================================");
        System.out.println("  TEST SUITE RESULTS: " + passed + " PASSED, " + failed + " FAILED (TOTAL: " + (passed + failed) + ")");
        System.out.println("===============================================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}