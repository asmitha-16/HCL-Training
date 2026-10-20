package com.campus.lostandfound;

import java.time.LocalDate;
import java.util.*;

/**
 * ============================================================================
 * DAY 8: COLLECTIONS FRAMEWORK — PART 2
 * ============================================================================
 * Topic: Map; Comparable & Comparator; HashMap vs TreeMap; FR4–FR6
 * Domain: Campus Lost and Found Portal (Campus Directory & Matching Engine)
 *
 * KEY CONCEPTS DEMONSTRATED & ARCHITECTURAL JUSTIFICATIONS:
 *
 * 1. HashMap vs LinkedHashMap vs TreeMap:
 *    - java.util.HashMap:
 *      * Internal Structure: Array of buckets (Node<K,V>[]), converting to Red-Black trees (TreeNode)
 *        when a bucket exceeds TREEIFY_THRESHOLD (8 items) to avoid O(N) hash flood attacks.
 *      * Time Complexity: O(1) average time for get() and put().
 *      * Ordering: No ordering guarantees; order changes when table rehashes.
 *      * Project Role: Primary registry for LostItem, FoundItem, Claim, and User lookups by ID.
 *    - java.util.LinkedHashMap:
 *      * Internal Structure: HashMap buckets combined with a doubly-linked list through all entries.
 *      * Time Complexity: O(1) average time.
 *      * Ordering: Strict insertion-order (or access-order).
 *      * Project Role: User registration log and chronological claim submission audit trail.
 *    - java.util.TreeMap:
 *      * Internal Structure: Red-Black self-balancing binary search tree.
 *      * Time Complexity: Guaranteed O(log N) for get(), put(), containsKey(), firstKey(), lastKey().
 *      * Ordering: Sorted according to natural ordering of keys (Comparable) or a custom Comparator.
 *      * Project Role: Generating sorted category inventory reports and department breakdown reports.
 *
 * 2. Frequency Counting with Map.merge():
 *    - Uses map.merge(key, 1, Integer::sum) for clean, atomic-like frequency accumulation without
 *      clunky containsKey() / get() boilerplate.
 *
 * 3. Comparable vs Comparator:
 *    - java.lang.Comparable<T>: Defines the natural ordering inherent to an object via compareTo().
 *      Used by LostItem and FoundItem to sort naturally by unique ID.
 *    - java.util.Comparator<T>: External sorting strategy. Allows multiple custom orderings.
 *      Demonstrates advanced chained comparator pipelines:
 *        Comparator.comparing().reversed().thenComparing()
 *
 * 4. The equals() and hashCode() Contract (Missing-hashCode Bug Demo):
 *    - Contract: If a.equals(b) == true, then a.hashCode() MUST equal b.hashCode().
 *    - Bug Demo: When hashCode() is omitted, distinct object instances with identical fields
 *      produce different default system identity hash codes. HashMap places them into different
 *      buckets, causing map.get(key) to return null even when key1.equals(key2) is true!
 *    - Fix Demo: Correctly implementing Objects.hash(id, category) fixes bucket resolution.
 *
 * 5. Functional Requirements FR4–FR6:
 *    - FR4: Item Matching & Correlation Engine using Map lookups and category indexing.
 *    - FR5: Verification Challenge Quiz Authentication verifying claimant secret in O(1).
 *    - FR6: Handover Resolution & Atomic Status Updates across Maps.
 * ============================================================================
 */
public class CampusDirectory {

    // -------------------------------------------------------------------------
    // DOMAIN MODELS
    // -------------------------------------------------------------------------

    public enum UserRole {
        FACULTY(1, "Faculty / Professor"),
        STAFF(2, "University Staff"),
        STUDENT(3, "Enrolled Student"),
        GUEST(4, "Campus Visitor");

        private final int priorityLevel;
        private final String label;

        UserRole(int priorityLevel, String label) {
            this.priorityLevel = priorityLevel;
            this.label = label;
        }

        public int getPriorityLevel() { return priorityLevel; }
        public String getLabel() { return label; }
    }

    public static class User {
        private final String username;
        private final String fullName;
        private final String email;
        private final String department;
        private final UserRole role;

        public User(String username, String fullName, String email, String department, UserRole role) {
            this.username = username.trim().toLowerCase();
            this.fullName = fullName;
            this.email = email;
            this.department = department;
            this.role = role != null ? role : UserRole.STUDENT;
        }

        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getDepartment() { return department; }
        public UserRole getRole() { return role; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return Objects.equals(username, user.username);
        }

        @Override
        public int hashCode() {
            return Objects.hash(username);
        }

