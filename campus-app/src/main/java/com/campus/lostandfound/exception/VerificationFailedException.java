package com.campus.lostandfound.exception;

/**
 * Thrown when an ownership verification quiz or challenge answer does not match
 * the confidential physical attributes in custody records (Business Rule: Anti-Fraud Guard).
 */
public class VerificationFailedException extends BusinessRuleViolationException {

    public VerificationFailedException(String message) {
        super("BR-03-VERIFICATION", message);
    }
}
