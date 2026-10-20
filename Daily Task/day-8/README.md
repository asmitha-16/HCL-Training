# Day 8: Collections Framework — Part 2

## Project: Campus Lost and Found Portal
**Module:** Collections Framework — Part 2 (Maps, Comparable & Comparator, HashMap vs. TreeMap, equals/hashCode Contract, FR4–FR6)  
**Deliverables:** CampusDirectory code + Text Report + Missing-hashCode Bug Demo + Sorted TreeMap Reports  
**Location:** `Daily Task/day-8/`

---

## 1. Executive Summary

Day 8 transitions the **Campus Lost and Found Portal** from sequential list searches to instant $O(1)$ key-based `Map` architectures, implements analytical frequency counting via `Map.merge()`, creates sorted institutional reports with `TreeMap`, constructs chained multi-tier `Comparator` pipelines, and resolves the subtle `equals()`/`hashCode()` contract violation bug.

### Core Deliverables Implemented:
1. **$O(1)$ Map Lookups:** Replaced $O(N)$ linear scans with `HashMap<String, LostItem>`, `HashMap<String, FoundItem>`, and `HashMap<String, Claim>`.
2. **Frequency Distribution via `Map.merge()`:** Aggregates item category and location distributions cleanly without boilerplate checks.
3. **`TreeMap` Analytical Reports:** Groups inventory and computes department asset valuations sorted automatically by category and department name.
4. **Natural vs. Custom Sorting:** Natural ordering by entity ID (`Comparable`) paired with chained `Comparator` pipelines (`Comparator.comparing().reversed().thenComparing()`).
5. **Missing-`hashCode()` Bug Demo & Fix:** Demonstrates why omitting `hashCode()` causes `map.get(key)` to fail and return `null` even for logically identical keys.
6. **FR4–FR6 Functional Requirements:**
   - **FR4:** Automated Item Matching & Correlation Engine based on category, location, and title keywords.
   - **FR5:** Verification Challenge Quiz Authentication in $O(1)$ time.
   - **FR6:** Handover Resolution & Atomic Status Transitions across Map stores.

---

## 2. Map Selection Decision Matrix

| Map Implementation | Underlying Data Structure | Time Complexity (Get / Put) | Key Ordering | Project Role in Campus Lost & Found |
|:---|:---|:---:|:---:|:---|
| **`HashMap`** | Array of buckets (`Node<K,V>[]`), treeifying to Red-Black Tree (`TreeNode`) when bucket $> 8$ | $O(1)$ average | None (Hash order) | **FR4/FR5 Registries:** Instant $O(1)$ lookups for `LostItem`, `FoundItem`, and `Claim` by ID. |
| **`LinkedHashMap`** | Hash table buckets with doubly-linked list through entries | $O(1)$ average | Insertion Order (or access order) | **Audit & Users:** Preserves exact chronological order of student registrations and claim submissions. |
| **`TreeMap`** | Red-Black self-balancing binary search tree | Guaranteed $O(\log N)$ | Sorted naturally by Key (or custom Comparator) | **FR4 Reports:** Generates alphabetical Category Inventory and Department Valuation reports. |

---

## 3. Frequency Analysis with `Map.merge()`

Instead of legacy `containsKey()` or `getOrDefault()` patterns, modern Java applications use `map.merge()`:
```java
// Atomic-like frequency accumulation
frequencyMap.merge(item.getCategory(), 1, Integer::sum);
```
- If the key is absent: inserts `1`.
- If the key is present: invokes the remapping function `Integer::sum`, adding `1` to the existing count in $O(1)$ time.

---

## 4. Comparable vs. Comparator & Chained Pipelines

### Natural Ordering (`Comparable<T>`)
- Implemented by `LostItem`, `FoundItem`, and `Claim` to sort naturally by unique ID:
```java
@Override
public int compareTo(LostItem other) {
    return this.id.compareTo(other.id);
}
```

### Chained Comparators (`Comparator<T>`)
1. **Top-Valued Lost Items Pipeline:**
```java
Comparator<LostItem> highValueComparator = Comparator
        .comparing(LostItem::getEstimatedValue)               // Primary: Value
        .reversed()                                          // DESCENDING (highest first)
        .thenComparing(LostItem::getLostDate)                // Secondary: Oldest lost date first
        .thenComparing(LostItem::getTitle, String.CASE_INSENSITIVE_ORDER); // Tertiary: Title
```
2. **Prioritized Claim Review Pipeline:**
```java
Comparator<Claim> claimComparator = Comparator
        .comparing((Claim c) -> c.getClaimantRole().getPriorityLevel()) // Faculty=1, Staff=2, Student=3
        .thenComparing(Claim::getClaimDate)                            // Earliest claim submission first
        .thenComparing(Claim::getId);                                  // Unique tie-breaker
```

---

## 5. The `equals()` / `hashCode()` Contract Bug & Fix

### The Contract
If `a.equals(b) == true`, then `a.hashCode()` **MUST** equal `b.hashCode()`.

### The Bug (`BrokenItemKey`)
When `equals()` is overridden to compare fields, but `hashCode()` is omitted:
1. `key1` and `key2` have identical fields (`"FOUND-2001"`, `"Electronics"`).
2. `key1.equals(key2)` evaluates to `true`.
3. However, Java assigns different default system identity hash codes (derived from memory address).
4. `map.put(key1, value)` puts the entry into Bucket A.
5. `map.get(key2)` computes key2's hash code and looks in Bucket B $\rightarrow$ **returns `null`!**

### The Fix (`FixedItemKey`)
```java
@Override
public int hashCode() {
    return Objects.hash(itemId, category); // Hashes the exact same fields as equals()
}
```
With `hashCode()` overridden, `key1` and `key2` produce identical hash codes, routing to Bucket A where `equals()` confirms the match.

---

## 6. How to Run

Compile and run directly with Java:
```bash
javac -d bin CampusDirectory.java
java -cp bin com.campus.lostandfound.CampusDirectory
```
