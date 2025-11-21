package com.skillstorm.proj1_demo.models;

import java.time.ZonedDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "warehouse")
public class Warehouse {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    @Column
    private String location;

    @Column(name = "max_capacity")
    private int capacity;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime updatedAt;

    @OneToMany(targetEntity = Warehouse_Shelf.class, mappedBy = "warehouse")
    @JsonIgnore
    private Set<Warehouse_Shelf> warehouseShelves;

    @OneToMany(targetEntity = Inventory.class, mappedBy = "warehouse")
    @JsonIgnore
    private Set<Inventory> inventories;

    @OneToMany(targetEntity = InventoryTransaction.class, mappedBy = "fromWarehouse")
    @JsonIgnore
    private Set<InventoryTransaction> inventoryTransactionsFromWarehouse;

    @OneToMany(targetEntity = InventoryTransaction.class, mappedBy = "toWarehouse")
    @JsonIgnore
    private Set<InventoryTransaction> inventoryTransactionsToWarehouse;

    @OneToMany(targetEntity = InventoryTransfer.class, mappedBy = "sourceWarehouse")
    @JsonIgnore
    private Set<InventoryTransfer> inventoryTransfersSourceWarehouse;

    @OneToMany(targetEntity = InventoryTransfer.class, mappedBy = "destinationWarehouse")
    @JsonIgnore
    private Set<InventoryTransfer> inventoryTransfersDestinationWarehouse;

    @OneToMany(targetEntity = Alert.class, mappedBy = "warehouse")
    @JsonIgnore
    private Set<Alert> alerts;

    @OneToMany(targetEntity = WarehouseCapacitySnapshot.class, mappedBy = "warehouse")
    @JsonIgnore
    private Set<WarehouseCapacitySnapshot> warehouseCapacitySnapshots;


    public Warehouse() {
    }   

