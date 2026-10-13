package com.campus.lostandfound.exception;

/**
 * Thrown when an operation violates domain business rules (e.g. invalid report date,
 * negative valuation, illegal status transition, missing required fields).
 */
public class BusinessRuleViolationException extends CampusLostAndFoundException {

    private final String ruleCode;

    public BusinessRuleViolationException(String message) {
        super(message);
        this.ruleCode = "BR-GENERAL";
    }

    public BusinessRuleViolationException(String ruleCode, String message) {
        super(message);
        this.ruleCode = ruleCode;
    }

    public String getRuleCode() {
        return ruleCode;
    }
}
