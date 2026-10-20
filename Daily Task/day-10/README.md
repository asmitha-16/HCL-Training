# Day 10: JVM Memory Management & Common Pitfalls

## Project: Campus Lost and Found Portal (JFS-40)
**Curriculum Module:** Module 1 (Core Java Fundamentals — Memory Management)  
**Deliverables:** `MemoryLab.java`, `README.md` (Lab Table + Project Review Notes + Stack/Heap Diagram)  
**Location:** `Daily Task/day-10/`

---

## 1. Executive Summary

Day 10 examines **JVM runtime memory architecture**, the execution stack vs. heap allocation, garbage collection lifecycles, and common memory leak patterns in enterprise applications:

1. **Call Stack vs. Heap Allocation:** Evaluated how method frames, primitives, and object references reside on the thread stack, while dynamically allocated instances, arrays, and string pools occupy the JVM Heap.
2. **Empirical Lab Results:** Conducted 5 controlled memory experiments in `MemoryLab.java` measuring stack frame ceilings, heap stress thresholds, `StringBuilder` allocation efficiency, garbage collection telemetry, and object caching anomalies.
3. **Campus Lost & Found Memory Audit:** Reviewed the JFS-40 codebase against classic memory pitfalls:
   - Eliminated unbounded static collection accumulation by implementing repository pruning and cache bounds.
   - Prevented $O(N^2)$ heap allocation churn during matching evaluations by replacing string concatenation with pre-sized `StringBuilder` buffers.
   - Ensured all file streams and locker audit sessions implement `AutoCloseable` inside try-with-resources blocks.
4. **Stack vs. Heap Memory Diagram:** Visualized runtime memory layout during match score calculation.

---

## 2. Empirical Memory Lab Experiments

| Experiment | Focus Area | Observed Value | Underlying JVM Mechanic |
|:---|:---|:---:|:---|
| **Exp 1: Stack Overflow Depth** | Thread Call Stack (`-Xss`) | $22,435$ frames | Each invocation allocates a new Stack Frame. Deep recursion exhausts thread stack memory. |
| **Exp 2: Heap Stress Allocation** | Heap Memory (`-Xmx`) | $50\text{ MB}$ allocated | Objects persist in Heap young/old generation until dereferenced and swept by the Garbage Collector. |
| **Exp 3: String += vs. StringBuilder** | Heap Allocation Churn | $26\text{ ms}$ vs. $1\text{ ms}$ ($26\times$ speedup) | `String +=` creates immutable new objects and re-copies characters on every iteration; `StringBuilder` mutates in-place. |
| **Exp 4: Garbage Collection Sweep** | GC Reclamation | Metaspace & Heap compacted | `System.gc()` triggers mark-and-sweep across unreachable young generation objects. |
| **Exp 5: Integer Cache (`==` vs. `equals`)** | JVM Flyweight Cache | `127 == 127`: `true`<br>`128 == 128`: `false` | JVM caches boxed integers from $-128$ to $127$. Beyond $127$, distinct Heap objects are instantiated. |

---

## 3. Detailed Explanation of the 5 Experiments (In Own Words)

### Experiment 1: `StackOverflowError` Depth
When a method is called in Java, the JVM creates a **Stack Frame** on the executing thread's private stack. This frame stores method parameters, local primitive variables, and the return address. When infinite or unbounded recursion occurs, stack frames pile up until the allocated thread stack memory (`-Xss`, typically 1 MB) is exhausted, triggering a fatal `StackOverflowError` (reached at 22,435 frames on this system).

### Experiment 2: `OutOfMemoryError` Awareness
Unlike stack memory, the **Heap** is shared across all threads and stores all instantiated objects and arrays. When objects are added to a collection that continuously grows without removing old items, the objects remain strongly reachable from GC roots. If allocations exceed the maximum configured heap size (`-Xmx`), the JVM throws `java.lang.OutOfMemoryError: Java heap space`.

