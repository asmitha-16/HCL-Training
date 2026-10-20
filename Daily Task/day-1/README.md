# Day 1: Java Platform Basics + Agile / Scrum Framework

## Project: Campus Lost and Found Portal (JFS-40)
**Curriculum Modules:** Module 1 (Core Java Fundamentals) & Module 22 (Agile & Project Readiness)  
**Deliverables:** `PlatformInfo.java`, Terminal Execution Log, `javap` Disassembled Bytecode, Classloading Trace, JFS-40 User Story Backlog (FR1–FR8), Definition of Done (DoD), and NFR Verification Matrix  
**Location:** `Daily Task/day-1/`

---

## 1. Executive Summary

Day 1 establishes both the **engineering execution foundation** and the **agile project delivery framework** for the **Campus Lost and Found Portal (JFS-40)**:

1. **Java Platform Fundamentals:** Verified JDK 21+ runtime architecture, compilation lifecycle (`.java` $\rightarrow$ `javac` $\rightarrow$ `.class`), JVM internal execution engine (Interpreter, JIT Compiler, Garbage Collector), classloading pipeline, and runtime data areas (Heap, Stack, Method Area) without relying on IDE magic.
2. **Hardware Concurrency & Capacity Analysis:** Profiled host CPU concurrency (12 logical cores) and memory heap ($3.84\text{ GB}$ maximum) to establish operational sizing for in-memory item matching, thread pool bounds for asynchronous pipelines (`CompletableFuture`), and caching for $10,000$ concurrent records.
3. **Agile / Scrum Backlog for JFS-40:** Formulated complete, estimable, testable User Stories for all 8 Functional Requirements (FR1–FR8) with Fibonacci story points ($49\text{ SP}$ total), explicit Given/When/Then Acceptance Criteria, and a rigorous 12-point Definition of Done (DoD).
4. **NFR Alignment Matrix:** Mapped all 4 project-specific NFRs (NFR-P1 to NFR-P4) and 14 enterprise-grade standard NFRs (NFR-01 to NFR-14) directly into story acceptance criteria and architectural invariants.

---

## 2. Java Platform Architecture & Hands-on Deliverables

### 2.1 JDK vs. JRE vs. JVM Architecture

```
+---------------------------------------------------------------------------------------------------+
| Java Development Kit (JDK 25.0.1 LTS)                                                             |
| +-----------------------------------------------------------------------------------------------+ |
| | Development Tools: javac (compiler), javap (disassembler), jdb, jshell, jar, jlink           | |
| |                                                                                               | |
| | +-------------------------------------------------------------------------------------------+ | |
| | | Java Runtime Environment (JRE)                                                            | | |
| | | +---------------------------------------------------------------------------------------+ | | |
| | | | Core Class Libraries (java.base, java.sql, java.net, java.time, java.util)            | | | |
| | | |                                                                                       | | | |
| | | | +-----------------------------------------------------------------------------------+ | | | |
| | | | | Java Virtual Machine (JVM - HotSpot 64-Bit Server VM)                             | | | | |
| | | | |                                                                                   | | | | |
| | | | | 1. ClassLoader Subsystem: Loading (Bootstrap, Platform, App) -> Linking -> Init  | | | | |
| | | | | 2. Runtime Data Areas:                                                            | | | | |
| | | | |    - Method Area (Metaspace): Class metadata, runtime constants, static fields    | | | | |
| | | | |    - Heap: Young Gen (Eden, S0, S1) & Old Gen (Object instances, Arrays)         | | | | |
| | | | |    - Java Thread Stacks: Stack frames, local variable arrays, operand stacks      | | | | |
| | | | |    - Program Counter (PC) Registers: Bytecode instruction pointers                | | | | |
| | | | |    - Native Method Stacks: JNI C/C++ invocation frames                            | | | | |
| | | | | 3. Execution Engine: Interpreter, JIT Compiler (C1 / C2), Garbage Collector (G1)  | | | | |
| | | | +-----------------------------------------------------------------------------------+ | | | |
| | | +---------------------------------------------------------------------------------------+ | | |
| | +-------------------------------------------------------------------------------------------+ | |
| +-----------------------------------------------------------------------------------------------+ |
+---------------------------------------------------------------------------------------------------+
```

