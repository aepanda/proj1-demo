package com.skillstorm.proj1_demo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillstorm.proj1_demo.models.Warehouse_Shelf;

/**
 * Repository interface for Warehouse_Shelf entity.
 * Provides database access and query operations for warehouse shelves/locations.
 */
@Repository
public interface Warehouse_ShelfRepository extends JpaRepository<Warehouse_Shelf, Integer> {

    /**
     * Finds a warehouse shelf by its code within a specific warehouse.
     * The combination of warehouse_id and code is unique.
     * 
     * @param warehouseId The warehouse ID
     * @param code The shelf code
     * @return Optional containing the shelf if found
     */
    public Optional<Warehouse_Shelf> findByWarehouseIdAndCode(int warehouseId, String code);

    /**
     * Finds all shelves in a warehouse.
     * 
     * @param warehouseId The warehouse ID
     * @return List of shelves in the warehouse
     */
    public List<Warehouse_Shelf> findByWarehouseId(int warehouseId);

    /**
     * Checks if a shelf exists with given code in a warehouse.
     * 
     * @param warehouseId The warehouse ID
     * @param code The shelf code
     * @return true if shelf exists, false otherwise
     */
    public boolean existsByWarehouseIdAndCode(int warehouseId, String code);
}
