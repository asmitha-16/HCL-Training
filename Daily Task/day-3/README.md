# Day 3: Control Flow & Maven Build Architecture

## Project: Campus Lost and Found Portal (JFS-40)
**Curriculum Modules:** Module 1 (Core Java Fundamentals) & Module 4 (Build Tools, IDE & Debugging)  
**Deliverables:** `CampusMenuApp.java`, `pom.xml`, `README.md`  
**Location:** `Daily Task/day-3/`

---

## 1. Executive Summary

Day 3 focuses on implementing robust, resilient **control flow structures** in Java and establishing standard **Maven build orchestration** directly tailored to the **Campus Lost and Found Portal**:

1. **Security Desk PIN Authentication (`while` loop + `break`):**
   - Implemented an authorization guard requiring a 4-digit Security PIN (`7376`).
   - Limits failed entries to a maximum of 3 attempts before locking access.
   - Leverages `break` to exit the loop immediately upon successful authentication.
2. **Interactive Operations Menu (`do-while` + `switch-case`):**
   - Guarantees the menu executes at least once and stays active until explicit termination (Option 10).
   - Directs all 8 functional requirements (FR1–FR8) via clean $O(1)$ switch dispatch.
3. **Defensive Input Validation (`continue`):**
   - Employs `scanner.hasNextInt()` to detect non-numeric input before reading.
   - Flushes bad tokens and calls `continue` to restart the prompt safely, guaranteeing **the application never crashes on invalid user input**.
4. **Enhanced `for` Loop:**
   - Traverses recent custody audit logs without index management overhead.
5. **Maven Project Architecture (`pom.xml`):**
   - Standard directory structure, Java 21 compiler configuration, and dual profiles (`dev` vs. `prod`) switching debug modes.

---

## 2. Control Flow Constructs Applied to Campus Lost & Found

| Construct | Syntax | JFS-40 Operational Role |
|:---|:---|:---|
| **`while`** | `while (attempts < 3) { ... }` | Security Desk 3-attempt PIN authentication guard |
| **`break`** | `if (pinValid) break;` | Exits authentication immediately upon correct PIN |
| **`do-while`** | `do { printMenu(); } while (sel != 10);` | Persistent interactive operations console loop |
| **`switch-case`** | `switch (selection) { case 1: ... }` | $O(1)$ dispatch across all 8 functional requirements |
| **`continue`** | `if (!valid) continue;` | Discards invalid scanner tokens and re-prompts user |
| **Enhanced `for`** | `for (String log : RECENT_AUDIT_LOGS)` | Clean sequential traversal of custody audit records |

---

## 3. Project Task: Campus Lost & Found Console Menu (`CampusMenuApp.java`)

Maps every single functional requirement (FR1–FR8) to an interactive handler:
- **PIN Verification:** Authenticates Security Desk officer (`7376`).
- **Option 1 (FR1):** Register lost property with category, location, and description.
- **Option 2 (FR2):** Log found item turned into custody, assign custody locker bin, and apply privacy masking (NFR-P1).
- **Option 3 (FR3):** Compute multi-factor candidate matches and demonstrate sub-second latency SLA (NFR-P2).
- **Option 4 (FR4):** Submit ownership verification claim with category-specific challenge questions.
- **Option 5 (FR5):** Perform security desk handover verification, check photo ID credential, and update custody status.
- **Option 6 (FR6):** Simulate atomic optimistic concurrency lock (`@Version`) preventing double-approvals.
- **Option 7 (FR7):** Scan custody items exceeding 60 days and batch into charitable donation manifests.
- **Option 8 (FR8):** Compute overall campus recovery rate percentage and operational KPIs.
- **Option 9:** View recent custody audit logs using enhanced `for` loop.
- **Option 10:** Exit application cleanly.

---

## 4. Maven Build Configuration (`pom.xml`)

### Profiles Configuration:
- **`dev` (Active by default):**
  - `<app.environment>development</app.environment>`
  - `<app.debug.enabled>true</app.debug.enabled>`
- **`prod`:**
  - `<app.environment>production</app.environment>`
  - `<app.debug.enabled>false</app.debug.enabled>`

---

## 5. How to Compile and Run

```bash
# Compile Day 3 source
javac -d bin CampusMenuApp.java

# Run JFS-40 Campus Lost & Found Console Menu
java -cp bin CampusMenuApp
```
