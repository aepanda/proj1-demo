package com.skillstorm.proj1_demo.dtos;

import java.time.ZonedDateTime;

/**
 * DTO for responding with transfer details after successful initiation.
 * 
 * This DTO contains the created transfer record information including:
 * - Transfer ID for tracking
 * - Status (should be 'PENDING' initially)
 * - Product details
 * - Warehouse names and IDs
 * - Timestamps
 * 
 * Example response:
 * {
 *     "transferId": 10,
 *     "productId": 5,
 *     "productName": "GPU Memory Module",
 *     "quantity": 50,
 *     "sourceWarehouseId": 1,
 *     "sourceWarehouseName": "Main Hub",
 *     "destinationWarehouseId": 2,
 *     "destinationWarehouseName": "Regional Center",
 *     "status": "PENDING",
 *     "createdAt": "2025-11-17T14:30:00Z"
 * }
 */
public class TransferInventoryResponseDTO {
    
    private int transferId;
    private int productId;
    private String productName;
    private int quantity;
    private int sourceWarehouseId;
    private String sourceWarehouseName;
    private int destinationWarehouseId;
    private String destinationWarehouseName;
    private String status;
    private ZonedDateTime createdAt;

    public TransferInventoryResponseDTO() {
    }

    public TransferInventoryResponseDTO(int transferId, int productId, String productName,
            int quantity, int sourceWarehouseId, String sourceWarehouseName,
            int destinationWarehouseId, String destinationWarehouseName,
            String status, ZonedDateTime createdAt) {
        this.transferId = transferId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.sourceWarehouseId = sourceWarehouseId;
        this.sourceWarehouseName = sourceWarehouseName;
        this.destinationWarehouseId = destinationWarehouseId;
        this.destinationWarehouseName = destinationWarehouseName;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSourceWarehouseId() {
        return sourceWarehouseId;
    }

    public void setSourceWarehouseId(int sourceWarehouseId) {
        this.sourceWarehouseId = sourceWarehouseId;
    }

    public String getSourceWarehouseName() {
        return sourceWarehouseName;
    }

    public void setSourceWarehouseName(String sourceWarehouseName) {
        this.sourceWarehouseName = sourceWarehouseName;
    }

    public int getDestinationWarehouseId() {
        return destinationWarehouseId;
    }

    public void setDestinationWarehouseId(int destinationWarehouseId) {
        this.destinationWarehouseId = destinationWarehouseId;
    }

    public String getDestinationWarehouseName() {
        return destinationWarehouseName;
    }

    public void setDestinationWarehouseName(String destinationWarehouseName) {
        this.destinationWarehouseName = destinationWarehouseName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "TransferInventoryResponseDTO{" +
                "transferId=" + transferId +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", sourceWarehouseId=" + sourceWarehouseId +
                ", sourceWarehouseName='" + sourceWarehouseName + '\'' +
                ", destinationWarehouseId=" + destinationWarehouseId +
                ", destinationWarehouseName='" + destinationWarehouseName + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
