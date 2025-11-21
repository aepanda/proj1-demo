package com.skillstorm.proj1_demo.models;

import java.time.ZonedDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    @Column
    private String sku;

    @Column
    private String description;

    @Column
    private boolean isActive = true;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(targetEntity = Inventory.class, mappedBy = "product")
    @JsonIgnore
    private Set<Inventory> inventories;

    @OneToMany(targetEntity = InventoryTransaction.class, mappedBy = "product")
    @JsonIgnore
    private Set<InventoryTransaction> inventoryTransactions;

    @OneToMany(targetEntity = InventoryTransfer.class, mappedBy = "product")
    @JsonIgnore
    private Set<InventoryTransfer> inventoryTransfers;


    public Product() {
    }

    public Product(int id, String name, String sku, String description, 
        boolean isActive, Category category, 
        Set<Inventory> inventories, Set<InventoryTransaction> inventoryTransactions,
        Set<InventoryTransfer> inventoryTransfers) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.isActive = isActive;
        this.category = category;
        this.inventories = inventories;
        this.inventoryTransactions = inventoryTransactions;
        this.inventoryTransfers = inventoryTransfers;
    }

    public Product(String name, String sku, String description, 
        boolean isActive, Category category, 
        Set<Inventory> inventories, Set<InventoryTransaction> inventoryTransactions,
        Set<InventoryTransfer> inventoryTransfers) {
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.isActive = isActive;
        this.category = category;
        this.inventoryTransactions = inventoryTransactions;
        this.inventories = inventories;
        this.inventoryTransfers = inventoryTransfers;
    }

    public int getId() {
        return id;
    }   

    public String getName() {
        return name;
    }

    public String getSku() {
        return sku;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return isActive;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Category getCategory() {
        return category;
    }

    public Set<Inventory> getInventories() {
        return inventories;
    }

    public Set<InventoryTransaction> getInventoryTransactions() {
        return inventoryTransactions;
    }

    public Set<InventoryTransfer> getInventoryTransfers() {
        return inventoryTransfers;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setInventories(Set<Inventory> inventories) {
        this.inventories = inventories;
    }

    public void setInventoryTransactions(Set<InventoryTransaction> inventoryTransactions) {
        this.inventoryTransactions = inventoryTransactions;
    }

    public void setInventoryTransfers(Set<InventoryTransfer> inventoryTransfers) {
        this.inventoryTransfers = inventoryTransfers;
    }


    @Override
    public String toString() {
        return "Product [id=" + id + ", name=" + name + ", sku=" + sku
                + ", description=" + description + ", isActive="
                + isActive + ", category=" + category + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((sku == null) ? 0 : sku.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((inventories == null) ? 0 : inventories.hashCode());
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
        Product other = (Product) obj;
        if (id != other.id)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (sku == null) {
            if (other.sku != null)
                return false;
        } else if (!sku.equals(other.sku))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (category == null) {
            if (other.category != null)
                return false;
        } else if (!category.equals(other.category))
            return false;
        if (inventories == null) {
            if (other.inventories != null)
                return false;
        } else if (!inventories.equals(other.inventories))
            return false;
        return true;
    }


}
