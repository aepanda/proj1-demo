package com.skillstorm.proj1_demo.dtos;

/**
 * Data Transfer Object for deletion reason (optional).
 * Used when confirming deletion to provide reason for audit trail.
 */
public class DeleteInventoryItemDTO {
    
    private String reason;  // Optional - reason for deletion
    
    // Constructors
    public DeleteInventoryItemDTO() {}
    
    public DeleteInventoryItemDTO(String reason) {
        this.reason = reason;
    }
    
    // Getters & Setters
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}