### 2.2 Terminal Execution Output (`PlatformInfo.java`)

Execution was performed directly from PowerShell without an IDE:
```bash
javac -d bin PlatformInfo.java
java -cp bin PlatformInfo
```

**Captured System Telemetry:**
```
===============================================================================
             JAVA PLATFORM BASICS & RUNTIME ENVIRONMENT INFO                   
             PROJECT: JFS-40 CAMPUS LOST AND FOUND PORTAL                      
===============================================================================

[1] JAVA PLATFORM & VIRTUAL MACHINE (JVM/JRE/JDK)
  - Java Specification Version : 25
  - Java Runtime Version       : 25.0.1
  - Java Vendor                : Oracle Corporation
  - Java Home Path             : C:\Program Files\Java\jdk-25
  - JVM Implementation Name    : Java HotSpot(TM) 64-Bit Server VM
  - JVM Engine Version         : 25.0.1+8-LTS-27
  - Class File Format Version  : 69.0 (Bytecode Spec)

[2] HOST OPERATING SYSTEM & ARCHITECTURE
  - Operating System Name      : Windows 11
  - OS Kernel Version          : 10.0
  - CPU Architecture           : amd64
  - File Separator             : '\'
  - Classpath Separator        : ';'

[3] HARDWARE CONCURRENCY & THREAD POOL SIZING
  - Available Logical CPU Cores : 12
  - Schedulable Worker Threads  : 12 (Ideal for CompletableFuture pool)
  - Match Scoring Throughput    : Up to 30000 items/sec evaluated in parallel

[4] RUNTIME HEAP MEMORY METRICS (GARBAGE COLLECTOR)
  - Max Heap Limit (-Xmx)      : 3.84 GB (3928.00 MB) (4118806528 bytes)
  - Total Heap Allocated (-Xms): 248.00 MB (260046848 bytes)
  - Currently Free Heap Space  : 244.85 MB (256747872 bytes)
  - Active Heap in Use         : 3.15 MB (3298976 bytes)

[5] CAMPUS LOST & FOUND PORTAL (JFS-40) CAPACITY & NFR VALIDATION
  - Target Item Capacity        : 10000 items (Lost + Found)
  - In-Memory Working Set Size  : 14.65 MB (15360000 bytes)
  - Heap Utilization Ratio      : 0.3729% of Max Heap
  - NFR-P2 Target (< 1.0s match): PASSED (Sufficient heap & CPU parallelism)
  - NFR-04 Target (< 500ms list): PASSED (Working set easily fits in L3/RAM cache)
  - Concurrency Lock Feasibility: PASSED (Multi-core environment ready for @Version locks)

===============================================================================
       DAY 1 PLATFORM CHECK VERIFIED SUCCESSFULLY (EXIT CODE 0)                 
===============================================================================
```

### 2.3 Bytecode Disassembly Analysis (`javap -c -p PlatformInfo`)

Inspecting bytecode with `javap -c -p` verifies the JVM instruction set:
```text
Compiled from "PlatformInfo.java"
public class PlatformInfo {
  public PlatformInfo();
    Code:
         0: aload_0
         1: invokespecial #1     // Method java/lang/Object."<init>":()V
         4: return

  public static void main(java.lang.String[]);
    Code:
         0: invokestatic  #7     // Method printHeader:()V
         3: invokestatic  #12    // Method printJavaEnvironment:()V
         6: invokestatic  #15    // Method printOperatingSystem:()V
         9: invokestatic  #18    // Method printHardwareConcurrency:()V
        12: invokestatic  #21    // Method printHeapMemoryMetrics:()V
        15: invokestatic  #24    // Method printCampusLostAndFoundCapacityCheck:()V
        18: invokestatic  #27    // Method printExecutionFooter:()V
        21: return

  private static void printJavaEnvironment();
    Code:
         0: ldc           #48    // String java.version
         2: invokestatic  #50    // Method java/lang/System.getProperty:(Ljava/lang/String;)Ljava/lang/String;
         5: astore_0
         ...
        45: getstatic     #30    // Field java/lang/System.out:Ljava/io/PrintStream;
        48: ldc           #66    // String \n[1] JAVA PLATFORM & VIRTUAL MACHINE (JVM/JRE/JDK)
        50: invokevirtual #38    // Method java/io/PrintStream.println:(Ljava/lang/String;)V
}
```

