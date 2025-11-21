package com.skillstorm.proj1_demo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillstorm.proj1_demo.models.Product;

/**
 * Repository interface for Product entity.
 * Provides database access and query operations for products.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    /**
     * Finds a product by its unique SKU.
     * 
     * @param sku The product SKU
     * @return Optional containing the product if found
     */
    public Optional<Product> findBySku(String sku);

    /**
     * Checks if a product exists by SKU.
     * 
     * @param sku The product SKU
     * @return true if product exists, false otherwise
     */
    public boolean existsBySku(String sku);

    /**
     * Finds all active products.
     * 
     * @return List of active products
     */
    public List<Product> findByIsActiveTrue();

    /**
     * Finds all products in a specific category.
     * 
     * @param categoryId The category ID
     * @return List of products in that category
     */
    public List<Product> findByCategoryId(Integer categoryId);
}
