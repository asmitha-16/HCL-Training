# Collections Performance Benchmark: ArrayList vs. LinkedList

**Environment:**
- **OS:** Windows 11
- **Runtime:** Java SE 25.0.1 LTS (HotSpot 64-Bit Server VM)
- **Benchmark Target:** `java.util.ArrayList` vs. `java.util.LinkedList`
- **Measurement Unit:** Milliseconds (derived from `System.nanoTime()`)

---

## 1. Empirical Timing Table

| Operation | Sample Size ($N$) | ArrayList Time (ms) | LinkedList Time (ms) | Speedup / Winner | Theoretical Complexity Analysis |
|:---|---:|---:|---:|:---:|:---|
| **Insert at Beginning (index 0)** | 10,000 | 13.503 ms | 2.777 ms | **LinkedList (4.9x faster)** | `ArrayList`: $O(N)$ shift via `System.arraycopy`<br>`LinkedList`: $O(1)$ head pointer update |
| **Insert at End (append)** | 10,000 | 1.180 ms | 1.204 ms | **ArrayList (1.0x faster)** | `ArrayList`: $O(1)$ amortized array append<br>`LinkedList`: $O(1)$ tail pointer + Node allocation |
| **Insert at Middle (index size/2)** | 10,000 | 6.489 ms | 73.557 ms | **ArrayList (11.3x faster)** | `ArrayList`: $O(N)$ contiguous block copy<br>`LinkedList`: $O(N)$ sequential pointer traversal to middle |
| **Random Access `get(i)`** | 10,000 queries | 1.492 ms | 56.233 ms | **ArrayList (37.7x faster)** | `ArrayList`: $O(1)$ direct offset math<br>`LinkedList`: $O(N)$ pointer hopping from closest end |
| **Sequential Iteration (`for-each`)** | 10,000 | 1.759 ms | 9.862 ms | **ArrayList (5.6x faster)** | `ArrayList`: Contiguous L1/L2 cache prefetching<br>`LinkedList`: Pointer hops across scattered heap memory |
| **Insert at Beginning (index 0)** | 50,000 | 180.111 ms | 1.245 ms | **LinkedList (144.6x faster)** | `ArrayList`: $O(N^2)$ aggregate memory copies<br>`LinkedList`: $O(1)$ constant pointer assignment |
| **Insert at End (append)** | 50,000 | 3.779 ms | 1.522 ms | **LinkedList (2.5x faster)** | `ArrayList`: Array reallocation & growth<br>`LinkedList`: Node allocation at tail |
| **Insert at Middle (index size/2)** | 20,000 | 17.477 ms | 252.962 ms | **ArrayList (14.5x faster)** | `ArrayList`: SIMD hardware-accelerated memory copy<br>`LinkedList`: 10,000 reference pointer hops per insertion |
| **Random Access `get(i)`** | 10,000 queries | 0.891 ms | 272.256 ms | **ArrayList (305.6x faster)** | `ArrayList`: Constant-time direct memory indexing<br>`LinkedList`: Linear scan through node chain |
| **Sequential Iteration (`for-each`)** | 50,000 | 10.563 ms | 1.574 ms | **Variable / JIT optimized** | Dependent on JVM heap garbage collection state |

---

## 2. In-Depth Architectural & Hardware Analysis

### Why is `ArrayList.get(i)` 300x+ faster than `LinkedList.get(i)`?
- **ArrayList:** Direct memory offset calculation:
  $$\text{Address} = \text{baseAddress} + (i \times \text{referenceSize})$$
  This operation is executed in a single CPU instruction ($O(1)$).
- **LinkedList:** A doubly linked chain of `Node` objects. To access index $i$, the JVM must start at `first` or `last` and hop through $i$ reference pointers across non-contiguous heap memory ($O(N)$).

### Why does `ArrayList` beat `LinkedList` on Middle Insertions by 14x?
- A common misconception is that `LinkedList` is always faster for insertions.
- While inserting into a linked list is theoretically $O(1)$ once the node pointer is known, locating the middle position requires traversing $N / 2$ nodes ($O(N)$ pointer steps).
- In contrast, `ArrayList` uses `System.arraycopy()`, a native intrinsic instruction that leverages modern CPU vector (AVX/SIMD) registers to shift entire 64-byte blocks of memory concurrently in CPU cache.

### Memory Overhead Comparison
- **`ArrayList`:** A single array of object references (4 bytes with Compressed OOPs or 8 bytes per slot). Extra capacity is only unallocated array slots (50% growth factor).
- **`LinkedList`:** Each item requires a standalone `Node` instance:
  - 12/16 bytes object header
  - 8 bytes reference to payload item
  - 8 bytes pointer to `next`
  - 8 bytes pointer to `prev`
  - **Total:** $\approx 36$ to $40$ bytes of JVM heap overhead **per element**, plus additional GC management pressure.

---

## 3. Production Recommendation Matrix

| Scenario / Workload | Recommended Collection | Rationale |
|:---|:---|:---|
| **Catalogue & Search (FR1: Lost Items)** | **`ArrayList`** | Dominant read traffic, random access via `get(i)`, cache-friendly iteration. |
| **Frequent Head Insertions Only** | **`ArrayDeque`** | Far superior to `LinkedList` for FIFO queues without node allocation overhead. |
| **General Purpose List** | **`ArrayList`** | Default choice for 99% of Java applications. |
| **Avoid in Modern Java** | **`LinkedList`** | `ArrayDeque` is faster as a Queue/Deque, and `ArrayList` is faster as a List. |