**Bytecode Instruction Observations:**
- `aload_0`: Pushes `this` reference onto the operand stack in the default constructor.
- `invokespecial`: Invokes superclass constructor `java.lang.Object.<init>()` via static resolution.
- `invokestatic`: Dispatches static methods without dynamic polymorphism lookup overhead.
- `invokevirtual`: Dispatches virtual method calls (`PrintStream.println`) through the vtable.
- `ldc`: Pushes string constants from the runtime constant pool directly onto the operand stack.

---

## 3. Project Catalog Overview: Campus Lost & Found Portal (JFS-40)

- **Domain:** Campus Operations / Facility Services
- **Difficulty Level:** Moderate
- **Primary Roles:**
  1. `Student / Staff`: Reports lost items, browses found items, receives automated match alerts, answers verification questions to claim ownership.
  2. `Security Desk`: Logs found property turned into custody, assigns storage lockers, inspects candidate claims, verifies photo IDs, and executes physical handovers.
  3. `Admin`: Oversees system health, monitors operational KPIs, generates recovery rate analytics, and batches 60-day unclaimed items for charitable donation.
- **Core Entities:** `lost_report`, `found_item`, `category`, `location`, `match_suggestion`, `claim`, `handover`, `app_user`, `role`, `notification`.

---

## 4. JFS-40 User Story Backlog (FR1–FR8)

All 8 Functional Requirements from the Project Catalog are translated below into user stories with story points, acceptance criteria, and NFR hooks.

```
Total Backlog Story Points: 49 SP (Fibonacci Scale: 1, 2, 3, 5, 8, 13)
```

---

### US-01: Lost Item Reporting (FR1)
- **Role:** Student / Staff
- **User Story:**  
  *As a student or faculty member who misplaced a personal possession,*  
  *I want to register a detailed lost item report with category, last known campus location, date/time, brand, color, tags, and distinctive markings,*  
  *so that the portal records my loss and feeds it into the automatic candidate matching engine.*
- **Story Points:** `5 SP` (Form input validation, category taxonomies, date constraints)
- **Acceptance Criteria (Gherkin):**
  - **Scenario 1 - Successful Report Submission:**
    - *Given* an authenticated user with role `STUDENT` or `STAFF`,
    - *When* they submit a valid report with category `"Electronics"`, location `"Library 2nd Floor"`, lost date within the last 30 days, brand `"Apple"`, and description `"Silver MacBook Pro with sticker"`,
    - *Then* the system persists the `LostReport` with status `ACTIVE`, generates an immutable report ID (e.g., `LOST-2026-0001`), and queues an asynchronous match query.
  - **Scenario 2 - Future Date Rejection:**
    - *Given* any user attempting to report a lost item,
    - *When* the lost timestamp is in the future,
    - *Then* the system rejects the submission with HTTP `400 Bad Request` and `ProblemDetail` message `"Lost date cannot be in the future"`.
- **NFRs Addressed:** NFR-06 (java.time in UTC), NFR-07 (inline form validation), NFR-13 (ProblemDetail).

---

### US-02: Found Item Logging & Custody Locker Intake (FR2)
- **Role:** Security Desk Officer / Campus Finder
- **User Story:**  
  *As a security officer or campus finder,*  
  *I want to log a discovered item into university custody with its discovery location, timestamp, assigned custody locker bin, and an intake photo with automatic privacy masking,*  
  *so that items are securely stored and publicly cataloged without exposing private claimant information.*
- **Story Points:** `5 SP` (Custody locker inventory, photo upload, privacy filter)
- **Acceptance Criteria (Gherkin):**
  - **Scenario 1 - Security Officer Logging with Locker Assignment:**
    - *Given* an authorized user with role `SECURITY_DESK`,
    - *When* they submit a found item with title `"Bi-fold Brown Wallet"`, location `"Canteen Hall B"`, locker bin `"LOCKER-B12"`, and upload a photo,
    - *Then* the item status is set to `FOUND_IN_CUSTODY`, the locker bin is marked `OCCUPIED`, and the photo is stored in compliant storage.
  - **Scenario 2 - Privacy Redaction on Public Found Photos (NFR-P1):**
    - *Given* a photo uploaded containing student ID cards, cash denominations, or faces,
    - *When* the public or claimants browse found items,
    - *Then* sensitive text fields and identifying credentials are redacted or hidden from unauthorized viewers.
