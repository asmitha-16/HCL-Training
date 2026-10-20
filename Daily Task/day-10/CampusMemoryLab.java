import java.util.ArrayList;
import java.util.List;

/**
 * CampusMemoryLab.java
 *
 * Day 10 Hands-on & Project Alignment: JVM Memory Management & Pitfalls
 * Project: JFS-40 Campus Lost & Found Portal
 * -----------------------------------------------------------------------------
 * Demonstrates 5 core JVM memory experiments applied to Campus Lost & Found:
 *   Experiment 1: StackOverflowError depth during recursive category tree traversal.
 *   Experiment 2: Heap memory boundary check for 10,000 active lost & found items.
 *   Experiment 3: String += vs. StringBuilder in generating match summary reports.
 *   Experiment 4: Garbage Collection telemetry when purging expired custody items.
 *   Experiment 5: Integer cache (-128 to 127) identity hazard on locker bin IDs.
 * -----------------------------------------------------------------------------
 */
public class CampusMemoryLab {

    private static int categoryRecursionDepth = 0;

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("     CAMPUS LOST & FOUND MEMORY LAB — DAY 10 MEMORY ARCHITECTURE               ");
        System.out.println("     PROJECT: JFS-40 CAMPUS LOST AND FOUND PORTAL                              ");
        System.out.println("===============================================================================");

        runExperiment1CategoryStackOverflow();
        runExperiment2LockerHeapCapacity();
        runExperiment3MatchReportStringBenchmark();
        runExperiment4CustodyGarbageCollection();
        runExperiment5LockerIdCacheHazard();

