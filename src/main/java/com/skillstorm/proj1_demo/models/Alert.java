package com.skillstorm.proj1_demo.models;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/**
 * Represents an alert for inventory management events.
 * 
 * Alerts are triggered by:
 * - Warehouse capacity thresholds (near limit, exceeded)
 * - Product obsolescence or loss risk
 * - Other inventory-related anomalies
 * 
 * NOTE: Product information is accessed via the Inventory relationship.
 * Alert must have at least one reference: warehouse_id OR inventory_id
 * 
 * @see Warehouse
 * @see Inventory
 */
@Entity
@Table(name = "alert")
public class Alert {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String type;

    @Column
    private String severity = "info";
    
    @Column
    private boolean isResolved = false;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt;

    @Column(name = "resolved_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime resolvedAt;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    /**
     * Validates that the alert has at least one reference.
     * Throws IllegalArgumentException if both warehouse and inventory are null.
     */
    @PrePersist
    @PreUpdate
    private void validateAtLeastOneReference() {
        if (warehouse == null && inventory == null) {
            throw new IllegalArgumentException(
                "Alert must have at least one reference: warehouse or inventory"
            );
        }
    }

    public Alert() {  
    }

    public Alert(int id, String type, String severity, boolean isResolved,
            Warehouse warehouse, Inventory inventory) {
        this.id = id;
        this.type = type;
        this.severity = severity;
        this.isResolved = isResolved;
        this.warehouse = warehouse;
        this.inventory = inventory;
    }

    public Alert(String type, String severity, boolean isResolved, 
            Warehouse warehouse, Inventory inventory) {
        this.type = type;
        this.severity = severity;
        this.isResolved = isResolved;
        this.warehouse = warehouse;
        this.inventory = inventory;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getSeverity() {
        return severity;
    }

    public boolean isResolved() {
        return isResolved;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getResolvedAt() {
        return resolvedAt;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public void setIsResolved(boolean isResolved) {
        this.isResolved = isResolved;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setResolvedAt(ZonedDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public String toString() {
        return "Alert [id=" + id + ", type=" + type + ", severity=" + severity 
            + ", isResolved=" + isResolved + ", warehouse=" + warehouse 
            + ", inventory=" + inventory + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((severity == null) ? 0 : severity.hashCode());
        result = prime * result + (isResolved ? 1231 : 1237);
        result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
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
        Alert other = (Alert) obj;
        if (id != other.id)
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (severity == null) {
            if (other.severity != null)
                return false;
        } else if (!severity.equals(other.severity))
            return false;
        if (isResolved != other.isResolved)
            return false;
        return true;
    }
}
