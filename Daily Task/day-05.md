# Day 05 — BaseEntity, Role Hierarchy & Strategy Pattern

## Objective

Implement a reusable `BaseEntity`, establish a role hierarchy, and introduce a `Strategy` interface with two implementations. Develop the changes on a feature branch and merge them into the main branch through a Pull Request (PR).

---

## Tasks Completed

### 1. BaseEntity

- Created a common `BaseEntity` class to hold shared properties for entities.
- Reduced code duplication by reusing common fields and methods.
- Enabled other entity classes to inherit common functionality.

### 2. Role Hierarchy

- Designed a role hierarchy for the Campus Lost & Found Portal.
- Defined roles such as Student, Staff, and Admin based on their responsibilities.
- Organized role-specific permissions and behavior to improve maintainability.

### 3. Strategy Design Pattern

- Created a `Strategy` interface to define a common contract for interchangeable behaviors.
- Implemented two different strategies using the same interface.
- Enabled the application to switch between implementations without changing the client code.

### 4. Git Feature Branch Workflow

- Created a separate feature branch for development.
- Implemented BaseEntity, role hierarchy, and the Strategy pattern.
- Committed the changes to the feature branch.
- Prepared a Pull Request to merge the feature branch into the main branch.
- Reviewed and merged the changes through the PR workflow.

---

## Concepts Learned

- Object-Oriented Programming (OOP)
- Inheritance and code reusability
- Role hierarchy and permissions
- Interfaces and abstraction
- Strategy Design Pattern
- Git branching and merging
- Pull Requests and code review

---

## Expected Outcome

A more modular and maintainable Campus Lost & Found Portal with reusable base entities, a structured role hierarchy, interchangeable strategy implementations, and a collaborative Git workflow using feature branches and Pull Requests.

---

## Conclusion

Day 5 focused on improving the project structure through reusable entities, role-based organization, and the Strategy Design Pattern. The feature branch and Pull Request workflow also helped practice collaborative development and code integration.
