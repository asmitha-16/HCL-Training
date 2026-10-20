# Day 4: Object-Oriented Programming (OOP) & Debugging Architecture

## Project: Campus Lost and Found Portal (JFS-40)
**Curriculum Modules:** Module 1 (Core Java Fundamentals) & Module 4 (Build Tools, IDE & Debugging)  
**Deliverables:** `CampusCustodyLocker.java`, `LostItem.java`, `FoundItem.java`, `Claim.java`, `User.java`, `README.md`  
**Location:** `Daily Task/day-04/`

---

## 1. Executive Summary

Day 4 addresses the fundamentals of **Object-Oriented Programming (OOP) encapsulation**, constructor chaining, static vs. instance lifecycle, the 4 Java access modifiers, and modern IDE **debugging workflows** directly modeled on **Campus Lost & Found Custody Management**:

1. **Encapsulation & Data Invariants:** All state fields are declared `private`, mutated exclusively via semantic business methods (`assignItem()`, `releaseToOwner()`, `flagForDonation()`), preventing invalid state transitions.
2. **Constructor Overloading & Chaining:** Demonstrated `this(...)` constructor chaining in `CampusCustodyLocker.java`, channeling defaults into a single master constructor to avoid code duplication.
3. **Static Sequence Generation:** Leveraged static members (`custodySequence`) to provide collision-free sequential custody intake IDs (`CUSTODY-1001`, `CUSTODY-1002`).
4. **Core Domain Entities for JFS-40:** Implemented encapsulated models for `LostItem` (FR1), `FoundItem` with optimistic concurrency versioning (FR2 & FR6), `Claim` with quiz responses (FR4), and `User` with campus roles.
5. **IDE Debugging Architecture:** Formalized debugging strategies replacing naive `System.out.println()` with conditional breakpoints, watches, call stack inspection, and hot code replace.

---

## 2. OOP Encapsulation & Constructor Chaining

### 2.1 The 4 Access Modifiers

| Modifier | Class Level | Package Level | Subclass (Extends) | World (Public) | Role in JFS-40 |
|:---|:---:|:---:|:---:|:---:|:---|
| **`private`** | Yes | No | No | No | Internal state fields (`custodyStatus`, `assignedItemId`, `version`) |
| **Default (package-private)** | Yes | Yes | No | No | Internal package helpers and package repositories |
| **`protected`** | Yes | Yes | Yes | No | Base entity audit properties inherited by subclasses |
| **`public`** | Yes | Yes | Yes | Yes | Public API methods and domain constructors |

### 2.2 Constructor Chaining via `this()`
```java
// 1-arg constructor chains to 2-arg constructor
public CampusCustodyLocker(String lockerBinCode) {
    this(lockerBinCode, "GENERAL", "SEC-DESK-DEFAULT");
}

// 2-arg constructor chains to 3-arg master constructor
public CampusCustodyLocker(String lockerBinCode, String itemCategory) {
    this(lockerBinCode, itemCategory, "SEC-DESK-DEFAULT");
}

// Master constructor contains single source of validation
public CampusCustodyLocker(String lockerBinCode, String itemCategory, String securityOfficerId) {
    // Single validation point prevents duplication
}
```

---

## 3. IDE Debugging Guide (Eliminating `println`)

```
+-----------------------------------------------------------------------------------------------+
| MODERN IDE DEBUGGING WORKFLOW                                                                 |
+-----------------------------------------------------------------------------------------------+
| 1. Standard Breakpoint: Pauses thread execution when line is reached.                        |
| 2. Conditional Breakpoint: Pauses ONLY when expression is true:                              |
|    Condition: ("OCCUPIED".equals(this.custodyStatus) && verifiedClaimId != null)             |
| 3. Logpoint: Evaluates expression and prints to Debug Console WITHOUT halting thread.        |
| 4. Watch Variables: Continuously evaluates expressions like:                                 |
|    locker.getCustodyStatus(), foundItem.getVersion(), claim.getChallengeAnswer1()            |
| 5. Call Stack Inspection: Traces call frame progression:                                      |
|    Thread [main] -> CampusMenuApp.handleSecurityHandover() -> Locker.releaseToOwner()        |
| 6. Hot Code Replace: Compiles modified bytecodes and swaps them live into the running JVM.    |
+-----------------------------------------------------------------------------------------------+
```

---

## 4. JFS-40 Domain Entity Models

- [`CampusCustodyLocker.java`](CampusCustodyLocker.java): Physical custody storage locker bins managing item intake, ownership release, and donation transitions.
- [`LostItem.java`](LostItem.java): Tracks lost report submissions (FR1) with category, location, date, estimated valuation, and `status` (`ACTIVE`, `MATCHED`, `CLOSED`).
- [`FoundItem.java`](FoundItem.java): Tracks items in custody (FR2) with `custodyLockerId`, privacy blur flag (NFR-P1), 60-day expiry donation method (FR7), and `version` counter for optimistic locking (FR6).
- [`Claim.java`](Claim.java): Tracks claimant verification challenge answers (FR4) and state machine (`PENDING` $\rightarrow$ `APPROVED` or `REJECTED`).
- [`User.java`](User.java): Models campus user identities across `STUDENT`, `STAFF`, `SECURITY_DESK`, and `ADMIN`.

---

## 5. How to Compile and Run

```bash
# Compile all Day 4 source files
javac -d bin CampusCustodyLocker.java LostItem.java FoundItem.java Claim.java User.java

# Run CampusCustodyLocker OOP demonstration
java -cp bin CampusCustodyLocker
```
