# CampusFound – Campus Lost & Found Portal
### Streamlining Campus Lost & Found Management for Faster, Secure Item Recovery

CampusFound is a centralized web-based Lost and Found management platform designed for colleges and universities. It simplifies the process of reporting lost items, registering found items, identifying possible matches, submitting ownership claims, verifying ownership, and completing secure handovers through the campus security desk.

The system replaces the traditional manual Lost and Found process with a structured digital workflow. It compares lost and found reports using category, location, and date, generates match suggestions, notifies users about potential matches, and provides a secure claim and handover process.

---

## Tech Stack

| Layer | Technologies |
|---|---|
| Frontend | React.js, HTML, CSS, Tailwind CSS |
| Backend | Java, Spring Boot |
| API | REST APIs |
| Database | MySQL |
| Authentication | Spring Security, JWT |
| Service Communication | OpenFeign |
| Testing | JUnit, Mockito |
| Build Tool | Maven |
| Version Control | Git, GitHub |
| Architecture | Service-Oriented / Microservices Architecture |

---

## Project Objectives

- Provide a centralized platform for managing campus Lost and Found activities.
- Allow students and staff to report lost items.
- Allow students, staff, and security personnel to register found items.
- Automatically suggest possible matches between lost and found items.
- Allow users to submit ownership claims.
- Verify ownership before releasing an item.
- Allow the security desk to record verified handovers.
- Prevent multiple approved claims for the same item.
- Maintain an audit trail for claims and handovers.
- Flag unclaimed items after 60 days.
- Provide administrators with recovery statistics.
- Provide a responsive and accessible user interface.

---

## User Roles

### Student / Staff

Students and staff can:

- Report lost items.
- View found items.
- Search for possible matches.
- Receive match notifications.
- Submit ownership claims.
- Answer ownership-verification questions.
- Track their lost reports.
- Track their claims and recovery status.

### Security Desk

Security personnel can:

- Register found items.
- Review ownership claims.
- Verify ownership.
- Approve or reject claims.
- Record item handovers.
- Manage items currently held at the security desk.

### Admin

Administrators can:

- Manage users and roles.
- Monitor lost and found activity.
- View claims and handovers.
- Monitor item recovery.
- View recovery statistics.
- Manage the overall Lost and Found process.

---

## Main Workflow

The complete workflow of CampusFound is:

Lost Item Report
        |
        v
Found Item Report
        |
        v
Automatic Match Suggestions
        |
        v
Ownership Claim
        |
        v
Security Verification
        |
        v
Claim Approval
        |
        v
Security Handover
        |
        v
Item Recovered

The system digitizes the complete recovery process from reporting an item to returning it to the verified owner.

---

## Core Features

### Lost Item Reporting

Users can report items that they have lost by providing relevant information such as:

- Item name
- Item description
- Category
- Location where the item was lost
- Date
- Additional identifying details

The lost report is stored in the system and becomes available to the matching engine.

### Found Item Reporting

Students, staff, or security personnel can register items that have been found on campus.

A found item can contain:

- Item description
- Category
- Found location
- Found date
- Photo
- Additional information

Sensitive details in found-item photos can be hidden or blurred to prevent unauthorized people from falsely claiming the item.

### Automatic Match Suggestions

The system automatically compares lost and found reports.

The matching system considers:

- Category
- Location
- Date

A match score is calculated based on these attributes.

Example:

Lost Item:

Category: Electronics
Location: Library
Date: 05-10-2026

The matching engine compares this information with available found items and generates possible matches.

The matching workflow is:

Lost Report
        |
        v
Category Comparison
        |
        v
Location Comparison
        |
        v
Date Comparison
        |
        v
Match Score
        |
        v
Potential Match Suggestions

The highest-scoring matches can be displayed first.

### Ownership Claims

When a user identifies a possible matching item, they can submit an ownership claim.

The claimant must answer verification questions about the item.

This provides an additional layer of ownership verification before the item is released.

