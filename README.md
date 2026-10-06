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

### M3 – Matching Module

Service:

item-service

The matching module compares lost and found items using:

- Category
- Location
- Date

The matching engine calculates a score and generates potential matches.

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

### M5 – Statistics Module

Service:

claim-service

Responsibilities:

- Recovery statistics
- Claim statistics
- Handover statistics
- Administrative reporting

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

### Factory Pattern

ItemFactory is used to centralize the creation of item-related objects.

Instead of creating objects throughout different parts of the application, object creation can be handled through the factory.

### Builder Pattern

MatchResult can contain multiple pieces of information such as:

- Item ID
- Match score
- Category score
- Location score
- Date score
- Match reason

The Builder Pattern can be used to construct MatchResult objects cleanly.

### Java Records

Java records can be used for immutable DTOs and result objects.

Example:

public record MatchResult(
    Long itemId,
    double score,
    String reason
) {}

Records reduce boilerplate code and are useful when an object is mainly used to carry data.

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

### CompletableFuture

CompletableFuture can be used for asynchronous operations.

For example, after a report is created, independent operations such as matching and notification processing can be handled asynchronously where appropriate.

Create Report
        |
        +---- Matching
        |
        +---- Notification

This can reduce unnecessary blocking of the main request.

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

## Security and Reliability

- Role-based access control.
- Authentication and authorization.
- Protected found-item information.
- Ownership verification.
- Prevention of duplicate approved claims.
- Claim and handover audit trail.
- Controlled security-desk operations.

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

## Application Workflow

User Reports Lost Item
        |
        v
System Stores Lost Report
        |
        v
Found Item Is Reported
        |
        v
Matching Engine Compares Reports
        |
        v
Match Score Is Calculated
        |
        v
Potential Match Is Suggested
        |
        v
Owner Submits Claim
        |
        v
Ownership Questions
        |
        v
Security Verification
        |
        v
Claim Approved
        |
        v
Security Records Handover
        |
        v
Item Recovered

---

## Real-World Example

Suppose a student loses a black backpack near the college library.

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

The student submits a claim and answers ownership questions such as:

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

The project demonstrates the practical application of:

- Java
- Object-Oriented Programming
- Spring Boot
- REST APIs
- Microservices
- MySQL
- JPA
- OpenFeign
- Asynchronous Programming
- Design Patterns
- JUnit
- Mockito
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

CampusFound transforms the traditional campus Lost and Found process into a centralized, automated, secure, and traceable digital platform for faster and safer item recovery.

---

## Author

ASMITHA B

Bachelor of Engineering
Computer Science and Design
