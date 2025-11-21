package com.skillstorm.proj1_demo.models;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Represents a serialized/tracked inventory unit.
 * 
 * Used for high-value items that require individual tracking with serial numbers.
 * Each unit can be tracked through its lifecycle:
 * - AVAILABLE: In stock and ready for use
 * - RESERVED: Allocated to a customer/order but not yet shipped
 * - SHIPPED: Shipped to customer
 * 
 * Each InventoryUnit belongs to a parent Inventory record (batch).
 * 
 * @see Inventory for batch-level inventory management
 */
@Entity
@Table(name = "inventory_unit")
public class InventoryUnit {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Unique serial number for this individual unit.
     * Serial numbers are stored as TEXT to support alphanumeric formats
     * (e.g., "SN-2025-00001", "ABC123XYZ789").
     */
    @Column(name = "serial_number")
    private String serialNumber;

    /**
     * Current status of this unit: AVAILABLE, RESERVED, or SHIPPED
     */
    @Column
    private String status = "available";

    /**
     * When this unit was created.
     */
    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt;

    /**
     * When this unit was last updated.
     */
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime updatedAt;
    
    /**
     * The parent Inventory batch this unit belongs to.
     */
    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    public InventoryUnit() {
    }

    public InventoryUnit(int id, String serialNumber, String status, Inventory inventory) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.status = status;
        this.inventory = inventory;
    }

    public InventoryUnit(String serialNumber, String status, Inventory inventory) {
        this.serialNumber = serialNumber;
        this.status = status;
        this.inventory = inventory;
    }

    public int getId() {
        return id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getStatus() {
        return status;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public String toString() {
        return "InventoryUnit [id=" + id + ", serialNumber=" + serialNumber
                + ", status=" + status + ", inventory=" + inventory + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((serialNumber == null) ? 0 : serialNumber.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((inventory == null) ? 0 : inventory.hashCode());
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
        InventoryUnit other = (InventoryUnit) obj;
        if (id != other.id)
            return false;
        if (serialNumber == null) {
            if (other.serialNumber != null)
                return false;
        } else if (!serialNumber.equals(other.serialNumber))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (inventory == null) {
            if (other.inventory != null)
                return false;
        } else if (!inventory.equals(other.inventory))
            return false;
        return true;
    }

}