        @Override
        public String toString() {
            return String.format("%s (%s, Dept: %s, @%s)", fullName, role.getLabel(), department, username);
        }
    }

    public static class LostItem implements Comparable<LostItem> {
        private final String id;
        private String title;
        private String category;
        private String location;
        private LocalDate lostDate;
        private double estimatedValue;
        private String reporterUsername;
        private String status;

        public LostItem(String id, String title, String category, String location,
                        LocalDate lostDate, double estimatedValue, String reporterUsername) {
            this.id = id;
            this.title = title;
            this.category = category;
            this.location = location;
            this.lostDate = lostDate != null ? lostDate : LocalDate.now();
            this.estimatedValue = estimatedValue;
            this.reporterUsername = reporterUsername;
            this.status = "REPORTED";
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getCategory() { return category; }
        public String getLocation() { return location; }
        public LocalDate getLostDate() { return lostDate; }
        public double getEstimatedValue() { return estimatedValue; }
        public String getReporterUsername() { return reporterUsername; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        @Override
        public int compareTo(LostItem other) {
            if (other == null) return -1;
            return this.id.compareTo(other.id); // Natural order by ID
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LostItem item = (LostItem) o;
            return Objects.equals(id, item.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return String.format("[%s] \"%s\" | Cat: %s | Loc: %s | Date: %s | Val: $%.2f | By: %s | Status: %s",
                    id, title, category, location, lostDate, estimatedValue, reporterUsername, status);
        }
    }

    public static class FoundItem implements Comparable<FoundItem> {
        private final String id;
        private String title;
        private String category;
        private String location;
        private LocalDate foundDate;
        private String lockerNumber;
        private String secretQuizAnswer;
        private String finderUsername;
        private String status;
        private String approvedClaimId;

        public FoundItem(String id, String title, String category, String location,
                         LocalDate foundDate, String lockerNumber, String secretQuizAnswer,
                         String finderUsername) {
            this.id = id;
            this.title = title;
            this.category = category;
            this.location = location;
            this.foundDate = foundDate != null ? foundDate : LocalDate.now();
            this.lockerNumber = lockerNumber;
            this.secretQuizAnswer = secretQuizAnswer;
            this.finderUsername = finderUsername;
            this.status = "IN_CUSTODY";
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getCategory() { return category; }
        public String getLocation() { return location; }
        public LocalDate getFoundDate() { return foundDate; }
        public String getLockerNumber() { return lockerNumber; }
        public String getSecretQuizAnswer() { return secretQuizAnswer; }
        public String getFinderUsername() { return finderUsername; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getApprovedClaimId() { return approvedClaimId; }
        public void setApprovedClaimId(String approvedClaimId) { this.approvedClaimId = approvedClaimId; }

        @Override
        public int compareTo(FoundItem other) {
            if (other == null) return -1;
            return this.id.compareTo(other.id);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FoundItem item = (FoundItem) o;
            return Objects.equals(id, item.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return String.format("[%s] \"%s\" | Cat: %s | Loc: %s | Found: %s | Locker: %s | Status: %s",
                    id, title, category, location, foundDate, lockerNumber, status);
        }
    }

    public static class Claim implements Comparable<Claim> {
        private final String id;
        private final String foundItemId;
        private final String claimantUsername;
        private final UserRole claimantRole;
        private final String quizAnswer;
        private final String proofDetails;
        private final LocalDate claimDate;
        private String status;
        private String reviewedBy;
        private String reviewRemarks;

        public Claim(String id, String foundItemId, String claimantUsername, UserRole claimantRole,
                     String quizAnswer, String proofDetails, LocalDate claimDate) {
            this.id = id;
            this.foundItemId = foundItemId;
            this.claimantUsername = claimantUsername;
            this.claimantRole = claimantRole != null ? claimantRole : UserRole.STUDENT;
            this.quizAnswer = quizAnswer;
            this.proofDetails = proofDetails;
            this.claimDate = claimDate != null ? claimDate : LocalDate.now();
            this.status = "PENDING";
        }

        public String getId() { return id; }
        public String getFoundItemId() { return foundItemId; }
        public String getClaimantUsername() { return claimantUsername; }
        public UserRole getClaimantRole() { return claimantRole; }
        public String getQuizAnswer() { return quizAnswer; }
        public String getProofDetails() { return proofDetails; }
        public LocalDate getClaimDate() { return claimDate; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getReviewedBy() { return reviewedBy; }
        public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
        public String getReviewRemarks() { return reviewRemarks; }
        public void setReviewRemarks(String reviewRemarks) { this.reviewRemarks = reviewRemarks; }

        @Override
        public int compareTo(Claim other) {
            if (other == null) return -1;
            return this.id.compareTo(other.id);
        }

        @Override
        public String toString() {
            return String.format("[%s] Item: %s | Claimant: %s (%s) | Date: %s | Status: %s",
                    id, foundItemId, claimantUsername, claimantRole, claimDate, status);
        }
    }

    // -------------------------------------------------------------------------
    // MISSING HASHCODE DEMO CLASSES
    // -------------------------------------------------------------------------

    /**
     * BROKEN KEY: Overrides equals() but intentionally OMITS hashCode()!
     * Violates the equals/hashCode contract, causing HashMap.get() to return null.
     */
    public static class BrokenItemKey {
        private final String itemId;
        private final String category;

        public BrokenItemKey(String itemId, String category) {
            this.itemId = itemId;
            this.category = category;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BrokenItemKey that = (BrokenItemKey) o;
            return Objects.equals(itemId, that.itemId) && Objects.equals(category, that.category);
        }

        // Intentionally missing hashCode() override! Uses default System.identityHashCode()
    }

    /**
     * FIXED KEY: Overrides both equals() and hashCode() consistently.
     * Complies with the contract, guaranteeing accurate HashMap.get() lookup.
     */
    public static class FixedItemKey {
        private final String itemId;
        private final String category;

        public FixedItemKey(String itemId, String category) {
            this.itemId = itemId;
            this.category = category;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FixedItemKey that = (FixedItemKey) o;
            return Objects.equals(itemId, that.itemId) && Objects.equals(category, that.category);
        }

        @Override
        public int hashCode() {
            return Objects.hash(itemId, category);
        }
    }

    // -------------------------------------------------------------------------
    // DIRECTORY ENGINE WITH MAP STORAGE & COMPARATORS
    // -------------------------------------------------------------------------

    // 1. Primary HashMap registries for instant O(1) lookups
    private final Map<String, LostItem> lostItemsMap = new HashMap<>();
    private final Map<String, FoundItem> foundItemsMap = new HashMap<>();
    private final Map<String, Claim> claimsMap = new HashMap<>();

    // 2. LinkedHashMap for preserving user registration chronological order
    private final Map<String, User> userRegistry = new LinkedHashMap<>();

    public CampusDirectory() {
        seedSampleData();
    }

    private void seedSampleData() {
        // Seed Users
        registerUser(new User("alan_turing", "Prof. Alan Turing", "turing@campus.edu", "Computer Science", UserRole.FACULTY));
        registerUser(new User("ada_lovelace", "Dr. Ada Lovelace", "lovelace@campus.edu", "Mathematics", UserRole.FACULTY));
        registerUser(new User("john_staff", "John Smith", "jsmith@campus.edu", "Facilities", UserRole.STAFF));
        registerUser(new User("alice_student", "Alice Williams", "alice@student.campus.edu", "Data Science", UserRole.STUDENT));
        registerUser(new User("bob_student", "Bob Johnson", "bob@student.campus.edu", "Electrical Eng", UserRole.STUDENT));
        registerUser(new User("charlie_student", "Charlie Brown", "charlie@student.campus.edu", "Physics", UserRole.STUDENT));

        // Seed Lost Items into HashMap
        putLostItem(new LostItem("LOST-1001", "ThinkPad X1 Carbon", "Electronics", "Library 2nd Floor",
                LocalDate.now().minusDays(4), 1400.00, "alice_student"));
        putLostItem(new LostItem("LOST-1002", "Leather Wallet", "Personal", "Campus Cafeteria",
                LocalDate.now().minusDays(3), 60.00, "bob_student"));
        putLostItem(new LostItem("LOST-1003", "Wireless Earbuds", "Electronics", "Gymnasium",
                LocalDate.now().minusDays(2), 150.00, "charlie_student"));
        putLostItem(new LostItem("LOST-1004", "Research Lab Notebook", "Documents", "Science Lab 3",
                LocalDate.now().minusDays(5), 45.00, "alan_turing"));
        putLostItem(new LostItem("LOST-1005", "MacBook Pro 16", "Electronics", "Computer Center",
                LocalDate.now().minusDays(1), 2400.00, "ada_lovelace"));

        // Seed Found Items into HashMap
        putFoundItem(new FoundItem("FOUND-2001", "ThinkPad Laptop", "Electronics", "Library Quiet Zone",
                LocalDate.now().minusDays(4), "Locker-12", "HCL sticker on lid", "john_staff"));
        putFoundItem(new FoundItem("FOUND-2002", "Brown Wallet", "Personal", "Cafeteria Table 4",
                LocalDate.now().minusDays(3), "Locker-05", "Student ID #7376231", "john_staff"));
        putFoundItem(new FoundItem("FOUND-2003", "Sony Earbuds Case", "Electronics", "Gymnasium Bench",
                LocalDate.now().minusDays(2), "Locker-08", "Silver finish with scratch", "john_staff"));
        putFoundItem(new FoundItem("FOUND-2004", "Physics Lab Journal", "Documents", "Science Building",
                LocalDate.now().minusDays(4), "Locker-02", "Blue hardcover quantum notes", "john_staff"));

        // Seed Claims into HashMap
        putClaim(new Claim("CLM-3001", "FOUND-2001", "alice_student", UserRole.STUDENT,
                "HCL sticker on lid", "Left during exam revision", LocalDate.now().minusDays(3)));
        putClaim(new Claim("CLM-3002", "FOUND-2001", "alan_turing", UserRole.FACULTY,
                "HCL sticker on lid", "Research lecture notes laptop", LocalDate.now().minusDays(1)));
        putClaim(new Claim("CLM-3003", "FOUND-2002", "bob_student", UserRole.STUDENT,
                "Student ID #7376231", "Dropped near tray return", LocalDate.now().minusDays(2)));
    }

    public void registerUser(User user) {
        userRegistry.put(user.getUsername(), user);
    }

    public void putLostItem(LostItem item) {
        lostItemsMap.put(item.getId(), item);
    }

    public void putFoundItem(FoundItem item) {
        foundItemsMap.put(item.getId(), item);
    }

    public void putClaim(Claim claim) {
        claimsMap.put(claim.getId(), claim);
    }

    // -------------------------------------------------------------------------
    // 1. FAST HASHMAP LOOKUPS (O(1))
    // -------------------------------------------------------------------------
    public LostItem getLostItem(String id) {
        return lostItemsMap.get(id); // O(1) hash bucket lookup
    }

    public FoundItem getFoundItem(String id) {
        return foundItemsMap.get(id); // O(1) hash bucket lookup
    }

    public Claim getClaim(String id) {
        return claimsMap.get(id); // O(1) hash bucket lookup
    }

    public User getUser(String username) {
        return userRegistry.get(username != null ? username.toLowerCase() : "");
    }

    // -------------------------------------------------------------------------
    // 2. FREQUENCY COUNTING WITH MAP.MERGE()
    // -------------------------------------------------------------------------

    /**
     * Frequency distribution of incidents by Category using Map.merge().
     * merge() cleanly handles both absent and existing keys atomically in O(1).
     */
    public Map<String, Integer> getCategoryFrequencyDistribution() {
        Map<String, Integer> freqMap = new HashMap<>();

        for (LostItem item : lostItemsMap.values()) {
            freqMap.merge(item.getCategory(), 1, Integer::sum);
        }
        for (FoundItem item : foundItemsMap.values()) {
            freqMap.merge(item.getCategory(), 1, Integer::sum);
        }

        return freqMap;
    }

    /**
     * Frequency distribution of incidents by Location using Map.merge().
     */
    public Map<String, Integer> getLocationFrequencyDistribution() {
        Map<String, Integer> freqMap = new HashMap<>();

        for (LostItem item : lostItemsMap.values()) {
            freqMap.merge(item.getLocation(), 1, Integer::sum);
        }
        for (FoundItem item : foundItemsMap.values()) {
            freqMap.merge(item.getLocation(), 1, Integer::sum);
        }

        return freqMap;
    }

    /**
     * Frequency distribution of reports by Department using Map.merge().
     */
    public Map<String, Integer> getDepartmentReportFrequency() {
        Map<String, Integer> freqMap = new HashMap<>();

        for (LostItem item : lostItemsMap.values()) {
            User reporter = getUser(item.getReporterUsername());
            String dept = reporter != null ? reporter.getDepartment() : "Unknown";
            freqMap.merge(dept, 1, Integer::sum);
        }

        return freqMap;
    }

    // -------------------------------------------------------------------------
    // 3. TREEMAP SORTED ANALYTICAL REPORTS
    // -------------------------------------------------------------------------

    /**
     * Groups custody inventory by Category using java.util.TreeMap.
     * Guarantees categories appear in strict alphabetical order in O(log N) per insert.
     */
    public Map<String, List<FoundItem>> getSortedCategoryInventoryReport() {
        Map<String, List<FoundItem>> report = new TreeMap<>(); // Sorted naturally by Category key

        for (FoundItem item : foundItemsMap.values()) {
            report.computeIfAbsent(item.getCategory(), k -> new ArrayList<>()).add(item);
        }

        return report;
    }

    /**
     * Department valuation report using java.util.TreeMap.
     * Computes total estimated value of lost assets per department, sorted by Department name.
     */
    public Map<String, Double> getSortedDepartmentValuationReport() {
        Map<String, Double> report = new TreeMap<>(); // Alphabetically sorted by department

        for (LostItem item : lostItemsMap.values()) {
            User user = getUser(item.getReporterUsername());
            String dept = user != null ? user.getDepartment() : "General";
            report.merge(dept, item.getEstimatedValue(), Double::sum);
        }

        return report;
    }

    // -------------------------------------------------------------------------
    // 4. COMPARATOR CHAINS (COMPARATOR.COMPARING().REVERSED().THENCOMPARING())
    // -------------------------------------------------------------------------

    /**
     * COMPARATOR 1: High-Value Priority Chain
     * 1. Estimated Value DESCENDING (.reversed())
     * 2. Lost Date ASCENDING (.thenComparing())
     * 3. Title ASCENDING (.thenComparing())
     */
    public List<LostItem> getTopValuedLostItems() {
        List<LostItem> list = new ArrayList<>(lostItemsMap.values());

        Comparator<LostItem> highValueComparator = Comparator
                .comparing(LostItem::getEstimatedValue)
                .reversed()
                .thenComparing(LostItem::getLostDate)
                .thenComparing(LostItem::getTitle, String.CASE_INSENSITIVE_ORDER);

        list.sort(highValueComparator);
        return list;
    }

    /**
     * COMPARATOR 2: Multi-Factor Claim Review Priority Chain
     * 1. Claimant Role Priority ASCENDING (Faculty = 1 > Staff = 2 > Student = 3)
     * 2. Claim Date ASCENDING (Earliest claim first)
     * 3. Claim ID ASCENDING (Tie-breaker)
     */
    public List<Claim> getPrioritizedClaims() {
        List<Claim> list = new ArrayList<>(claimsMap.values());

        Comparator<Claim> claimComparator = Comparator
                .comparing((Claim c) -> c.getClaimantRole().getPriorityLevel())
                .thenComparing(Claim::getClaimDate)
                .thenComparing(Claim::getId);

        list.sort(claimComparator);
        return list;
    }

    // -------------------------------------------------------------------------
    // 5. MISSING HASHCODE BUG DEMONSTRATION & FIX
    // -------------------------------------------------------------------------

    /**
     * Demonstrates the missing-hashCode bug and the fix.
     */
    public String demonstrateMissingHashCodeBug() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================================================\n");
        sb.append("  DEMO: MISSING HASHCODE BUG AND FIX (EQUALS/HASHCODE CONTRACT)\n");
        sb.append("====================================================================================\n\n");

        sb.append("SCENARIO 1: Broken Key (implements equals() but OMITS hashCode())\n");
        Map<BrokenItemKey, String> brokenMap = new HashMap<>();

        BrokenItemKey key1 = new BrokenItemKey("FOUND-2001", "Electronics");
        BrokenItemKey key2 = new BrokenItemKey("FOUND-2001", "Electronics");

        sb.append("1. Creating key1: new BrokenItemKey(\"FOUND-2001\", \"Electronics\")\n");
        sb.append("2. Creating key2: new BrokenItemKey(\"FOUND-2001\", \"Electronics\")\n");
        sb.append("   - key1.equals(key2) result: ").append(key1.equals(key2)).append(" (Logically Equal!)\n");
        sb.append("   - key1.hashCode(): ").append(System.identityHashCode(key1)).append("\n");
        sb.append("   - key2.hashCode(): ").append(System.identityHashCode(key2)).append("\n");
        sb.append("   -> Notice: System identity hashCodes are DIFFERENT!\n\n");

        brokenMap.put(key1, "Locker-12 Secured ThinkPad");
        sb.append("3. Executed: brokenMap.put(key1, \"Locker-12 Secured ThinkPad\")\n");
        String lookupWithKey2 = brokenMap.get(key2);
        sb.append("4. Executed: brokenMap.get(key2)\n");
        sb.append("   -> Result: ").append(lookupWithKey2).append(" (BUG: Returned NULL despite equal key!)\n\n");

        sb.append("------------------------------------------------------------------------------------\n");
        sb.append("SCENARIO 2: Fixed Key (implements both equals() and hashCode() with Objects.hash)\n");
        Map<FixedItemKey, String> fixedMap = new HashMap<>();

        FixedItemKey fixedKey1 = new FixedItemKey("FOUND-2001", "Electronics");
        FixedItemKey fixedKey2 = new FixedItemKey("FOUND-2001", "Electronics");

        sb.append("1. Creating fixedKey1: new FixedItemKey(\"FOUND-2001\", \"Electronics\")\n");
        sb.append("2. Creating fixedKey2: new FixedItemKey(\"FOUND-2001\", \"Electronics\")\n");
        sb.append("   - fixedKey1.equals(fixedKey2) result: ").append(fixedKey1.equals(fixedKey2)).append("\n");
        sb.append("   - fixedKey1.hashCode(): ").append(fixedKey1.hashCode()).append("\n");
        sb.append("   - fixedKey2.hashCode(): ").append(fixedKey2.hashCode()).append("\n");
        sb.append("   -> Hash codes are IDENTICAL!\n\n");

        fixedMap.put(fixedKey1, "Locker-12 Secured ThinkPad");
        sb.append("3. Executed: fixedMap.put(fixedKey1, \"Locker-12 Secured ThinkPad\")\n");
        String lookupFixed = fixedMap.get(fixedKey2);
        sb.append("4. Executed: fixedMap.get(fixedKey2)\n");
        sb.append("   -> Result: \"").append(lookupFixed).append("\" (SUCCESS: Retrieved correctly in O(1)!)\n");
        sb.append("====================================================================================\n");

        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // 6. FUNCTIONAL REQUIREMENTS FR4–FR6
    // -------------------------------------------------------------------------

    /**
     * FR4: Automated Item Matching & Correlation Engine
     * Given a lost item ID, searches foundItemsMap for candidate matches based on
     * category alignment, title keyword overlap, and location similarity.
     */
    public List<String> matchLostItem(String lostItemId) {
        List<String> results = new ArrayList<>();
        LostItem lost = getLostItem(lostItemId);
        if (lost == null) {
            results.add("[ERROR] Lost item '" + lostItemId + "' not found.");
            return results;
        }

        results.add(String.format("Scanning custody inventory for Lost Item [%s] \"%s\" (Category: %s, Location: %s)...",
                lost.getId(), lost.getTitle(), lost.getCategory(), lost.getLocation()));

        for (FoundItem found : foundItemsMap.values()) {
            if ("CLAIMED".equalsIgnoreCase(found.getStatus())) continue;

            int score = 0;
            // 1. Category match (40 points)
            if (lost.getCategory().equalsIgnoreCase(found.getCategory())) {
                score += 40;
            }
            // 2. Location proximity (30 points)
            if (lost.getLocation().toLowerCase().contains(found.getLocation().toLowerCase())
                    || found.getLocation().toLowerCase().contains(lost.getLocation().toLowerCase())) {
                score += 30;
            }
            // 3. Keyword similarity in title (30 points)
            String[] lostKeywords = lost.getTitle().toLowerCase().split("\\s+");
            for (String kw : lostKeywords) {
                if (kw.length() > 2 && found.getTitle().toLowerCase().contains(kw)) {
                    score += 30;
                    break;
                }
            }

            if (score >= 40) {
                results.add(String.format("   -> MATCH CANDIDATE [%d%%]: Found Item [%s] \"%s\" in %s (Found: %s, Locker: %s)",
                        score, found.getId(), found.getTitle(), found.getLocation(), found.getFoundDate(), found.getLockerNumber()));
            }
        }

        if (results.size() == 1) {
            results.add("   No matching found items currently in custody.");
        }

        return results;
    }

    /**
     * FR5: Verification Challenge Quiz Authentication
     * Verifies the claimant's secret answer against the stored secret in O(1) time.
     */
    public boolean verifyClaimChallenge(String foundItemId, String providedSecretAnswer) {
        FoundItem item = getFoundItem(foundItemId);
        if (item == null || providedSecretAnswer == null) {
            return false;
        }
        return item.getSecretQuizAnswer().equalsIgnoreCase(providedSecretAnswer.trim());
    }

    /**
     * FR6: Handover Resolution & Atomic Status Updates across Maps
     */
    public String resolveHandover(String claimId, String securityOfficerUsername) {
        Claim claim = getClaim(claimId);
        if (claim == null) {
            return "[ERROR] Claim '" + claimId + "' does not exist.";
        }

        FoundItem foundItem = getFoundItem(claim.getFoundItemId());
        if (foundItem == null) {
            return "[ERROR] Target found item '" + claim.getFoundItemId() + "' does not exist.";
        }

        if ("CLAIMED".equalsIgnoreCase(foundItem.getStatus())) {
            return "[ERROR] Item is already claimed and handed over.";
        }

        // 1. Verify Quiz Secret Authentication
        if (!verifyClaimChallenge(foundItem.getId(), claim.getQuizAnswer())) {
            claim.setStatus("REJECTED");
            claim.setReviewedBy(securityOfficerUsername);
            claim.setReviewRemarks("Ownership verification challenge quiz FAILED.");
            return String.format("[REJECTED] Claim %s rejected: Secret verification answer did not match.", claim.getId());
        }

        // 2. Perform Atomic Handover Updates across Maps
        claim.setStatus("APPROVED");
        claim.setReviewedBy(securityOfficerUsername);
        claim.setReviewRemarks("Physical identity verified. Handover signature logged.");

        foundItem.setStatus("CLAIMED");
        foundItem.setApprovedClaimId(claim.getId());

        // Update corresponding lost item if present
        for (LostItem lost : lostItemsMap.values()) {
            if (lost.getReporterUsername().equalsIgnoreCase(claim.getClaimantUsername())
                    && lost.getCategory().equalsIgnoreCase(foundItem.getCategory())) {
                lost.setStatus("RESOLVED");
            }
        }

        return String.format("[HANDOVER COMPLETE] Item [%s] \"%s\" handed over to %s (%s). Locker %s released.",
                foundItem.getId(), foundItem.getTitle(), claim.getClaimantUsername(),
                claim.getClaimantRole(), foundItem.getLockerNumber());
    }

    // -------------------------------------------------------------------------
    // CONSOLE APP INTERACTIVE RUNNER
    // -------------------------------------------------------------------------
    public static void main(String[] args) {
        CampusDirectory directory = new CampusDirectory();
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        System.out.println("====================================================================================");
        System.out.println("  CAMPUS LOST AND FOUND PORTAL -- DAY 8: COLLECTIONS FRAMEWORK PART 2");
        System.out.println("  Maps, Comparators, TreeMap Reports, Missing-hashCode Bug Demo & FR4-FR6");
        System.out.println("====================================================================================");

        while (!exit) {
            System.out.println("\n------------------------------------------------------------------------------------");
            System.out.println("MENU OPTIONS:");
            System.out.println("  1. [FR4] Fast HashMap Lookups (Lost Items, Found Items, Claims in O(1))");
            System.out.println("  2. [FR4] Run Automated Item Matching & Correlation Engine");
            System.out.println("  3. [FR5] Submit & Verify Claim Challenge Quiz (O(1) Map Authentication)");
            System.out.println("  4. [FR6] Authorize Handover & Atomic Status Transition");
            System.out.println("  5. [Analytics] Frequency Analysis with Map.merge() (Categories & Locations)");
            System.out.println("  6. [Reports] TreeMap Sorted Reports (Categories & Department Valuations)");
            System.out.println("  7. [Sorting] Custom Comparator Chains (Top-Valued Items & Prioritized Claims)");
            System.out.println("  8. [Demo] Missing-hashCode Bug & Fix Demonstration");
            System.out.println("  9. [Guide] View HashMap vs LinkedHashMap vs TreeMap Decision Matrix");
            System.out.println("  0. Exit");
            System.out.print("Enter choice (0-9): ");

            String input = scanner.hasNextLine() ? scanner.nextLine().trim() : "0";

            switch (input) {
                case "1":
                    System.out.println("\n--- [FR4] HASHMAP O(1) REGISTRIES ---");
                    System.out.println("Lost Items Map Size: " + directory.lostItemsMap.size());
                    for (LostItem li : directory.lostItemsMap.values()) System.out.println("  " + li);
                    System.out.println("\nFound Items Map Size: " + directory.foundItemsMap.size());
                    for (FoundItem fi : directory.foundItemsMap.values()) System.out.println("  " + fi);
                    break;

                case "2":
                    System.out.println("\n--- [FR4] ITEM MATCHING ENGINE ---");
                    System.out.print("Enter Lost Item ID to match (e.g. LOST-1001, LOST-1002): ");
                    String lostId = scanner.hasNextLine() ? scanner.nextLine().trim() : "LOST-1001";
                    List<String> matchResults = directory.matchLostItem(lostId);
                    for (String res : matchResults) System.out.println(res);
                    break;

                case "3":
                    System.out.println("\n--- [FR5] VERIFICATION CHALLENGE QUIZ AUTHENTICATION ---");
                    System.out.print("Found Item ID: ");
                    String fid = scanner.hasNextLine() ? scanner.nextLine().trim() : "FOUND-2001";
                    System.out.print("Enter Secret Challenge Answer: ");
                    String answer = scanner.hasNextLine() ? scanner.nextLine().trim() : "HCL sticker on lid";
                    boolean auth = directory.verifyClaimChallenge(fid, answer);
                    System.out.println("Authentication Result: " + (auth ? "[PASS] Secret matched!" : "[FAIL] Invalid secret answer."));
                    break;

                case "4":
                    System.out.println("\n--- [FR6] HANDOVER RESOLUTION & ATOMIC UPDATES ---");
                    System.out.print("Enter Claim ID to resolve (e.g. CLM-3001, CLM-3002): ");
                    String cid = scanner.hasNextLine() ? scanner.nextLine().trim() : "CLM-3001";
                    String outcome = directory.resolveHandover(cid, "security_officer_bob");
                    System.out.println(outcome);
                    break;

                case "5":
                    System.out.println("\n--- [ANALYTICS] FREQUENCY DISTRIBUTION VIA MAP.MERGE() ---");
                    System.out.println("Category Incident Frequencies (Map.merge):");
                    directory.getCategoryFrequencyDistribution().forEach((cat, count) ->
                            System.out.printf("  %-16s : %d incidents\n", cat, count));

                    System.out.println("\nLocation Incident Frequencies (Map.merge):");
                    directory.getLocationFrequencyDistribution().forEach((loc, count) ->
                            System.out.printf("  %-24s : %d incidents\n", loc, count));

                    System.out.println("\nDepartment Report Frequencies (Map.merge):");
                    directory.getDepartmentReportFrequency().forEach((dept, count) ->
                            System.out.printf("  %-20s : %d reports\n", dept, count));
                    break;

                case "6":
                    System.out.println("\n--- [REPORTS] TREEMAP SORTED ANALYTICAL REPORTS ---");
                    System.out.println("1. Custody Inventory Sorted by Category (TreeMap):");
                    directory.getSortedCategoryInventoryReport().forEach((cat, items) -> {
                        System.out.println("  Category [" + cat + "]:");
                        for (FoundItem fi : items) System.out.println("    - " + fi);
                    });

                    System.out.println("\n2. Department Estimated Asset Valuation (TreeMap):");
                    directory.getSortedDepartmentValuationReport().forEach((dept, val) ->
                            System.out.printf("  Department: %-20s -> Total Lost Valuation: $%.2f\n", dept, val));
                    break;

                case "7":
                    System.out.println("\n--- [SORTING] CUSTOM COMPARATOR CHAINS ---");
                    System.out.println("1. Top-Valued Lost Items (Comparator.comparing(Value).reversed().thenComparing(Date)):");
                    List<LostItem> topValued = directory.getTopValuedLostItems();
                    for (int i = 0; i < topValued.size(); i++) {
                        LostItem item = topValued.get(i);
                        System.out.printf("   #%d: $%-8.2f | %-24s | Date: %s | ID: %s\n",
                                i + 1, item.getEstimatedValue(), item.getTitle(), item.getLostDate(), item.getId());
                    }

                    System.out.println("\n2. Prioritized Claims (Comparator.comparing(RolePriority).thenComparing(Date)):");
                    List<Claim> prioritizedClaims = directory.getPrioritizedClaims();
                    for (int i = 0; i < prioritizedClaims.size(); i++) {
                        Claim c = prioritizedClaims.get(i);
                        System.out.printf("   #%d: Pri %d (%s) | Date: %s | Claim ID: %s | Item: %s\n",
                                i + 1, c.getClaimantRole().getPriorityLevel(), c.getClaimantRole(),
                                c.getClaimDate(), c.getId(), c.getFoundItemId());
                    }
                    break;

                case "8":
                    System.out.println("\n" + directory.demonstrateMissingHashCodeBug());
                    break;

                case "9":
                    System.out.println("\n==================================================================================================================");
                    System.out.println("                            MAP SELECTION DECISION MATRIX");
                    System.out.println("==================================================================================================================");
                    System.out.printf("%-16s | %-22s | %-16s | %-16s | %-32s\n",
                            "Map Type", "Underlying Structure", "Time Complexity", "Ordering", "Campus Lost & Found Project Usage");
                    System.out.println("------------------------------------------------------------------------------------------------------------------");
                    System.out.printf("%-16s | %-22s | %-16s | %-16s | %-32s\n",
                            "HashMap", "Bucket Array + Tree", "O(1) average", "None", "FR4/FR5: Primary registry lookups by ID");
                    System.out.printf("%-16s | %-22s | %-16s | %-16s | %-32s\n",
                            "LinkedHashMap", "HashMap + Doubly-Link", "O(1) average", "Insertion Order", "User registry & chronological audit logs");
                    System.out.printf("%-16s | %-22s | %-16s | %-16s | %-32s\n",
                            "TreeMap", "Red-Black Tree", "O(log N)", "Sorted by Key", "FR4: Category & Department breakdown reports");
                    System.out.println("==================================================================================================================");
                    break;

                case "0":
                    exit = true;
                    System.out.println("\nExiting Campus Directory. Goodbye!");
                    break;

                default:
                    System.out.println("[ERROR] Invalid choice. Select 0-9.");
            }
        }
    }
}
