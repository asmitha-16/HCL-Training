package com.campus.lostandfound;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * ============================================================================
 * DAY 9: GENERICS & I/O
 * ============================================================================
 * Topic: Generics basics; Bounded types; Wildcards (PECS); try-with-resources
 * Domain: Campus Lost and Found Portal (Generic Repository & Toolkit Architecture)
 *
 * KEY CONCEPTS DEMONSTRATED & ARCHITECTURAL JUSTIFICATIONS:
 *
 * 1. Generic Interface & Class (CrudRepository<T, ID> & InMemoryRepository<T, ID>):
 *    - Replaces duplicated boilerplate CRUD code across all domain entities (LostItem, FoundItem, Claim).
 *    - Bounded type parameter <T extends BaseEntity, ID> enforces that only domain entities with an
 *      identity can be persisted, allowing type-safe calls to entity.getId().
 *
 * 2. Type Erasure:
 *    - Java implements generics via compiler type erasure: generic type parameters (like T) are
 *      erased to their upper bound (BaseEntity or Object) in bytecode, with synthetic type casts
 *      inserted at compile-time to maintain 100% backwards compatibility with pre-Java 5 JVMs.
 *
 * 3. Bounded Types (<T extends Number>, <T extends Comparable<T>>):
 *    - Box<T extends Number>: Limits type arguments to numeric types (Double, Integer, Long),
 *      allowing safe calls to Number.doubleValue() without runtime casting errors.
 *    - <T extends Comparable<T>> T findMax(List<T>): Generic method guaranteeing that elements
 *      can compare themselves to other elements of the exact same type.
 *
 * 4. Wildcards & PECS Principle (Producer Extends, Consumer Super):
 *    - ? extends T (Producer): Use when you only READ elements from the collection.
 *      The collection "produces" T instances. (You CANNOT add elements to a List<? extends T>
 *      except null, because the compiler cannot guarantee the specific subtype).
 *    - ? super T (Consumer): Use when you only WRITE elements to the collection.
 *      The collection "consumes" T instances.
 *    - Example: copyEntities(List<? extends T> src, List<? super T> dest).
 *
 * 5. try-with-resources & AutoCloseable:
 *    - Automatically releases OS resources (file handles, streams, database sessions) without
 *      convoluted finally blocks.
 *    - Demonstrates reading seed data from CSV files via try (BufferedReader br = ...).
 *    - Demonstrates custom AutoCloseable resources (LockerAuditSession) to prove the exact
 *      LIFO (Last-In-First-Out) destruction/closing order.
 * ============================================================================
 */
public class CampusGenericsApp {

    // =========================================================================
    // 1. DOMAIN MODELS & BASE ENTITY
    // =========================================================================

    public static abstract class BaseEntity {
        private String id;
        private final LocalDateTime createdAt;

        public BaseEntity() {
            this.createdAt = LocalDateTime.now();
        }

