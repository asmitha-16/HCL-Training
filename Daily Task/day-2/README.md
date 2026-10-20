# Day 2: Language Fundamentals & Git Fundamentals

## Project: Campus Lost and Found Portal (JFS-40)
**Curriculum Modules:** Module 1 (Core Java Fundamentals) & Module 3 (Version Control – Git)  
**Deliverables:** `MonthlyUsageAnalyser.java`, `CampusConstants.java`, `CampusSampleData.java`, `README.md`  
**Location:** `Daily Task/day-2/`

---

## 1. Executive Summary

Day 2 focuses on mastering the core primitive type system, arrays (1-D and 2-D), arithmetic operators, type casting, overflow prevention, and establishing Git repository hygiene:

1. **Primitive Data Types & Memory Bounds:** Evaluated the 8 Java primitives (`byte`, `short`, `int`, `long`, `float`, `double`, `char`, `boolean`), their bit-widths, IEEE 754 floating-point precision, and arithmetic bounds.
2. **Numeric Precision & Overflow Safety:** Demonstrated how integer arithmetic wraps around negatively when exceeding `Integer.MAX_VALUE` ($2,147,483,647$) and resolved overflow hazards by widening accumulator variables to `long`.
3. **1-D and 2-D Arrays:** Analyzed monthly utility consumption across single and multi-facility campus buildings using nested iteration patterns.
4. **Centralized Business Rule Constants:** Implemented `CampusConstants.java` to prevent magic numbers in JFS-40 (60-day expiry threshold, match weights: 35% category, 25% location, 20% date, 20% keywords, sub-second latency SLAs).
5. **Operational Sample Data:** Modelled a 7-day operational week of campus lost reports, found items, and verification challenge questions in `CampusSampleData.java`.
6. **Git Hygiene & Workflow:** Established clean tracking without committing binary or temporary artifacts, enforcing `.gitignore` exclusions.

---

## 2. Core Concepts Implemented

### 2.1 The 8 Primitive Types & Memory Layout

| Type | Size | Range | Default | JFS-40 Campus Lost & Found Role |
|:---|:---:|:---|:---:|:---|
| `byte` | 8 bits | $-128$ to $127$ | `0` | Small locker bin indicators, status flags |
| `short`| 16 bits | $-32,768$ to $32,767$ | `0` | Locker bin numerical coordinates |
| `int` | 32 bits | $-2^{31}$ to $2^{31}-1$ | `0` | Item counts, age in days, loop indices |
| `long` | 64 bits | $-2^{63}$ to $2^{63}-1$ | `0L` | Epoch timestamps in milliseconds, audit IDs |
| `float`| 32 bits | $\approx \pm 3.4 \times 10^{38}$ (6-7 digits) | `0.0f` | Temporary ratios |
| `double`| 64 bits | $\approx \pm 1.7 \times 10^{308}$ (15 digits) | `0.0d` | Match probability scores ($0.00$ to $1.00$), valuation |
| `char` | 16 bits | `\u0000` to `\uffff` (Unicode) | `\u0000` | Locker bank tiers (`'A'`, `'B'`, `'C'`) |
| `boolean` | 1 bit | `true` or `false` | `false` | Verification status, privacy blur flags |

### 2.2 Widening vs. Narrowing Type Casting & Overflow

- **Widening Casting (Implicit / Safe):**
  ```java
  int unitCount = 500;
  long safeTotal = unitCount; // Implicit widening (int -> long)
  double accurateMean = (double) safeTotal / 12; // Explicit cast to preserve decimal precision
  ```
- **Overflow Prevention:**
  Adding `1` to `Integer.MAX_VALUE` wraps to `-2147483648`. By using `long` for accumulators, values up to $9 \times 10^{18}$ are safely tracked without data corruption.

---

## 3. Hands-on Task: Campus Incident Analyser (`CampusIncidentAnalyser.java`)

`CampusIncidentAnalyser.java` implements:
- **1-D Array:** 12-month campus lost item reporting statistics (total units, average with decimal cast, min and max usage detection).
- **Ternary Operator:** Operational intensity tier grading:
  ```java
  char operationalGrade = (averageMonthlyIncidents <= SLAB_TIER_LOW) ? 'A'
                        : (averageMonthlyIncidents <= SLAB_TIER_MEDIUM) ? 'B'
                        : (averageMonthlyIncidents <= SLAB_TIER_HIGH) ? 'C' : 'D';
  ```
- **2-D Array:** Matrix representation of 3 campus zones (Library, Student Center, Labs) over 12 months.
- **Overflow Demonstration:** Live comparison of unchecked integer arithmetic vs. widened long accumulation.

---

## 4. Project Task: JFS-40 Business Rules & Sample Data

### 4.1 Business Rule Constants (`CampusConstants.java`)
Centralizes all project requirements to eliminate magic numbers:
- **FR7 (Item Expiry):** `ITEM_EXPIRY_THRESHOLD_DAYS = 60`
- **FR3 (Matching Weights):**
  - Category: `0.35` ($35\%$)
  - Location: `0.25` ($25\%$)
  - Date Proximity: `0.20` ($20\%$)
  - Keywords: `0.20` ($20\%$)
  - Minimum Cutoff Score: `0.50` ($50\%$)
- **FR6 (Claim Concurrency Lock):** `CLAIM_LOCK_TIMEOUT_SECONDS = 30L`, `MAX_PENDING_CLAIMS_PER_ITEM = 5`
- **NFR-P1 (Privacy Masking):** `SENSITIVE_CATEGORIES` list (Electronics, Wallets, IDs)
- **NFR-P2 (SLA Latency):** `MAX_MATCHING_LATENCY_MS = 1000L`

### 4.2 7-Day Operational Cycle Sample Data (`CampusSampleData.java`)
Represents Monday-to-Sunday operational records:
- 5 campus facility locations with designated locker banks (`LIB`, `SAC`, `ENG`, `SCI`, `DIN`).
- 7 registered lost item reports with estimated valuations and categories.
- 6 found items held in university custody (including 1 expired item $>60$ days to validate FR7).
- Category-specific verification challenge question templates to validate FR4.

---

## 5. How to Compile and Run

```bash
# Compile all Day 2 sources
javac -d bin CampusIncidentAnalyser.java CampusConstants.java CampusSampleData.java

# Run hands-on incident analyser
java -cp bin CampusIncidentAnalyser

# Run domain sample data verification
java -cp bin CampusSampleData
```
