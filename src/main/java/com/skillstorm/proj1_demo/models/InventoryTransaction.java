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
 * Represents a detailed transaction log entry for inventory movements.
 * 
 * This is the TRANSACTIONAL RECORD of individual inventory movements.
 * Use when you need to:
 * - Track detailed history of every inventory change
 * - Record shelf-level movements within or between warehouses
 * - Log specific transaction types: INBOUND, OUTBOUND, TRANSFER, ADJUSTMENT
 * - Maintain audit trails with precise location information
 * 
 * Example: Moving 5 units of Product X from Warehouse A, Shelf 1 to Warehouse B, Shelf 3
 * → Creates InventoryTransaction with from_warehouse_id, from_shelf_id, to_warehouse_id, to_shelf_id
 * 
 * Transaction Types:
 * - INBOUND: Receiving stock from supplier
 * - OUTBOUND: Shipping stock to customer
 * - TRANSFER: Moving between warehouses (with shelf tracking)
 * - ADJUSTMENT: Inventory corrections/reconciliation
 * 
 * NOTE: Inventory can be derived from warehouse + shelf + product combination.
 * For workflow-based transfers, see {@link InventoryTransfer}.
 * 
 * @see InventoryTransfer for workflow-based warehouse transfers
 * @see Warehouse
 * @see Product
 */
@Entity
@Table(name = "inventory_transaction")
public class InventoryTransaction {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * The quantity affected by this transaction.
     */
    @Column
    private int quantity;

    /**
     * Type of transaction: INBOUND, OUTBOUND, TRANSFER, or ADJUSTMENT.
     * Determines the business logic applied to this movement.
     */
    @Column(name = "transaction_type")
    private String transactionType;

    /**
     * When this transaction was created.
     */
    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt;

    /**
     * The product involved in this transaction.
     */
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * Source warehouse (null for inbound transactions from suppliers).
     */
    @ManyToOne
    @JoinColumn(name = "from_warehouse_id")
    private Warehouse fromWarehouse;

    /**
     * Destination warehouse (null for outbound transactions to customers).
     */
    @ManyToOne
    @JoinColumn(name = "to_warehouse_id")
    private Warehouse toWarehouse;

    /**
     * Source shelf/location within warehouse (null if warehouse-level transaction).
     */
    @ManyToOne
    @JoinColumn(name = "from_shelf_id")
    private Warehouse_Shelf fromShelf;

    /**
     * Destination shelf/location within warehouse (null if warehouse-level transaction).
     */
    @ManyToOne
    @JoinColumn(name = "to_shelf_id")
    private Warehouse_Shelf toShelf;

    public InventoryTransaction() {
    }

    public InventoryTransaction(int id, int quantity, String transactionType,
             Product product, Warehouse fromWarehouse,
            Warehouse toWarehouse, Warehouse_Shelf fromShelf,
             Warehouse_Shelf toShelf) {
        this.id = id;
        this.quantity = quantity;
        this.transactionType = transactionType;
        this.product = product;
        this.fromWarehouse = fromWarehouse;
        this.toWarehouse = toWarehouse;
        this.fromShelf = fromShelf;
        this.toShelf = toShelf;
    }

    public InventoryTransaction(int quantity, String transactionType,
             Product product, Warehouse fromWarehouse,
            Warehouse toWarehouse, Warehouse_Shelf fromShelf,
             Warehouse_Shelf toShelf) {
        this.quantity = quantity;
        this.transactionType = transactionType;
        this.product = product;
        this.fromWarehouse = fromWarehouse;
        this.toWarehouse = toWarehouse;
        this.fromShelf = fromShelf;
        this.toShelf = toShelf;
    }

    public int getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public Product getProduct() {
        return product;
    }

    public Warehouse getFromWarehouse() {
        return fromWarehouse;
    }

    public Warehouse getToWarehouse() {
        return toWarehouse;
    }

    public Warehouse_Shelf getFromShelf() {
        return fromShelf;
    }

    public Warehouse_Shelf getToShelf() {
        return toShelf;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setFromWarehouse(Warehouse fromWarehouse) {
        this.fromWarehouse = fromWarehouse;
    }

    public void setToWarehouse(Warehouse toWarehouse) {
        this.toWarehouse = toWarehouse;
    }

    public void setFromShelf(Warehouse_Shelf fromShelf) {
        this.fromShelf = fromShelf;
    }

    public void setToShelf(Warehouse_Shelf toShelf) {
        this.toShelf = toShelf;
    }

    @Override
    public String toString() {
        return "InventoryTransaction [id=" + id + ", quantity=" + quantity 
            + ", transactionType=" + transactionType + ", product=" + product
            + ", fromWarehouse=" + fromWarehouse + ", toWarehouse=" + toWarehouse
            + ", fromShelf=" + fromShelf + ", toShelf=" + toShelf + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + quantity;
        result = prime * result + ((transactionType == null) ? 0 : transactionType.hashCode());
        result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
        result = prime * result + ((product == null) ? 0 : product.hashCode());
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
        InventoryTransaction other = (InventoryTransaction) obj;
        if (id != other.id)
            return false;
        if (quantity != other.quantity)
            return false;
        if (transactionType == null) {
            if (other.transactionType != null)
                return false;
        } else if (!transactionType.equals(other.transactionType))
            return false;
        if (product == null) {
            if (other.product != null)
                return false;
        } else if (!product.equals(other.product))
            return false;
        if (fromWarehouse == null) {
            if (other.fromWarehouse != null)
                return false;
        } else if (!fromWarehouse.equals(other.fromWarehouse))
            return false;
        if (toWarehouse == null) {
            if (other.toWarehouse != null)
                return false;
        } else if (!toWarehouse.equals(other.toWarehouse))
            return false;
        if (fromShelf == null) {
            if (other.fromShelf != null)
                return false;
        } else if (!fromShelf.equals(other.fromShelf))
            return false;
        if (toShelf == null) {
            if (other.toShelf != null)
                return false;
        } else if (!toShelf.equals(other.toShelf))
            return false;
        return true;
    }
}
