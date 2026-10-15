package com.campus.lostandfound.service;

import com.campus.lostandfound.model.*;

import java.time.LocalDate;
import java.util.*;

/**
 * ============================================================================
 * Service: CampusLostAndFoundService
 * ============================================================================
 * Core business service implementing FR1 through FR3 for the Campus Lost and
 * Found Portal using Java Collections Framework Part 1.
 *
 * ----------------------------------------------------------------------------
 * COMPREHENSIVE COLLECTION CHOICES & ARCHITECTURAL JUSTIFICATIONS:
 * ----------------------------------------------------------------------------
 *
 * 1. java.util.ArrayList (FR1: Lost Items Catalogue & History):
 *    - JUSTIFICATION:
 *      Lost item reporting is write-occasional and read-intensive. Users and
 *      campus security frequently search and page through reported lost items.
 *      ArrayList is backed by a contiguous, resizable dynamic array.
 *      It provides constant-time O(1) indexed random access via get(int index).
 *      Because elements are contiguous in RAM, modern CPU hardware pre-fetches
 *      elements into L1/L2 cache lines (spatial locality), yielding up to 5x-10x
 *      faster iteration performance than LinkedList.
 *    - ALTERNATIVE REJECTED:
 *      LinkedList was rejected because get(int index) is O(N), requiring pointer
 *      traversal from the nearest end. Furthermore, LinkedList allocates a 24-byte
 *      Node object per entry (item reference, next, prev), generating GC overhead.
 *
 * 2. java.util.LinkedHashSet (FR2: Found Items Inventory & Claim De-dup):
 *    - JUSTIFICATION:
 *      Physical found items placed into custody lockers MUST be unique by ID.
 *      Additionally, a claimant must not submit duplicate claims for the same item.
 *      LinkedHashSet provides O(1) constant-time contains() and add() operations
 *      via hash bucketing, while maintaining a doubly-linked list through all
 *      elements to preserve exact insertion order.
 *      Preserving insertion order ensures that the custody log and claim intake
 *      reflect the exact chronological sequence of campus physical security events.
 *    - ALTERNATIVES REJECTED:
 *      - HashSet: Scrambles element order based on hash buckets, destroying custody audit logs.
 *      - TreeSet: Incurs O(log N) Red-Black tree balancing overhead, which is unnecessary
 *        when pure hash-based uniqueness with insertion ordering suffices.
 *
 * 3. java.util.ArrayDeque (FR3: Standard Claims Review Queue):
 *    - JUSTIFICATION:
 *      Standard student and visitor claims are processed on a First-In-First-Out (FIFO) basis.
 *      ArrayDeque implements both Queue and Deque interfaces backed by an efficient,
 *      resizable circular array buffer with head and tail pointers.
 *      It provides O(1) amortized enqueue (offerLast) and dequeue (pollFirst) operations
 *      without allocating a separate Node object for each claim.
 *    - ALTERNATIVE REJECTED:
 *      LinkedList as a Queue was rejected because it creates a new Node instance on
 *      every push, stressing Garbage Collection under heavy load. The Java API
 *      documentation explicitly states: "This class [ArrayDeque] is likely to be
 *      faster than Stack when used as a stack, and faster than LinkedList when
 *      used as a queue."
 *
 * 4. java.util.PriorityQueue (FR3: Faculty-First Expedited Claims Queue):
 *    - JUSTIFICATION:
 *      Campus institutional policy mandates that Faculty and Senior Researchers receive
 *      expedited review for items critical to lectures, labs, and research (e.g. lab keys,
 *      research laptops, grade records).
 *      PriorityQueue is implemented as an unbounded binary min-heap stored in an array.
 *      It provides O(log N) insertion (offer) and extraction (poll), and O(1) peek.
 *      Ordering is driven by Claim.compareTo(), sorting by UserRole priority level first
 *      (FACULTY = 1 > STAFF = 2 > STUDENT = 3 > GUEST = 4), with ties broken chronologically
 *      by claim date.
 *
 * 5. java.util.TreeSet (Sorted Inventory Views):
 *    - JUSTIFICATION:
 *      NavigableSet backed by a Red-Black balanced binary search tree.
 *      Provides guaranteed O(log N) time complexity for search and insertion while
 *      maintaining elements in naturally sorted order (e.g. alphabetical by item title).
 *
 * 6. java.util.HashSet (Unique Categories & Locations Registry):
 *    - JUSTIFICATION:
 *      Provides O(1) lookup and uniqueness when ordering is completely irrelevant.
 *
 * ----------------------------------------------------------------------------
 * ITERATION PATTERNS & CONCURRENTMODIFICATIONEXCEPTION:
 * ----------------------------------------------------------------------------
 * - For-each loop: Clean syntactic sugar over Iterator, but calling collection.remove()
 *   inside it throws ConcurrentModificationException due to modCount mismatch.
 * - Iterator.remove(): The ONLY safe way to delete items during iteration. It
 *   synchronizes expectedModCount = modCount inside the iterator.
 * - ListIterator: Bidirectional traversal (hasPrevious, previous) and index manipulation.
 * ============================================================================
 */
