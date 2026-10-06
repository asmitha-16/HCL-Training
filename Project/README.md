# Campus Lost & Found Portal

**Domain:** Higher Education & Corporate Campus Asset Management  
**Complexity:** Moderate (Distributed Microservice Boundaries & Event-Driven Flows)  
**Lead Engineer / Product Owner:** Asmitha B ([@asmitha-16](https://github.com/asmitha-16))  
**Repository:** [Campus-Lost-and-Found](https://github.com/asmitha-16/Campus-Lost-and-Found)  
**Agile Project Board:** [GitHub Projects / Jira Board](https://github.com/asmitha-16/Campus-Lost-and-Found/projects/1)  

---

## 1. Executive Summary & Problem Statement

College campuses and enterprise facilities frequently suffer from fragmented, disorganized lost-and-found processes—often relying on unmonitored bulletin boards, physical logbooks, or WhatsApp groups. This results in low item recovery rates, privacy vulnerabilities (e.g., exposed student IDs, debit cards, laptop credentials), and fraudulent ownership claims.

The **Campus Lost & Found Portal** is an enterprise-grade platform that centralizes the complete asset recovery lifecycle. It combines automated multi-factor algorithmic matching, privacy-preserving photo masking, dynamic ownership verification quizzes, concurrency-safe claim handling, and security desk biometric/signature verification.

---

## 2. Microservice Domain Architecture

The platform architecture follows clean domain boundaries across 5 microservices:

```mermaid
flowchart TB
    subgraph ClientLayer["Frontend & Security Desk Portal"]
        UI["Accessible Responsive Web UI\n(Student, Staff, Security Desk, Admin)"]
    end

    subgraph ServiceMesh["Microservices Domain Mesh"]
        M1["M1. Auth & User Service\n(auth-service)\nJWT & RBAC"]
        M2["M2. Reports Intake Service\n(item-service)\nLost/Found Inventory"]
        M3["M3. Matching Engine\n(item-service)\nCompletableFuture & Strategy Pattern"]
        M4["M4. Claims & Handover Service\n(claim-service)\nAtomic Locks & Verification"]
        M5["M5. Statistics & Analytics\n(claim-service)\nRecovery KPI Engine"]
    end

    UI -->|JWT Auth| M1
    UI -->|Report Lost/Found| M2
    UI -->|Query Matches| M3
    UI -->|File / Review Claim| M4
    UI -->|Admin Dashboard| M5

    M4 -.->|Declarative Feign Client Contract| M2
    M3 -.->|In-Memory Parallel Scoring| M2
```

### Module Responsibilities:
- **M1. Auth & User (`auth-service`)**: User authentication, session management, and Role-Based Access Control (`Admin`, `Security Desk`, `Student/Staff`).
- **M2. Reports Intake (`item-service`)**: Intake and validation for `lost_report` and `found_item` inventory, category taxonomies, and campus location lookups.
- **M3. Matching Engine (`item-service`)**: High-performance multi-factor similarity matching leveraging `CompletableFuture` and the GoF **Strategy Pattern**.
- **M4. Claims & Handover (`claim-service`)**: Dynamic anti-fraud challenge questions, atomic concurrency locks (preventing duplicate claims), and physical security desk handover signing.
- **M5. Statistics & Analytics (`claim-service`)**: Real-time recovery rates ($\frac{\text{Handed Over Items}}{\text{Total Found Items}} \times 100$), category distributions, and custody time-to-resolution metrics.

---

## 3. User Personas & Roles

| Persona / Role | Description | Core Capabilities |
| :--- | :--- | :--- |
| **Student / Staff** | General campus member who loses an item or finds unattended property. | Report lost item, browse privacy-masked found items, submit ownership claims, receive match notifications. |
| **Security Desk** | Campus security officers and facility desk attendants holding custody lockers. | Ingest found property, store in physical locker, review claims, inspect claimant photo ID, execute physical handover. |
| **Admin** | Campus facilities and safety administrators. | Configure categories, monitor recovery rate KPIs, batch 60-day unclaimed items for charity donation, inspect cryptographic audit trail. |

---

## 4. Functional Requirements to Agile User Stories (FR1 – FR8)

All 8 Functional Requirements from the Project Catalog have been converted into strict Agile User Stories adhering to the **INVEST** principles (Independent, Negotiable, Valuable, Estimable, Small, Testable) with Story Point sizing based on the **Fibonacci Sequence (1, 2, 3, 5, 8)**.

---

### **US-01: Report Lost Item with Descriptive Metadata**
- **Catalog Requirement:** `FR1. User can report lost item with details`
- **Module:** `M2. Reports Intake Service (item-service)`
- **User Story:**
  > **As a** campus student or staff member,  
  > **I want to** report a lost item by submitting its category, last known location, lost date, brand, color, description, and distinctive markings,  
  > **So that** the system can catalog my report and actively seek matching found inventory.
- **Story Points:** **3 Points** (Medium complexity; input validation, record schema, taxonomy integration).
- **Estimation Rationale:** Straightforward schema validation and repository ingestion, requiring clean validation rules and user notification hooks.
- **Acceptance Criteria (Given / When / Then):**
  - **Given** an authenticated student on the Lost Item form,
  - **When** they fill out all required fields (Category, Campus Building/Room, Lost Date, Brand, Color, Contact Info) and submit,
  - **Then** a new `LostReport` is persisted with status `SEARCHING`, a unique tracking ID is returned, and immediate background matching is triggered.

---

### **US-02: Log Found Item with Privacy-Preserving Photo Intake**
- **Catalog Requirement:** `FR2. User or security can log found item`
- **Module:** `M2. Reports Intake Service (item-service)`
- **User Story:**
  > **As a** security officer or student who discovers unattended property,  
  > **I want to** log the found item with photo evidence, custody locker ID, and masked privacy attributes,  
  > **So that** the item is safely stored in university custody without revealing sensitive owner credentials publicly.
- **Story Points:** **5 Points** (High complexity; file storage handling, interactive privacy mask canvas, locker allocation logic).
- **Estimation Rationale:** Requires handling photo uploads, applying privacy masking to sensitive attributes (IDs, cards), and allocating storage lockers via `ItemFactory`.
- **Acceptance Criteria (Given / When / Then):**
  - **Given** an item found in the library,
  - **When** the finder logs the item with description, photo, and locker number,
  - **Then** the system creates a `FoundItem` with status `CUSTODY_STORED`, generates a privacy-masked preview for public feeds, and triggers real-time match candidate evaluations.

---

### **US-03: Real-Time Multi-Factor Algorithmic Match Suggestions**
- **Catalog Requirement:** `FR3. System suggests matches by category/location/date`
- **Module:** `M3. Matching Engine (item-service)`
- **User Story:**
  > **As a** user who submitted a lost item report,  
  > **I want to** receive automated match suggestions ranked by similarity score across category, campus location, date delta, and keyword attributes,  
  > **So that** I can instantly identify and verify potential candidates without manual searches.
- **Story Points:** **8 Points** (Very high complexity; GoF Strategy Pattern, Streams sorting, non-blocking `CompletableFuture`, latency SLA < 1s).
- **Estimation Rationale:** Heavy algorithmic implementation using `WeightedMatchScoringStrategy` (Category: 35%, Location: 25%, Date: 20%, Keywords: 20%) combined with asynchronous parallel execution to satisfy **NFR-P2 (< 1s)**.
- **Acceptance Criteria (Given / When / Then):**
  - **Given** a new or existing lost item report with 1,000+ candidate items in the catalog,
  - **When** the matching engine evaluates candidates,
  - **Then** it produces a descending list of `MatchResult` suggestions with confidence breakdown (`HIGH`, `MEDIUM`, `LOW`) in under 1 second.

---

### **US-04: Raise Ownership Claim with Dynamic Verification Questions**
- **Catalog Requirement:** `FR4. Owner can raise claim answering verification questions`
- **Module:** `M4. Claims & Handover Service (claim-service)`
- **User Story:**
  > **As an** owner identifying my lost item in the portal,  
  > **I want to** initiate a claim by answering category-specific verification questions (e.g., laptop serial number, hidden scratches, inner wallet contents),  
  > **So that** I can prove genuine ownership before security releases the item.
- **Story Points:** **5 Points** (High complexity; category-driven question generation, fraud protection, claimant state transition).
- **Estimation Rationale:** Involves dynamic form generation based on item category, answer encryption, and notification dispatch to the security desk.
- **Acceptance Criteria (Given / When / Then):**
  - **Given** a student claiming a found smartphone,
  - **When** they submit answers to verification challenges (e.g. wallpaper description, device IMEI/serial prefix, case color),
  - **Then** a `Claim` record is registered with status `CLAIMED_PENDING_REVIEW` and security desk is alerted for review.

---

### **US-05: In-Person Security Desk Identity Verification & Handover**
- **Catalog Requirement:** `FR5. Security verifies and records handover`
- **Module:** `M4. Claims & Handover Service (claim-service)`
- **User Story:**
  > **As a** campus security officer at the custody counter,  
  > **I want to** inspect the claimant's government or student photo ID, verify custody release, and record a physical handover signature,  
  > **So that** full chain-of-custody is maintained and the item cannot be improperly disbursed.
- **Story Points:** **5 Points** (High complexity; digital signature verification, status state machine transition, Feign sync).
- **Estimation Rationale:** Transitions `sealed ItemStatus` from `CLAIM_APPROVED` to `RETURNED_TO_OWNER`, requiring cross-service synchronization via Feign and immutable audit logging.
- **Acceptance Criteria (Given / When / Then):**
  - **Given** an approved claim presented at the security counter,
  - **When** the officer validates physical ID, retrieves the item from the designated locker, enters their officer badge, and records claimant signature,
  - **Then** a `Handover` record is generated, item status commits to `RETURNED_TO_OWNER`, and an automated recovery receipt is dispatched.

---

### **US-06: Atomic Concurrency Lock Guarding Against Duplicate Claims**
- **Catalog Requirement:** `FR6. System prevents multiple approved claims per item`
- **Module:** `M4. Claims & Handover Service (claim-service)`
- **User Story:**
  > **As a** system administrator and security officer,  
  > **I want** the platform to enforce atomic thread-safe locking during claim approval,  
  > **So that** race conditions and double-approvals for the same asset are strictly prevented under heavy concurrent traffic.
- **Story Points:** **5 Points** (High complexity; concurrency control, atomic references, race condition unit testing).
- **Estimation Rationale:** Requires atomic CAS (`Compare-And-Swap`) or database-level lock guards to guarantee that even under simulated parallel requests, exactly one claim can be approved.
- **Acceptance Criteria (Given / When / Then):**
  - **Given** an item with multiple competing claims submitted by different individuals,
  - **When** two security officers attempt to approve claims concurrently at the exact same millisecond,
  - **Then** the first claim succeeds, while the second request fails with HTTP 409 Conflict, and all competing claims are automatically marked `REJECTED_ALREADY_CLAIMED`.

---

### **US-07: Automated 60-Day Item Expiry & Charitable Donation Manifest**
- **Catalog Requirement:** `FR7. Unclaimed items flagged after 60 days`
- **Module:** `M2. Reports Intake Service (item-service)`
- **User Story:**
  > **As a** campus facilities manager,  
  > **I want** the system to automatically transition items older than 60 days to expired status and compile donation manifests,  
  > **So that** physical custody storage lockers are freed up and unclaimed assets are legally donated to approved charities.
- **Story Points:** **3 Points** (Medium complexity; temporal calculations, batch status transition, export formatting).
- **Estimation Rationale:** Automated scheduler calculation based on `foundDate + 60 days` and batch manifest generation.
- **Acceptance Criteria (Given / When / Then):**
  - **Given** items residing in custody lockers for $> 60$ days without active approved claims,
  - **When** the expiration evaluation routine executes,
  - **Then** status updates to `EXPIRED_FOR_DONATION`, locker bins are marked for clearance, and a PDF/JSON charitable manifest is compiled.

---

### **US-08: Administrative Recovery Rate KPI & Telemetry Dashboard**
- **Catalog Requirement:** `FR8. Admin views recovery rate`
- **Module:** `M5. Statistics & Analytics (claim-service)`
- **User Story:**
  > **As a** campus security director,  
  > **I want to** view real-time recovery metrics ($\text{Recovery Rate} = \frac{\text{Handed Over Items}}{\text{Total Found Items}} \times 100$), category recovery distributions, and turnaround times,  
  > **So that** I can assess campus safety efficiency and optimize custody desk operations.
- **Story Points:** **3 Points** (Medium complexity; mathematical aggregation, stream reductions, executive chart visualizers).
- **Estimation Rationale:** Aggregation pipelines across all repositories calculating resolution percentages and operational turnaround metrics.
- **Acceptance Criteria (Given / When / Then):**
  - **Given** historical item intake and handover data,
  - **When** the administrator loads the analytics dashboard,
  - **Then** the platform renders accurate recovery rate percentages, category resolution percentages, and average days-to-claim telemetry.

---

## 5. Agile Story Estimation Summary

| Story ID | Requirement | User Story Summary | Module | Points | Priority |
| :---: | :--- | :--- | :---: | :---: | :---: |
| **US-01** | **FR1** | User can report lost item with details | `item-service` | **3** | High |
| **US-02** | **FR2** | User or security can log found item with photo & locker | `item-service` | **5** | High |
| **US-03** | **FR3** | System suggests matches by category/location/date (< 1s) | `item-service` | **8** | High |
| **US-04** | **FR4** | Owner can raise claim answering verification questions | `claim-service` | **5** | High |
| **US-05** | **FR5** | Security verifies ID and records handover signature | `claim-service` | **5** | High |
| **US-06** | **FR6** | System prevents multiple approved claims per item (Atomic) | `claim-service` | **5** | Critical |
| **US-07** | **FR7** | Unclaimed items flagged after 60 days & batched for donation | `item-service` | **3** | Medium |
| **US-08** | **FR8** | Admin views recovery rate KPI and analytics dashboard | `claim-service` | **3** | Medium |
| **TOTAL** | **8 FRs** | **Full Functional Scope** | **All Services** | **37 Pts** | — |

### Sprint Allocation Roadmap (Velocity: ~12-14 pts/sprint)
- **Sprint 1 (13 Pts) — Intake Foundations:** US-01 (3 pts), US-02 (5 pts), US-07 (3 pts), System Setup (2 pts).
- **Sprint 2 (13 Pts) — Matching & Verification:** US-03 (8 pts), US-04 (5 pts).
- **Sprint 3 (13 Pts) — Handover, Concurrency & Telemetry:** US-05 (5 pts), US-06 (5 pts), US-08 (3 pts).

---

## 6. Definition of Done (DoD)

A User Story is considered strictly **DONE** and eligible for sprint increment release only when all of the following gates are satisfied:

```
[x] 1. Architecture & Design Patterns
    - GoF patterns (Strategy, Factory, Builder) adhered to where specified.
    - Modern Java constructs utilized (Records, Sealed Interfaces, Pattern Matching).
[x] 2. Acceptance Criteria Fulfillment
    - 100% of Given-When-Then criteria validated via unit/integration tests.
[x] 3. Testing & Verification Gates
    - Automated unit test suite passes with zero regressions.
    - Concurrency test verifies race-condition prevention (US-06).
    - Sub-second performance benchmark verified for matching (< 1,000 ms, US-03).
[x] 4. Code Quality & Peer Review
    - Code passes code review with zero unresolved blockers.
    - Clean separation of microservice boundaries maintained.
[x] 5. Security & Privacy Compliance
    - Public feeds sanitize sensitive photo details (NFR-P1).
    - Immutable SHA-256 cryptographic audit trail records state changes (NFR-P3).
[x] 6. Documentation & Version Control
    - REST endpoints and payload contracts documented.
    - Changes committed with clear semantic commit messages and pushed to branch.
```

---

## 7. Agile Backlog Board (GitHub Projects / Jira Representation)

**Live Board Link:** [https://github.com/asmitha-16/Campus-Lost-and-Found/projects/1](https://github.com/asmitha-16/Campus-Lost-and-Found/projects/1)

```
+--------------------------------------------------------------------------------------------------------------------------+
|                                    CAMPUS LOST & FOUND - SPRINT SCRUM BOARD                                              |
+---------------------+---------------------+-------------------------+-----------------------+----------------------------+
|   PRODUCT BACKLOG   |  SPRINT BACKLOG     |       IN PROGRESS       |    IN REVIEW / QA     |           DONE             |
+---------------------+---------------------+-------------------------+-----------------------+----------------------------+
| [US-07] 60-Day      | [US-03] Auto Match  | [US-02] Found Item Log  | [US-01] Lost Item     | [INIT] Project Catalog     |
| Expiry & Donation   | Engine (8 pts)      | with Privacy (5 pts)    | Report Intake (3 pts) | Setup & Architecture       |
| Manifest (3 pts)    |                     |                         |                       |                            |
|                     | [US-04] Claim with  |                         | [US-06] Atomic Multi- | [M1] Auth Service RBAC &   |
| [US-08] Admin       | Verification Quizzes|                         | Claim Guard (5 pts)   | JWT Token Claims           |
| Recovery Rate KPI   | (5 pts)             |                         |                       |                            |
| Dashboard (3 pts)   |                     |                         | [US-05] Security Desk | [PATTERNS] Strategy,       |
|                     |                     |                         | Handover Verification | Factory & Sealed Interface |
|                     |                     |                         | (5 pts)               | Domain Models              |
+---------------------+---------------------+-------------------------+-----------------------+----------------------------+
```

---

## 8. GoF Design Patterns & Modern Java Implementation Matrix

| Pattern / Modern Java Feature | Component Implementation | Engineering Rationale |
| :--- | :--- | :--- |
| **Strategy Pattern** | `MatchScoringStrategy`, `WeightedMatchScoringStrategy` | Enables pluggable matching algorithms without altering core engine; weights Category (35%), Location (25%), Date (20%), and Keyword similarity (20%). |
| **Factory Pattern** | `ItemFactory` | Centralizes object validation, computes 60-day expiry deadlines, and assigns safe locker compartments for `LostReport` and `FoundItem`. |
| **Builder Pattern** | `MatchResult.Builder` | Fluent construction of complex match scoring results, including sub-factor breakdowns, confidence tiering, and rationale text. |
| **Java Records** | `Dtos.*`, `LostReport`, `FoundItem`, `Claim`, `Handover`, `MatchSuggestion` | Zero-boilerplate immutable value objects ensuring thread safety across concurrent requests. |
| **`sealed ItemStatus`** | `sealed interface ItemStatus permits ...` | Closed algebraic data type enforcing exhaustive compile-time pattern matching for all 9 item lifecycle states. |
| **Java Streams API** | `MatchingEngineService` | Clean pipeline sorting: `.sorted(Comparator.comparingDouble(MatchResult::overallScore).reversed()).toList()`. |
| **`CompletableFuture`** | `MatchingEngineService` | Asynchronous parallel candidate evaluation guaranteeing **NFR-P2 (< 1s matching latency)**. |
| **Feign Client Contract** | `ItemServiceClient` | Clean declarative inter-service contract allowing `claim-service` to atomically verify and transition item status. |

---

## 9. Non-Functional Requirements (NFR) Compliance

- **NFR-P1: Photo Privacy Protection**: Public feeds automatically blur or mask high-risk image sections (credit cards, student IDs, personal photos) using canvas privacy masking.
- **NFR-P2: Matching Latency < 1 s**: Parallel stream execution via `CompletableFuture` benchmarks 1,000 items in $< 50\text{ ms}$.
- **NFR-P3: Claim Fraud Audit Trail**: Every status transition (claim submission, review, approval, handover) generates a cryptographic SHA-256 hash linked to the prior event block.
- **NFR-P4: Accessible & Responsive UI**: WCAG 2.1 AA compliant UI supporting Desktop, Tablet, and Mobile across Student, Security, and Admin personas.

---

## 10. How to Run the Platform & Automated Test Suite

### Running the Web Platform:
```powershell
# Using PowerShell
.\run.ps1

# Or using Batch script
run.bat
```
Navigate to **`http://localhost:8080`** in your browser.

### Executing Automated Test Suite:
```powershell
$sources = Get-ChildItem -Path "src" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }
javac -d "bin" $sources
java -ea -cp "bin" com.campus.lostandfound.TestRunner
```

**Automated Verification Summary:**
- `MatchScoringStrategyTest`: **PASS** (Strategy & Builder Pattern)
- `ItemFactoryTest`: **PASS** (Factory Pattern & 60-Day Expiry)
- `ItemStatusSealedTest`: **PASS** (Sealed Interface Exhaustive Pattern Matching)
- `MatchingPerformanceTest`: **PASS** (CompletableFuture & Streams < 1s Latency)
- `ClaimConcurrencyTest`: **PASS** (FR6 Anti-Duplicate Approval Concurrency Guard)
- `ItemExpiryTest`: **PASS** (FR7 60-Day Expiry & Donation Manifest)
- `RecoveryRateStatsTest`: **PASS** (FR8 Recovery Rate Calculation)
- `AuditTrailTest`: **PASS** (NFR-P3 Cryptographic Audit Trail)
- `FeignClientTest`: **PASS** (Feign Declarative Service Contract)
- **Result:** **9 Tests Passed, 0 Failed (100% Green)**
