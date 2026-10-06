# Day 02 — Project Setup, Constants & Sample Data

## Objective
Set up the initial Campus Lost & Found Portal project repository and establish the foundation for business rules and sample data.

---

## Tasks Completed

### 1. Project Repository
Created the Campus Lost & Found Portal Git repository with:
- `.gitignore`
- `README.md`
- `Daily Task` directory
- Initial Java microservices source structure (`Project/src/main/java/...`)

### 2. Git Configuration
Configured `.gitignore` to exclude:
- Java compiled files (`*.class`)
- Maven/Gradle build output (`bin/`, `target/`)
- IDE configuration files (`.idea/`, `*.iml`, `.vscode/`)
- Logs (`hs_err_pid*`, `*.log`)
- Environment files
- Operating-system generated files (`Thumbs.db`, `.DS_Store`)

### 3. Business Rule Constants
Created a constants class to centralize initial Campus Lost & Found Portal business rules instead of scattering hard-coded values throughout the application.

Examples include:
- Unclaimed item expiry threshold (60 days for charity donation)
- Multi-factor match scoring weights (Category: 35%, Location: 25%, Date: 20%, Keywords: 20%)
- Match similarity score thresholds (Minimum candidate cutoff: 0.50, High-confidence tier: 0.75)
- Atomic claim approval lock timeout (preventing duplicate claims and race conditions)
- Privacy masking configuration for photos (obscuring student IDs, cards, faces)
- Authorized security handover identification types (`STUDENT_ID`, `EMPLOYEE_ID`, `GOVT_PHOTO_ID`)

### 4. Sample Data
Created sample campus lost-and-found data using Java arrays and records to represent initial domain information such as:
- Campus buildings & custody storage locker bins
- Item categories (Electronics, Wallets/Cards, Keys, Bags, Academic IDs)
- Registered lost item reports and logged found items
- Category-specific ownership verification challenge questions
- Application user personas (`STUDENT`, `SECURITY_DESK`, `ADMIN`)

This sample data will be replaced/evolved into persistent database-backed data as the microservices architecture is developed.

---

## Outcome
The initial project repository and Java foundation are ready for further development into the Campus Lost & Found Portal microservices architecture.

---

## Key Learning
Business rules should be centralized rather than hard-coded throughout the application. Sample data also provides an early way to validate domain requirements before introducing database persistence.
