package com.skillstorm.proj1_demo.dtos;

import java.time.ZonedDateTime;
import java.util.Date;

/**
 * DTO for viewing inventory items with comprehensive product, category, warehouse, and shelf details.
 * 
 * Used in list/search/filter responses to display inventory items to admins.
 * Includes all relevant information for presenting inventory data in UI.
 * 
 * Data Mapping from Inventory entity:
 * - inventoryId: Inventory.id
 * - quantityOnHand: Inventory.quantity_on_hand
 * - expirationDate: Inventory.expiration_date (can be null)
 * - createdAt: Inventory.created_at
 * - updatedAt: Inventory.updated_at
 * - productId, productName, productSku, productDescription: From Inventory.product
 * - categoryId, categoryName: From Inventory.product.category (optional)
 * - warehouseId, warehouseName, warehouseLocation: From Inventory.warehouse
 * - warehouseShelfId, warehouseShelfCode: From Inventory.warehouse_shelf (optional)
 */
public class ViewInventoryItemDTO {

    // Inventory Details
    private Integer inventoryId;
    private Integer quantityOnHand;
    private Date expirationDate;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    // Product Details
    private Integer productId;
    private String productName;
    private String productSku;
    private String productDescription;

    // Category Details (optional)
    private Integer categoryId;
    private String categoryName;

    // Warehouse Details
    private Integer warehouseId;
    private String warehouseName;
    private String warehouseLocation;

    // Warehouse Shelf Details (optional)
    private Integer warehouseShelfId;
    private String warehouseShelfCode;

    // Constructors
    public ViewInventoryItemDTO() {
    }

    public ViewInventoryItemDTO(Integer inventoryId, Integer quantityOnHand, Date expirationDate,
            ZonedDateTime createdAt, ZonedDateTime updatedAt, Integer productId, String productName,
            String productSku, String productDescription, Integer categoryId, String categoryName,
            Integer warehouseId, String warehouseName, String warehouseLocation,
            Integer warehouseShelfId, String warehouseShelfCode) {
        this.inventoryId = inventoryId;
        this.quantityOnHand = quantityOnHand;
        this.expirationDate = expirationDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.productId = productId;
        this.productName = productName;
        this.productSku = productSku;
        this.productDescription = productDescription;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.warehouseLocation = warehouseLocation;
        this.warehouseShelfId = warehouseShelfId;
        this.warehouseShelfCode = warehouseShelfCode;
    }

    // Getters
    public Integer getInventoryId() {
        return inventoryId;
    }

    public Integer getQuantityOnHand() {
        return quantityOnHand;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductSku() {
        return productSku;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public String getWarehouseLocation() {
        return warehouseLocation;
    }

    public Integer getWarehouseShelfId() {
        return warehouseShelfId;
    }

    public String getWarehouseShelfCode() {
        return warehouseShelfCode;
    }

    // Setters
    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public void setQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public void setWarehouseLocation(String warehouseLocation) {
        this.warehouseLocation = warehouseLocation;
    }

    public void setWarehouseShelfId(Integer warehouseShelfId) {
        this.warehouseShelfId = warehouseShelfId;
    }

    public void setWarehouseShelfCode(String warehouseShelfCode) {
        this.warehouseShelfCode = warehouseShelfCode;
    }

    @Override
    public String toString() {
        return "ViewInventoryItemDTO [inventoryId=" + inventoryId + ", quantityOnHand="
                + quantityOnHand + ", expirationDate=" + expirationDate + ", productName="
                + productName + ", productSku=" + productSku + ", warehouseName=" + warehouseName
                + ", warehouseShelfCode=" + warehouseShelfCode + ", categoryName=" + categoryName
                + "]";
    }
}