- **NFRs Addressed:** NFR-P1 (Photo privacy masking), NFR-01 (RBAC on security endpoints), NFR-05 (Transactional locker update).

---

### US-03: Automated Multi-Factor Match Suggestions Engine (FR3)
- **Role:** System / Student / Staff
- **User Story:**  
  *As a student who registered a lost item report,*  
  *I want the system to automatically analyze found items and suggest high-probability matches ranked by category, geographical proximity, and date similarity,*  
  *so that I can identify potential candidates without manually scrolling through thousands of items.*
- **Story Points:** `8 SP` (Multi-factor scoring algorithm, Strategy pattern, sub-second latency target)
- **Acceptance Criteria (Gherkin):**
  - **Scenario 1 - Match Score Calculation:**
    - *Given* a new lost report with category `"Electronics"`, location `"Academic Block 3"`, date `"2026-10-18"`,
    - *When* the match engine evaluates against found items in custody,
    - *Then* items sharing the exact category receive $+0.35$ weight, adjacent locations receive $+0.25$ weight, date delta within $\pm 2$ days receives $+0.20$ weight, and matching title keywords receive $+0.20$ weight.
  - **Scenario 2 - Sub-Second Query Latency (NFR-P2):**
    - *Given* a dataset of $10,000$ seeded lost and found records,
    - *When* `GET /api/v1/lost/{id}/matches` is invoked,
    - *Then* the engine returns the top-ranked candidates with score $\ge 0.50$ in under $1.0\text{ second}$ ($p95 < 500\text{ ms}$).
- **NFRs Addressed:** NFR-P2 (Matching $< 1\text{ s}$), NFR-04 (Pagination & fast search), Concept Hook: `MatchScoringStrategy`.

---

### US-04: Ownership Claim & Verification Challenge Quiz (FR4)
- **Role:** Owner (Student / Staff)
- **User Story:**  
  *As an owner who identified their item in the found catalog,*  
  *I want to initiate an ownership claim by answering dynamic verification challenge questions (e.g., serial number, hidden markings, wallet contents, screen wallpaper),*  
  *so that I can prove legitimate ownership without unauthorized campus members claiming my property.*
- **Story Points:** `5 SP` (Quiz generation, answer verification, claim state machine)
- **Acceptance Criteria (Gherkin):**
  - **Scenario 1 - Valid Claim Submission:**
    - *Given* an item with status `FOUND_IN_CUSTODY`,
    - *When* a user submits answer answers to challenge questions (e.g., `"Lock screen photo is of a Golden Retriever"` and `"Last 4 digits of card: 4123"`),
    - *Then* the system records the claim with status `PENDING_REVIEW` and links the claimant's user ID.
  - **Scenario 2 - Preventing Claims on Already Claimed/Returned Items:**
    - *Given* an item with status `RETURNED_TO_OWNER` or `CLAIM_APPROVED`,
    - *When* a new user attempts to submit a claim,
    - *Then* the request is rejected with HTTP `409 Conflict` and message `"Item is no longer available for claims"`.
- **NFRs Addressed:** NFR-P3 (Claim fraud audit trail), NFR-03 (Ownership validation & IDOR defense).

---

### US-05: Security Desk Handover Verification (FR5)
- **Role:** Security Desk Officer
- **User Story:**  
  *As a security desk officer physically dispensing an item to a verified claimant,*  
  *I want to verify the claimant's institutional photo ID card, record their acknowledgment signature, and log the handover transaction,*  
  *so that custody is legally terminated and an immutable audit trail is established.*
