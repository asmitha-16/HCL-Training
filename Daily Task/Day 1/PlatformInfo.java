/**
 * PlatformInfo.java
 *
 * Day 1 Task: Java Platform Basics
 * -------------------------------------------------------------
 * 1. Queries runtime parameters: java.version, os.name, available processors, max/free heap.
 * 2. Compiles and executes via terminal without IDE:
 *      javac PlatformInfo.java
 *      java PlatformInfo
 * 3. Bytecode disassembly inspected via javap -c:
 *      javap -c PlatformInfo
 * 4. Runtime class loading verified via:
 *      java -verbose:class PlatformInfo
 *
 * -------------------------------------------------------------
 * SAMPLE TERMINAL OUTPUT:
 * -------------------------------------------------------------
 * [1] JAVA ENVIRONMENT
 *   - Java Version        : 25.0.1
 *   - Java Vendor         : Oracle Corporation
 *   - Java Home           : C:\Program Files\Java\jdk-25
 *   - JVM Name            : Java HotSpot(TM) 64-Bit Server VM
 * 
 * [2] OPERATING SYSTEM
 *   - OS Name             : Windows 11
 *   - Architecture        : amd64
 * 
 * [3] HARDWARE CONCURRENCY
 *   - Available Processors: 12 logical core(s)
 * 
 * [4] RUNTIME HEAP MEMORY METRICS
 *   - Max Heap Memory     : 3.84 GB (3928.00 MB) (4118806528 bytes)
 *   - Total Heap Allocated: 248.00 MB (260046848 bytes)
 *   - Free Heap Memory    : 244.85 MB (256747872 bytes)
 *   - Used Heap Memory    : 3.15 MB (3298976 bytes)
 *
 * -------------------------------------------------------------
 * DISASSEMBLED BYTECODE (javap -c PlatformInfo snippet):
 * -------------------------------------------------------------
 *   public static void main(java.lang.String[]);
 *     Code:
 *        0: getstatic     #7   // Field java/lang/System.out:Ljava/io/PrintStream;
 *        3: ldc           #13  // String ===============================================================
 *        5: invokevirtual #15  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
 *       24: ldc           #23  // String java.version
 *       26: invokestatic  #25  // Method java/lang/System.getProperty:(Ljava/lang/String;)Ljava/lang/String;
 *       29: astore_1
 *       68: invokedynamic #39  // InvokeDynamic #0:makeConcatWithConstants
 *       73: invokevirtual #15  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
 */
public class PlatformInfo {

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("             JAVA PLATFORM & RUNTIME ENVIRONMENT INFO          ");
        System.out.println("===============================================================");

        // 1. Java Runtime Environment Specifications
        String javaVersion = System.getProperty("java.version");
        String javaVendor  = System.getProperty("java.vendor");
        String javaHome    = System.getProperty("java.home");
        String vmName      = System.getProperty("java.vm.name");
        String vmVersion   = System.getProperty("java.vm.version");

        System.out.println("\n[1] JAVA ENVIRONMENT");
        System.out.println("  - Java Version        : " + javaVersion);
        System.out.println("  - Java Vendor         : " + javaVendor);
        System.out.println("  - Java Home           : " + javaHome);
        System.out.println("  - JVM Name            : " + vmName);
        System.out.println("  - JVM Version         : " + vmVersion);

        // 2. Underlying Operating System Properties
        String osName    = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String osArch    = System.getProperty("os.arch");

        System.out.println("\n[2] OPERATING SYSTEM");
        System.out.println("  - OS Name             : " + osName);
        System.out.println("  - OS Version          : " + osVersion);
        System.out.println("  - Architecture        : " + osArch);

        // 3. Hardware / Processor Availability
        Runtime runtime = Runtime.getRuntime();
        int processors  = runtime.availableProcessors();

        System.out.println("\n[3] HARDWARE CONCURRENCY");
        System.out.println("  - Available Processors: " + processors + " logical core(s)");

        // 4. JVM Heap Memory Statistics
        long maxMemory   = runtime.maxMemory();   // Maximum heap JVM will attempt to use
        long totalMemory = runtime.totalMemory(); // Currently allocated heap memory
        long freeMemory  = runtime.freeMemory();  // Free memory within total allocated heap
        long usedMemory  = totalMemory - freeMemory;

        System.out.println("\n[4] RUNTIME HEAP MEMORY METRICS");
        System.out.printf("  - Max Heap Memory     : %s (%d bytes)%n", formatBytes(maxMemory), maxMemory);
        System.out.printf("  - Total Heap Allocated: %s (%d bytes)%n", formatBytes(totalMemory), totalMemory);
        System.out.printf("  - Free Heap Memory    : %s (%d bytes)%n", formatBytes(freeMemory), freeMemory);
        System.out.printf("  - Used Heap Memory    : %s (%d bytes)%n", formatBytes(usedMemory), usedMemory);

        System.out.println("===============================================================");
        System.out.println("           EXECUTION COMPLETED SUCCESSFULLY (EXIT CODE 0)      ");
        System.out.println("===============================================================");
    }

    /**
     * Helper utility to convert raw byte counts into human-readable MB / GB.
     */
    private static String formatBytes(long bytes) {
        if (bytes == Long.MAX_VALUE) {
            return "Unlimited";
        }
        double mb = bytes / (1024.0 * 1024.0);
        double gb = mb / 1024.0;
        if (gb >= 1.0) {
            return String.format("%.2f GB (%.2f MB)", gb, mb);
        } else {
            return String.format("%.2f MB", mb);
        }
    }
}
