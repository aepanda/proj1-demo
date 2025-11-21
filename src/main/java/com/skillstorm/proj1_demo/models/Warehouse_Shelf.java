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
@Table(name = "warehouse_shelf")
public class Warehouse_Shelf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String code;

    @Column
    private String description;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @OneToMany(targetEntity = Inventory.class, mappedBy = "warehouseShelf")
    @JsonIgnore
    private Set<Inventory> inventories;

    @OneToMany(targetEntity = InventoryTransaction.class, mappedBy = "fromShelf")
    @JsonIgnore
    private Set<InventoryTransaction> inventoryTransactionsFromShelf;

    @OneToMany(targetEntity = InventoryTransaction.class, mappedBy = "toShelf")
    @JsonIgnore
    private Set<InventoryTransaction> inventoryTransactionsToShelf;


    public Warehouse_Shelf() {
    }

    public Warehouse_Shelf(int id, String code, String description,
        Warehouse warehouse, Set<Inventory> inventories, 
        Set<InventoryTransaction> inventoryTransactionsFromShelf,
        Set<InventoryTransaction> inventoryTransactionsToShelf) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.warehouse = warehouse;
        this.inventories = inventories;
        this.inventoryTransactionsFromShelf = inventoryTransactionsFromShelf;
        this.inventoryTransactionsToShelf = inventoryTransactionsToShelf;
    }

    public Warehouse_Shelf(String code, String description,
        Warehouse warehouse, Set<Inventory> inventories, 
        Set<InventoryTransaction> inventoryTransactionsFromShelf,
        Set<InventoryTransaction> inventoryTransactionsToShelf) {
        this.code = code;
        this.description = description;
        this.warehouse = warehouse;
        this.inventories = inventories;
        this.inventoryTransactionsFromShelf = inventoryTransactionsFromShelf;
        this.inventoryTransactionsToShelf = inventoryTransactionsToShelf;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public Set<Inventory> getInventories() {
        return inventories;
    }

    public Set<InventoryTransaction> getInventoryTransactionsFromShelf() {
        return inventoryTransactionsFromShelf;
    }

    public Set<InventoryTransaction> getInventoryTransactionsToShelf() {
        return inventoryTransactionsToShelf;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public void setInventories(Set<Inventory> inventories) {
        this.inventories = inventories;
    }

    public void setInventoryTransactionsFromShelf(Set<InventoryTransaction> inventoryTransactionsFromShelf) {
        this.inventoryTransactionsFromShelf = inventoryTransactionsFromShelf;
    }

    public void setInventoryTransactionsToShelf(Set<InventoryTransaction> inventoryTransactionsToShelf) {
        this.inventoryTransactionsToShelf = inventoryTransactionsToShelf;
    }

    @Override
    public String toString() {
        return "Warehouse_Shelf [id=" + id + ", code=" + code 
        + ", description=" + description + ", warehouse="
                + warehouse + "]";
    }
    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((warehouse == null) ? 0 : warehouse.hashCode());
        result = prime * result + ((inventories == null) ? 0 : inventories.hashCode());
        result = prime * result
                + ((inventoryTransactionsFromShelf == null) ? 0 : inventoryTransactionsFromShelf.hashCode());
        result = prime * result
                + ((inventoryTransactionsToShelf == null) ? 0 : inventoryTransactionsToShelf.hashCode());
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
        Warehouse_Shelf other = (Warehouse_Shelf) obj;
        if (id != other.id)
            return false;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (warehouse == null) {
            if (other.warehouse != null)
                return false;
        } else if (!warehouse.equals(other.warehouse))
            return false;
        if (inventories == null) {
            if (other.inventories != null)
                return false;
        } else if (!inventories.equals(other.inventories))
            return false;
        if (inventoryTransactionsFromShelf == null) {
            if (other.inventoryTransactionsFromShelf != null)
                return false;
        } else if (!inventoryTransactionsFromShelf.equals(other.inventoryTransactionsFromShelf))
            return false;
        if (inventoryTransactionsToShelf == null) {
            if (other.inventoryTransactionsToShelf != null)
                return false;
        } else if (!inventoryTransactionsToShelf.equals(other.inventoryTransactionsToShelf))
            return false;
        return true;
    }

    
}