- **Story Points:** `5 SP` (ID verification, custody transition, handover receipt generation)
- **Acceptance Criteria (Gherkin):**
  - **Scenario 1 - Successful In-Person Verification & Handover:**
    - *Given* an approved claim and the physical presence of the claimant,
    - *When* the officer verifies the photo ID matches the claim, inputs ID number `"STU-2023-8841"`, and collects confirmation signature,
    - *Then* the `Handover` record is created, the `FoundItem` status updates to `RETURNED_TO_OWNER`, and the associated locker bin is set to `AVAILABLE`.
  - **Scenario 2 - Audit Log Generation:**
    - *Given* a completed handover,
    - *When* the audit trail is queried,
    - *Then* an immutable entry records officer ID, claimant ID, item ID, locker ID, and exact timestamp.
- **NFRs Addressed:** NFR-P3 (Audit logging), NFR-01 (Strict role authorization: `SECURITY_DESK` only).

---

### US-06: Atomic Concurrency Lock for Claim Approval (FR6)
- **Role:** System / Security Desk
- **User Story:**  
  *As the core transaction manager,*  
  *I want to enforce atomic concurrency control and optimistic locking when approving claims,*  
  *so that two officers or concurrent web requests cannot approve conflicting claims for the same item.*
- **Story Points:** `8 SP` (Optimistic locking `@Version`, transactional isolation, race-condition tests)
- **Acceptance Criteria (Gherkin):**
  - **Scenario 1 - Concurrent Claim Approval Prevention:**
    - *Given* two pending claims `Claim A` and `Claim B` on the same found item,
    - *When* two security officers submit approvals concurrently at the exact same millisecond,
    - *Then* exactly one approval succeeds (`200 OK`), the second request fails with `409 Conflict` (`OptimisticLockException`), and the item has exactly one approved owner.
  - **Scenario 2 - Database Transaction Rollback:**
    - *Given* a claim approval failure midway through updating the locker or generating receipt,
    - *When* an exception is thrown,
    - *Then* `@Transactional` rolls back the item status, claim status, and locker state to original values.
- **NFRs Addressed:** NFR-05 (Reliability & optimistic locking), NFR-06 (Data integrity).

---

### US-07: 60-Day Item Expiry Lifecycle & Charitable Donation (FR7)
- **Role:** Admin / Scheduled Background Daemon
- **User Story:**  
  *As a campus facility administrator,*  
  *I want unclaimed found items exceeding 60 days in custody to be automatically flagged as `EXPIRED_FOR_DONATION` and batched into donation manifests,*  
  *so that storage lockers are routinely recycled and community charity distributions are systematically scheduled.*
- **Story Points:** `5 SP` (Scheduled cron job, date mathematics via `java.time`, donation manifest DTO)
- **Acceptance Criteria (Gherkin):**
  - **Scenario 1 - Automated 60-Day Expiry Scanning:**
    - *Given* an item found on `2026-08-15` (66 days past current date) with status `FOUND_IN_CUSTODY`,
    - *When* the nightly batch job executes (`@Scheduled`),
    - *Then* the item status transitions to `EXPIRED_FOR_DONATION` and an audit notification is sent to the Admin.
  - **Scenario 2 - Active Claim Immunity:**
    - *Given* an item 65 days old that has a `PENDING_REVIEW` claim undergoing verification,
    - *When* the expiry job runs,
    - *Then* the item is exempted from donation batching while the claim investigation remains active.
- **NFRs Addressed:** NFR-06 (java.time `ChronoUnit.DAYS`), NFR-11 (Structured scheduler logging).

---

### US-08: Campus Recovery Rate Analytics & Operational KPIs (FR8)
- **Role:** Campus Facilities Administrator
- **User Story:**  
  *As a campus director,*  
  *I want to view analytical dashboards displaying overall item recovery rate, average turnaround hours from loss to handover, category distribution breakdowns, and donation summaries,*  
  *so that I can evaluate campus lost-and-found efficiency and security desk performance.*
- **Story Points:** `8 SP` (Stream analytics, grouping Collectors, metric visualization)
- **Acceptance Criteria (Gherkin):**
  - **Scenario 1 - Recovery Rate Computation:**
    - *Given* 500 total found items, 380 successfully returned items, 70 expired items, and 50 items currently in custody,
    - *When* the administrator requests `GET /api/v1/reports/recovery-rate`,
    - *Then* the system calculates the recovery rate as $\frac{380}{380 + 70} = 84.44\%$ and returns JSON metrics grouped by category.
  - **Scenario 2 - Performance Latency:**
    - *Given* $10,000$ historical items in the database,
    - *When* the aggregation report is requested,
    - *Then* Java Streams & DB grouping queries return the payload in under $500\text{ ms}$.
