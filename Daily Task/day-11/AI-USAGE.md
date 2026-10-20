# AI-USAGE.md — Core Java Copilot Review Log

## Project: Campus Lost and Found Portal (JFS-40)
**Day 11 Deliverable:** Code Review Log of AI-Generated OOP Implementations

---

## Executive Summary

During Sprint 1 development of the Campus Lost and Found Portal, GitHub Copilot was utilized to generate boilerplate data models, service interfaces, and collection operations. All AI outputs were subjected to a 6-point review checklist:
1. Encapsulation (Private fields with validated getters/setters)
2. Immutability & Defensive Copying (No leaking of mutable internal collections)
3. Object Identity Contract (`equals()` and `hashCode()` consistency)
4. Safe Null Handling (No unshielded method chaining)
5. Optimal Collection Choice ($O(1)$ lookups vs. naive $O(N)$ scans)
6. Robust Exception Handling (Zero swallowed exceptions; explicit domain propagation)

A total of **5 genuine defects** were caught, analyzed, and corrected as detailed below.

---

## Defect Log 1: Exposed Public Fields in `LostReport`

- **Prompt Used:** *"Generate a Java class for a lost item report in a campus portal."*
- **AI-Generated Code (Flawed):**
  ```java
  public class LostReport {
      public String id;
      public String title;
      public String category;
      public String location;
  }
  ```
- **Technical Critique:** Violates Object-Oriented Encapsulation. Callers could directly mutate `report.id = null` or assign blank titles after instantiation without validation.
- **Corrected Code:**
  ```java
  public class LostReport {
      private final String id;
      private final String title;
      private final String category;
      private final String location;

      public LostReport(String id, String title, String category, String location) {
          if (id == null || id.isBlank()) throw new IllegalArgumentException("ID cannot be blank");
          this.id = id.trim();
          this.title = Objects.requireNonNull(title, "Title required").trim();
          this.category = Objects.requireNonNull(category, "Category required").toUpperCase();
          this.location = (location != null) ? location.trim() : "CAMPUS_UNKNOWN";
      }
      public String getId() { return id; }
      public String getTitle() { return title; }
      public String getCategory() { return category; }
      public String getLocation() { return location; }
  }
  ```

---

## Defect Log 2: Leaking Mutable Internal Collection in `LostItemService`

- **Prompt Used:** *"Write a service class to store and return all registered lost items."*
- **AI-Generated Code (Flawed):**
  ```java
  public class LostItemService {
      private List<LostReport> reports = new ArrayList<>();
      public List<LostReport> getAllReports() {
          return reports; // LEAK! Direct reference exposure
      }
  }
  ```
- **Technical Critique:** Returning the internal `List` reference violates defensive programming. Any external caller could invoke `service.getAllReports().clear()`, wiping the entire campus lost repository without security authorization.
- **Corrected Code:**
  ```java
  public List<LostReport> getAllReports() {
      // Defensive unmodifiable wrapper over a shallow copy
      return Collections.unmodifiableList(new ArrayList<>(reportRegistry.values()));
  }
  ```

---

## Defect Log 3: Missing `equals()` and `hashCode()` Contract on Item Keys

- **Prompt Used:** *"Create a composite key class for lost item caching using category and itemId."*
- **AI-Generated Code (Flawed):**
  ```java
  public class ItemKey {
      private String itemId;
      private String category;
      @Override
      public boolean equals(Object o) {
          if (this == o) return true;
          if (!(o instanceof ItemKey that)) return false;
          return itemId.equals(that.itemId) && category.equals(that.category);
      }
      // hashCode() was completely omitted by AI!
  }
  ```
- **Technical Critique:** Severe contract violation. Java mandates: if `a.equals(b) == true`, then `a.hashCode() == b.hashCode()`. Omitting `hashCode()` causes `map.get(new ItemKey("FND-201", "ELEC"))` to return `null` because different system identity hash codes route to different hash buckets.
- **Corrected Code:**
  ```java
  @Override
  public int hashCode() {
      return Objects.hash(itemId, category);
  }
  ```

---

## Defect Log 4: Swallowed Exception with Silent Failure

- **Prompt Used:** *"Write a method to save a claim and handle exceptions."*
- **AI-Generated Code (Flawed):**
  ```java
  public void saveClaim(Claim claim) {
      try {
          db.insert(claim);
      } catch (Exception e) {
          // Empty catch block swallowed by AI
      }
  }
  ```
- **Technical Critique:** Swallowing exceptions hides duplicate claim attempts, database deadlocks, and connection drops. The caller assumes the claim succeeded, corrupting audit trails (violates NFR-P3).
- **Corrected Code:**
  ```java
  public void saveClaim(Claim claim) {
      try {
          db.insert(claim);
      } catch (DuplicateKeyException e) {
          throw new DuplicateClaimException("Claim already exists for item " + claim.getFoundItemId(), e);
      }
  }
  ```

---

## Defect Log 5: Naive $O(N)$ Linear Search in Repository

- **Prompt Used:** *"Create an in-memory repository to search lost items by their string ID."*
- **AI-Generated Code (Flawed):**
  ```java
  public class LostRepository {
      private List<LostItem> items = new ArrayList<>();
      public LostItem findById(String id) {
          for (LostItem item : items) {
              if (item.getId().equals(id)) return item; // O(N) scan
          }
          return null;
      }
  }
  ```
- **Technical Critique:** For $10,000$ seeded campus records, linear search takes $O(N)$ time, degrading throughput and violating NFR-04 ($p95 < 500\text{ ms}$) and NFR-P2.
- **Corrected Code:**
  ```java
  public class LostRepository {
      private final Map<String, LostItem> items = new HashMap<>(); // O(1) bucket lookup
      public Optional<LostItem> findById(String id) {
          if (id == null) return Optional.empty();
          return Optional.ofNullable(items.get(id.trim()));
      }
  }
  ```