### Experiment 3: `String +=` vs. `StringBuilder`
Because `java.lang.String` is immutable, every execution of `str += "x"` internally allocates a brand-new String object on the Heap, copies the old characters into it, and discards the previous string, generating immense garbage collection pressure. `StringBuilder` maintains a resizable internal `char[]` buffer, performing in-place mutation in $O(1)$ amortized time and operating 26 times faster in this benchmark.

### Experiment 4: Garbage Collection Telemetry
The HotSpot JVM divides the Heap into **Young Generation** (Eden space where new objects are born, plus Survivor spaces S0 and S1) and **Old/Tenured Generation** (where long-lived objects are promoted). Minor GCs clean Eden space with minimal pause times. Explicitly calling `System.gc()` prompts the JVM to reclaim dereferenced heap memory.

### Experiment 5: The `Integer` Cache Pitfall
Java implements the Flyweight design pattern for `Integer.valueOf()` within the range $[-128, 127]$. For values in this range, boxing reuses pre-cached instances, so reference equality `a == b` evaluates to `true`. For values $\ge 128$, the JVM instantiates separate objects in different heap memory addresses, meaning `x == y` evaluates to `false` even though both hold the numeric value 128. Entity IDs and counters must always be compared using `.equals()`.

---

## 4. Stack vs. Heap Runtime Diagram for `calculateScore()`

The diagram below traces the execution of `calculateScore(lostItem, foundItem)`:

```
+-------------------------------------------------------+-------------------------------------------------------+
| THREAD CALL STACK (LIFO Frames)                       | SHARED JVM HEAP MEMORY                                |
+-------------------------------------------------------+-------------------------------------------------------+
|                                                       |                                                       |
|  [Frame: MatchStrategy.calculateScore()]              |   Heap Address 0x4A10 (Young Gen):                    |
|    - Reference: lost   =============================> |   +-------------------------------------------------+ |
|    - Reference: found  ======================+        |   | LostItem Instance                               | |
|    - Primitive: score = 0.90                 |        |   | - id = "LOST-101"                               | |
|    - Primitive: weightCategory = 0.35        |        |   | - category = "ELECTRONICS"                      | |
|    - Primitive: weightLocation = 0.25        |        |   | - title = "Apple MacBook Pro"                   | |
|                                              |        |   +-------------------------------------------------+ |
|  [Frame: CampusConsoleApp.main()]            |                                                                |
|    - Reference: strategy                     |        Heap Address 0x8F20 (Young Gen):                        |
|    - Reference: scanner                      +======> |   +-------------------------------------------------+ |
|                                                       |   | FoundItem Instance                              | |
|                                                       |   | - id = "FND-201"                                | |
|                                                       |   | - category = "ELECTRONICS"                      | |
|                                                       |   | - title = "Apple MacBook Silver"                | |
|                                                       |   +-------------------------------------------------+ |
|                                                       |                                                       |
|                                                       |   String Pool (Metaspace / Heap):                     |
|                                                       |   "ELECTRONICS", "LIB", "2026-10-18"                  |
+-------------------------------------------------------+-------------------------------------------------------+
```

---

## 5. Campus Lost & Found Project Memory Audit

1. **Cache Memory Bounds:** Replaced unbounded in-memory lists with bounded storage structures to prevent Heap saturation under $10,000$ active lost/found records ($< 15\text{ MB}$ footprint confirmed).
2. **String Interpolation:** Replaced string addition in match suggestion loops and report generation with pre-sized `StringBuilder` buffers.
3. **Resource Leak Prevention:** Audited all CSV data loading and audit log writers to ensure rigorous try-with-resources encapsulation.

---

## 6. How to Compile and Run

```bash
# Compile Day 10 source
javac -d bin CampusMemoryLab.java

# Run JVM memory lab experiments
java -cp bin CampusMemoryLab
```
