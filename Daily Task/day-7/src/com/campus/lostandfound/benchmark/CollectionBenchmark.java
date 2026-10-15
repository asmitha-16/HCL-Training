package com.campus.lostandfound.benchmark;

import java.util.*;

/**
 * ============================================================================
 * Benchmark: CollectionBenchmark
 * ============================================================================
 * High-precision benchmarking harness comparing java.util.ArrayList vs
 * java.util.LinkedList for Campus Lost and Found entity catalogues:
 *
 * 1. Insert at Beginning (index 0): add(0, element)
 * 2. Insert at Middle (index size/2): add(size/2, element)
 * 3. Insert at End (append): add(element)
 * 4. Random Access: get(randomIndex)
 * 5. Sequential Traversal (for-each iteration)
 *
 * ----------------------------------------------------------------------------
 * EMPIRICAL VS THEORETICAL ANALYSIS:
 * ----------------------------------------------------------------------------
 * - Head Insertion (index 0): LinkedList wins for large N because it only updates
 *   head pointers (O(1)), whereas ArrayList must shift all N elements using System.arraycopy (O(N)).
 * - Tail Insertion (append): ArrayList wins because appending is amortized O(1) into
 *   a contiguous array without allocating any new objects. LinkedList must allocate a new
 *   Node(prev, item, next) object for EVERY element, triggering garbage collection churn.
 * - Random Access get(i): ArrayList dominates (O(1) direct memory pointer math: base + i * 4/8 bytes).
 *   LinkedList takes O(N) because it must traverse sequentially from head or tail to index i.
 * - Sequential Traversal: ArrayList wins by 3x-6x due to CPU hardware cache spatial locality
 *   (L1/L2 cache line pre-fetching). LinkedList causes CPU cache misses on every pointer hop.
 * ============================================================================
 */
public class CollectionBenchmark {

    public static class BenchmarkResult {
        public final String operation;
        public final int sampleSize;
        public final double arrayListMs;
        public final double linkedListMs;
        public final String winner;
        public final String complexityNotes;

        public BenchmarkResult(String operation, int sampleSize, double arrayListMs,
                               double linkedListMs, String winner, String complexityNotes) {
            this.operation = operation;
            this.sampleSize = sampleSize;
            this.arrayListMs = arrayListMs;
            this.linkedListMs = linkedListMs;
            this.winner = winner;
            this.complexityNotes = complexityNotes;
        }
    }

    public static List<BenchmarkResult> runBenchmarkSuite(int[] sampleSizes) {
        warmupJvm();
        List<BenchmarkResult> results = new ArrayList<>();

        for (int n : sampleSizes) {
            // 1. Insert at Beginning (index 0)
            double alInsertHead = benchmarkInsertBeginning(new ArrayList<>(), n);
            double llInsertHead = benchmarkInsertBeginning(new LinkedList<>(), n);
            results.add(new BenchmarkResult(
                    "Insert at Beginning (index 0)",
                    n, alInsertHead, llInsertHead,
                    alInsertHead < llInsertHead ? "ArrayList" : "LinkedList",
                    "AL: O(N) arraycopy shift | LL: O(1) head pointer update"
            ));

            // 2. Insert at End (append)
            double alInsertTail = benchmarkInsertEnd(new ArrayList<>(), n);
            double llInsertTail = benchmarkInsertEnd(new LinkedList<>(), n);
            results.add(new BenchmarkResult(
                    "Insert at End (append)",
                    n, alInsertTail, llInsertTail,
                    alInsertTail < llInsertTail ? "ArrayList" : "LinkedList",
                    "AL: O(1) amortized array append | LL: O(1) tail node alloc"
            ));

            // 3. Insert at Middle (index size / 2)
            int midN = Math.min(n, 20000); // capped at 20k to avoid extreme N^2 execution times in LinkedList
            double alInsertMid = benchmarkInsertMiddle(new ArrayList<>(), midN);
            double llInsertMid = benchmarkInsertMiddle(new LinkedList<>(), midN);
            results.add(new BenchmarkResult(
                    "Insert at Middle (index size/2)",
                    midN, alInsertMid, llInsertMid,
                    alInsertMid < llInsertMid ? "ArrayList" : "LinkedList",
                    "AL: O(N) arraycopy shift | LL: O(N) pointer seek to mid"
            ));

            // 4. Random Access get(index)
            List<Integer> alPopulated = new ArrayList<>();
            List<Integer> llPopulated = new LinkedList<>();
            for (int i = 0; i < n; i++) {
                alPopulated.add(i);
                llPopulated.add(i);
            }
            int getQueries = Math.min(n, 10000);
            double alGet = benchmarkRandomAccess(alPopulated, getQueries, n);
            double llGet = benchmarkRandomAccess(llPopulated, getQueries, n);
            results.add(new BenchmarkResult(
                    "Random Access get(i) [" + getQueries + " ops]",
                    n, alGet, llGet,
                    alGet < llGet ? "ArrayList" : "LinkedList",
                    "AL: O(1) direct address math | LL: O(N) traversal from end"
            ));

            // 5. Sequential Traversal (for-each)
            double alIter = benchmarkSequentialIteration(alPopulated);
            double llIter = benchmarkSequentialIteration(llPopulated);
            results.add(new BenchmarkResult(
                    "Sequential Iteration (for-each)",
                    n, alIter, llIter,
                    alIter < llIter ? "ArrayList" : "LinkedList",
                    "AL: L1/L2 CPU cache prefetching | LL: Heap pointer hopping"
            ));
        }

        return results;
    }

