package com.skillstorm.proj1_demo.dtos;

import java.util.Date;

/**
 * Data Transfer Object for updating inventory items.
 * All fields are optional - only provided fields will be updated.
 */
public class UpdateInventoryItemDTO {
    
    private Integer quantityOnHand;
    private Date expirationDate;
    private Integer warehouseShelfId;
    
    // Constructors
    public UpdateInventoryItemDTO() {}
    
    public UpdateInventoryItemDTO(Integer quantityOnHand, 
                                  Date expirationDate, 
                                  Integer warehouseShelfId) {
        this.quantityOnHand = quantityOnHand;
        this.expirationDate = expirationDate;
        this.warehouseShelfId = warehouseShelfId;
    }
    
    // Getters & Setters
    public Integer getQuantityOnHand() {
        return quantityOnHand;
    }
    
    public void setQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }
    
    public Date getExpirationDate() {
        return expirationDate;
    }
    
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
    
    public Integer getWarehouseShelfId() {
        return warehouseShelfId;
    }
    
    public void setWarehouseShelfId(Integer warehouseShelfId) {
        this.warehouseShelfId = warehouseShelfId;
    }
}
