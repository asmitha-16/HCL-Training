package com.campus.lostandfound.exception;

/**
 * Thrown when an item or claim reference cannot be resolved in the store.
 */
public class ItemNotFoundException extends CampusLostAndFoundException {

    public ItemNotFoundException(String message) {
        super(message);
    }
}
