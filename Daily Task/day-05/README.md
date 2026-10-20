# Day 5: Inheritance, Polymorphism & Git Branching Workflows

## Project: Campus Lost and Found Portal (JFS-40)
**Curriculum Modules:** Module 1 (Core Java Fundamentals) & Module 3 (Version Control – Git)  
**Deliverables:** `ClaimVerificationHierarchy.java`, `BaseEntity.java`, `RoleHierarchy.java`, `MatchStrategy.java`, `README.md`  
**Location:** `Daily Task/day-05/`

---

## 1. Executive Summary

Day 5 emphasizes building extensible, reusable domain hierarchies using **inheritance, abstraction, runtime polymorphism**, the **Strategy Design Pattern**, and collaborative **Git feature branching** directly focused on **Campus Lost & Found Claim Processing**:

1. **Abstract Classes vs. Interfaces:**
   - `abstract class CampusClaim`: Holds shared state (`claimId`, `foundItemId`, `claimantId`, `status`, `reviewingOfficerId`) and overloaded approval operations.
   - `interface VerifiableClaim`: Specifies an orthogonal verification contract (`verifyOwnership(...)`) implemented by category-specific claims (`ElectronicsClaim`, `WalletClaim`, `KeysClaim`).
2. **Method Overloading vs. Overriding:**
   - Overloading: `approve(officerId)` vs. `approve(officerId, notes)` resolved at compile time based on parameter signatures.
   - Overriding: `@Override public boolean verifyOwnership(...)` resolved dynamically at runtime through dynamic method dispatch.
3. **Core Domain Hierarchy for JFS-40:**
   - `BaseEntity`: Abstract superclass providing standard ID generation and timestamp tracking (`createdAt`, `updatedAt`).
   - `RoleHierarchy`: `User` $\rightarrow$ `StudentUser`, `StaffUser`, `SecurityOfficerUser`, `AdminUser` with polymorphic permission inquiries (`canVerifyClaims()`, `canManageInventory()`).
4. **Strategy Pattern (`MatchStrategy.java`):**
   - Implemented `MatchScoringStrategy` with two interchangeable engines:
     - `ExactCategoryMatchStrategy`: Fast $O(1)$ binary evaluation.
     - `WeightedMultiFactorMatchStrategy`: Multi-tier algorithmic scoring (Category 35%, Location 25%, Date 20%, Keywords 20%).
5. **Git Branching, Merge Conflicts & Rebase:** Formalized the feature branch workflow, resolving merge conflicts, and contrasting 3-way merges vs. linear rebasing.

---

## 2. Architecture & Class Relationships

```
+---------------------------------------------------------------------------------------------------+
| JFS-40 CLASS & STRATEGY HIERARCHY                                                                 |
+---------------------------------------------------------------------------------------------------+
|                                                                                                   |
|     <<abstract>>                                 <<interface>>                                    |
|      BaseEntity                              MatchScoringStrategy                                 |
|     + String id                              + double calculateScore(...)                         |
|     + long createdAt                                  ^                                           |
|     + long updatedAt                                  |                                           |
|           ^                         +-----------------+-----------------+                         |
|           |                         |                                   |                         |
|     <<abstract>>        ExactCategoryMatchStrategy       WeightedMultiFactorMatchStrategy         |
|         User            (Fast Binary Cutoff)             (35% Cat, 25% Loc, 20% Date, 20% Key)   |
|           ^                                                                                       |
|   +-------+-------+---------------+                                                               |
|   |               |               |                                                               |
| StudentUser    StaffUser   SecurityOfficerUser   AdminUser                                        |
|                                                                                                   |
+---------------------------------------------------------------------------------------------------+
```

---

## 3. Abstract Class vs. Interface Comparison

| Feature | Abstract Class (`CampusClaim`, `BaseEntity`) | Interface (`VerifiableClaim`, `MatchScoringStrategy`) |
|:---|:---|:---|
| **State / Fields** | Can maintain instance state (`protected`, `private`) | Implicitly `public static final` constants only |
| **Constructors** | Supported via constructor chaining (`super()`) | Not allowed |
| **Multiple Inheritance**| Single inheritance only (`extends`) | Multiple inheritance supported (`implements`) |
| **Primary Intent** | Is-A identity hierarchy with code reuse | Can-Do behavioral contracts and strategy plugging |

---

## 4. Git Branching & Conflict Resolution Workflow

```bash
# 1. Ensure latest main branch is up to date
git checkout main
git pull origin main

# 2. Create and switch to an isolated feature branch
git checkout -b feature/claim-verification-hierarchy

# 3. Commit iterative changes on the branch
git add .
git commit -m "feat: implement ClaimVerificationHierarchy and Strategy pattern"

# 4. Scenario: Resolving a 3-Way Merge Conflict
# If main has progressed, pull or rebase main into feature branch:
git fetch origin
git rebase origin/main

# 5. When conflict markers appear (<<<<<<< HEAD ... ======= ... >>>>>>>):
# Manually inspect the file, select the clean union of changes, remove markers:
git add <resolved-files>
git rebase --continue

# 6. Push feature branch and merge via Pull Request
git push -u origin feature/claim-verification-hierarchy
```

---

## 5. How to Compile and Run

```bash
# Compile all Day 5 source files
javac -d bin ClaimVerificationHierarchy.java BaseEntity.java RoleHierarchy.java MatchStrategy.java

# Run Claim Verification polymorphism demonstration
java -cp bin ClaimVerificationHierarchy

# Run Strategy Pattern dynamic interchangeability demo
java -cp bin MatchStrategy
```