        System.out.println("===============================================================================");
    }

    // -------------------------------------------------------------------------
    // Experiment 1: StackOverflowError Depth (Call Stack Limits)
    // -------------------------------------------------------------------------
    private static void runExperiment1CategoryStackOverflow() {
        System.out.println("\n[EXPERIMENT 1] CALL STACK DEPTH DURING RECURSIVE CATEGORY RESOLUTION");
        try {
            traverseSubCategories(1);
        } catch (StackOverflowError err) {
            System.out.println("  [CAUGHT StackOverflowError] Thread stack limit reached at frame depth: " + categoryRecursionDepth);
            System.out.println("  Analysis: Each recursive subcategory lookup allocates a Stack Frame (parameters,");
            System.out.println("  local references, return address). Deep or cyclic taxonomies exhaust thread stack (-Xss).");
        }
    }

    private static void traverseSubCategories(int depth) {
        categoryRecursionDepth = depth;
        traverseSubCategories(depth + 1);
    }

    // -------------------------------------------------------------------------
    // Experiment 2: Heap Memory Sizing for 10,000 Campus Custody Items
    // -------------------------------------------------------------------------
    private static void runExperiment2LockerHeapCapacity() {
        System.out.println("\n[EXPERIMENT 2] HEAP ALLOCATION & OOM BOUNDARY AWARENESS (10,000 ITEMS)");
        Runtime rt = Runtime.getRuntime();
        long initialFree = rt.freeMemory();
        List<byte[]> custodyPayloads = new ArrayList<>();

        try {
            // Allocate 10,000 simulated item representations (each ~ 1.5 KB payload)
            for (int i = 0; i < 10_000; i++) {
                custodyPayloads.add(new byte[1536]); // 1.5 KB per item record
            }
            long postAllocFree = rt.freeMemory();
            long memoryUsed = initialFree - postAllocFree;
            System.out.printf("  Allocated 10,000 custody item working set. Memory consumed: %d MB (%d bytes)%n",
                    memoryUsed / (1024 * 1024), memoryUsed);
            System.out.printf("  Free Heap remaining: %d MB. Heap saturation ratio: < 1.0%%%n",
                    postAllocFree / (1024 * 1024));
        } catch (OutOfMemoryError oom) {
            System.err.println("  [CAUGHT OutOfMemoryError] Heap limit (-Xmx) exceeded during custody intake!");
        } finally {
            custodyPayloads.clear(); // Release references for GC reclaim
            System.out.println("  Released 10,000 references. Memory eligible for Garbage Collector sweep.");
        }
    }

    // -------------------------------------------------------------------------
    // Experiment 3: String += vs. StringBuilder in Match Report Generation
    // -------------------------------------------------------------------------
    private static void runExperiment3MatchReportStringBenchmark() {
        System.out.println("\n[EXPERIMENT 3] STRING += VS. STRINGBUILDER IN MATCH DIGEST GENERATION (15,000 ROWS)");
        int itemsCount = 15_000;

        // String += concatenation (Creates new immutable String on Heap every iteration)
        long startStr = System.nanoTime();
        String reportStr = "";
        for (int i = 0; i < itemsCount; i++) {
            reportStr += "MATCH:[LOST-101<->FND-201];";
        }
        long durationStrMs = (System.nanoTime() - startStr) / 1_000_000;

        // StringBuilder append (Mutates internal buffer, zero Heap garbage churn)
        long startSb = System.nanoTime();
        StringBuilder reportSb = new StringBuilder(itemsCount * 28);
        for (int i = 0; i < itemsCount; i++) {
            reportSb.append("MATCH:[LOST-101<->FND-201];");
        }
        long durationSbMs = (System.nanoTime() - startSb) / 1_000_000;

        System.out.printf("  - String += Concatenation : %4d ms (O(N^2) memory reallocation)%n", durationStrMs);
        System.out.printf("  - StringBuilder.append()  : %4d ms (O(N) in-place buffer)%n", durationSbMs);
        double speedup = (durationSbMs > 0) ? (double) durationStrMs / durationSbMs : durationStrMs;
        System.out.printf("  - Performance Improvement : %.1fx faster with StringBuilder%n", speedup);
    }

    // -------------------------------------------------------------------------
    // Experiment 4: Garbage Collection Telemetry (Custody Cleanup)
    // -------------------------------------------------------------------------
    private static void runExperiment4CustodyGarbageCollection() {
        System.out.println("\n[EXPERIMENT 4] GARBAGE COLLECTION TELEMETRY (PURGING EXPIRED CUSTODY CACHE)");
        Runtime rt = Runtime.getRuntime();

        long beforeGcFree = rt.freeMemory();
        System.gc(); // Request JVM Garbage Collector sweep
        long afterGcFree = rt.freeMemory();
        long reclaimedBytes = afterGcFree - beforeGcFree;

        System.out.printf("  - Heap Total Allocated    : %s%n", formatBytes(rt.totalMemory()));
        System.out.printf("  - Heap Free Before GC     : %s%n", formatBytes(beforeGcFree));
        System.out.printf("  - Heap Free After GC      : %s%n", formatBytes(afterGcFree));
        System.out.printf("  - Memory Reclaimed by GC  : %s%n", formatBytes(Math.max(0, reclaimedBytes)));
    }

    // -------------------------------------------------------------------------
    // Experiment 5: Integer Cache Identity Hazard on Locker Bins
    // -------------------------------------------------------------------------
    private static void runExperiment5LockerIdCacheHazard() {
        System.out.println("\n[EXPERIMENT 5] INTEGER CACHE IDENTITY HAZARD ON LOCKER BIN NUMBERS");

        // Locker bins within cache range [-128, 127] -> Reuses Flyweight objects
        Integer binA = 127;
        Integer binB = 127;
        boolean withinCacheSameRef = (binA == binB);

        // Locker bins outside cache range [>= 128] -> Allocates distinct Heap objects
        Integer lockerX = 128;
        Integer lockerY = 128;
        boolean outsideCacheSameRef = (lockerX == lockerY);
        boolean outsideCacheEquals = lockerX.equals(lockerY);

        System.out.println("  Locker Bin A = 127, Bin B = 127;");
        System.out.println("    binA == binB            : " + withinCacheSameRef + " (Points to identical Flyweight instance)");
        System.out.println("  Locker Bin X = 128, Bin Y = 128;");
        System.out.println("    lockerX == lockerY (==) : " + outsideCacheSameRef + " (HAZARD! Returns false due to separate Heap objects)");
        System.out.println("    lockerX.equals(lockerY) : " + outsideCacheEquals + " (CORRECT! Compares bin numeric equality)");
    }

    private static String formatBytes(long bytes) {
        return String.format("%.2f MB (%d bytes)", bytes / (1024.0 * 1024.0), bytes);
    }
}
