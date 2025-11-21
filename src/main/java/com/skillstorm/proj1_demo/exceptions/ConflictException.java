package com.skillstorm.proj1_demo.exceptions;

/**
 * Exception thrown when an operation violates a constraint or business rule.
 * Typically used for UNIQUE constraint violations or conflict scenarios.
 */
public class ConflictException extends RuntimeException {
    
    public ConflictException(String message) {
        super(message);
    }
    
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
