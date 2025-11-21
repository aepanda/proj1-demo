package com.skillstorm.proj1_demo.dtos;

/**
 * DTO for initiating an inventory transfer between warehouses.
 * 
 * This DTO is used when a client wants to transfer items from one warehouse to another.
 * The service will validate:
 * - Source warehouse has enough inventory
 * - Destination warehouse has enough capacity
 * - Both warehouses are active
 * - Product exists
 * 
 * Example usage:
 * {
 *     "productId": 5,
 *     "quantity": 50,
 *     "sourceWarehouseId": 1,
 *     "destinationWarehouseId": 2
 * }
 */
public class TransferInventoryRequestDTO {
    
    private int productId;
    private int quantity;
    private int sourceWarehouseId;
    private int destinationWarehouseId;

    public TransferInventoryRequestDTO() {
    }

    public TransferInventoryRequestDTO(int productId, int quantity, 
            int sourceWarehouseId, int destinationWarehouseId) {
        this.productId = productId;
        this.quantity = quantity;
        this.sourceWarehouseId = sourceWarehouseId;
        this.destinationWarehouseId = destinationWarehouseId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
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

    public int getDestinationWarehouseId() {
        return destinationWarehouseId;
    }

    public void setDestinationWarehouseId(int destinationWarehouseId) {
        this.destinationWarehouseId = destinationWarehouseId;
    }

    @Override
    public String toString() {
        return "TransferInventoryRequestDTO{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                ", sourceWarehouseId=" + sourceWarehouseId +
                ", destinationWarehouseId=" + destinationWarehouseId +
                '}';
    }
}