public class CampusLostAndFoundService {

    // 1. Master List: Contiguous ArrayList for Lost Item Reports (FR1)
    private final List<LostItem> lostItems = new ArrayList<>();

    // 2. Master Set: LinkedHashSet for Unique Found Items in Custody (FR2)
    private final Set<FoundItem> foundItemsInventory = new LinkedHashSet<>();

    // 3. Deduplication Cache: Prevents duplicate active claims ("CLAIMANT:FOUND_ID")
    private final Set<String> activeClaimDeduplicationSet = new LinkedHashSet<>();

    // 4. Standard FIFO Queue: ArrayDeque circular buffer for Student & Guest claims (FR3)
    private final Queue<Claim> standardClaimsQueue = new ArrayDeque<>();

    // 5. Faculty-First Priority Queue: PriorityQueue min-heap for Faculty & Staff claims (FR3)
    private final PriorityQueue<Claim> facultyFirstClaimsQueue = new PriorityQueue<>();

    // 6. Complete Claim History Audit List
    private final List<Claim> allClaimsHistory = new ArrayList<>();

    // Registered Campus Users Index (username -> User)
    private final Map<String, User> userRegistry = new HashMap<>();

    // Auto-increment counters
    private int lostIdCounter = 1001;
    private int foundIdCounter = 2001;
    private int claimIdCounter = 3001;

    public CampusLostAndFoundService() {
        seedSampleData();
    }