    private static void warmupJvm() {
        List<Integer> al = new ArrayList<>();
        List<Integer> ll = new LinkedList<>();
        for (int i = 0; i < 5000; i++) {
            al.add(i);
            ll.add(i);
        }
        for (int i = 0; i < 1000; i++) {
            al.get(i);
            ll.get(i);
        }
    }

    private static double benchmarkInsertBeginning(List<Integer> list, int n) {
        System.gc();
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            list.add(0, i);
        }
        long duration = System.nanoTime() - start;
        return duration / 1_000_000.0;
    }

    private static double benchmarkInsertEnd(List<Integer> list, int n) {
        System.gc();
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            list.add(i);
        }
        long duration = System.nanoTime() - start;
        return duration / 1_000_000.0;
    }

    private static double benchmarkInsertMiddle(List<Integer> list, int n) {
        System.gc();
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            list.add(list.size() / 2, i);
        }
        long duration = System.nanoTime() - start;
        return duration / 1_000_000.0;
    }

    private static double benchmarkRandomAccess(List<Integer> list, int queryCount, int listSize) {
        Random random = new Random(42);
        int[] indices = new int[queryCount];
        for (int i = 0; i < queryCount; i++) {
            indices[i] = random.nextInt(listSize);
        }

        System.gc();
        long start = System.nanoTime();
        long dummySum = 0;
        for (int idx : indices) {
            dummySum += list.get(idx);
        }
        long duration = System.nanoTime() - start;
        if (dummySum == 42) System.out.print("");
        return duration / 1_000_000.0;
    }

    private static double benchmarkSequentialIteration(List<Integer> list) {
        System.gc();
        long start = System.nanoTime();
        long dummySum = 0;
        for (Integer val : list) {
            dummySum += val;
        }
        long duration = System.nanoTime() - start;
        if (dummySum == 42) System.out.print("");
        return duration / 1_000_000.0;
    }

    public static String formatResultsAsMarkdown(List<BenchmarkResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("| Operation | Sample Size (N) | ArrayList (ms) | LinkedList (ms) | Speedup / Winner | Theoretical Complexity Analysis |\n");
        sb.append("|:---|---:|---:|---:|:---:|:---|\n");

        for (BenchmarkResult r : results) {
            double ratio;
            String speedup;
            if (r.arrayListMs <= r.linkedListMs) {
                ratio = r.linkedListMs / Math.max(r.arrayListMs, 0.0001);
                speedup = String.format("**ArrayList** (%.1fx faster)", ratio);
            } else {
                ratio = r.arrayListMs / Math.max(r.linkedListMs, 0.0001);
                speedup = String.format("**LinkedList** (%.1fx faster)", ratio);
            }

            sb.append(String.format("| %s | %,d | %.3f ms | %.3f ms | %s | %s |\n",
                    r.operation, r.sampleSize, r.arrayListMs, r.linkedListMs, speedup, r.complexityNotes));
        }

        return sb.toString();
    }

    public static void printConsoleTable(List<BenchmarkResult> results) {
        System.out.println("==========================================================================================================================");
        System.out.printf("%-34s | %-12s | %-15s | %-15s | %-22s\n",
                "Operation", "Sample Size", "ArrayList (ms)", "LinkedList (ms)", "Winner / Speedup");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------");

        for (BenchmarkResult r : results) {
            double ratio;
            String speedup;
            if (r.arrayListMs <= r.linkedListMs) {
                ratio = r.linkedListMs / Math.max(r.arrayListMs, 0.0001);
                speedup = String.format("ArrayList (%.1fx)", ratio);
            } else {
                ratio = r.arrayListMs / Math.max(r.linkedListMs, 0.0001);
                speedup = String.format("LinkedList (%.1fx)", ratio);
            }

            System.out.printf("%-34s | %,12d | %12.3f ms | %12.3f ms | %-22s\n",
                    r.operation, r.sampleSize, r.arrayListMs, r.linkedListMs, speedup);
        }
        System.out.println("==========================================================================================================================");
    }

    public static void main(String[] args) {
        System.out.println("Running Collections Framework Benchmark Suite (ArrayList vs LinkedList)...");
        int[] sizes = {10000, 50000};
        List<BenchmarkResult> results = runBenchmarkSuite(sizes);
        printConsoleTable(results);
    }
}
