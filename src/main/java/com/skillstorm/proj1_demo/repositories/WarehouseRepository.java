package com.skillstorm.proj1_demo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.skillstorm.proj1_demo.models.Warehouse;

/**
 * Repository for Warehouse entity.
 * Provides database operations for warehouse records.
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Integer> {

    /**
     * Check if a warehouse with the given name exists.
     * 
     * @param name The warehouse name to check
     * @return true if warehouse exists, false otherwise
     */
    public boolean existsByName(String name);


     /**
     * Find a warehouse by its name.
     * 
     * @param name The warehouse name to search for
     * @return Optional containing the warehouse if found
     */
    public Optional<Warehouse> findByName(String name);


    /**
     * Find a warehouse by its location.
     * 
     * @param location The location to search for
     * @return Optional containing the warehouse if found
     */
    public Optional<Warehouse> findByLocation(String location);


    /**
     * Find all active warehouses.
     * 
     * @return List of active warehouses
     */
    public List<Warehouse> findByIsActive(boolean isActive);
    

    // Query to get the sum of quantities for a specific warehouse
    @Query("SELECT SUM(i.quantityOnHand) FROM Inventory i WHERE i.warehouse.id = :warehouseId")
    public Integer getTotalInventoryQuantity(@Param("warehouseId") int warehouseId);

    // Query to count inventory items in a warehouse
    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.warehouse.id = :warehouseId")
    public Long countInventoryItemsByWarehouse(@Param("warehouseId") int warehouseId);


    /**
     * Check if a warehouse with the given name exists, excluding a specific warehouse.
     * Used during updates to validate that the new name doesn't conflict with existing names.
     * 
     * Example Use Case:
     * - Warehouse 1 wants to change name from "W-A" to "W-B"
     * - W-B already exists for warehouse 3
     * - Query: existsByNameExcludingId("W-B", 1) → returns true (conflict)
     * - Reject update
     * 
     * @param name The warehouse name to check
     * @param warehouseId The warehouse ID to exclude from the check
     * @return true if name exists (excluding the specified warehouse), false otherwise
     */
    @Query("SELECT COUNT(w) > 0 FROM Warehouse w WHERE w.name = :name AND w.id != :warehouseId")
    public boolean existsByNameExcludingId(@Param("name") String name, @Param("warehouseId") int warehouseId);



}