    /**
     * Seeds initial realistic campus data.
     */
    private void seedSampleData() {
        // Register Users across roles
        registerUser(new User("alan_turing", "Prof. Alan Turing", "turing@campus.edu", "Computer Science", UserRole.FACULTY));
        registerUser(new User("ada_lovelace", "Dr. Ada Lovelace", "lovelace@campus.edu", "Mathematics", UserRole.FACULTY));
        registerUser(new User("john_staff", "John Smith", "jsmith@campus.edu", "Campus Facilities", UserRole.STAFF));
        registerUser(new User("alice_student", "Alice Williams", "alice@student.campus.edu", "Data Science", UserRole.STUDENT));
        registerUser(new User("bob_student", "Bob Johnson", "bob@student.campus.edu", "Electrical Eng", UserRole.STUDENT));
        registerUser(new User("charlie_student", "Charlie Brown", "charlie@student.campus.edu", "Physics", UserRole.STUDENT));
        registerUser(new User("visitor_dave", "Dave Miller", "dave@visitor.org", "Visitor", UserRole.GUEST));

        // FR1: Seed Lost Items (ArrayList)
        reportLostItem("ThinkPad X1 Carbon", "Electronics", "Library 2nd Floor", LocalDate.now().minusDays(3),
                "Lenovo", "Black", "HCL sticker on lid", 1200.00, "alice_student");
        reportLostItem("Leather Wallet", "Personal", "Campus Cafeteria", LocalDate.now().minusDays(2),
                "Fossil", "Brown", "Contains College ID #7376231", 60.00, "bob_student");
        reportLostItem("Wireless Earbuds", "Electronics", "Gymnasium Locker Room", LocalDate.now().minusDays(1),
                "Sony", "Silver", "Left earbud has scratch", 150.00, "charlie_student");
        reportLostItem("Research Lab Notebook", "Documents", "Science Lab 3", LocalDate.now().minusDays(5),
                "Moleskine", "Blue", "Quantum computing notes", 45.00, "alan_turing");

        // FR2: Seed Found Items (LinkedHashSet)
        logFoundItem("Black ThinkPad Laptop", "Electronics", "Library Quiet Zone", LocalDate.now().minusDays(3),
                "Locker-12", "HCL sticker on lid, serial ending 884", "john_staff");
        logFoundItem("Brown Leather Wallet", "Personal", "Cafeteria Table 4", LocalDate.now().minusDays(2),
                "Locker-05", "College ID card in window slot", "john_staff");
        logFoundItem("Scientific Calculator", "Electronics", "Math Hall 101", LocalDate.now().minusDays(10),
                "Locker-08", "Name tag 'A. Lovelace' on back", "john_staff");

        // Preload an OVERDUE unclaimed found item (discovered 45 days ago, past 30-day retention threshold)
        FoundItem overdueItem = new FoundItem("FOUND-2000", "Sports Water Bottle", "Personal",
                "Tennis Courts", LocalDate.now().minusDays(45), "Locker-99", "Red cap with dents", "john_staff", 30);
        foundItemsInventory.add(overdueItem);

        // FR3: Seed Claims demonstrating PriorityQueue vs ArrayDeque
        // Student claim submitted earlier (2 days ago)
        submitClaim("FOUND-2001", "alice_student", "HCL sticker on top lid",
                "Lost my laptop while studying for exams.", LocalDate.now().minusDays(2));

        // Faculty claim submitted later (today) for testing Faculty-First priority
        submitClaim("FOUND-2001", "alan_turing", "HCL sticker on lid, contains thesis work",
                "Used during seminar in library.", LocalDate.now());

        // Another student claim
        submitClaim("FOUND-2002", "bob_student", "College ID card #7376231",
                "Dropped wallet after lunch.", LocalDate.now().minusDays(1));
    }

    public synchronized void registerUser(User user) {
        if (user != null) {
            userRegistry.put(user.getUsername(), user);
        }
    }

    public User getUserByUsername(String username) {
        return userRegistry.get(username != null ? username.trim().toLowerCase() : "");
    }

    // =========================================================================
    // FUNCTIONAL REQUIREMENT 1 (FR1): REPORT LOST ITEM (ARRAYLIST)
    // =========================================================================

    /**
     * FR1: Reports a lost item and stores it in the master ArrayList.
     *
     * COLLECTION JUSTIFICATION:
     * - ArrayList.add(item) offers O(1) amortized append time.
     * - Contiguous storage facilitates ultra-fast O(1) indexed lookup via get(i)
     *   during searches, filters, and paginated display.
     */
    public synchronized LostItem reportLostItem(String title, String category, String location,
                                                LocalDate lostDate, String brand, String color,
                                                String distinctiveFeatures, double estimatedValue,
                                                String reporterUsername) {
        String id = "LOST-" + (lostIdCounter++);
        LostItem item = new LostItem(id, title, category, location, lostDate, brand, color,
                distinctiveFeatures, estimatedValue, reporterUsername);
        lostItems.add(item); // ArrayList O(1) append
        return item;
    }

    /**
     * FR1: Returns an unmodifiable view of the master ArrayList catalogue.
     */
    public List<LostItem> getLostItemsCatalogue() {
        return Collections.unmodifiableList(lostItems);
    }

    /**
     * FR1: Finds a lost item by ID using O(1) indexed access over ArrayList.
     */
    public LostItem findLostItemById(String id) {
        if (id == null) return null;
        for (int i = 0; i < lostItems.size(); i++) {
            LostItem item = lostItems.get(i); // O(1) indexed access in ArrayList
            if (item.getId().equalsIgnoreCase(id.trim())) {
                return item;
            }
        }
        return null;
    }

    /**
     * FR1: Returns a sorted view of lost items using java.util.TreeSet.
     * COLLECTION JUSTIFICATION:
     * - TreeSet provides natural sorting (newest first via LostItem.compareTo)
     *   using a Red-Black tree with O(log N) operations.
     */
    public Set<LostItem> getSortedLostItemsChronological() {
        return Collections.unmodifiableSet(new TreeSet<>(lostItems));
    }

