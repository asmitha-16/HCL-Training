package com.campus.lostandfound.store;

import com.campus.lostandfound.model.Claim;
import com.campus.lostandfound.model.FoundItem;
import com.campus.lostandfound.model.LostItem;
import com.campus.lostandfound.model.role.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * In-memory repository implementing Java Collections requirement:
 * - List for ordered lost item reports
 * - Set for unique found items in custody
 * - Queue for FIFO claims processing
 */
public class CampusDataStore {

    // 1. List for lost items (preserves sequential reporting history)
    private final List<LostItem> lostItems = new ArrayList<>();

    // 2. Set for found items (enforces distinctness of physical inventory by BaseEntity ID)
    private final Set<FoundItem> foundItems = new LinkedHashSet<>();

    // 3. Queue for pending claims (First-In-First-Out FIFO custody review processing)
    private final Queue<Claim> pendingClaims = new LinkedList<>();

    // Ancillary index lookups
    private final Map<String, Claim> allClaimsById = new HashMap<>();
    private final Map<String, User> usersByUsername = new HashMap<>();

    public List<LostItem> getLostItems() {
        return lostItems;
    }

    public Set<FoundItem> getFoundItems() {
        return foundItems;
    }

    public Queue<Claim> getPendingClaims() {
        return pendingClaims;
    }

    public Map<String, Claim> getAllClaimsById() {
        return allClaimsById;
    }

    public Map<String, User> getUsersByUsername() {
        return usersByUsername;
    }

    public synchronized void addLostItem(LostItem item) {
        lostItems.add(item);
    }

    public synchronized boolean addFoundItem(FoundItem item) {
        return foundItems.add(item);
    }

    public synchronized void enqueueClaim(Claim claim) {
        pendingClaims.offer(claim);
        allClaimsById.put(claim.getId(), claim);
    }

    public synchronized Claim pollNextClaim() {
        return pendingClaims.poll();
    }

    public synchronized void registerUser(User user) {
        usersByUsername.put(user.getUsername(), user);
    }

    public FoundItem findFoundItemById(String id) {
        for (FoundItem item : foundItems) {
            if (item.getId().equalsIgnoreCase(id)) {
                return item;
            }
        }
        return null;
    }

    public LostItem findLostItemById(String id) {
        for (LostItem item : lostItems) {
            if (item.getId().equalsIgnoreCase(id)) {
                return item;
            }
        }
        return null;
    }
}
