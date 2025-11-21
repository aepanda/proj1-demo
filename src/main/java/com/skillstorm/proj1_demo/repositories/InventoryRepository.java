package com.skillstorm.proj1_demo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.skillstorm.proj1_demo.models.Inventory;

/**
 * Repository interface for Inventory entity.
 * Provides database access and query operations for inventory records.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    /**
     * Finds inventory by warehouse, shelf, product, and expiration date.
     * This follows the unique constraint in the database schema.
     * 
     * @param warehouseId The warehouse ID
     * @param warehouseShelfId The warehouse shelf ID (can be null)
     * @param productId The product ID
     * @param expirationDate The expiration date (can be null)
     * @return Optional containing the inventory record if found
     */
    public Optional<Inventory> findByWarehouseIdAndWarehouseShelfIdAndProductIdAndExpirationDate(
        int warehouseId, 
        Integer warehouseShelfId, 
        int productId, 
        java.util.Date expirationDate
    );

    /**
     * Finds all inventory items in a warehouse.
     * 
     * @param warehouseId The warehouse ID
     * @return List of inventory records in the warehouse
     */
    public List<Inventory> findByWarehouseId(int warehouseId);

    /**
     * Finds all inventory items for a specific product.
     * 
     * @param productId The product ID
     * @return List of inventory records for the product
     */
    public List<Inventory> findByProductId(int productId);

    /**
     * Finds inventory items in a specific warehouse shelf.
     * 
     * @param warehouseShelfId The warehouse shelf ID
     * @return List of inventory records on that shelf
     */
    public List<Inventory> findByWarehouseShelfId(Integer warehouseShelfId);

    /**
     * Searches for inventory items in a warehouse by product name (case-insensitive).
     * Performs partial matching using LIKE operator.
     * 
     * @param warehouseId The warehouse ID to filter by
     * @param productName The product name or partial name to search for
     * @return List of matching inventory records sorted by name and expiration date
     */
    @Query("SELECT i FROM Inventory i " +
           "JOIN i.product p " +
           "WHERE i.warehouse.id = :warehouseId " +
           "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :productName, '%')) " +
           "ORDER BY p.name ASC, i.expirationDate ASC")
    public List<Inventory> searchByWarehouseAndProductName(
        @Param("warehouseId") int warehouseId,
        @Param("productName") String productName
    );

    /**
     * Searches for inventory items in a warehouse by product SKU (case-insensitive).
     * Performs partial matching using LIKE operator.
     * 
     * @param warehouseId The warehouse ID to filter by
     * @param productSku The product SKU or partial SKU to search for
     * @return List of matching inventory records sorted by SKU and expiration date
     */
    @Query("SELECT i FROM Inventory i " +
           "JOIN i.product p " +
           "WHERE i.warehouse.id = :warehouseId " +
           "AND LOWER(p.sku) LIKE LOWER(CONCAT('%', :productSku, '%')) " +
           "ORDER BY p.sku ASC, i.expirationDate ASC")
    public List<Inventory> searchByWarehouseAndProductSku(
        @Param("warehouseId") int warehouseId,
        @Param("productSku") String productSku
    );

    /**
     * Filters inventory items in a warehouse by product category.
     * 
     * @param warehouseId The warehouse ID to filter by
     * @param categoryId The category ID to match
     * @return List of inventory records in the specified category sorted by name
     */
    @Query("SELECT i FROM Inventory i " +
           "JOIN i.product p " +
           "WHERE i.warehouse.id = :warehouseId " +
           "AND p.category.id = :categoryId " +
           "ORDER BY p.name ASC, i.expirationDate ASC")
    public List<Inventory> filterByWarehouseAndCategory(
        @Param("warehouseId") int warehouseId,
        @Param("categoryId") int categoryId
    );

    /**
     * Performs comprehensive search on inventory using multiple optional filters (OR logic).
     * Searches inventory in a warehouse by exact product name, SKU, and/or category.
     * Returns items matching ANY of the provided criteria with exact matching.
     * 
     * @param warehouseId The warehouse ID (required)
     * @param productName Product name for exact match (optional, null to skip)
     * @param productSku Product SKU for exact match (optional, null to skip)
     * @param categoryId Category ID to filter by (optional, null to skip)
     * @return List of matching inventory records sorted by name and expiration date
     */
    @Query("SELECT i FROM Inventory i " +
           "JOIN i.product p " +
           "LEFT JOIN p.category c " +
           "WHERE i.warehouse.id = :warehouseId " +
           "AND ( " +
           "  (:productName IS NULL OR LOWER(p.name) = LOWER(:productName)) " +
           "  OR (:productSku IS NULL OR LOWER(p.sku) = LOWER(:productSku)) " +
           "  OR (:categoryId IS NULL OR c.id = :categoryId) " +
           ") " +
           "ORDER BY p.name ASC, i.expirationDate ASC")
    public List<Inventory> searchInventory(
        @Param("warehouseId") int warehouseId,
        @Param("productName") String productName,
        @Param("productSku") String productSku,
        @Param("categoryId") Integer categoryId
    );

    /**
     * Finds all inventory items in a specific warehouse for a specific product.
     * Used for transfer operations to get all batches of a product.
     * 
     * @param warehouseId The warehouse ID
     * @param productId The product ID
     * @return List of inventory records matching warehouse and product
     */
    @Query("SELECT i FROM Inventory i WHERE i.warehouse.id = :warehouseId AND i.product.id = :productId")
    public List<Inventory> findByWarehouseIdAndProductId(
        @Param("warehouseId") int warehouseId,
        @Param("productId") int productId
    );
}