    // =========================================================================
    // FUNCTIONAL REQUIREMENT 2 (FR2): LOG FOUND ITEM & DE-DUPLICATION (LINKEDHASHSET)
    // =========================================================================

    /**
     * FR2: Logs a found item into custody using java.util.LinkedHashSet.
     *
     * COLLECTION JUSTIFICATION:
     * - LinkedHashSet guarantees uniqueness of custody items by BaseEntity ID (O(1)).
     * - Preserves the exact chronological intake sequence via internal doubly-linked list.
     * - Rejects duplicates cleanly if the item ID already exists.
     */
    public synchronized FoundItem logFoundItem(String title, String category, String location,
                                               LocalDate foundDate, String lockerNumber,
                                               String secretIdentificationFeature, String finderUsername) {
        String id = "FOUND-" + (foundIdCounter++);
        FoundItem item = new FoundItem(id, title, category, location, foundDate, lockerNumber,
                secretIdentificationFeature, finderUsername);
        foundItemsInventory.add(item); // LinkedHashSet O(1) insert with order preservation
        return item;
    }

    public Set<FoundItem> getFoundItemsInventory() {
        return Collections.unmodifiableSet(foundItemsInventory);
    }

    public FoundItem findFoundItemById(String id) {
        if (id == null) return null;
        for (FoundItem item : foundItemsInventory) {
            if (item.getId().equalsIgnoreCase(id.trim())) {
                return item;
            }
        }
        return null;
    }

    /**
     * FR2: Returns an alphabetical view of found items using java.util.TreeSet.
     */
    public Set<FoundItem> getSortedFoundItemsAlphabetical() {
        Set<FoundItem> sorted = new TreeSet<>(Comparator.comparing(FoundItem::getTitle, String.CASE_INSENSITIVE_ORDER));
        sorted.addAll(foundItemsInventory);
        return Collections.unmodifiableSet(sorted);
    }

    /**
     * FR2: Returns distinct locations using java.util.HashSet.
     * COLLECTION JUSTIFICATION:
     * - HashSet eliminates duplicate location strings in O(1) time with minimal overhead.
     */
    public Set<String> getDistinctLocations() {
        Set<String> locations = new HashSet<>();
        for (LostItem li : lostItems) {
            locations.add(li.getLocation());
        }
        for (FoundItem fi : foundItemsInventory) {
            locations.add(fi.getLocation());
        }
        return locations;
    }

    // =========================================================================
    // FUNCTIONAL REQUIREMENT 3 (FR3): CLAIM PROCESSING & MULTI-TIER WAITLIST
    // =========================================================================

    /**
     * FR3: Submits an ownership claim with de-duplication and multi-tier queue routing.
     *
     * BUSINESS RULES & COLLECTIONS LOGIC:
     * 1. De-duplication Check via LinkedHashSet:
     *    - A claimant cannot submit multiple pending claims for the same found item.
     *    - Checked in O(1) time using composite key "CLAIMANT:FOUND_ID".
     * 2. Multi-Tier Queue Routing:
     *    - FACULTY / STAFF -> Enqueued into PriorityQueue (Faculty-First expedited review).
     *    - STUDENT / GUEST -> Enqueued into ArrayDeque (Standard FIFO review).
     */
    public synchronized ClaimSubmissionResult submitClaim(String foundItemId, String claimantUsername,
                                                         String quizAnswerSecret, String proofDescription) {
        return submitClaim(foundItemId, claimantUsername, quizAnswerSecret, proofDescription, LocalDate.now());
    }

