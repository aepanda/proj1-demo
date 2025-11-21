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

@Entity
@Table(name = "warehouse_capacity_snapshot")
public class WarehouseCapacitySnapshot {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "snapshot_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime snapshotAt;

    @Column(name = "capacity_used_units")
    private int capacityUsedUnits;

    @Column(name = "capacity_percent")
    private int capacityPercent;

    @Column(name = "total_items")
    private int totalItems;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    public WarehouseCapacitySnapshot() {
    }

    public WarehouseCapacitySnapshot(int id, ZonedDateTime snapshotAt, int capacityUsedUnits, 
            int capacityPercent,
            int totalItems, Warehouse warehouse) {
        this.id = id;
        this.snapshotAt = snapshotAt;
        this.capacityUsedUnits = capacityUsedUnits;
        this.capacityPercent = capacityPercent;
        this.totalItems = totalItems;
        this.warehouse = warehouse;
    }

    public WarehouseCapacitySnapshot(ZonedDateTime snapshotAt, int capacityUsedUnits, int capacityPercent,
            int totalItems, Warehouse warehouse) {
        this.snapshotAt = snapshotAt;
        this.capacityUsedUnits = capacityUsedUnits;
        this.capacityPercent = capacityPercent;
        this.totalItems = totalItems;
        this.warehouse = warehouse;
    }

    public int getId() {
        return id;
    }

    public ZonedDateTime getSnapshotAt() {
        return snapshotAt;
    }

    public int getCapacityUsedUnits() {
        return capacityUsedUnits;
    }

    public int getCapacityPercent() {
        return capacityPercent;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSnapshotAt(ZonedDateTime snapshotAt) {
        this.snapshotAt = snapshotAt;
    }

    public void setCapacityUsedUnits(int capacityUsedUnits) {
        this.capacityUsedUnits = capacityUsedUnits;
    }

    public void setCapacityPercent(int capacityPercent) {
        this.capacityPercent = capacityPercent;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public String toString() {
        return "WarehouseCapacitySnapshot [id=" + id 
            + ", capacityUsedUnits=" + capacityUsedUnits + ", capacityPercent="
                + capacityPercent + ", totalItems=" 
                    + totalItems + ", warehouse=" + warehouse + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((snapshotAt == null) ? 0 : snapshotAt.hashCode());
        result = prime * result + capacityUsedUnits;
        result = prime * result + capacityPercent;
        result = prime * result + totalItems;
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
        WarehouseCapacitySnapshot other = (WarehouseCapacitySnapshot) obj;
        if (id != other.id)
            return false;
        if (snapshotAt == null) {
            if (other.snapshotAt != null)
                return false;
        } else if (!snapshotAt.equals(other.snapshotAt))
            return false;
        if (capacityUsedUnits != other.capacityUsedUnits)
            return false;
        if (capacityPercent != other.capacityPercent)
            return false;
        if (totalItems != other.totalItems)
            return false;
        if (warehouse == null) {
            if (other.warehouse != null)
                return false;
        } else if (!warehouse.equals(other.warehouse))
            return false;
        return true;
    }

    
}
