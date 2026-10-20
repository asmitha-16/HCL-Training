# JFS-40 Campus Lost & Found Portal — Daily Tasks (Day 01 to Day 13)

This directory contains the daily technical implementations, engineering drills, performance labs, and architectural milestones for the **Campus Lost and Found Portal (JFS-40)**, structured chronologically from **Day 01** to **Day 13**.

Every daily module strictly enforces the project's Functional Requirements (**FR1 to FR8**) and Non-Functional Requirements (**NFR-P1 to NFR-P4**, **NFR-01 to NFR-14**), using clean Java code without unnecessary scripts or external dependencies.

---

## Daily Modules Directory Index

| Day | Folder | Topic / Focus Area | Campus Lost & Found Implementation |
| :---: | :--- | :--- | :--- |
| **01** | [`day-01/`](day-01/) | **Java Platform Basics & Agile/Scrum Backlog** | JVM runtime telemetry, heap capacity validation for 10,000 active campus items, 8 core User Stories (US-01 to US-08, 49 Story Points), Gherkin Acceptance Criteria, 12-point DoD, and NFR compliance matrix. |
| **02** | [`day-02/`](day-02/) | **Java Data Types, Operators & Control Structures** | Campus incident analyzer using 1D arrays for monthly reporting trends, 2D arrays mapping items across campus zones (Library, Cafeteria, Sports Complex, Hostels), widening casts, and overflow-safe counters. |
| **03** | [`day-03/`](day-03/) | **Control Flow & Console Interface** | Interactive console portal with 3-attempt PIN authentication for security staff, `do-while` menu supporting FR1–FR8 workflows, input validation with `continue`, and audit trail iteration. |
| **04** | [`day-04/`](day-04/) | **OOP Fundamentals & Encapsulation** | Custody locker storage with strict field encapsulation, static locker tracking, 3 chained constructors (`this()`), ownership release & donation transitions, and robust `equals`/`hashCode`. |
| **05** | [`day-05/`](day-05/) | **Inheritance, Polymorphism & Verification Hierarchy** | `VerifiableClaim` interface, `CampusClaim` abstract base, specialized `ElectronicsClaim`, `WalletClaim`, and `KeysClaim`, role hierarchy (Student, Staff, Security Officer, Admin), and matching strategies. |
| **06** | [`day-06/`](day-06/) | **Exception Handling & Business Rule Validation** | Custom exception hierarchy (`ItemAlreadyClaimedException`, `InvalidClaimDataException`), chained exceptions into `ClaimProcessingException`, multi-catch blocks, and `finally` security audit logging. |
| **07** | [`day-07/`](day-07/) | **Collections Framework — Part 1 (Lists, Sets, Queues)** | Complete in-memory domain catalog backed by `ArrayList` ($O(1)$ random access), `LinkedHashSet` (duplicate prevention and chronological audit), and `PriorityQueue` (expedited faculty claims), plus comparative timing benchmarks. |
| **08** | [`day-08/`](day-08/) | **Collections Framework — Part 2 (Maps & Hashing Deep Dive)** | Custom hashing key `StudentKey` demonstrating bucket collision resolution, strict `hashCode()` and `equals()` contract compliance, hash distribution analysis, and sorted `TreeMap` reports. |
| **09** | [`day-09/`](day-09/) | **Generics & CSV Data Ingestion** | Generic repository with bounded wildcards (`<? extends BaseEntity>`), type-safe CSV data ingestion for lost items, found items, and campus users with `AutoCloseable` resource management. |
| **10** | [`day-10/`](day-10/) | **JVM Memory Management & Performance Lab** | 5 performance experiments: recursion depth profiling, 10,000 item heap allocation, `String` vs `StringBuilder` benchmark (529x speedup), GC telemetry, and locker `Integer` cache hazard analysis (`==` vs `equals`). |
| **11** | [`day-11/`](day-11/) | **AI Code Review Drill & Lifecycle Analytics** | Refactoring 5 AI-generated bugs into defensive production code, automated 60-day item expiry sweep (FR7), and recovery rate analytics (FR8) documented in `AI-USAGE.md`. |
| **12** | [`day-12/`](day-12/) | **Assessment 1: Core Java Checkpoint** | Full standalone interactive application featuring Flow 1 (Report Lost Item + Auto-Match Found Catalog) and Flow 2 (Claim Ownership Challenge + Security Handover Verification), tagged as `v1.0-core-java`. |
| **13** | [`day-13/`](day-13/) | **Functional Programming & Lambdas** | `FilterEngine` with `Predicate` composition (`and`/`or`/`negate`), `Function` transformation chains, `Consumer` audit logging, `Supplier` fallbacks, 4 method reference forms, and `@FunctionalInterface MatchRule`. |

---

## Architectural Principles Enforced

1. **Strict Domain Alignment:** All exercises reflect campus lost items, found registries, security desk custody lockers, student claims, and resolution workflows.
2. **Clean Project Workspace:** Only source code, documentation reports, and sample CSV data are stored—no compiled `.class` binaries, scripts, or output dumps.
3. **Natural Sorting:** Folders are named with two-digit padding (`day-01` through `day-13`) to guarantee chronological ordering across all git clients and web interfaces.