    public synchronized ClaimSubmissionResult submitClaim(String foundItemId, String claimantUsername,
                                                         String quizAnswerSecret, String proofDescription,
                                                         LocalDate claimDate) {
        FoundItem item = findFoundItemById(foundItemId);
        if (item == null) {
            return new ClaimSubmissionResult(false, null, "Found item '" + foundItemId + "' does not exist.");
        }

        if (!"IN_CUSTODY".equalsIgnoreCase(item.getStatus()) && !"CLAIM_PENDING".equalsIgnoreCase(item.getStatus())) {
            return new ClaimSubmissionResult(false, null,
                    "Found item '" + foundItemId + "' is not available for claim (Current Status: " + item.getStatus() + ").");
        }

        User claimant = getUserByUsername(claimantUsername);
        UserRole role = claimant != null ? claimant.getRole() : UserRole.STUDENT;

        // 1. De-duplication Check using LinkedHashSet (O(1))
        String dedupKey = claimantUsername.toLowerCase() + ":" + item.getId();
        if (activeClaimDeduplicationSet.contains(dedupKey)) {
            return new ClaimSubmissionResult(false, null,
                    String.format("[REJECTED - DUPLICATE] User '%s' already has a pending claim on item %s.",
                            claimantUsername, item.getId()));
        }

        // Create Claim
        String claimId = "CLM-" + (claimIdCounter++);
        Claim claim = new Claim(claimId, item.getId(), claimantUsername, role, quizAnswerSecret,
                proofDescription, claimDate, 7);

        // Record in de-duplication store
        activeClaimDeduplicationSet.add(dedupKey);

        // Add to global audit history
        allClaimsHistory.add(claim);

        // Update item status
        item.setStatus("CLAIM_PENDING");

        // 2. Queue into appropriate tier:
        if (role == UserRole.FACULTY || role == UserRole.STAFF) {
            // PriorityQueue (Min-Heap): O(log N) insertion, Faculty jumps to head
            facultyFirstClaimsQueue.offer(claim);
            return new ClaimSubmissionResult(true, claim,
                    String.format("[QUEUED - FACULTY PRIORITY] Claim %s queued in PriorityQueue (Role Priority Level: %d).",
                            claim.getId(), role.getPriorityLevel()));
        } else {
            // ArrayDeque: O(1) amortized FIFO enqueue
            standardClaimsQueue.offer(claim);
            return new ClaimSubmissionResult(true, claim,
                    String.format("[QUEUED - STANDARD FIFO] Claim %s queued in ArrayDeque (FIFO Order).",
                            claim.getId()));
        }
    }

    public List<Claim> getStandardClaimsQueueSnapshot() {
        return new ArrayList<>(standardClaimsQueue);
    }

    public List<Claim> getFacultyFirstClaimsQueueSnapshot() {
        List<Claim> snapshot = new ArrayList<>(facultyFirstClaimsQueue);
        Collections.sort(snapshot); // Sorted according to PriorityQueue heap extraction order
        return snapshot;
    }

    public List<Claim> getAllClaimsHistory() {
        return Collections.unmodifiableList(allClaimsHistory);
    }

    /**
     * FR3: Processes and reviews the next highest-priority claim in the system.
     * Priority Hierarchy:
     * 1. Inspects Faculty-First PriorityQueue first.
     * 2. If no faculty claims exist, falls back to Standard FIFO ArrayDeque.
     */
    public synchronized ClaimProcessingResult processNextClaim(String reviewerUsername) {
        Claim claim = null;
        String queueSource = null;

        // Check Faculty-First PriorityQueue first
        if (!facultyFirstClaimsQueue.isEmpty()) {
            claim = facultyFirstClaimsQueue.poll(); // O(log N) extraction of highest priority
            queueSource = "Faculty-First PriorityQueue (Heap Top)";
        } else if (!standardClaimsQueue.isEmpty()) {
            claim = standardClaimsQueue.poll(); // O(1) FIFO extraction
            queueSource = "Standard ArrayDeque (FIFO Head)";
        }

        if (claim == null) {
            return new ClaimProcessingResult(false, null, "No pending claims in either queue.");
        }

        FoundItem item = findFoundItemById(claim.getFoundItemId());
        if (item == null) {
            claim.setStatus(Claim.Status.REJECTED);
            claim.setReviewRemarks("Target item no longer exists in inventory.");
            return new ClaimProcessingResult(false, claim, "Target item was missing.");
        }

        // Approve and resolve
        claim.setStatus(Claim.Status.APPROVED);
        claim.setReviewedBy(reviewerUsername);
        claim.setReviewRemarks("Verified ownership challenge match. Handover authorized.");

        item.setStatus("CLAIMED");
        item.setApprovedClaimId(claim.getId());

        // Remove from deduplication cache
        activeClaimDeduplicationSet.remove(claim.getDeduplicationKey());

        return new ClaimProcessingResult(true, claim,
                String.format("Processed claim [%s] from %s. Claimant: %s (%s). Item: \"%s\". Status: APPROVED.",
                        claim.getId(), queueSource, claim.getClaimantUsername(),
                        claim.getClaimantRole(), item.getTitle()));
    }