    public Warehouse(int id, String name, String location, 
        int capacity, boolean isActive, Set<Warehouse_Shelf> warehouseShelves, 
        Set<Inventory> inventories, Set<InventoryTransaction> inventoryTransactionsFromWarehouse,
         Set<InventoryTransaction> inventoryTransactionsToWarehouse,
         Set<InventoryTransfer> inventoryTransfersSourceWarehouse,
         Set<InventoryTransfer> inventoryTransfersDestinationWarehouse,
         Set<Alert> alerts, 
         Set<WarehouseCapacitySnapshot> warehouseCapacitySnapshots) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.isActive = isActive;
        this.warehouseShelves = warehouseShelves;
        this.inventories = inventories;
        this.inventoryTransactionsFromWarehouse = inventoryTransactionsFromWarehouse;
        this.inventoryTransactionsToWarehouse = inventoryTransactionsToWarehouse;
        this.inventoryTransfersSourceWarehouse = inventoryTransfersSourceWarehouse;
        this.inventoryTransfersDestinationWarehouse = inventoryTransfersDestinationWarehouse;
        this.alerts = alerts;
        this.warehouseCapacitySnapshots = warehouseCapacitySnapshots;
    }

    public Warehouse(String name, String location, int capacity,
        boolean isActive, Set<Warehouse_Shelf> warehouseShelves,
         Set<Inventory> inventories, Set<InventoryTransaction> inventoryTransactionsFromWarehouse,
          Set<InventoryTransaction> inventoryTransactionsToWarehouse,
          Set<InventoryTransfer> inventoryTransfersSourceWarehouse,
          Set<InventoryTransfer> inventoryTransfersDestinationWarehouse,
          Set<Alert> alerts, 
          Set<WarehouseCapacitySnapshot> warehouseCapacitySnapshots) {
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.isActive = isActive;
        this.warehouseShelves = warehouseShelves;
        this.inventories = inventories;
        this.inventoryTransactionsFromWarehouse = inventoryTransactionsFromWarehouse;
        this.inventoryTransactionsToWarehouse = inventoryTransactionsToWarehouse;
        this.inventoryTransfersSourceWarehouse = inventoryTransfersSourceWarehouse;
        this.inventoryTransfersDestinationWarehouse = inventoryTransfersDestinationWarehouse;
        this.alerts = alerts;
        this.warehouseCapacitySnapshots = warehouseCapacitySnapshots;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public Set<Warehouse_Shelf> getWarehouseShelves() {
        return warehouseShelves;
    }

    public Set<Inventory> getInventories() {
        return inventories;
    }

    public Set<InventoryTransaction> getInventoryTransactionsFromWarehouse() {
        return inventoryTransactionsFromWarehouse;
    }

    public Set<InventoryTransaction> getInventoryTransactionsToWarehouse() {
        return inventoryTransactionsToWarehouse;
    }

    public Set<InventoryTransfer> getInventoryTransfersSourceWarehouse() {
        return inventoryTransfersSourceWarehouse;
    }

    public Set<InventoryTransfer> getInventoryTransfersDestinationWarehouse() {
        return inventoryTransfersDestinationWarehouse;
    }

    public Set<WarehouseCapacitySnapshot> getWarehouseCapacitySnapshots() {
        return warehouseCapacitySnapshots;
    }

    public Set<Alert> getAlerts() {
        return alerts;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setWarehouseShelves(Set<Warehouse_Shelf> warehouseShelves) {
        this.warehouseShelves = warehouseShelves;
    }

    public void setInventories(Set<Inventory> inventories) {
        this.inventories = inventories;
    }

    public void setInventoryTransactionsFromWarehouse(Set<InventoryTransaction> inventoryTransactionsFromWarehouse) {
        this.inventoryTransactionsFromWarehouse = inventoryTransactionsFromWarehouse;
    }

    public void setInventoryTransactionsToWarehouse(Set<InventoryTransaction> inventoryTransactionsToWarehouse) {
        this.inventoryTransactionsToWarehouse = inventoryTransactionsToWarehouse;
    }

    public void setInventoryTransfersSourceWarehouse(Set<InventoryTransfer> inventoryTransfersSourceWarehouse) {
        this.inventoryTransfersSourceWarehouse = inventoryTransfersSourceWarehouse;
    }

    public void setInventoryTransfersDestinationWarehouse(Set<InventoryTransfer> inventoryTransfersDestinationWarehouse) {
        this.inventoryTransfersDestinationWarehouse = inventoryTransfersDestinationWarehouse;
    }

    public void setAlerts(Set<Alert> alerts) {
        this.alerts = alerts;
    }

    public void setWarehouseCapacitySnapshots(Set<WarehouseCapacitySnapshot> warehouseCapacitySnapshots) {
        this.warehouseCapacitySnapshots = warehouseCapacitySnapshots;
    }

    @Override
    public String toString() {
        return "Warehouse [id=" + id + ", name=" + name + ", location=" + location + ", "
            + "capacity=" + capacity + ", isActive=" + isActive + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + capacity;
        result = prime * result + (isActive ? 1231 : 1237);
        result = prime * result + ((warehouseShelves == null) ? 0 : warehouseShelves.hashCode());
        result = prime * result + ((inventories == null) ? 0 : inventories.hashCode());
        result = prime * result
                + ((inventoryTransactionsFromWarehouse == null) ? 0 : inventoryTransactionsFromWarehouse.hashCode());
        result = prime * result
                + ((inventoryTransactionsToWarehouse == null) ? 0 : inventoryTransactionsToWarehouse.hashCode());
        result = prime * result
                + ((inventoryTransfersSourceWarehouse == null) ? 0 : inventoryTransfersSourceWarehouse.hashCode());
        result = prime * result + ((inventoryTransfersDestinationWarehouse == null) ? 0
                : inventoryTransfersDestinationWarehouse.hashCode());
        result = prime * result + ((alerts == null) ? 0 : alerts.hashCode());
        result = prime * result + ((warehouseCapacitySnapshots == null) ? 0 : warehouseCapacitySnapshots.hashCode());
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
        Warehouse other = (Warehouse) obj;
        if (id != other.id)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        if (capacity != other.capacity)
            return false;
        if (isActive != other.isActive)
            return false;
        if (warehouseShelves == null) {
            if (other.warehouseShelves != null)
                return false;
        } else if (!warehouseShelves.equals(other.warehouseShelves))
            return false;
        if (inventories == null) {
            if (other.inventories != null)
                return false;
        } else if (!inventories.equals(other.inventories))
            return false;
        if (inventoryTransactionsFromWarehouse == null) {
            if (other.inventoryTransactionsFromWarehouse != null)
                return false;
        } else if (!inventoryTransactionsFromWarehouse.equals(other.inventoryTransactionsFromWarehouse))
            return false;
        if (inventoryTransactionsToWarehouse == null) {
            if (other.inventoryTransactionsToWarehouse != null)
                return false;
        } else if (!inventoryTransactionsToWarehouse.equals(other.inventoryTransactionsToWarehouse))
            return false;
        if (inventoryTransfersSourceWarehouse == null) {
            if (other.inventoryTransfersSourceWarehouse != null)
                return false;
        } else if (!inventoryTransfersSourceWarehouse.equals(other.inventoryTransfersSourceWarehouse))
            return false;
        if (inventoryTransfersDestinationWarehouse == null) {
            if (other.inventoryTransfersDestinationWarehouse != null)
                return false;
        } else if (!inventoryTransfersDestinationWarehouse.equals(other.inventoryTransfersDestinationWarehouse))
            return false;
        if (alerts == null) {
            if (other.alerts != null)
                return false;
        } else if (!alerts.equals(other.alerts))
            return false;
        if (warehouseCapacitySnapshots == null) {
            if (other.warehouseCapacitySnapshots != null)
                return false;
        } else if (!warehouseCapacitySnapshots.equals(other.warehouseCapacitySnapshots))
            return false;
        return true;
    }

    

        
}
