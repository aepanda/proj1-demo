package com.skillstorm.proj1_demo.models;

import java.time.ZonedDateTime;
import java.util.Date;
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
@Table(name = "inventory")
public class Inventory {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private int quantityOnHand;

    @Column
    private Date expirationDate;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "warehouse_shelf_id")
    private Warehouse_Shelf warehouseShelf;

    @OneToMany(targetEntity = InventoryUnit.class, mappedBy = "inventory")
    @JsonIgnore
    private Set<InventoryUnit> inventoryUnits;

    @OneToMany(targetEntity = Alert.class, mappedBy = "inventory")
    @JsonIgnore
    private Set<Alert> alerts;

    public Inventory() {
    }

    public Inventory(int id, int quantityOnHand, Date expirationDate, Product product, Warehouse warehouse,
            Warehouse_Shelf warehouseShelf, Set<InventoryUnit> inventoryUnits,
            Set<Alert> alerts) {
        this.id = id;
        this.quantityOnHand = quantityOnHand;
        this.expirationDate = expirationDate;
        this.product = product;
        this.warehouse = warehouse;
        this.warehouseShelf = warehouseShelf;
        this.inventoryUnits = inventoryUnits;
        this.alerts = alerts;
    }

    public Inventory(int quantityOnHand, Date expirationDate, Product product, Warehouse warehouse,
            Warehouse_Shelf warehouseShelf, Set<InventoryUnit> inventoryUnits,
             Set<Alert> alerts) {
        this.quantityOnHand = quantityOnHand;
        this.expirationDate = expirationDate;
        this.product = product;
        this.warehouse = warehouse;
        this.warehouseShelf = warehouseShelf;
        this.inventoryUnits = inventoryUnits;
        this.alerts = alerts;
    }

    public int getId() {
        return id;
    }

    public int getQuantityOnHand() {
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

    public Product getProduct() {
        return product;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public Warehouse_Shelf getWarehouseShelf() {
        return warehouseShelf;
    }

    public Set<InventoryUnit> getInventoryUnits() {
        return inventoryUnits;
    }

    public Set<Alert> getAlerts() {
        return alerts;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQuantityOnHand(int quantityOnHand) {
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

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public void setWarehouseShelf(Warehouse_Shelf warehouseShelf) {
        this.warehouseShelf = warehouseShelf;
    }

    public void setInventoryUnits(Set<InventoryUnit> inventoryUnits) {
        this.inventoryUnits = inventoryUnits;
    }

    public void setAlerts(Set<Alert> alerts) {
        this.alerts = alerts;
    }

    @Override
    public String toString() {
        return "Inventory [id=" + id + ", quantityOnHand=" + quantityOnHand 
        + ", expirationDate=" + expirationDate
                + ", product=" + product + ", warehouse=" + warehouse
                + ", warehouseShelf=" + warehouseShelf + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + quantityOnHand;
        result = prime * result + ((product == null) ? 0 : product.hashCode());
        result = prime * result + ((warehouse == null) ? 0 : warehouse.hashCode());
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
        Inventory other = (Inventory) obj;
        if (id != other.id)
            return false;
        if (quantityOnHand != other.quantityOnHand)
            return false;
        if (product == null) {
            if (other.product != null)
                return false;
        } else if (!product.equals(other.product))
            return false;
        if (warehouse == null) {
            if (other.warehouse != null)
                return false;
        } else if (!warehouse.equals(other.warehouse))
            return false;
        return true;
    }

    
}