    // =========================================================================
    // OVERDUE RETENTION PURGING USING ITERATOR.REMOVE
    // =========================================================================

    /**
     * Safely purges overdue unclaimed found items and expired claims using java.util.Iterator.remove().
     *
     * ----------------------------------------------------------------------------
     * CRITICAL ITERATION PATTERN: ITERATOR.REMOVE VS CONCURRENTMODIFICATIONEXCEPTION
     * ----------------------------------------------------------------------------
     * Calling collection.remove(item) inside an enhanced for-each loop triggers:
     *     java.util.ConcurrentModificationException!
     *
     * Internal Mechanics:
     * - Collections track structural mutations (add/remove) via an internal 'modCount' field.
     * - An Iterator copies this counter at creation into 'expectedModCount'.
     * - On every next() invocation, the iterator verifies:
     *       if (modCount != expectedModCount) throw new ConcurrentModificationException();
     * - Calling collection.remove() modifies 'modCount' without updating 'expectedModCount',
     *   guaranteeing a crash on the subsequent loop iteration.
     *
     * Safe Solution:
     * Calling iterator.remove() safely updates the collection structure AND synchronizes
     * expectedModCount = modCount inside the iterator, preventing any exception!
     */
    public synchronized PurgeResult purgeOverdueItemsAndClaims(LocalDate cutoffDate) {
        List<FoundItem> purgedFoundItems = new ArrayList<>();
        List<Claim> purgedClaims = new ArrayList<>();

        // 1. Purge Overdue Unclaimed Items from LinkedHashSet Inventory using Iterator.remove()
        Iterator<FoundItem> foundIterator = foundItemsInventory.iterator();
        while (foundIterator.hasNext()) {
            FoundItem item = foundIterator.next();
            if (item.isOverdue(cutoffDate)) {
                item.setStatus("EXPIRED_AUCTIONED");
                purgedFoundItems.add(item);
                foundIterator.remove(); // SAFE STRUCTURAL REMOVAL IN LINKEDHASHSET
            }
        }

        // 2. Purge Expired Claims from Standard ArrayDeque Queue using Iterator.remove()
        Iterator<Claim> dequeIterator = standardClaimsQueue.iterator();
        while (dequeIterator.hasNext()) {
            Claim claim = dequeIterator.next();
            if (claim.isOverdue(cutoffDate)) {
                claim.setStatus(Claim.Status.EXPIRED);
                purgedClaims.add(claim);
                activeClaimDeduplicationSet.remove(claim.getDeduplicationKey());
                dequeIterator.remove(); // SAFE STRUCTURAL REMOVAL IN ARRAYDEQUE
            }
        }

        // 3. Purge Expired Claims from PriorityQueue using Iterator.remove()
        Iterator<Claim> priorityIterator = facultyFirstClaimsQueue.iterator();
        while (priorityIterator.hasNext()) {
            Claim claim = priorityIterator.next();
            if (claim.isOverdue(cutoffDate)) {
                claim.setStatus(Claim.Status.EXPIRED);
                purgedClaims.add(claim);
                activeClaimDeduplicationSet.remove(claim.getDeduplicationKey());
                priorityIterator.remove(); // SAFE STRUCTURAL REMOVAL IN PRIORITYQUEUE
            }
        }

        return new PurgeResult(purgedFoundItems, purgedClaims);
    }

    /**
     * EDUCATIONAL DEMO 1:
     * Intentionally triggers ConcurrentModificationException using a for-each loop,
     * catches it, explains the internals, and demonstrates the safe Iterator.remove() pattern.
     */
    public String demonstrateConcurrentModificationExceptionDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================================================\n");
        sb.append("  DEMO: CONCURRENTMODIFICATIONEXCEPTION VS ITERATOR.REMOVE\n");
        sb.append("====================================================================================\n\n");