        public BaseEntity(String id) {
            this.id = id;
            this.createdAt = LocalDateTime.now();
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public LocalDateTime getCreatedAt() { return createdAt; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BaseEntity that = (BaseEntity) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    public static class LostItem extends BaseEntity implements Comparable<LostItem> {
        private String title;
        private String category;
        private String location;
        private LocalDate lostDate;
        private double estimatedValue;
        private String reporterUsername;

        public LostItem(String id, String title, String category, String location,
                        LocalDate lostDate, double estimatedValue, String reporterUsername) {
            super(id);
            this.title = title;
            this.category = category;
            this.location = location;
            this.lostDate = lostDate;
            this.estimatedValue = estimatedValue;
            this.reporterUsername = reporterUsername;
        }

        public String getTitle() { return title; }
        public String getCategory() { return category; }
        public String getLocation() { return location; }
        public LocalDate getLostDate() { return lostDate; }
        public double getEstimatedValue() { return estimatedValue; }
        public String getReporterUsername() { return reporterUsername; }

        @Override
        public int compareTo(LostItem other) {
            return Double.compare(this.estimatedValue, other.estimatedValue); // Natural sort by valuation
        }

        @Override
        public String toString() {
            return String.format("[%s] \"%s\" | Cat: %s | Loc: %s | Val: $%.2f | By: %s",
                    getId(), title, category, location, estimatedValue, reporterUsername);
        }
    }

    public static class FoundItem extends BaseEntity implements Comparable<FoundItem> {
        private String title;
        private String category;
        private String location;
        private LocalDate foundDate;
        private String lockerNumber;
        private String secretQuizAnswer;
        private String finderUsername;

        public FoundItem(String id, String title, String category, String location,
                         LocalDate foundDate, String lockerNumber, String secretQuizAnswer,
                         String finderUsername) {
            super(id);
            this.title = title;
            this.category = category;
            this.location = location;
            this.foundDate = foundDate;
            this.lockerNumber = lockerNumber;
            this.secretQuizAnswer = secretQuizAnswer;
            this.finderUsername = finderUsername;
        }

        public String getTitle() { return title; }
        public String getCategory() { return category; }
        public String getLocation() { return location; }
        public LocalDate getFoundDate() { return foundDate; }
        public String getLockerNumber() { return lockerNumber; }
        public String getSecretQuizAnswer() { return secretQuizAnswer; }
        public String getFinderUsername() { return finderUsername; }

        @Override
        public int compareTo(FoundItem other) {
            return this.getId().compareTo(other.getId());
        }

        @Override
        public String toString() {
            return String.format("[%s] \"%s\" | Cat: %s | Loc: %s | Locker: %s | By: %s",
                    getId(), title, category, location, lockerNumber, finderUsername);
        }
    }

    public static class User extends BaseEntity {
        private String fullName;
        private String email;
        private String department;
        private String role;

        public User(String username, String fullName, String email, String department, String role) {
            super(username); // Username serves as ID
            this.fullName = fullName;
            this.email = email;
            this.department = department;
            this.role = role;
        }

        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getDepartment() { return department; }
        public String getRole() { return role; }

        @Override
        public String toString() {
            return String.format("%s (%s, Dept: %s, ID: %s)", fullName, role, department, getId());
        }
    }

    // =========================================================================
    // 2. GENERIC CRUD REPOSITORY ARCHITECTURE
    // =========================================================================

    /**
     * Generic CRUD Repository Interface.
     * Enforces type-safe repository pattern for any entity type T with identity ID.
     */
    public interface CrudRepository<T, ID> {
        T save(T entity);
        Optional<T> findById(ID id);
        List<T> findAll();
        boolean deleteById(ID id);
        long count();
        boolean existsById(ID id);
    }

    /**
     * In-Memory Generic Repository implementation reused across ALL domain entities.
     * Bound: <T extends BaseEntity, ID> guarantees entity.getId() is available.
     */
    public static class InMemoryRepository<T extends BaseEntity, ID> implements CrudRepository<T, ID> {
        private final Map<ID, T> storage = new LinkedHashMap<>(); // Preserves insertion order

        @Override
        public synchronized T save(T entity) {
            if (entity == null) throw new IllegalArgumentException("Entity cannot be null");
            @SuppressWarnings("unchecked")
            ID id = (ID) entity.getId();
            storage.put(id, entity);
            return entity;
        }

        @Override
        public Optional<T> findById(ID id) {
            if (id == null) return Optional.empty();
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public List<T> findAll() {
            return new ArrayList<>(storage.values());
        }

        @Override
        public synchronized boolean deleteById(ID id) {
            return storage.remove(id) != null;
        }

        @Override
        public long count() {
            return storage.size();
        }

        @Override
        public boolean existsById(ID id) {
            return storage.containsKey(id);
        }
    }

    // =========================================================================
    // 3. GENERIC TOOLKIT (Pair, Box, findMax, Wildcards)
    // =========================================================================

    /**
     * Generic Pair class to hold two related objects of arbitrary types K and V.
     */
    public static class Pair<K, V> {
        private final K first;
        private final V second;

        public Pair(K first, V second) {
            this.first = first;
            this.second = second;
        }

        public K getFirst() { return first; }
        public V getSecond() { return second; }

        @Override
        public String toString() {
            return "(" + first + " <-> " + second + ")";
        }
    }

    /**
     * Bounded Generic Type: Box<T extends Number>
     * Restricts payload to numeric types (Double, Integer, Float, Long).
     */
    public static class Box<T extends Number> {
        private final T value;
        private final String metricLabel;

        public Box(String metricLabel, T value) {
            this.metricLabel = metricLabel;
            this.value = value;
        }

        public T getValue() { return value; }
        public String getMetricLabel() { return metricLabel; }

        public double toDouble() {
            return value.doubleValue(); // Safe because T extends Number!
        }

        public boolean isGreaterThan(Box<? extends Number> other) {
            return this.toDouble() > other.toDouble();
        }

        @Override
        public String toString() {
            return String.format("[Box: %s = %s (double: %.2f)]", metricLabel, value, toDouble());
        }
    }

    /**
     * Bounded Generic Method: Finds the maximum element in any list where
     * elements implement Comparable<T>.
     */
    public static <T extends Comparable<T>> Optional<T> findMax(List<T> list) {
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        }
        T max = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            T candidate = list.get(i);
            if (candidate.compareTo(max) > 0) {
                max = candidate;
            }
        }
        return Optional.of(max);
    }

    /**
     * Wildcard Method Demonstrating PECS (Producer Extends, Consumer Super):
     * - src is a PRODUCER (? extends T): We read elements from it.
     * - dest is a CONSUMER (? super T): We write elements into it.
     */
    public static <T> void copyEntities(List<? extends T> src, List<? super T> dest) {
        for (T item : src) { // Reading from producer
            dest.add(item);  // Writing into consumer
        }
    }

    /**
     * Upper-bounded wildcard method: accepts any list of items extending BaseEntity.
     */
    public static void printEntitySummary(List<? extends BaseEntity> entities) {
        System.out.printf("Total Entities: %d | Types: %s\n",
                entities.size(),
                entities.isEmpty() ? "Empty" : entities.get(0).getClass().getSimpleName());
        for (BaseEntity e : entities) {
            System.out.println("   * " + e);
        }
    }

    // =========================================================================
    // 4. CSV SEED DATA LOADER WITH TRY-WITH-RESOURCES
    // =========================================================================

    public static class CsvDataLoader {

        public static int loadLostItems(File csvFile, CrudRepository<LostItem, String> repository) {
            int count = 0;
            // TRY-WITH-RESOURCES: BufferedReader and FileReader automatically closed!
            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                String line;
                boolean header = true;
                while ((line = br.readLine()) != null) {
                    if (header) { header = false; continue; } // Skip CSV header
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    String[] tokens = line.split(",");
                    if (tokens.length >= 7) {
                        String id = tokens[0].trim();
                        String title = tokens[1].trim();
                        String category = tokens[2].trim();
                        String location = tokens[3].trim();
                        LocalDate date = LocalDate.parse(tokens[4].trim());
                        double value = Double.parseDouble(tokens[5].trim());
                        String reporter = tokens[6].trim();

                        repository.save(new LostItem(id, title, category, location, date, value, reporter));
                        count++;
                    }
                }
            } catch (IOException e) {
                System.err.println("[ERROR] Failed to read CSV: " + e.getMessage());
            }
            return count;
        }

        public static int loadFoundItems(File csvFile, CrudRepository<FoundItem, String> repository) {
            int count = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                String line;
                boolean header = true;
                while ((line = br.readLine()) != null) {
                    if (header) { header = false; continue; }
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    String[] tokens = line.split(",");
                    if (tokens.length >= 8) {
                        String id = tokens[0].trim();
                        String title = tokens[1].trim();
                        String category = tokens[2].trim();
                        String location = tokens[3].trim();
                        LocalDate date = LocalDate.parse(tokens[4].trim());
                        String locker = tokens[5].trim();
                        String secret = tokens[6].trim();
                        String finder = tokens[7].trim();

                        repository.save(new FoundItem(id, title, category, location, date, locker, secret, finder));
                        count++;
                    }
                }
            } catch (IOException e) {
                System.err.println("[ERROR] Failed to read CSV: " + e.getMessage());
            }
            return count;
        }

