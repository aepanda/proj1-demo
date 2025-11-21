package com.skillstorm.proj1_demo.dtos;

import java.util.Date;

/**
 * Response DTO for deletion-check endpoint.
 * Returns inventory details and deletion eligibility.
 */
public class DeletionCheckResponse {
    
    private Integer inventoryId;
    private Integer quantity;
    private Date expirationDate;
    private String warehouseName;
    private String warehouseShelfCode;
    private String productName;
    private String productSku;
    private Boolean canDelete;
    private String reason;  // Null if can delete, error message if cannot
    
    // Constructor
    public DeletionCheckResponse(Integer inventoryId, Integer quantity, Date expirationDate,
                                 String warehouseName, String warehouseShelfCode,
                                 String productName, String productSku,
                                 Boolean canDelete, String reason) {
        this.inventoryId = inventoryId;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.warehouseName = warehouseName;
        this.warehouseShelfCode = warehouseShelfCode;
        this.productName = productName;
        this.productSku = productSku;
        this.canDelete = canDelete;
        this.reason = reason;
    }
    
    // Getters
    public Integer getInventoryId() {
        return inventoryId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public Date getExpirationDate() {
        return expirationDate;
    }
    
    public String getWarehouseName() {
        return warehouseName;
    }
    
    public String getWarehouseShelfCode() {
        return warehouseShelfCode;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public String getProductSku() {
        return productSku;
    }
    
    public Boolean getCanDelete() {
        return canDelete;
    }
    
    public String getReason() {
        return reason;
    }
}