        List<String> badList = new ArrayList<>(Arrays.asList("ITEM-101", "ITEM-102 (Overdue)", "ITEM-103", "ITEM-104"));
        sb.append("Initial List: ").append(badList).append("\n");
        sb.append("Attempting deletion of overdue item inside an enhanced for-each loop...\n");

        try {
            for (String item : badList) {
                if (item.contains("Overdue")) {
                    badList.remove(item); // INCORRECT: direct list mutation during for-each
                }
            }
            sb.append("[UNEXPECTED] No exception was thrown!\n");
        } catch (ConcurrentModificationException e) {
            sb.append("\n>> CAUGHT java.util.ConcurrentModificationException as expected!\n");
            sb.append("   - Root Cause: Calling badList.remove() incremented modCount directly.\n");
            sb.append("   - On next iteration, Iterator verified: (modCount != expectedModCount) and threw exception.\n\n");
        }

        // Safe Alternative using Iterator.remove()
        List<String> goodList = new ArrayList<>(Arrays.asList("ITEM-101", "ITEM-102 (Overdue)", "ITEM-103", "ITEM-104"));
        sb.append("Now demonstrating SAFE removal using java.util.Iterator.remove():\n");
        Iterator<String> iterator = goodList.iterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            if (item.contains("Overdue")) {
                iterator.remove(); // CORRECT: safely updates both list and expectedModCount!
                sb.append("   [SUCCESS] Safely removed '").append(item).append("' using iterator.remove()\n");
            }
        }
        sb.append("\nFinal List after safe Iterator.remove: ").append(goodList).append("\n");
        sb.append("Result: Safe eviction completed without error!\n");
        return sb.toString();
    }

    /**
     * EDUCATIONAL DEMO 2:
     * Demonstrates ListIterator bidirectional traversal and in-place list modification.
     */
    public String demonstrateListIteratorDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================================================\n");
        sb.append("  DEMO: LISTITERATOR BIDIRECTIONAL TRAVERSAL & MODIFICATION\n");
        sb.append("====================================================================================\n\n");

        List<String> items = new ArrayList<>(Arrays.asList(
                "LOST-1001 (ThinkPad)",
                "LOST-1002 (Wallet)",
                "LOST-1003 (Earbuds)",
                "LOST-1004 (Notebook)"
        ));
        sb.append("Initial Lost Items List: ").append(items).append("\n\n");

        ListIterator<String> listIterator = items.listIterator();

        sb.append("1. Forward Traversal (hasNext / next / nextIndex):\n");
        while (listIterator.hasNext()) {
            int idx = listIterator.nextIndex();
            String item = listIterator.next();
            sb.append(String.format("   Index [%d]: %s\n", idx, item));
            if (item.contains("Wallet")) {
                listIterator.set("LOST-1002 (Wallet - CLAIM VERIFIED)");
                sb.append("   -> Modified in-place via listIterator.set()\n");
            }
        }

        sb.append("\n2. Backward Traversal (hasPrevious / previous / previousIndex):\n");
        while (listIterator.hasPrevious()) {
            int idx = listIterator.previousIndex();
            String item = listIterator.previous();
            sb.append(String.format("   Reverse Index [%d]: %s\n", idx, item));
        }

        sb.append("\nFinal List after ListIterator operations: ").append(items).append("\n");
        return sb.toString();
    }

    // Helper Result Classes
    public static class ClaimSubmissionResult {
        public final boolean success;
        public final Claim claim;
        public final String message;

        public ClaimSubmissionResult(boolean success, Claim claim, String message) {
            this.success = success;
            this.claim = claim;
            this.message = message;
        }
    }

    public static class ClaimProcessingResult {
        public final boolean success;
        public final Claim claim;
        public final String message;

        public ClaimProcessingResult(boolean success, Claim claim, String message) {
            this.success = success;
            this.claim = claim;
            this.message = message;
        }
    }

    public static class PurgeResult {
        public final List<FoundItem> purgedFoundItems;
        public final List<Claim> purgedClaims;

        public PurgeResult(List<FoundItem> purgedFoundItems, List<Claim> purgedClaims) {
            this.purgedFoundItems = purgedFoundItems;
            this.purgedClaims = purgedClaims;
        }
    }
}
