package com.skillstorm.proj1_demo.dtos;

import java.util.Date;

/**
 * Data Transfer Object for adding inventory items to a warehouse.
 * 
 * Allows admins to add items to a warehouse, specifying:
 * - Item details (name, SKU, description, category)
 * - Quantity to add
 * - Storage location (warehouse and optional shelf/location code)
 * - Optional expiration date
 */
public class AddInventoryItemDTO {

    // Product Details
    private String productSku;              // Required: Unique identifier for product
    private String productName;             // Required when creating new product
    private String productDescription;      // Optional: Product description
    private Integer categoryId;             // Optional: Category for the product

    // Inventory Details
    private int quantity;                   // Required: Quantity to add (must be > 0)
    private Date expirationDate;            // Optional: Expiration date for batch

    // Warehouse Location
    private int warehouseId;                // Required: Warehouse where item will be stored
    private String warehouseShelfCode;      // Optional: Specific shelf/location code


    // Constructors
    public AddInventoryItemDTO() {
    }

    public AddInventoryItemDTO(String productSku, String productName, 
            String productDescription, Integer categoryId, int quantity, 
            Date expirationDate, int warehouseId, String warehouseShelfCode) {
        this.productSku = productSku;
        this.productName = productName;
        this.productDescription = productDescription;
        this.categoryId = categoryId;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.warehouseId = warehouseId;
        this.warehouseShelfCode = warehouseShelfCode;
    }


    // Getters and Setters
    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseShelfCode() {
        return warehouseShelfCode;
    }

    public void setWarehouseShelfCode(String warehouseShelfCode) {
        this.warehouseShelfCode = warehouseShelfCode;
    }


    @Override
    public String toString() {
        return "AddInventoryItemDTO [productSku=" + productSku 
            + ", productName=" + productName 
            + ", productDescription=" + productDescription 
            + ", categoryId=" + categoryId 
            + ", quantity=" + quantity 
            + ", expirationDate=" + expirationDate 
            + ", warehouseId=" + warehouseId 
            + ", warehouseShelfCode=" + warehouseShelfCode + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((productSku == null) ? 0 : productSku.hashCode());
        result = prime * result + ((productName == null) ? 0 : productName.hashCode());
        result = prime * result + quantity;
        result = prime * result + ((expirationDate == null) ? 0 : expirationDate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AddInventoryItemDTO other = (AddInventoryItemDTO) obj;
        if (productSku == null) {
            if (other.productSku != null)
                return false;
        } else if (!productSku.equals(other.productSku))
            return false;
        if (productName == null) {
            if (other.productName != null)
                return false;
        } else if (!productName.equals(other.productName))
            return false;
        if (quantity != other.quantity)
            return false;
        if (expirationDate == null) {
            if (other.expirationDate != null)
                return false;
        } else if (!expirationDate.equals(other.expirationDate))
            return false;
        return true;
    }

}
