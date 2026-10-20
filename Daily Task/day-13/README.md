# Day 13: Functional Programming in Modern Java (8–21)

## Project: Campus Lost and Found Portal (JFS-40)
**Curriculum Module:** Module 2 (Modern Java — Functional Programming)  
**Deliverables:** `FilterEngine.java`, `CampusFunctionalApp.java`, `README.md`  
**Location:** `Daily Task/day-13/`

---

## 1. Executive Summary

Day 13 inaugurates **Module 2: Modern Java**, transitioning the Campus Lost & Found Portal from imperative object-oriented boilerplate to declarative **functional programming**:

1. **Lambda Expressions & Closures:** Utilized `(param) -> body` lambda expressions to encapsulate behaviors as first-class citizens, demonstrating effectively-final lexical scope capture.
2. **Core Built-in Functional Interfaces:**
   - `Predicate<T>`: Evaluates boolean filter conditions with `.and()`, `.or()`, and `.negate()`.
   - `Function<T, R>`: Transforms domain entities into summary DTOs with `.andThen()` pipeline chaining.
   - `Consumer<T>`: Performs side-effect actions (security custody audit logging, event notifications).
   - `Supplier<T>`: Lazily supplies default inventory fallback items.
3. **The 4 Kinds of Method References:** Replaced trivial lambdas with clean method references (`Class::method`).
4. **Refactoring Anonymous Classes & If-Ladders:** Replaced cumbersome anonymous inner classes (`new Comparator<T>()`, `new Strategy()`) with inline lambda expressions.
5. **Custom `@FunctionalInterface`:** Authored `MatchRule` expressing matching scoring algorithms as composable lambdas.

---

## 2. Core Functional Interfaces Matrix

| Functional Interface | Signature | Core Method | JFS-40 Operational Role |
|:---|:---:|:---:|:---|
| **`Predicate<T>`** | `T -> boolean` | `boolean test(T t)` | Filtering active, high-value, or unclaimed items |
| **`Function<T, R>`** | `T -> R` | `R apply(T t)` | Mapping `FoundItem` entity to `ItemSummaryDTO` |
| **`Consumer<T>`** | `T -> void` | `void accept(T t)` | Security Desk custody audit logging |
| **`Supplier<T>`** | `() -> T` | `T get()` | Supplying default unclassified items on missing lookups |
| **`MatchRule` (Custom)** | `(String, String, String, String) -> double` | `double evaluate(...)` | Custom functional match scoring rule |

---

## 3. The 4 Kinds of Method References

1. **Static Method Reference:**
   `FilterEngine::formatLabel` equivalent to `title -> FilterEngine.formatLabel(title)`
2. **Bound Instance Method Reference:**
   `auditLogger::accept` equivalent to `item -> auditLogger.accept(item)`
3. **Unbound Instance Method Reference:**
   `FoundItem::category` equivalent to `item -> item.category()`
4. **Constructor Reference:**
   `ItemSummaryDTO::new` equivalent to `(id, title, loc) -> new ItemSummaryDTO(id, title, loc)`

---

## 4. Refactoring Anonymous Classes to Lambdas

### Legacy Java 7 Anonymous Inner Class:
```java
SimpleStrategy strategy = new SimpleStrategy() {
    @Override
    public boolean isHighRisk(SimpleIncident incident) {
        return incident.value() > 500.00;
    }
};
```

### Modern Java 8–21 Functional Lambda:
```java
SimpleStrategy strategy = incident -> incident.value() > 500.00;
```

---

## 5. How to Compile and Run

```bash
# Compile Day 13 source files
javac -d bin FilterEngine.java CampusFunctionalApp.java

# Run functional filter engine demonstration
java -cp bin FilterEngine

# Run lambda refactoring demonstration
java -cp bin CampusFunctionalApp
```