### Security Verification

The security desk reviews the claim and verifies the information provided by the claimant.

Security can:

- Review the reported item.
- Review claimant information.
- Check ownership answers.
- Approve the claim.
- Reject the claim.
- Record the verification process.

### Handover Management

After successful ownership verification, the item is handed over through the security desk.

The system records the handover and updates the item status.

The process is:

Claim Submitted
        |
        v
Security Verification
        |
        v
Claim Approved
        |
        v
Physical Handover
        |
        v
Handover Recorded
        |
        v
Item Returned

### Claim Protection

The system prevents multiple claims from being approved for the same item.

This helps prevent:

- Duplicate ownership claims
- Fraudulent claims
- Conflicting approvals

### Item Expiry

Items that remain unclaimed for 60 days are flagged by the system.

After expiry, the item can be handled according to the campus policy, such as donation or another approved disposal process.

### Notifications

Users can receive notifications for important events such as:

- Potential item matches
- New match suggestions
- Claim submission
- Claim approval
- Claim rejection
- Handover updates

### Statistics

The administrator can view statistics such as:

- Total lost items
- Total found items
- Total claims
- Approved claims
- Rejected claims
- Successful handovers
- Recovered items
- Recovery rate

---

## System Architecture

CampusFound follows a service-oriented / microservices-style architecture.

The high-level architecture is:

                    CampusFound
                         |
                         v
                     Frontend
                         |
                         v
                    REST APIs
                         |
        +----------------+----------------+
        |                |                |
        v                v                v
  auth-service      item-service     claim-service
        |                |                |
        |                |                |
 Authentication    Lost Reports       Claims
 Users             Found Items        Verification
 Roles             Matching           Handover
 Authorization     Categories         Statistics
                   Locations
                         |
                         v
                       MySQL

The application is divided into separate services based on business responsibilities.

---

## Microservices

### M1 – Authentication and User Service

Service Name:

auth-service

Responsibilities:

- User authentication
- Login
- User management
- Role management
- Authorization
- JWT-based security

The service manages different roles such as Student/Staff, Security Desk, and Admin.

---

### M2 – Reports / Lost and Found Service

Service Name:

item-service

Responsibilities:

- Lost item reports
- Found item reports
- Categories
- Locations
- Item information
- Item status
- Matching functionality

This service manages the main Lost and Found domain.

---

### M3 – Matching Module

Service:

item-service

The matching module compares lost and found items using:

- Category
- Location
- Date

The matching engine calculates a score and generates potential matches.

The results can then be sorted according to the match score.

---

### M4 – Claims and Handover Service

Service Name:

claim-service

Responsibilities:

- Ownership claims
- Verification questions
- Claim approval
- Claim rejection
- Security verification
- Handover recording
- Claim audit trail

This service communicates with the item-service when it requires item information.

---

### M5 – Statistics Module

Service:

claim-service

Responsibilities:

- Recovery statistics
- Claim statistics
- Handover statistics
- Administrative reports
- Recovery rate

---

## Service-to-Service Communication

The services communicate using REST APIs.

OpenFeign is used for communication between microservices.

For example:

claim-service
       |
       | OpenFeign
       v
item-service

When the claim service needs information about a found item, it can call the item-service through a Feign client instead of directly accessing the item-service database.

This maintains separation between services.

---

## Domain Entities

The major entities in the system are:

- app_user
- role
- lost_report
- found_item
- category
- location
- match_suggestion
- claim
- handover
- notification

The main relationship is:

User
  |
  v
Lost Report
  |
  v
Match Suggestion
  |
  v
Found Item
  |
  v
Claim
  |
  v
Handover

Notifications are generated during important stages of the workflow.

---

## API Endpoints

### Create Lost Report

POST /api/v1/lost

Creates a new lost-item report.

### Create Found Item

POST /api/v1/found

Creates a new found-item report.

### Get Match Suggestions

GET /api/v1/lost/{id}/matches

