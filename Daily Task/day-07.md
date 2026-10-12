# Day 7 – Exception Handling and Business Rule Validation

## Project: Campus Lost and Found Portal

### Objective
Implement a dedicated exception package, enforce two business rules, and ensure that the application menu continues to function even when an operation encounters an error.

### Tasks

- [ ] **Create an Exception Package**
  - Create a separate package to organize custom exceptions.
  - Implement meaningful exception classes for business validation failures.
  - Provide clear error messages to help users understand what went wrong.

- [ ] **Enforce Business Rule 1: Duplicate Item Prevention**
  - Prevent users from reporting the same item multiple times when the reports match the project's defined duplicate criteria.
  - Throw a custom exception when a duplicate report is detected.
  - Display an appropriate error message instead of creating another report.

- [ ] **Enforce Business Rule 2: Claim Verification**
  - Allow an item to be claimed only when it has been reported as found and is available for claiming.
  - Reject claims for items that are already claimed, handed over, or otherwise unavailable.
  - Throw a custom exception when the claim violates the business rule.

- [ ] **Menu Recovery After Errors**
  - Handle exceptions without terminating the application unexpectedly.
  - Display a meaningful error message when an operation fails.
  - Return to the main menu after handling the error.
  - Allow users to continue performing other operations.

### Suggested Package Structure

```text
src/
└── main/
    └── java/
        └── com/campus/lostfound/
            ├── entity/
            ├── service/
            ├── repository/
            ├── exception/
            │   ├── DuplicateItemException.java
            │   └── InvalidClaimException.java
            └── Main.java
```

*Adjust the package names and directory structure to match your existing project.*

### Implementation Guidelines

1. Create custom exceptions for the selected business rules.
2. Validate input and business conditions in the service layer.
3. Throw the relevant exception when a rule is violated.
4. Catch exceptions at the appropriate application or menu boundary.
5. Ensure that the menu loop continues after an error.

### Git Workflow

Create a feature branch:

```bash
git checkout main
git pull origin main
git checkout -b feature/exception-handling
```

Commit and push your changes:

```bash
git add .
git commit -m "Add custom exceptions and business rule validation"
git push -u origin feature/exception-handling
```

Then create a Pull Request on GitHub, review the changes, and merge into `main` when approved.

### Expected Outcome

- A dedicated exception package organizes custom exceptions.
- Duplicate item reports are rejected according to the defined criteria.
- Invalid or unavailable item claims are prevented.
- Error messages are clear and meaningful.
- The menu recovers after an operation fails.
- Changes are integrated through a feature branch and Pull Request.

### Testing Checklist

- [ ] Valid item reports are accepted.
- [ ] Duplicate reports trigger the appropriate exception.
- [ ] Valid claims are processed successfully.
- [ ] Invalid claims trigger the appropriate exception.
- [ ] The application returns to the menu after an error.
- [ ] Subsequent operations work correctly after a failed operation.

### Status
In Progress / Completed — update after implementation and testing.
```

