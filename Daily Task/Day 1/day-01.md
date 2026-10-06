# Day 01 — User Stories, Story Points & Definition of Done

## Objective
Convert the Campus Lost & Found Portal functional requirements into user stories with story-point estimates and a common Definition of Done.

---

## User Stories

### US-01 — Lost Item Reporting
As a Student or Staff member,  
I want to report a lost item with details such as category, last known location, lost date, brand, color, description, and distinctive markings,  
so that the campus lost and found system can record it and look for matching items.

**Story Points:** 5

---

### US-02 — Found Item Logging & Custody Intake
As a Security Officer or Campus Member,  
I want to log a found item with item details, storage locker location, and photo upload,  
so that the found item is securely registered in custody without exposing sensitive details.

**Story Points:** 5

---

### US-03 — Automated Match Suggestions
As a Student or Staff member who reported a lost item,  
I want the system to suggest potential matching items based on category, location proximity, and date delta,  
so that I can quickly identify candidate items that match my report.

**Story Points:** 8

---

### US-04 — Ownership Claim & Verification Quiz
As an Owner,  
I want to submit an ownership claim by answering specific verification questions about the item,  
so that I can prove ownership before the item is released.

**Story Points:** 5

---

### US-05 — Security Handover Verification
As a Security Officer,  
I want to verify the claimant's photo ID and record their signature during physical handover,  
so that items are securely released and full custody records are maintained.

**Story Points:** 5

---

### US-06 — Atomic Concurrency Lock for Claims
As the System,  
I want to enforce atomic locking when a claim is approved,  
so that multiple approved claims for the same item are prevented under concurrent requests.

**Story Points:** 8

---

### US-07 — 60-Day Item Expiry & Donation
As an Admin,  
I want unclaimed items to be automatically flagged after 60 days for charitable donation,  
so that campus custody storage lockers are cleared systematically according to policy.

**Story Points:** 8

---

### US-08 — Recovery Rate Analytics & Reports
As an Admin,  
I want to view recovery rate statistics, turnaround times, and category distributions,  
so that I can evaluate lost and found operations and campus resolution rates.

**Story Points:** 8

---

## Definition of Done
A user story is considered complete when:

- The required functionality is implemented.
- Business rules are validated.
- Appropriate authorization is enforced.
- API responses and error handling are implemented.
- Database changes are completed where required.
- Unit/integration tests are added where applicable.
- The functionality is manually tested.
- No known critical defects remain.
- Code follows the project's coding standards.
- Changes are reviewed.
- Documentation is updated where necessary.
- Changes are committed and pushed to the Git repository.

---

## GitHub Project
The user stories are added to the project board and can be tracked through stages such as:

`Backlog` → `To Do` → `In Progress` → `Review` → `Done`

Each story is assigned its corresponding story-point estimate.

---

## Key Learning
Through this task, the functional requirements were converted into user-focused, independently trackable work items. Story points were assigned based on the relative complexity of implementation, dependencies, validation, and testing.
