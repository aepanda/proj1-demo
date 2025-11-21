package com.skillstorm.proj1_demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.skillstorm.proj1_demo.models.InventoryTransfer;

/**
 * Repository interface for InventoryTransfer entity.
 * Provides database access and query operations for transfer records.
 */
@Repository
public interface InventoryTransferRepository extends JpaRepository<InventoryTransfer, Integer> {

    /**
     * Finds all transfers involving a specific warehouse as the source.
     * 
     * @param sourceWarehouseId The source warehouse ID
     * @return List of transfer records from the warehouse
     */
    public List<InventoryTransfer> findBySourceWarehouseId(int sourceWarehouseId);

    /**
     * Finds all transfers involving a specific warehouse as the destination.
     * 
     * @param destinationWarehouseId The destination warehouse ID
     * @return List of transfer records to the warehouse
     */
    public List<InventoryTransfer> findByDestinationWarehouseId(int destinationWarehouseId);

    /**
     * Finds all transfers for a specific product.
     * 
     * @param productId The product ID
     * @return List of transfer records for the product
     */
    public List<InventoryTransfer> findByProductId(int productId);

    /**
     * Finds all transfers with a specific status.
     * 
     * @param status The transfer status (PENDING, IN_TRANSIT, COMPLETED, CANCELLED)
     * @return List of transfer records with the given status
     */
    public List<InventoryTransfer> findByStatus(String status);

    /**
     * Finds all pending transfers (status = 'PENDING').
     * 
     * @return List of pending transfer records
     */
    @Query("SELECT it FROM InventoryTransfer it WHERE it.status = 'PENDING'")
    public List<InventoryTransfer> findAllPending();

    /**
     * Finds all in-transit transfers (status = 'IN_TRANSIT').
     * 
     * @return List of in-transit transfer records
     */
    @Query("SELECT it FROM InventoryTransfer it WHERE it.status = 'IN_TRANSIT'")
    public List<InventoryTransfer> findAllInTransit();

    /**
     * Finds transfers between two specific warehouses.
     * 
     * @param sourceWarehouseId The source warehouse ID
     * @param destinationWarehouseId The destination warehouse ID
     * @return List of transfer records between the two warehouses
     */
    @Query("SELECT it FROM InventoryTransfer it WHERE it.sourceWarehouse.id = :sourceWarehouseId " +
           "AND it.destinationWarehouse.id = :destinationWarehouseId")
    public List<InventoryTransfer> findTransfersBetweenWarehouses(
        @Param("sourceWarehouseId") int sourceWarehouseId,
        @Param("destinationWarehouseId") int destinationWarehouseId
    );

    /**
     * Counts transfers for a specific product between two warehouses.
     * 
     * @param productId The product ID
     * @param sourceWarehouseId The source warehouse ID
     * @param destinationWarehouseId The destination warehouse ID
     * @return Number of transfer records matching the criteria
     */
    @Query("SELECT COUNT(it) FROM InventoryTransfer it WHERE it.product.id = :productId " +
           "AND it.sourceWarehouse.id = :sourceWarehouseId " +
           "AND it.destinationWarehouse.id = :destinationWarehouseId")
    public long countTransfersForProductBetweenWarehouses(
        @Param("productId") int productId,
        @Param("sourceWarehouseId") int sourceWarehouseId,
        @Param("destinationWarehouseId") int destinationWarehouseId
    );
}