Returns possible found-item matches for a particular lost report.

### Create Claim

POST /api/v1/found/{id}/claims

Creates an ownership claim for a found item.

---

## Java Design Patterns

The project also demonstrates important Java design patterns and modern Java features.

### Strategy Pattern

MatchScoringStrategy is used for matching logic.

The Strategy Pattern allows different matching algorithms to be implemented independently.

Example:

MatchScoringStrategy
        |
        +-- Category Matching
        |
        +-- Location Matching
        |
        +-- Date Matching

This makes the matching algorithm easier to modify or extend.

---

### Factory Pattern

ItemFactory is used to centralize the creation of item-related objects.

Instead of creating objects throughout different parts of the application, object creation can be handled through the factory.

---

### Builder Pattern

MatchResult can contain multiple pieces of information such as:

- Item ID
- Match score
- Category score
- Location score
- Date score
- Match reason

The Builder Pattern can be used to construct MatchResult objects cleanly.

---

### Java Records

Java records can be used for immutable DTOs and result objects.

Example:

public record MatchResult(
    Long itemId,
    double score,
    String reason
) {}

Records reduce boilerplate code and are useful when an object is mainly used to carry data.

---

### Sealed Classes

A sealed ItemStatus hierarchy can be used to restrict valid item states.

Example:

ItemStatus
    |
    +-- Lost
    +-- Found
    +-- Claimed
    +-- Returned

This provides better control over the valid states of an item.

---

### Java Streams

Streams can be used to process match suggestions.

For example:

Match Suggestions
        |
        v
Filter
        |
        v
Process
        |
        v
Sort by Score
        |
        v
Return Results

Streams make collection processing more concise and readable.

---

### CompletableFuture

CompletableFuture can be used for asynchronous operations.

For example, after a report is created, independent operations such as matching and notification processing can be handled asynchronously where appropriate.

Create Report
        |
        +---- Matching
        |
        +---- Notification

This can reduce unnecessary blocking of the main request.

---

### OpenFeign

OpenFeign is used for communication between microservices.

Example:

claim-service
       |
       v
Feign Client
       |
       v
item-service
       |
       v
Item Information

---

## Functional Requirements

FR1: User can report a lost item with details.

FR2: User or security can log a found item.

FR3: System suggests matches based on category, location, and date.

FR4: Owner can raise a claim by answering verification questions.

FR5: Security verifies and records the handover.

FR6: System prevents multiple approved claims for one item.

FR7: System flags unclaimed items after 60 days.

FR8: Admin can view recovery statistics.

---

## Non-Functional Requirements

### Performance

The matching operation should complete within 1 second under expected system conditions.

### Privacy

Found-item photos should hide sensitive identifying details.

### Auditability

Claims, verification actions, approvals, and handovers should maintain an audit trail.

### Accessibility

The application should be responsive and accessible across supported devices.

### Security

Authentication, authorization, and role-based permissions should protect restricted operations.

---

## Repository Structure

CampusFound/
|
+-- auth-service/
|   +-- src/
|   +-- pom.xml
|
+-- item-service/
|   +-- src/
|   +-- pom.xml
|
+-- claim-service/
|   +-- src/
|   +-- pom.xml
|
+-- frontend/
|   +-- src/
|   +-- public/
|   +-- package.json
|
+-- README.md
+-- pom.xml

The project repository can also contain the daily HCL training tasks separately from the main CampusFound implementation.

---

## Agile and Scrum

CampusFound is developed using Agile/Scrum principles as part of the HCL training.

The functional requirements are converted into user stories.

Example:

As a student or staff member, I want to report a lost item with relevant details so that the system can help me find a matching found item.

Another example:

As security personnel, I want to verify ownership claims so that only the legitimate owner receives the found item.

The development workflow is:

Requirement
        |
        v
User Story
        |
        v
Story Point Estimation
        |
        v
Sprint Planning
        |
        v
Development
        |
        v