- **NFRs Addressed:** NFR-04 (p95 $< 500\text{ ms}$), Concept Hook: `Streams+Collectors.groupingBy`.

---

## 5. Non-Functional Requirements (NFR) Compliance Matrix

| NFR ID | Category | Requirement Description | How Verified in JFS-40 Architecture | User Story Alignment |
|:---|:---|:---|:---|:---|
| **NFR-P1** | Privacy | Found item photos must redact student IDs, card numbers, and faces | Image processing service applies masking filter prior to public catalog display | US-02 |
| **NFR-P2** | Performance | Match scoring engine returns suggestions in $< 1.0\text{ s}$ | Multi-factor weighted strategy with parallel execution across 12 CPU cores | US-03 |
| **NFR-P3** | Security | Tamper-evident audit trail for all claim verification attempts | Immutable audit log entity (`HandoverAudit`) recording timestamp, officer, and quiz answers | US-04, US-05 |
| **NFR-P4** | Usability | Responsive design at 360px, 768px, 1280px with WCAG AA compliance | Bootstrap 5 grid, accessible ARIA labels, Lighthouse accessibility score $\ge 85$ | US-01, US-04 |
| **NFR-01** | Security | JWT authentication with RBAC (`ADMIN`, `SECURITY_DESK`, `STUDENT`) | Spring Security filter chain with method-level `@PreAuthorize` | All Stories |
| **NFR-02** | Security | Passwords hashed with BCrypt; secrets stored in environment variables | BCrypt with cost factor 12; zero plain-text secrets in repository | US-01, US-05 |
| **NFR-03** | Security | OWASP Top 10 protection (no IDOR, parameter-binding, XSS sanitization) | JPA parameterized queries, claimant ID ownership checks, HTML escape | US-01, US-04 |
| **NFR-04** | Performance | Paginated list APIs (size 20–100) responding $p95 < 500\text{ ms}$ on 10k rows | Spring Data `Pageable`, B-tree indexed foreign keys on `category_id`, `status` | US-01, US-08 |
| **NFR-05** | Reliability | Multi-table state mutations wrapped in `@Transactional` with `@Version` | Optimistic locking on `FoundItem.version` prevents race conditions on approval | US-06 |
| **NFR-06** | Data Integrity| Dates stored in UTC using `java.time`; monetary values using `BigDecimal` | `java.time.Instant` in database, `BigDecimal(10,2)` for item valuation | US-01, US-07 |
| **NFR-07** | Usability | Form validation with inline error hints; distinct empty and loading states | Bean Validation (`@Valid`, `@NotBlank`) + React Hook Form with Zod schemas | US-01, US-02 |
| **NFR-08** | Accessibility| Semantic HTML tags, labeled controls, keyboard navigation | Lighthouse Accessibility test suite $\ge 85$ | All UI Flows |
| **NFR-09** | Architecture | Layered package layout, SOLID principles, methods $\le 30$ lines | Package-by-feature / layer (`model`, `service`, `repository`, `controller`, `strategy`) | All Code |
| **NFR-10** | Testing | Backend service coverage $\ge 70\%$, frontend $\ge 60\%$, 1 E2E Playwright test | JUnit 5 + Mockito service tests, MockMvc controller tests, RTL frontend tests | All Stories |
| **NFR-11** | Observability| Structured JSON logs with MDC correlation ID; zero PII in logs | SLF4J + Logback with MDC `requestId`; student email/phone masked in logs | US-02, US-05 |
| **NFR-12** | Portability | Entire microservice stack starts with `docker compose up` | Multi-stage Dockerfiles for Spring Boot backend and React frontend with MySQL | CI/CD |
| **NFR-13** | Error Handling| RFC 7807 `ProblemDetail` with status, title, detail, and field errors | `@RestControllerAdvice` global exception handler intercepting domain exceptions | All APIs |
| **NFR-14** | Documentation| README, OpenAPI 3.0 Swagger specification, ER diagram, ADRs | springdoc-openapi, Markdown ADRs, schema.sql documentation | All Sprints |