        public static int loadUsers(File csvFile, CrudRepository<User, String> repository) {
            int count = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                String line;
                boolean header = true;
                while ((line = br.readLine()) != null) {
                    if (header) { header = false; continue; }
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    String[] tokens = line.split(",");
                    if (tokens.length >= 5) {
                        String username = tokens[0].trim();
                        String fullName = tokens[1].trim();
                        String email = tokens[2].trim();
                        String dept = tokens[3].trim();
                        String role = tokens[4].trim();

                        repository.save(new User(username, fullName, email, dept, role));
                        count++;
                    }
                }
            } catch (IOException e) {
                System.err.println("[ERROR] Failed to read CSV: " + e.getMessage());
            }
            return count;
        }
    }

    // =========================================================================
    // 5. CUSTOM AUTOCLOSEABLE RESOURCE SHOWING LIFO CLOSING ORDER
    // =========================================================================

    /**
     * Custom AutoCloseable Resource representing a secure custody locker audit session.
     * Demonstrates that in a try-with-resources statement:
     * Resources are closed in REVERSE order of their declaration (LIFO)!
     */
    public static class LockerAuditSession implements AutoCloseable {
        private final String sessionName;
        private final String lockerId;

        public LockerAuditSession(String sessionName, String lockerId) {
            this.sessionName = sessionName;
            this.lockerId = lockerId;
            System.out.printf("   [OPENED] Audit Session '%s' on %s\n", sessionName, lockerId);
        }

        public void performInspection(String itemDescription) {
            System.out.printf("   [INSPECTING] Session '%s' verifying physical security of: %s\n",
                    sessionName, itemDescription);
        }

        @Override
        public void close() {
            // Invoked automatically by JVM when exiting try-with-resources block
            System.out.printf("   [CLOSED - LIFO] Audit Session '%s' on %s successfully finalized.\n",
                    sessionName, lockerId);
        }
    }

    /**
     * Demonstrates LIFO closing order with try-with-resources.
     */
    public static void demonstrateClosingOrder() {
        System.out.println("====================================================================================");
        System.out.println("  DEMONSTRATION: TRY-WITH-RESOURCES & AUTOCLOSEABLE CLOSING ORDER (LIFO)");
        System.out.println("====================================================================================");
        System.out.println("Opening two resources: (1) Primary Vault Session, then (2) Secondary Locker Session...\n");

        // Declaring multiple resources in try statement
        try (LockerAuditSession session1 = new LockerAuditSession("Session-1", "Locker-A (Primary Vault)");
             LockerAuditSession session2 = new LockerAuditSession("Session-2", "Locker-B (High-Value)")) {

            session1.performInspection("Box of Found Smartwatches");
            session2.performInspection("MacBook Pro 16");
            System.out.println("\nExiting try block now... Watch the automatic LIFO closing sequence:");

        } // JVM automatically invokes close() in REVERSE declaration order: session2, then session1!

        System.out.println("\nObservation: Session-2 (declared 2nd) closed BEFORE Session-1 (declared 1st)!");
        System.out.println("====================================================================================\n");
    }

    // =========================================================================
    // 6. MAIN APPLICATION & INTERACTIVE CONSOLE MENU
    // =========================================================================

    public static void main(String[] args) {
        // Generic Repositories reused for all domain entities
        CrudRepository<LostItem, String> lostItemRepo = new InMemoryRepository<>();
        CrudRepository<FoundItem, String> foundItemRepo = new InMemoryRepository<>();
        CrudRepository<User, String> userRepo = new InMemoryRepository<>();

        // Load seed data from CSV files
        File baseDir = new File(".");
        File lostCsv = new File(baseDir, "lost_items.csv");
        File foundCsv = new File(baseDir, "found_items.csv");
        File userCsv = new File(baseDir, "users.csv");

        // If running from repo root, adjust path
        if (!lostCsv.exists()) {
            lostCsv = new File("Daily Task/day-9/lost_items.csv");
            foundCsv = new File("Daily Task/day-9/found_items.csv");
            userCsv = new File("Daily Task/day-9/users.csv");
        }

        CsvDataLoader.loadLostItems(lostCsv, lostItemRepo);
        CsvDataLoader.loadFoundItems(foundCsv, foundItemRepo);
        CsvDataLoader.loadUsers(userCsv, userRepo);

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        System.out.println("====================================================================================");
        System.out.println("  CAMPUS LOST AND FOUND PORTAL -- DAY 9: GENERICS & I/O");
        System.out.println("  Generic Repositories, Bounded Types, PECS Wildcards & try-with-resources");
        System.out.println("====================================================================================");

        while (!exit) {
            System.out.println("\n------------------------------------------------------------------------------------");
            System.out.println("MENU OPTIONS:");
            System.out.println("  1. Inspect Seed Data loaded from CSV via try-with-resources");
            System.out.println("  2. Test Generic CrudRepository Operations (Save, FindById, Delete, Count)");
            System.out.println("  3. Test Generic Toolkit: Pair<K,V>, Box<T extends Number>, & findMax()");
            System.out.println("  4. Demonstrate PECS Wildcards (copyEntities & upper/lower bounds)");
            System.out.println("  5. Demonstrate Custom AutoCloseable & LIFO Resource Close Order");
            System.out.println("  6. View Generics & Type Erasure Deep-Dive Guide");
            System.out.println("  0. Exit");
            System.out.print("Enter choice (0-6): ");

            String input = scanner.hasNextLine() ? scanner.nextLine().trim() : "0";

            switch (input) {
                case "1":
                    System.out.println("\n--- [CSV SEED DATA LOADED INTO GENERIC REPOSITORIES] ---");
                    System.out.println("Lost Items Repository (Count: " + lostItemRepo.count() + "):");
                    printEntitySummary(lostItemRepo.findAll());

                    System.out.println("\nFound Items Repository (Count: " + foundItemRepo.count() + "):");
                    printEntitySummary(foundItemRepo.findAll());

                    System.out.println("\nUsers Repository (Count: " + userRepo.count() + "):");
                    printEntitySummary(userRepo.findAll());
                    break;

                case "2":
                    System.out.println("\n--- [GENERIC CRUD REPOSITORY OPERATIONS] ---");
                    System.out.println("1. Find By ID in lostItemRepo: id = 'LOST-1005'");
                    Optional<LostItem> item = lostItemRepo.findById("LOST-1005");
                    item.ifPresentOrElse(
                            it -> System.out.println("   Found: " + it),
                            () -> System.out.println("   Not found.")
                    );

                    System.out.println("2. Save new dynamic entity to generic foundItemRepo:");
                    FoundItem newItem = new FoundItem("FOUND-2999", "Noise-Cancelling Headphones", "Electronics",
                            "Quiet Study Room", LocalDate.now(), "Locker-42", "Black Bose 700 with case", "john_staff");
                    foundItemRepo.save(newItem);
                    System.out.println("   Saved: " + newItem);
                    System.out.println("   Updated foundItemRepo.count(): " + foundItemRepo.count());

                    System.out.println("3. Delete newly created entity by ID: 'FOUND-2999'");
                    boolean deleted = foundItemRepo.deleteById("FOUND-2999");
                    System.out.println("   Delete status: " + deleted + " | Count now: " + foundItemRepo.count());
                    break;

                case "3":
                    System.out.println("\n--- [GENERIC TOOLKIT: PAIR, BOX & FINDMAX] ---");
                    // 1. Pair demo
                    LostItem l = lostItemRepo.findById("LOST-1001").orElse(null);
                    FoundItem f = foundItemRepo.findById("FOUND-2001").orElse(null);
                    Pair<LostItem, FoundItem> matchPair = new Pair<>(l, f);
                    System.out.println("1. Generic Pair<LostItem, FoundItem>:");
                    System.out.println("   Match Candidate: " + matchPair);

                    // 2. Bounded Box demo
                    Box<Double> valueBox = new Box<>("MacBook Valuation", 2400.00);
                    Box<Integer> countBox = new Box<>("Total Items Count", 12);
                    System.out.println("\n2. Bounded Box<T extends Number>:");
                    System.out.println("   " + valueBox);
                    System.out.println("   " + countBox);
                    System.out.println("   Is Value Box ($2400) > Count Box (12)? " + valueBox.isGreaterThan(countBox));

                    // 3. Generic findMax demo
                    System.out.println("\n3. Bounded Generic Method <T extends Comparable<T>> findMax():");
                    Optional<LostItem> maxValuedItem = findMax(lostItemRepo.findAll());
                    maxValuedItem.ifPresent(m ->
                            System.out.println("   Highest-Valued Lost Item in System: " + m.getTitle() + " ($" + m.getEstimatedValue() + ")"));
                    break;

                case "4":
                    System.out.println("\n--- [PECS WILDCARD DEMONSTRATION: PRODUCER EXTENDS, CONSUMER SUPER] ---");
                    List<LostItem> sourceList = lostItemRepo.findAll();
                    List<BaseEntity> targetList = new ArrayList<>();

                    System.out.println("Calling: copyEntities(List<? extends BaseEntity> src, List<? super BaseEntity> dest)");
                    System.out.println("Source: List<LostItem> (Subtype Producer)");
                    System.out.println("Dest:   List<BaseEntity> (Supertype Consumer)");

                    copyEntities(sourceList, targetList);
                    System.out.println("Successfully copied " + targetList.size() + " items using PECS wildcards!");
                    System.out.println("First element in dest: " + targetList.get(0));
                    break;

                case "5":
                    demonstrateClosingOrder();
                    break;

                case "6":
                    System.out.println("\n==================================================================================================================");
                    System.out.println("                          JAVA GENERICS & TYPE ERASURE SUMMARY");
                    System.out.println("==================================================================================================================");
                    System.out.println("1. Type Erasure: Generics exist only at compile time. In bytecode, <T> becomes BaseEntity or Object.");
                    System.out.println("2. Bounded Types (<T extends Number>): Constrains parameters to valid type hierarchies.");
                    System.out.println("3. PECS Rule: 'Producer Extends, Consumer Super'. Use ? extends T when reading, ? super T when writing.");
                    System.out.println("4. try-with-resources: Closes AutoCloseable instances in LIFO reverse declaration order.");
                    System.out.println("==================================================================================================================");
                    break;

                case "0":
                    exit = true;
                    System.out.println("\nExiting Campus Generics App. Goodbye!");
                    break;

                default:
                    System.out.println("[ERROR] Invalid choice. Select 0-6.");
            }
        }
    }
}
