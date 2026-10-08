# Day 04 — Business Logic & Workflow Implementation

## Objective

Implement the core business logic of the Campus Lost & Found Portal using Java OOP concepts.

The focus is on converting the domain models created in Day 03 into meaningful workflows such as item matching, claim verification, handover processing, item expiry, and status management.

---

## Tasks Completed

### 1. Item Matching Logic

Implemented the basic matching logic between lost and found items.

The matching process considers important attributes such as:

- Item category
- Item name or description
- Location
- Date
- Keywords

A match is suggested when the lost and found item details have sufficient similarity.

#### Example

```java
class ItemMatcher {

    public boolean isMatch(LostItem lostItem, FoundItem foundItem) {

        if (!lostItem.getCategory().equalsIgnoreCase(foundItem.getCategory())) {
            return false;
        }

        if (!lostItem.getLocation().equalsIgnoreCase(foundItem.getLocation())) {
            return false;
        }

        return true;
    }
}
