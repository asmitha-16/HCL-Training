# Day 11: AI Integration (Core Java) & Code Review Checklist

## Project: Campus Lost and Found Portal (JFS-40)
**Curriculum Modules:** Module 1 (Core Java Fundamentals) & Module 21 (AI-Augmented Development)  
**Deliverables:** `CampusAiReviewDrill.java`, `HandoverReceipt.java`, `HandoverRepository.java`, `CampusExpiryAndAnalyticsService.java`, `AI-USAGE.md`, `README.md`  
**Location:** `Daily Task/day-11/`

---

## 1. Executive Summary

Day 11 focuses on developing a disciplined, critical engineering approach toward **AI-generated OOP code (GitHub Copilot)**, contrasting vague vs. specific prompts, auditing generated output against core Java idioms, and implementing FR7 (60-day expiry & donation) and FR8 (recovery analytics):

1. **Prompt Engineering for Java Backend:** Proved that specific prompts specifying immutability, data bounds, and exception contracts produce vastly superior code compared to vague one-line prompts.
2. **Defect Audit & Verification:** Caught and corrected 5 genuine production hazards in AI output (encapsulation violations, collection reference leaks, missing `equals`/`hashCode`, swallowed exceptions, and $O(N)$ linear scans).
3. **Core Entities for FR7 & FR8:**
   - `HandoverReceipt.java`: Domain model for verified physical custody handovers.
   - `HandoverRepository.java`: Encapsulated in-memory repository managing receipt records.
   - `CampusExpiryAndAnalyticsService.java`: Evaluates 60-day expiry thresholds (FR7) and computes overall campus recovery rate KPIs (FR8).
4. **Comprehensive Log:** Documented all 5 defects with before-and-after code diffs in [`AI-USAGE.md`](AI-USAGE.md).

---

## 2. Prompting Comparison Matrix

| Evaluation Criterion | Vague Prompt (*"Make a class for lost item"*) | Specific Prompt (*"Create an immutable record with validation and equals"*) |
|:---|:---|:---|
| **Field Encapsulation** | Public mutable fields (`public String id`) | `private final` fields with constructor null-checks |
| **Data Immutability** | Returns direct mutable list references | Returns `Collections.unmodifiableList()` |
| **Identity Contract** | Omitted `hashCode()` entirely | Overrode both `equals()` and `hashCode()` using `Objects.hash()` |
| **Performance Complexity**| $O(N)$ linear search loops with `ArrayList` | $O(1)$ fast key-based indexing with `HashMap` |
| **Exception Strategy** | Swallowed exceptions with empty `catch` blocks | Explicit domain exception propagation preserving root cause |

---

## 3. How to Compile and Run

```bash
# Compile all Day 11 source files
javac -d bin CampusAiReviewDrill.java HandoverReceipt.java HandoverRepository.java CampusExpiryAndAnalyticsService.java

# Run AI review drill demonstration
java -cp bin CampusAiReviewDrill

# Run FR7 & FR8 Expiry and Analytics demonstration
java -cp bin CampusExpiryAndAnalyticsService
```
