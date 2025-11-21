package com.skillstorm.proj1_demo.exceptions;

/**
 * Exception thrown when a requested entity is not found in the database.
 */
public class EntityNotFoundException extends RuntimeException {
    
    public EntityNotFoundException(String message) {
        super(message);
    }
    
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
