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
 * Represents a WORKFLOW-BASED transfer request between warehouses.
 * 
 * This is the REQUEST/APPROVAL WORKFLOW for warehouse-to-warehouse stock transfers.
 * Use when you need to:
 * - Create a transfer request that requires approval
 * - Track multi-step workflow: PENDING → IN_TRANSIT → COMPLETED
 * - Transfer complete batches between warehouses (no shelf-level detail)
 * - Manage requested_by, approved_by, completion timestamps
 * 
 * Example: Request to move 100 units of Product X from Warehouse A to Warehouse C
 * → Creates InventoryTransfer with status PENDING, waiting for approval
 * → Once approved, status moves to IN_TRANSIT
 * → Once received, status becomes COMPLETED
 * 
 * Status Values:
 * - PENDING: Awaiting approval
 * - IN_TRANSIT: Transfer approved and in progress
 * - COMPLETED: Transfer finished and received
 * - CANCELLED: Transfer rejected or cancelled
 * 
 * NOTE: This is WORKFLOW-focused (approval process).
 * For detailed transaction logging at shelf level, see {@link InventoryTransaction}.
 * 
 * @see InventoryTransaction for transactional records with shelf-level detail
 * @see Warehouse
 * @see Product
 */
@Entity
@Table(name = "inventory_transfer")
public class InventoryTransfer {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * The quantity to be transferred.
     */
    @Column
    private int quantity;

    /**
     * Current status of the transfer workflow.
     * Values: PENDING, IN_TRANSIT, COMPLETED, CANCELLED
     */
    @Column
    private String status = "PENDING";

    /**
     * When the transfer was initiated.
     */
    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt;

    /**
     * When the transfer was completed (null until status = COMPLETED).
     */
    @Column(name = "completed_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime completedAt;

    /**
     * The product being transferred.
     */
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * Source warehouse - where the inventory comes from.
     */
    @ManyToOne
    @JoinColumn(name = "source_warehouse_id")
    private Warehouse sourceWarehouse;

    /**
     * Destination warehouse - where the inventory is going.
     */
    @ManyToOne
    @JoinColumn(name = "destination_warehouse_id")
    private Warehouse destinationWarehouse;


    public InventoryTransfer() {
    }


    public InventoryTransfer(int id, int quantity, String status, 
            Product product, Warehouse sourceWarehouse,
            Warehouse destinationWarehouse) {
        this.id = id;
        this.quantity = quantity;
        this.status = status;
        this.product = product;
        this.sourceWarehouse = sourceWarehouse;
        this.destinationWarehouse = destinationWarehouse;
    }


    public InventoryTransfer(int quantity, String status,
            Product product, Warehouse sourceWarehouse,
            Warehouse destinationWarehouse) {
        this.quantity = quantity;
        this.status = status;
        this.product = product;
        this.sourceWarehouse = sourceWarehouse;
        this.destinationWarehouse = destinationWarehouse;
    }


    public int getId() {
        return id;
    }


    public int getQuantity() {
        return quantity;
    }


    public String getStatus() {
        return status;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getCompletedAt() {
        return completedAt;
    }

    public Product getProduct() {
        return product;
    }


    public Warehouse getSourceWarehouse() {
        return sourceWarehouse;
    }


    public Warehouse getDestinationWarehouse() {
        return destinationWarehouse;
    }


    public void setId(int id) {
        this.id = id;
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setCompletedAt(ZonedDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public void setProduct(Product product) {
        this.product = product;
    }


    public void setSourceWarehouse(Warehouse sourceWarehouse) {
        this.sourceWarehouse = sourceWarehouse;
    }


    public void setDestinationWarehouse(Warehouse destinationWarehouse) {
        this.destinationWarehouse = destinationWarehouse;
    }


    @Override
    public String toString() {
        return "InventoryTransfer [id=" + id + ", quantity=" 
            + quantity + ", status=" + status + ", product=" + product
                + ", sourceWarehouse=" + sourceWarehouse 
                + ", destinationWarehouse=" + destinationWarehouse + "]";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + quantity;
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((product == null) ? 0 : product.hashCode());
        result = prime * result + ((sourceWarehouse == null) ? 0 : sourceWarehouse.hashCode());
        result = prime * result + ((destinationWarehouse == null) ? 0 : destinationWarehouse.hashCode());
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
        InventoryTransfer other = (InventoryTransfer) obj;
        if (id != other.id)
            return false;
        if (quantity != other.quantity)
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (product == null) {
            if (other.product != null)
                return false;
        } else if (!product.equals(other.product))
            return false;
        if (sourceWarehouse == null) {
            if (other.sourceWarehouse != null)
                return false;
        } else if (!sourceWarehouse.equals(other.sourceWarehouse))
            return false;
        if (destinationWarehouse == null) {
            if (other.destinationWarehouse != null)
                return false;
        } else if (!destinationWarehouse.equals(other.destinationWarehouse))
            return false;
        return true;
    }


}