Testing
        |
        v
Code Review
        |
        v
Definition of Done
        |
        v
Sprint Completion

---

## User Stories

### FR1 – Lost Item Report

As a student or staff member, I want to report a lost item with relevant details so that the system can help me find a matching found item.

### FR2 – Found Item Report

As a student, staff member, or security personnel, I want to report a found item so that its owner can potentially recover it.

### FR3 – Match Items

As a user who reported a lost item, I want the system to suggest possible matches based on category, location, and date so that I can identify my item.

### FR4 – Submit Claim

As an owner, I want to submit a claim and answer verification questions so that I can prove ownership of a found item.

### FR5 – Verify Handover

As security personnel, I want to verify an approved claim and record the handover so that the item is returned securely.

### FR6 – Prevent Duplicate Approval

As an administrator, I want the system to prevent multiple approved claims for the same item so that fraudulent or conflicting claims are avoided.

### FR7 – Expire Unclaimed Items

As an administrator, I want unclaimed items to be flagged after 60 days so that they can be handled according to campus policy.

### FR8 – View Statistics

As an administrator, I want to view recovery statistics so that I can monitor the effectiveness of the Lost and Found system.

---

## Story Point Estimation

FR1 – Lost Item Report: 3 points

FR2 – Found Item Report: 3 points

FR3 – Matching: 8 points

FR4 – Claims: 5 points

FR5 – Handover: 5 points

FR6 – Duplicate Claim Protection: 3 points

FR7 – Item Expiry: 3 points

FR8 – Statistics: 5 points

The matching functionality has a higher estimate because it contains more complex business logic involving multiple attributes and score calculation.

---

## Definition of Done

A feature is considered complete when:

- Implementation is completed.
- Functional requirements are satisfied.
- Validation is implemented.
- Error handling is implemented.
- Unit tests are written and passing.
- API behavior is tested.
- Database changes are completed where required.
- Authentication and authorization are applied where required.
- Code is committed to Git.
- Code review is completed where applicable.
- No critical defects remain.
- Acceptance criteria are satisfied.

---

## HCL Training Integration

CampusFound is the main practical project used to apply the concepts learned during the HCL training.

The daily training tasks are connected to different parts of the project.

The overall learning path is:

Java Platform Basics
        |
        v
Object-Oriented Programming
        |
        v
Collections and Generics
        |
        v
Modern Java Features
        |
        v
Design Patterns
        |
        v
Spring Boot
        |
        v
REST APIs
        |
        v
JPA and Database
        |
        v
Microservices
        |
        v
OpenFeign
        |
        v
Asynchronous Processing
        |
        v
JUnit and Mockito
        |
        v
CampusFound Implementation

Examples:

OOP
→ Used for domain models, services, controllers, and business logic.

Interfaces
→ Used for defining contracts such as matching strategies.

Collections
→ Used to store and process reports, claims, and match suggestions.

Generics
→ Used to create reusable and type-safe components.

Streams
→ Used to filter and sort match suggestions.

Records
→ Used for immutable DTOs and result objects.

Sealed Classes
→ Used to control valid item states.

Strategy Pattern
→ Used for match-scoring logic.

Factory Pattern
→ Used for item object creation.

Builder Pattern
→ Used for constructing MatchResult objects.

CompletableFuture
→ Used for asynchronous processing.

Spring Boot
→ Used to build backend services.

REST APIs
→ Used for frontend-backend communication.

OpenFeign
→ Used for service-to-service communication.

JPA
→ Used for database persistence.

JUnit and Mockito
→ Used for testing.

Agile/Scrum
→ Used to organize development into user stories, sprints, estimates, standups, reviews, and retrospectives.

---

## Daily Task Application

Each HCL training task is connected to the CampusFound project.

For example, Day 1 covers Java Platform Basics and Agile/Scrum.

Java Platform Basics can be applied by understanding how the CampusFound Java services are compiled and executed.

The flow is:

