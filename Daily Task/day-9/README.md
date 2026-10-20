# Day 9: Generics & I/O

## Project: Campus Lost and Found Portal
**Module:** Generics basics; Bounded Types; Wildcards (PECS); try-with-resources; AutoCloseable  
**Deliverables:** GenericToolkit + CrudRepository + CSV Loader + AutoCloseable Demo + Text Report  
**Location:** `Daily Task/day-9/`

---

## 1. Executive Summary

Day 9 establishes a reusable, type-safe persistence and utility architecture for the **Campus Lost and Found Portal** using Java Generics and modern I/O management. It replaces duplicated repository code with a single generic `CrudRepository<T, ID>`, implements mathematical and comparison bounds, demonstrates the PECS (Producer Extends, Consumer Super) principle, safely parses CSV data files using `try-with-resources`, and proves the LIFO close order of `AutoCloseable` resources.

### Core Deliverables Implemented:
1. **Generic Repository Pattern:** Unified `CrudRepository<T, ID>` interface and `InMemoryRepository<T extends BaseEntity, ID>` reused for all entities (`LostItem`, `FoundItem`, `User`).
2. **Bounded Types:**
   - `<T extends Number>`: Bounded generic container `Box<T extends Number>` supporting calculations.
   - `<T extends Comparable<T>>`: Bounded generic method `findMax(List<T>)` finding the maximum element across any comparable type.
3. **PECS Wildcard Methods:** Method `copyEntities(List<? extends T> src, List<? super T> dest)` proving read-only producer and write-only consumer semantics.
4. **CSV Parsing with `try-with-resources`:** Automatically closes file streams when parsing `lost_items.csv`, `found_items.csv`, and `users.csv`.
5. **Custom `AutoCloseable` Resource:** `LockerAuditSession` demonstrating that multiple resources declared in a `try` statement are destroyed in strict **LIFO (Last-In-First-Out)** reverse declaration order.

---

## 2. Generic Repository Architecture

### The Interface: `CrudRepository<T, ID>`
```java
public interface CrudRepository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    boolean deleteById(ID id);
    long count();
    boolean existsById(ID id);
}
```

### The Reusable In-Memory Implementation
```java
public static class InMemoryRepository<T extends BaseEntity, ID> implements CrudRepository<T, ID> {
    private final Map<ID, T> storage = new LinkedHashMap<>();

    @Override
    public synchronized T save(T entity) {
        storage.put((ID) entity.getId(), entity);
        return entity;
    }
    // ...
}
```
**Benefits:**
- Eliminates 80% of repetitive data-access code.
- Ensures compile-time type safety: `lostItemRepo.findById("LOST-1001")` returns `Optional<LostItem>`, eliminating unsafe down-casting.

---

## 3. Generic Toolkit Component

### 1. `Pair<K, V>`
Encapsulates two related objects of arbitrary types (e.g. `Pair<LostItem, FoundItem>` correlation candidates).

### 2. Bounded Type: `Box<T extends Number>`
Restricts `T` to numeric classes (`Double`, `Integer`, `Long`):
```java
public class Box<T extends Number> {
    private final T value;

    public double toDouble() {
        return value.doubleValue(); // Safe because T extends java.lang.Number!
    }
}
```

### 3. Bounded Generic Method: `findMax()`
```java
public static <T extends Comparable<T>> Optional<T> findMax(List<T> list) {
    if (list == null || list.isEmpty()) return Optional.empty();
    T max = list.get(0);
    for (T item : list) {
        if (item.compareTo(max) > 0) max = item;
    }
    return Optional.of(max);
}
```
Used to find the most valuable lost item ($2400.00 MacBook Pro) without writing custom sorting loops.

---

## 4. Wildcards & The PECS Principle

> **PECS: Producer Extends, Consumer Super**

| Wildcard Syntax | Role in Method | What You Can Do | What You Cannot Do |
|:---|:---|:---|:---|
| **`List<? extends T>`** | **Producer** | You can **READ** elements as type `T`. | You **CANNOT write** elements (compiler cannot guarantee the concrete subtype). |
| **`List<? super T>`** | **Consumer** | You can **WRITE** elements of type `T` (or subtypes). | You **CANNOT read** specific types (reading returns `Object`). |

### The Demonstration: `copyEntities`
```java
public static <T> void copyEntities(List<? extends T> src, List<? super T> dest) {
    for (T item : src) { // src produces T
        dest.add(item);  // dest consumes T
    }
}
```
Allows copying from `List<LostItem>` into `List<BaseEntity>` in complete compile-time safety.

---

## 5. `try-with-resources` & AutoCloseable Closing Order

### Safe CSV File Parsing
```java
// Automatic resource management guarantees stream closure even upon exception
try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
    String line;
    while ((line = br.readLine()) != null) {
        // parse and save to repository
    }
}
```

### LIFO Destruction Order
When multiple resources are declared in a single `try` statement:
```java
try (LockerAuditSession session1 = new LockerAuditSession("Session-1", "Locker-A");
     LockerAuditSession session2 = new LockerAuditSession("Session-2", "Locker-B")) {
    // operations
}
```
**Execution Output:**
```text
[OPENED] Audit Session 'Session-1' on Locker-A
[OPENED] Audit Session 'Session-2' on Locker-B
[CLOSED - LIFO] Audit Session 'Session-2' on Locker-B successfully finalized.
[CLOSED - LIFO] Audit Session 'Session-1' on Locker-A successfully finalized.
```
**Key Observation:** `Session-2` (declared second) is closed **before** `Session-1` (declared first).

---

## 6. How to Run

Compile and run directly with Java:
```bash
javac -d bin CampusGenericsApp.java
java -cp bin com.campus.lostandfound.CampusGenericsApp
```
