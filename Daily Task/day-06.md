# Day 6 – BaseEntity, Role Hierarchy & Strategy Pattern

## Project: Campus Lost and Found Portal

### Objective
Implement a reusable base entity, establish a role hierarchy, and apply the Strategy Design Pattern with two implementations. Develop the changes on a feature branch and merge them into the main branch through a Pull Request (PR).

### Tasks Completed

- [ ] **BaseEntity Implementation**
  - Create a reusable `BaseEntity` class.
  - Define common entity properties such as `id`, `createdAt`, and `updatedAt`, where applicable.
  - Reuse common fields in entity classes through inheritance.

- [ ] **Role Hierarchy**
  - Establish a role hierarchy for the portal.
  - Define appropriate roles, such as `USER`, `ADMIN`, and `MODERATOR`, based on project requirements.
  - Apply role-based access control to relevant operations.

- [ ] **Strategy Design Pattern**
  - Create a common Strategy interface.
  - Implement two concrete strategy classes.
  - Use the interface to select the required behavior without tightly coupling the calling code to specific implementations.

- [ ] **Git Feature Branch Workflow**
  - Create a separate feature branch.
  - Implement and commit the changes on the feature branch.
  - Push the branch to the remote repository.
  - Create a Pull Request for review.
  - Merge the approved Pull Request into the main branch.

### Git Commands

**1. Switch to the latest main branch**
```bash
git checkout main
git pull origin main
```

**2. Create a feature branch**
```bash
git checkout -b feature/base-entity-role-strategy
```

**3. Stage and commit changes**
```bash
git add .
git commit -m "Add BaseEntity role hierarchy and Strategy pattern"
```

**4. Push the feature branch**
```bash
git push -u origin feature/base-entity-role-strategy
```

**5. Create a Pull Request**

- Open the repository on GitHub.
- Select **Compare & pull request**.
- Set the base branch to `main` and the compare branch to `feature/base-entity-role-strategy`.
- Add a description of the changes and submit the PR.
- Review, approve, and merge the PR when the changes are ready.

**6. Update the local main branch after merging**
```bash
git checkout main
git pull origin main
```

### Expected Outcome

- Common entity fields are reusable across applicable entities.
- Role hierarchy supports organized authorization.
- Two implementations share a common Strategy interface.
- Changes are isolated in a feature branch and integrated into `main` through a Pull Request.

### Learning Outcomes

- Understanding inheritance and reusable entity design.
- Understanding role hierarchies and authorization.
- Applying the Strategy Design Pattern.
- Practicing Git branching, commits, remote pushes, and Pull Requests.

### Status
In Progress / Completed — update this status after verifying the implementation and successful PR merge.
