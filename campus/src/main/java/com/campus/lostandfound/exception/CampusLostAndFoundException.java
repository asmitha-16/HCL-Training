package com.campus.lostandfound.exception;

/**
 * Base unchecked exception for all Campus Lost and Found domain errors.
 */
public class CampusLostAndFoundException extends RuntimeException {

    public CampusLostAndFoundException(String message) {
        super(message);
    }

    public CampusLostAndFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
