package com.campus.lostandfound.exception;

/**
 * Thrown when an approval or duplicate claim attempt is made on a found item
 * that is already approved or handed over (Business Rule 1: FR6 Anti-Duplicate Approval Lock).
 */
public class ItemAlreadyClaimedException extends BusinessRuleViolationException {

    private final String itemId;

    public ItemAlreadyClaimedException(String itemId, String message) {
        super("BR-01-LOCK", message);
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }
}
