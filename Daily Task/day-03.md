# Day 03 — Java OOP & Domain Modeling

## Objective
Convert the Campus Lost & Found Portal requirements into Java domain models using Object-Oriented Programming (OOP) concepts. The domain models represent users, lost reports, found items, claims, handovers, locations, categories, and item statuses that establish the core CampusFound domain workflow.

---

## Tasks Completed

### 1. User Domain Model (DM-01)
Created the `User` domain model to represent campus participants across all personas:
- **Attributes:** User ID, Full Name, Campus Email, Phone Number, and User Role.
- **Role Assignment:** Enforced via `UserRole` enum (`STUDENT`, `STAFF`, `SECURITY_OFFICER`, `ADMIN`) ensuring strict Role-Based Access Control (RBAC).

---

### 2. Lost Report Domain Model (DM-02)
Created the `LostReport` model to encapsulate lost item submissions (FR1):
- **Attributes:** Report ID, Item Name, Category, Last Known Campus Location, Lost Date, Brand, Color, Description, Distinctive Markings, and Reporter Contact.
- **Purpose:** Serves as the query input model evaluated by the automated candidate matching engine.

---

### 3. Found Item Domain Model (DM-03)
Created the `FoundItem` model to encapsulate found inventory held in university custody (FR2):
- **Attributes:** Item ID, Item Name, Category, Found Location, Found Date, Brand, Color, Description, Photo URL, Storage Locker ID, and Item Status.
- **Lifecycle Tracking:** Enforces the item's state transitions across custody, review, claim, handover, or 60-day charitable donation.

---

### 4. Claim Domain Model (DM-04)
Created the `Claim` model representing an ownership claim submitted for found property (FR4):
- **Attributes:** Claim ID, Found Item Reference, Claimant Details, Verification Quiz Answers, and Claim Status (`PENDING`, `APPROVED`, `REJECTED`).
- **Fraud Prevention:** Links category-specific challenge answers (e.g., serial numbers, wallpaper descriptions, card names) to claimant identity before release approval.

---

### 5. Handover Domain Model (DM-05)
Created the `Handover` model to maintain a verified chain-of-custody log (FR5):
- **Attributes:** Handover ID, Found Item Reference, Approved Claim Reference, Authorizing Security Officer, Handover Timestamp, and Digital Signature Confirmation.
- **Integrity:** Records final physical item disbursement and triggers transition to `RETURNED_TO_OWNER`.

---

## Domain Enums & State Management

### User Roles
- `STUDENT` — Campus learner reporting lost property or browsing found items.
- `STAFF` — Faculty or employee using campus facilities.
- `SECURITY_OFFICER` — Custody desk officer inspecting items, lockers, and handovers.
- `ADMIN` — Facility director monitoring KPIs, recovery rates, and donation manifests.

### Item Categories
- `ELECTRONICS` (Laptops, Phones, Tablets, Audio devices)
- `WALLET_CARD` (Bi-fold wallets, Campus ID cards, Payment cards)
- `KEYS` (Hostel keys, Vehicle keys, Access fobs)
- `BAGS` (Backpacks, Totes, Handbags)
- `ACADEMIC_ID` (Student ID cards, Lab passes)
- `CLOTHING` (Jackets, Lab coats, Sportswear)
- `ACCESSORIES` (Watches, Eyewear, Jewelry)
- `OTHER` (General stationery and miscellaneous assets)

### Item Lifecycle States
- `LOST` — Registered report actively searching for matches.
- `FOUND` — Discovered item secured into a custody locker.
- `CLAIMED` — Verified claim currently pending review or approval.
- `RETURNED` — Successfully disbursed to verified owner via signed handover.
- `EXPIRED` — Unclaimed property past 60 days batched for charitable donation.

### Claim States
- `PENDING` — Submitted claim awaiting security desk verification review.
- `APPROVED` — Ownership verified; claimant authorized to collect item.
- `REJECTED` — Challenge answers mismatched or rejected.

---

## OOP Concepts Applied

### 1. Encapsulation
- All domain entity fields are declared `private` to safeguard internal state.
- Controlled mutation is provided through explicit getters, constructors, and domain transition methods.

### 2. Abstraction
- Business capabilities are exposed through semantic domain operations (e.g., `approveClaim()`, `recordHandover()`, `flagForDonation()`) hiding complex internal mechanics from service callers.

### 3. Composition
- Real-world campus relationships are modeled using reference composition:
  - `LostReport` → `User` (Reporter) & `Location`
  - `FoundItem` → `Location` & `StorageLocker`
  - `Claim` → `User` (Claimant) & `FoundItem`
  - `Handover` → `Claim` & `SecurityOfficer`

---

## CampusFound Item Lifecycle Flow

```text
[Student / Staff Report] ───────► LostReport
                                      │
                               (Matching Engine)
                                      ▼
[Discovered Property]    ───────► FoundItem (Locker Custody)
                                      │
                              (Ownership Claim)
                                      ▼
[Verification Quiz]      ───────► Claim (PENDING)
                                      │
                              (Security Review)
                                      ▼
                              Claim (APPROVED)
                                      │
                          (ID Check & Signature)
                                      ▼
                                   Handover
                                      │
                                      ▼
                              RETURNED TO OWNER
```

*For Unclaimed Property:*
```text
FoundItem (In Locker) ───[> 60 Days Unclaimed]───► EXPIRED_FOR_DONATION ───► Charity Manifest
```

---

## Acceptance Criteria
Day 3 is considered complete when:

- [x] User domain model is implemented.
- [x] Lost report domain model is implemented.
- [x] Found item domain model is implemented.
- [x] Claim domain model is implemented.
- [x] Handover domain model is implemented.
- [x] Campus location model is implemented.
- [x] User roles are represented using an enum.
- [x] Item categories are represented using an enum.
- [x] Item statuses are represented using an enum.
- [x] Claim statuses are represented using an enum.
- [x] Domain fields are properly encapsulated with private modifiers.
- [x] Robust constructors and accessors are implemented.
- [x] Entity composition accurately reflects domain relationships.
- [x] The complete item lifecycle (Lost → Found → Claim → Handover → Returned / Expired) is represented.
- [x] Changes are committed and pushed to the Git repository.

---

## Outcome
The core domain entities, relationships, state machines, and business operations are established in Java OOP models, preparing the codebase for service orchestration and persistence layers.

---

## Key Learning
Through this task, the Campus Lost & Found functional requirements were translated into structured, maintainable Java domain models. Leveraging classes, records, enums, encapsulation, abstraction, and composition creates a cohesive domain model that supports algorithmic matching, ownership verification, concurrency control, security handovers, and policy-driven 60-day expiry workflows.
