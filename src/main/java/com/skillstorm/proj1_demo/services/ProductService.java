package com.skillstorm.proj1_demo.services;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillstorm.proj1_demo.models.ActivityLog;
import com.skillstorm.proj1_demo.models.Category;
import com.skillstorm.proj1_demo.models.Product;
import com.skillstorm.proj1_demo.repositories.ActivityLogRepository;
import com.skillstorm.proj1_demo.repositories.CategoryRepository;
import com.skillstorm.proj1_demo.repositories.ProductRepository;

/**
 * Service class for product management operations.
 * Handles business logic for creating, updating, and managing products.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ActivityLogRepository activityLogRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ActivityLogRepository activityLogRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.activityLogRepository = activityLogRepository;
    }

    /**
     * Gets an existing product by SKU or creates a new one if it doesn't exist.
     * 
     * If product exists: Returns the existing product
     * If product doesn't exist: Creates a new product with provided details
     * 
     * @param sku The product SKU (required)
     * @param name The product name (required when creating new product)
     * @param description The product description (optional)
     * @param categoryId The category ID (optional)
     * @return The existing or newly created Product
     * @throws IllegalArgumentException if validation fails
     * @throws RuntimeException if category not found
     */
    @Transactional
    public Product getOrCreateProduct(String sku, String name, String description, Integer categoryId) {
        
        // Validate SKU
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("Product SKU cannot be null or empty");
        }

        // Try to find existing product by SKU
        Optional<Product> existingProduct = productRepository.findBySku(sku.trim());
        
        if (existingProduct.isPresent()) {
            return existingProduct.get();
        }

        // Create new product
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Product name is required when creating a new product with SKU: " + sku
            );
        }

        Product newProduct = new Product();
        newProduct.setName(name.trim());
        newProduct.setSku(sku.trim());
        newProduct.setDescription(description);
        newProduct.setActive(true);
        newProduct.setCreatedAt(ZonedDateTime.now());

        // Set category if provided and exists
        if (categoryId != null && categoryId > 0) {
            Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException(
                    "Category with ID " + categoryId + " not found"
                ));
            newProduct.setCategory(category);
        }

        Product savedProduct = productRepository.save(newProduct);
        
        // Record activity in audit log
        logActivity("PRODUCT", savedProduct.getId(), "CREATE",
            "Created new product: " + savedProduct.getName() + " (SKU: " + savedProduct.getSku() + ")");
        
        return savedProduct;
    }

    /**
     * Retrieves a product by its ID.
     * 
     * @param id The product ID
     * @return Optional containing the product if found
     */
    public Optional<Product> getProductById(int id) {
        return productRepository.findById(id);
    }

    /**
     * Retrieves a product by its SKU.
     * 
     * @param sku The product SKU
     * @return Optional containing the product if found
     */
    public Optional<Product> getProductBySku(String sku) {
        if (sku == null || sku.trim().isEmpty()) {
            return Optional.empty();
        }
        return productRepository.findBySku(sku.trim());
    }

    /**
     * Updates a product with new information.
     * 
     * @param productId The ID of the product to update
     * @param name The new product name (optional)
     * @param description The new product description (optional)
     * @param isActive The new active status (optional)
     * @param categoryId The new category ID (optional)
     * @return The updated Product entity
     * @throws RuntimeException if product not found or category not found
     */
    @Transactional
    public Product updateProduct(int productId, String name, String description, Boolean isActive, Integer categoryId) {
        
        // Fetch existing product
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException(
                "Product with ID " + productId + " not found"
            ));

        boolean updated = false;

        // Update name if provided
        if (name != null && !name.trim().isEmpty()) {
            product.setName(name.trim());
            updated = true;
        }

        // Update description if provided
        if (description != null) {
            product.setDescription(description);
            updated = true;
        }

        // Update active status if provided
        if (isActive != null) {
            product.setActive(isActive);
            updated = true;
        }

        // Update category if provided
        if (categoryId != null && categoryId > 0) {
            Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException(
                    "Category with ID " + categoryId + " not found"
                ));
            product.setCategory(category);
            updated = true;
        }

        if (!updated) {
            throw new IllegalArgumentException("No fields provided to update");
        }

        // Update timestamp
        product.setUpdatedAt(ZonedDateTime.now());

        // Save changes
        Product updatedProduct = productRepository.save(product);

        // Record activity in audit log
        logActivity("PRODUCT", updatedProduct.getId(), "UPDATE",
            "Updated product: " + updatedProduct.getName() + " (SKU: " + updatedProduct.getSku() + ")");

        return updatedProduct;
    }

    /**
     * Deletes a product by ID.
     * 
     * Deletion Rules:
     * - Product must exist
     * 
     * @param productId The ID of the product to delete
     * @throws RuntimeException if product not found
     */
    @Transactional
    public void deleteProduct(int productId) {
        
        // Fetch existing product
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException(
                "Product with ID " + productId + " not found"
            ));

        String productName = product.getName();
        String productSku = product.getSku();

        // Delete the product
        productRepository.deleteById(productId);

        // Record activity in audit log
        logActivity("PRODUCT", productId, "DELETE",
            "Deleted product: " + productName + " (SKU: " + productSku + ")");
    }

    /**
     * Records an activity log entry for product operations.
     * 
     * This method creates an audit trail entry for CREATE, UPDATE, DELETE operations
     * on products, enabling compliance auditing, historical tracking, and debugging.
     * 
     * Example:
     *   logActivity("PRODUCT", 5, "CREATE", "Created new product: Widget (SKU: WIDGET-001)");
     *   logActivity("PRODUCT", 5, "UPDATE", "Updated product: Widget");
     *   logActivity("PRODUCT", 5, "DELETE", "Deleted product: Widget (SKU: WIDGET-001)");
     * 
     * @param entityType The type of entity (must be "PRODUCT")
     * @param entityId The ID of the product being tracked
     * @param action The action performed (CREATE, UPDATE, or DELETE)
     * @param details Additional details about the action for audit trail
     * @throws IllegalArgumentException if parameters are invalid
     * 
     * Note: Errors in logging do not affect product operations.
     * Logging uses graceful degradation - if activity log save fails,
     * a warning is printed but the product operation succeeds.
     */
    private void logActivity(String entityType, int entityId, String action, String details) {
        try {
            // Get current time for the appropriate action-specific column
            ZonedDateTime now = ZonedDateTime.now();
            
            // Create new ActivityLog entry with action-specific timestamp mapping:
            // - CREATE action: populate created_at
            // - UPDATE action: populate updated_at
            // - DELETE action: populate deleted_at
            ActivityLog activityLog;
            
            if ("CREATE".equalsIgnoreCase(action)) {
                activityLog = new ActivityLog(entityType, action, entityId, now, null, null);
            } else if ("UPDATE".equalsIgnoreCase(action)) {
                activityLog = new ActivityLog(entityType, action, entityId, null, now, null);
            } else if ("DELETE".equalsIgnoreCase(action)) {
                activityLog = new ActivityLog(entityType, action, entityId, null, null, now);
            } else {
                // Fallback for unexpected actions (validation in @PrePersist will catch this)
                activityLog = new ActivityLog(entityType, action, entityId);
            }
            
            // Save to database
            // The @PrePersist validation in ActivityLog will verify:
            // - entityType is valid (WAREHOUSE, INVENTORY, PRODUCT, ALERT)
            // - action is valid (CREATE, UPDATE, DELETE)
            // - entityType and action are normalized to uppercase
            activityLogRepository.save(activityLog);
            
        } catch (Exception e) {
            // Log error but don't fail the product operation
            // In production, replace with proper logging framework (SLF4J, Log4j)
            System.err.println(
                "Warning: Failed to log activity for product ID " + entityId 
                + " action " + action + ": " + e.getMessage()
            );
        }
    }
}