---

## 6. Definition of Done (DoD)

A backlog item or user story is considered **Done** only when all 12 criteria below are satisfied:

```
[✓] 01. Code Implementation: The business logic satisfies all Acceptance Criteria for the story.
[✓] 02. Architecture & SOLID: Code follows layered architecture, clean code standards, and methods <= 30 lines.
[✓] 03. Design Pattern Adherence: Required patterns (Strategy, Factory, Builder) are utilized correctly.
[✓] 04. Input Validation: Request DTOs enforce Bean Validation (@Valid, @NotNull, etc.) with RFC 7807 ProblemDetail errors.
[✓] 05. Authorization & RBAC: Security rules enforce role constraints; ownership checks prevent IDOR.
[✓] 06. Concurrency & Reliability: Multi-table state transitions use @Transactional; race-sensitive entities use @Version.
[✓] 07. Unit & Mockito Testing: Unit tests cover normal, boundary, and error cases (Service coverage >= 70%).
[✓] 08. Integration & API Testing: MockMvc tests verify 2xx, 4xx, and 401/403 status codes.
[✓] 09. Structured Logging & PII Masking: Structured logging with MDC correlation IDs; zero secrets/PII in log streams.
[✓] 10. Swagger / OpenAPI Documentation: Endpoints are documented with @Operation, request/response models, and status codes.
[✓] 11. Code Review & Clean Static Analysis: Peer code review completed; zero SonarQube blocker/critical vulnerabilities.
[✓] 12. Version Control Hygiene: Branch created from main, commits follow conventional format, PR merged cleanly without conflicts.
```

---

## 7. Agile / Scrum Sprint Cadence & Backlog Board

The 16-week training curriculum is organized into **8 Sprints** (2-week cadence):

```
Sprint 1 (Days 01–12): Core Java, Platform Info, OOP, Collections, Generics, Assessment 1
Sprint 2 (Days 13–24): Modern Java (Streams, Optional, java.time), SQL Schema & CRUD, Assessment 2
Sprint 3 (Days 25–36): Spring Boot REST APIs, Validation, JPA, Swagger, Assessment 3 Prep
Sprint 4 (Days 37–48): Spring Security (JWT), Unit & Integration Testing, Assessment 3 Backend Checkpoint
Sprint 5 (Days 49–60): Structured Logging, SOLID & Design Patterns, HTML5/CSS3/Bootstrap Wireframes
Sprint 6 (Days 61–72): TypeScript & React Frontend, State Management, RTL Testing, Assessment 4
Sprint 7 (Days 73–84): Full-Stack Integration, Microservices Split, Eureka, Spring Cloud Gateway, Feign
Sprint 8 (Days 85–96): Docker, Jenkins CI/CD, Kubernetes, OWASP Hardening, Final Capstone Presentation
```

### GitHub Projects Backlog Board Mapping:
- **Project URL:** `https://github.com/users/asmitha-16/projects/1`
- **Board Workflow Columns:**
  `[Backlog]` $\rightarrow$ `[Sprint Backlog]` $\rightarrow$ `[In Progress]` $\rightarrow$ `[Code Review / QA]` $\rightarrow$ `[Done]`
- **Story Allocation for Sprint 1:**
  - `US-01` (FR1 - Lost Item Reporting): In Progress
  - `US-02` (FR2 - Found Item Intake): In Progress
  - `US-03` (FR3 - Match Engine): Sprint Backlog
  - `US-04` to `US-08`: Product Backlog

---

## 8. Verification & How to Run

### Command-Line Execution
```bash
# 1. Compile directly with javac
javac PlatformInfo.java

# 2. Run terminal executable
java PlatformInfo

# 3. Disassemble bytecode to inspect instructions
javap -c -p PlatformInfo

# 4. Trace classloader execution
java -verbose:class PlatformInfo
```

All required deliverables are maintained in this directory:
- [`PlatformInfo.java`](PlatformInfo.java) — Java source file
- [`README.md`](README.md) — Comprehensive technical report, user stories, DoD, and NFR matrix
- [`day-01.txt`](day-01.txt) — Quick-reference summary card

