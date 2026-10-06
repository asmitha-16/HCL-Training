# HCL Training — Java Full-Stack & Agile Sprint Workspace

**Engineer:** Asmitha B ([@asmitha-16](https://github.com/asmitha-16))  
**Organization:** HCL Training  
**Repository Structure:**
- `Folder 1: Daily Task/` — Daily hands-on coding exercises, platform verification, bytecode disassembly, and theoretical deep-dives.
- `Folder 2: Project/` — Enterprise Campus Lost & Found Platform microservices codebase, comprehensive project README with 8 User Stories, story point estimation, Definition of Done, GoF patterns, and automated tests.

---

## 📁 Repository Directory Structure

```text
HCL Training/
│
├── Daily Task/
│   ├── README.md                      # Index tracker for all daily tasks
│   └── Day-01/                        # Day 1: Java Platform Basics + Agile/Scrum Basics
│       ├── PlatformInfo.java          # Java runtime & heap inspector program
│       ├── PlatformInfo.class         # Compiled bytecode (compiled via javac)
│       ├── javap_bytecode.txt         # Bytecode disassembly generated via javap -c
│       ├── verbose_class_output.txt   # Class loading trace from java -verbose:class
│       └── README.md                  # Comprehensive theory: JVM/JRE/JDK, ClassLoader, Memory, Agile, DoD
│
├── Project/
│   ├── README.md                      # [MUST] Full Project Documentation: 8 User Stories, DoD, Architecture
│   ├── pom.xml                        # Maven project descriptor
│   ├── run.bat                        # Windows CMD launcher
│   ├── run.ps1                        # PowerShell launcher
│   ├── src/                           # Complete microservices domain implementation & tests
│   │   ├── main/java/com/campus/lostandfound/...
│   │   ├── main/resources/static/...  # Web portal UI (HTML/CSS/JS)
│   │   └── test/java/com/campus/lostandfound/...
│   └── bin/                           # Compiled binaries & test classes
│
└── README.md                          # Top-level workspace overview
```

---

## 🚀 Quick Execution Guide

### 1. Execute Day-1 Daily Task:
```powershell
cd "Daily Task\Day-01"
javac PlatformInfo.java
java PlatformInfo
& "C:\Program Files\Java\jdk-25\bin\javap.exe" -c PlatformInfo
java -verbose:class PlatformInfo
```

### 2. Run Project Microservices Portal:
```powershell
cd "Project"
.\run.ps1
# Open http://localhost:8080 in your web browser
```

### 3. Run Project Automated Tests (All 8 FRs + NFRs):
```powershell
cd "Project"
$sources = Get-ChildItem -Path "src" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }
javac -d "bin" $sources
java -ea -cp "bin" com.campus.lostandfound.TestRunner
```

---

## 📅 Daily Curriculum Progress Map

| Day | Module / Track | Daily Task Deliverables | Project Deliverables | Status |
| :---: | :--- | :--- | :--- | :---: |
| **Day 01** | **Java Platform Basics + Agile/Scrum Basics** | `PlatformInfo.java`, CLI run, `javap -c`, `-verbose:class`, JVM architecture & Scrum notes | 8 FRs converted to User Stories with story points, DoD, Project Catalog specs in `Project/README.md` | **COMPLETED** |
| **Day 02** | OOP & Java Type System | Encapsulation, Records, Sealed Hierarchies | Domain Model Refactoring | Planned |
| **Day 03** | Collections & Streams API | Pipeline operations, Collectors, Parallel Streams | Scoring Strategy Enhancements | Planned |
| **Day 04** | Concurrency & Async I/O | Virtual Threads, `CompletableFuture`, Atomic Locks | Thread-safe Claim Engine | Planned |
| **Day 05** | GoF Design Patterns | Strategy, Factory, Builder, Observer | Microservice Client Interop | Planned |
