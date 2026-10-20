# Day 6: Exception Handling & AI-Assisted Debugging

## Project: Campus Lost and Found Portal (JFS-40)
**Curriculum Modules:** Module 1 (Core Java Fundamentals) & Module 4 (Build Tools, IDE & Debugging)  
**Deliverables:** `CampusClaimProcessor.java`, `CampusExceptionApp.java`, `README.md`  
**Location:** `Daily Task/day-6/`

---

## 1. Executive Summary

Day 6 addresses robust **exception handling architecture**, comparing checked vs. unchecked exceptions, exception chaining, multi-catch handling, `finally` cleanup semantics, and **AI-assisted stack trace interpretation** directly mapped to **Campus Lost & Found Claim Processing**:

1. **Checked vs. Unchecked Exception Philosophy:**
   - **Checked Exceptions (e.g., `ItemAlreadyClaimedException`):** Represent anticipated, recoverable business rule violations that callers are required by the compiler to explicitly handle or declare (`throws`).
   - **Unchecked Exceptions (e.g., `DuplicateReportException`, `InvalidClaimDataException`):** Subclasses of `RuntimeException` indicating programming bugs, invalid input arguments, or boundary violations.
2. **Exception Chaining & Root Cause Preservation:** Wrapped low-level exceptions inside high-level service exceptions (`ClaimProcessingException`) using `new Exception(message, cause)`, ensuring no debugging context or original stack traces are lost.
3. **Multi-Catch & Finally Cleanup:** Demonstrates unified multi-catch blocks (`catch (InvalidClaimDataException | ItemAlreadyClaimedException ex)`) and guaranteed execution of cleanup/audit flushes in `finally` blocks.
4. **Enforcing 2 JFS-40 Business Rules:**
   - **Rule 1 (Duplicate Item Prevention):** Detects matching category, location, and description combinations and throws `DuplicateReportException`.
   - **Rule 2 (Claim Availability & Anti-Fraud):** Verifies that custody property is in `AVAILABLE` status before allowing ownership claims, throwing `ItemAlreadyClaimedException` if the item is already claimed or returned.
5. **Console Menu Graceful Error Recovery:** Demonstrated that caught exceptions display informative user feedback and return cleanly to the command prompt without terminating the JVM.
6. **AI Stack Trace Diagnosis:** Analyzed 2 distinct Java stack traces with AI Copilot Chat, validating accuracy against actual runtime root causes.

---

## 2. Exception Hierarchy Architecture

```
+---------------------------------------------------------------------------------------------------+
| JFS-40 EXCEPTION TAXONOMY                                                                         |
+---------------------------------------------------------------------------------------------------+
|                                                                                                   |
|                           java.lang.Throwable                                                     |
|                                    ^                                                              |
|                                    |                                                              |
|                            java.lang.Exception                                                    |
|                           (Checked Exceptions)                                                    |
|                                    ^                                                              |
|               +--------------------+--------------------+                                         |
|               |                                         |                                         |
|    ItemAlreadyClaimedException               java.lang.RuntimeException                           |
|    (Rule 2: Checked Business Rule)             (Unchecked Exceptions)                             |
|                                                         ^                                         |
|                                                         |                                         |
|                                           CampusLostAndFoundException                             |
|                                           (Base Unchecked Domain Fault)                           |
|                                                         ^                                         |
|                                       +-----------------+-----------------+                       |
|                                       |                                   |                       |
|                            DuplicateReportException             ItemNotFoundException             |
|                            (Rule 1: Duplicate Prevention)       (Resource 404 Resolution)         |
|                                                                                                   |
+---------------------------------------------------------------------------------------------------+
```

---

## 3. Hands-on Task: Claim Processor (`CampusClaimProcessor.java`)

Demonstrates:
- `validateClaimData(claimantId, quizAnswers)`: Throws unchecked `InvalidClaimDataException` if inputs are blank or invalid.
- `verifyAndReserveItem(claimId)`: Throws checked `ItemAlreadyClaimedException` if item is already claimed.
- Multi-catch block intercepting both specific exceptions.
- Exception chaining wrapping both into `ClaimProcessingException` while preserving `ex` as the underlying cause.
- Guaranteed `finally` block recording custody audit logs.

---

## 4. Project Task: Business Rule Enforcement (`CampusExceptionApp.java`)

### Rule 1: Duplicate Item Prevention
- Prevents redundant reporting of the same loss.
- Evaluates composite key (`category:location:title`).
- Throws `DuplicateReportException` and prompts user with existing report ID.

### Rule 2: Claim Availability Validation
- Intercepts claims on items marked `CLAIMED` or `RETURNED`.
- Throws checked `ItemAlreadyClaimedException` carrying current status and item ID.
- Blocks unauthorized transfers while permitting valid claims on `AVAILABLE` inventory.

---

## 5. AI-Assisted Stack Trace Analysis

### Stack Trace 1: `NullPointerException` in Match Scoring
```text
Exception in thread "main" java.lang.NullPointerException: Cannot invoke "String.toLowerCase()" because the return value of "LostItem.getTitle()" is null
    at com.campus.lostandfound.strategy.WeightedMultiFactorMatchStrategy.calculateScore(WeightedMultiFactorMatchStrategy.java:42)
    at com.campus.lostandfound.app.CampusConsoleApp.matchFlow(CampusConsoleApp.java:112)
    at com.campus.lostandfound.app.CampusConsoleApp.main(CampusConsoleApp.java:34)
```
- **Copilot Explanation:**
  *"The exception occurred at line 42 of `WeightedMultiFactorMatchStrategy` because `LostItem.getTitle()` returned `null`, and calling `.toLowerCase()` on a null reference triggers `NullPointerException`."*
- **Verification Evaluation:** **CORRECT (100% accurate).**
- **Action Taken:** Added defensive null-check and default empty string fallback in `LostItem` constructor and getter:
  `this.title = (title != null) ? title.trim() : "";`

### Stack Trace 2: `ConcurrentModificationException` during Stale Item Expiry Sweep
```text
Exception in thread "main" java.util.ConcurrentModificationException
    at java.base/java.util.ArrayList$Itr.checkForComodification(ArrayList.java:1095)
    at java.base/java.util.ArrayList$Itr.next(ArrayList.java:1049)
    at com.campus.lostandfound.service.CampusLostAndFoundService.sweepExpiredItems(CampusLostAndFoundService.java:85)
```
- **Copilot Explanation:**
  *"An `ArrayList` was structurally modified (e.g. `list.remove(item)`) while iterating through it with an enhanced for-each loop, mutating `modCount` unexpectedly."*
- **Verification Evaluation:** **CORRECT (100% accurate).**
- **Action Taken:** Replaced naive `for-each` list modification with explicit `Iterator.remove()` or `list.removeIf(predicate)`.

---

## 6. How to Compile and Run

```bash
# Compile Day 6 source files
javac -d bin CampusClaimProcessor.java CampusExceptionApp.java

# Run hands-on CampusClaimProcessor demo
java -cp bin CampusClaimProcessor

# Run JFS-40 Business Rule Validation & Error Recovery demo
java -cp bin CampusExceptionApp
```