.java Source Code
        |
        v
javac Compiler
        |
        v
Bytecode (.class)
        |
        v
JVM
        |
        +-- Class Loader
        +-- Runtime Data Areas
        +-- Execution Engine
        +-- Garbage Collector
        |
        v
Application Execution

The Agile/Scrum part is applied by converting the eight functional requirements into user stories, assigning story points, planning them into sprints, and defining a Definition of Done.

---

## Daily-Life Problem Solved by CampusFound

CampusFound is based on a common real-world campus problem.

For example:

A student loses a wallet in the college library.

Normally, the student may have to:

- Ask classmates.
- Contact the security desk.
- Search manually.
- Check whether someone submitted the wallet.
- Repeatedly visit the security desk.

With CampusFound:

Student reports the lost wallet
        |
        v
System stores the report
        |
        v
Another person finds the wallet
        |
        v
Found item is registered
        |
        v
Matching engine compares both reports
        |
        v
Potential match is generated
        |
        v
Student receives notification
        |
        v
Student submits ownership claim
        |
        v
Student answers verification questions
        |
        v
Security verifies ownership
        |
        v
Security records handover
        |
        v
Student gets the wallet back

This is how the project solves a practical daily campus problem.

---

## Example Real-World Scenario

Suppose a student loses a black backpack near the library.

The student opens CampusFound and submits:

Category: Bag
Color: Black
Location: Library
Date: 05-10-2026
Description: Black backpack with specific contents

Later, another student finds a black backpack near the library and submits a Found Item Report.

The system compares:

Category → Bag
Location → Library
Date → 05-10-2026

The matching engine identifies the found backpack as a potential match and assigns a high match score.

The original student receives a notification.

The student submits a claim and answers ownership questions, for example:

- What was inside the bag?
- What is a unique mark on the bag?
- What brand is the bag?
- What specific item was stored inside?

The security desk checks the answers and verifies the claim.

If the claim is valid:

Claim → Approved
        |
        v
Security Handover
        |
        v
Item Status → Returned

The complete process is recorded in the system.

---

## Future Enhancements

- AI-based item matching.
- Image-based similarity detection.
- Advanced fraud detection.
- Real-time notifications.
- Mobile application.
- Advanced analytics dashboard.
- QR-based handover verification.
- Digital signatures for handover confirmation.
- Cloud deployment.
- Improved role-based access control.
- Machine-learning-based match scoring.
- Advanced search and filtering.

---

## Expected Outcome

CampusFound provides a centralized, secure, and traceable platform for managing lost and found items within a campus.

The system reduces the manual effort required to recover lost belongings and provides a structured workflow from reporting to verified handover.

The project demonstrates practical application of:

- Java
- Object-Oriented Programming
- Modern Java features
- Design Patterns
- Spring Boot
- REST APIs
- Microservices
- MySQL
- JPA
- OpenFeign
- Asynchronous Programming
- JUnit
- Mockito
- Agile
- Scrum
- Git and GitHub

---

## Project Summary

CampusFound is a Campus Lost and Found Portal that connects people who lose items with people or security personnel who find them.

The core workflow is:

Report Lost Item
        |
        v
Report Found Item
        |
        v
Match Lost and Found Reports
        |
        v
Notify User
        |
        v
Submit Ownership Claim
        |
        v
Security Verification
        |
        v
Claim Approval
        |
        v
Security Handover
        |
        v
Item Recovered

The project is developed as part of the HCL Training program. Each daily training task introduces a technical or software-engineering concept that is applied to the CampusFound project.

The overall goal is to build a practical, maintainable, secure, and scalable Lost and Found management system while applying Java, Spring Boot, REST APIs, microservices, database concepts, design patterns, testing, and Agile software development.

---

## Author

ASMITHA B

Bachelor of Engineering
Computer Science and Design

CampusFound transforms the traditional campus Lost and Found process into a centralized, automated, secure, and traceable digital platform for faster and safer item recovery.
