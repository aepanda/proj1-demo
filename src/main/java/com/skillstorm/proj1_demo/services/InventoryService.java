package com.skillstorm.proj1_demo.services;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillstorm.proj1_demo.dtos.AddInventoryItemDTO;
import com.skillstorm.proj1_demo.dtos.DeleteInventoryItemDTO;
import com.skillstorm.proj1_demo.dtos.DeletionCheckResponse;
import com.skillstorm.proj1_demo.dtos.TransferInventoryRequestDTO;
import com.skillstorm.proj1_demo.dtos.TransferInventoryResponseDTO;
import com.skillstorm.proj1_demo.dtos.UpdateInventoryItemDTO;
import com.skillstorm.proj1_demo.dtos.ViewInventoryItemDTO;
import com.skillstorm.proj1_demo.exceptions.ConflictException;
import com.skillstorm.proj1_demo.exceptions.EntityNotFoundException;
import com.skillstorm.proj1_demo.models.ActivityLog;
import com.skillstorm.proj1_demo.models.Inventory;
import com.skillstorm.proj1_demo.models.InventoryTransfer;
import com.skillstorm.proj1_demo.models.Product;
import com.skillstorm.proj1_demo.models.Warehouse;
import com.skillstorm.proj1_demo.models.Warehouse_Shelf;
import com.skillstorm.proj1_demo.repositories.ActivityLogRepository;
import com.skillstorm.proj1_demo.repositories.InventoryRepository;
import com.skillstorm.proj1_demo.repositories.InventoryTransferRepository;
import com.skillstorm.proj1_demo.repositories.WarehouseRepository;
import com.skillstorm.proj1_demo.repositories.Warehouse_ShelfRepository;


/**
 * Service class for inventory management operations.
 * Handles business logic for adding and managing inventory items in warehouses.
 * 
 * Communication flow:
 * Controller → InventoryService → ProductService, InventoryRepository, WarehouseRepository
 */
@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final Warehouse_ShelfRepository warehouseShelfRepository;
    private final ProductService productService;
    private final ActivityLogRepository activityLogRepository;
    private final InventoryTransferRepository inventoryTransferRepository;

    public InventoryService(
            InventoryRepository inventoryRepository,
            WarehouseRepository warehouseRepository,
            Warehouse_ShelfRepository warehouseShelfRepository,
            ProductService productService,
            ActivityLogRepository activityLogRepository,
            InventoryTransferRepository inventoryTransferRepository) {
        this.inventoryRepository = inventoryRepository;
        this.warehouseRepository = warehouseRepository;
        this.warehouseShelfRepository = warehouseShelfRepository;
        this.productService = productService;
        this.activityLogRepository = activityLogRepository;
        this.inventoryTransferRepository = inventoryTransferRepository;
    }

    /**
     * Adds an inventory item to a warehouse.
     * 
     * This method:
     * 1. Validates input data
     * 2. Verifies warehouse exists and is active
     * 3. Gets or creates the product (if doesn't exist)
     * 4. Gets warehouse shelf (if provided)
     * 5. Checks if inventory record already exists for this combination
     *    - If exists: Updates quantity
     *    - If new: Creates new inventory record
     * 
     * Validation:
     * - Quantity must be > 0
     * - Warehouse must exist and be active
     * - If warehouseShelfCode provided, shelf must exist
     * - Product SKU must not be empty
     * - Product name required when creating new product
     * 
     * @param dto The DTO containing inventory item details
     * @return The created or updated Inventory entity
     * @throws IllegalArgumentException if validation fails
     * @throws RuntimeException if warehouse or shelf not found, or warehouse inactive
     */
    @Transactional
    public Inventory addInventoryItem(AddInventoryItemDTO dto) {
        
        // Validate input
        validateAddInventoryInput(dto);

        // Verify warehouse exists and is active
        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
            .orElseThrow(() -> new RuntimeException(
                "Warehouse with ID " + dto.getWarehouseId() + " not found"
            ));

        if (!warehouse.isActive()) {
            throw new RuntimeException(
                "Cannot add inventory to inactive warehouse: " + warehouse.getName()
            );
        }

        // Get or create product using ProductService
        Product product = productService.getOrCreateProduct(
            dto.getProductSku(),
            dto.getProductName(),
            dto.getProductDescription(),
            dto.getCategoryId()
        );

        // Get warehouse shelf (if provided)
        Warehouse_Shelf warehouseShelf = null;
        if (dto.getWarehouseShelfCode() != null && !dto.getWarehouseShelfCode().isEmpty()) {
            warehouseShelf = warehouseShelfRepository
                .findByWarehouseIdAndCode(dto.getWarehouseId(), dto.getWarehouseShelfCode())
                .orElseThrow(() -> new RuntimeException(
                    "Warehouse shelf with code '" + dto.getWarehouseShelfCode() 
                    + "' not found in warehouse '" + warehouse.getName() + "'"
                ));
        }

        // Check if inventory record already exists for this combination
        Optional<Inventory> existingInventory = inventoryRepository
            .findByWarehouseIdAndWarehouseShelfIdAndProductIdAndExpirationDate(
                dto.getWarehouseId(),
                warehouseShelf != null ? warehouseShelf.getId() : null,
                product.getId(),
                dto.getExpirationDate()
            );

        Inventory inventory;
        if (existingInventory.isPresent()) {
            // Update existing inventory - add to quantity
            inventory = existingInventory.get();
            int newQuantity = inventory.getQuantityOnHand() + dto.getQuantity();
            inventory.setQuantityOnHand(newQuantity);
            inventory.setUpdatedAt(ZonedDateTime.now());
            inventory = inventoryRepository.save(inventory);
            
            // Record activity in audit log
            logActivity("INVENTORY", inventory.getId(), "UPDATE",
                "Added " + dto.getQuantity() + " units of " + product.getName() + 
                " to existing inventory in warehouse " + warehouse.getName());
        } else {
            // Create new inventory record
            inventory = new Inventory();
            inventory.setQuantityOnHand(dto.getQuantity());
            inventory.setExpirationDate(dto.getExpirationDate());
            inventory.setProduct(product);
            inventory.setWarehouse(warehouse);
            inventory.setWarehouseShelf(warehouseShelf);
            inventory.setCreatedAt(ZonedDateTime.now());
            inventory = inventoryRepository.save(inventory);
            
            // Record activity in audit log
            logActivity("INVENTORY", inventory.getId(), "CREATE",
                "Created new inventory: " + dto.getQuantity() + " units of " + product.getName() + 
                " in warehouse " + warehouse.getName() + 
                (warehouseShelf != null ? " at shelf " + warehouseShelf.getCode() : ""));
        }

        return inventory;
    }

    /**
     * Updates an existing inventory item with provided fields.
     * Only non-null fields in the DTO will be updated.
     * 
     * Validation Rules:
     * - Quantity: Must be >= 0
     * - Expiration Date: Must be today or in the future
     * - Warehouse Shelf: Must exist and belong to the same warehouse
     * - UNIQUE constraint: No other inventory record with same warehouse, shelf, product, expiration
     * 
     * @param inventoryId the ID of inventory to update
     * @param updateDTO the update data (fields can be null for no change)
     * @return updated Inventory entity
     * @throws EntityNotFoundException if inventory not found
     * @throws IllegalArgumentException if validation fails
     * @throws ConflictException if UNIQUE constraint would be violated
     */
    @Transactional
    public Inventory updateInventoryItem(Integer inventoryId, UpdateInventoryItemDTO updateDTO) {
        
        // Fetch inventory record
        
        @SuppressWarnings("null")
        Inventory inventory = inventoryRepository.findById(inventoryId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Inventory with ID " + inventoryId + " not found"
            ));

        // Store old values for audit logging
        int oldQuantity = inventory.getQuantityOnHand();
        Date oldExpirationDate = inventory.getExpirationDate();
        Integer oldShelfId = inventory.getWarehouseShelf() != null ? inventory.getWarehouseShelf().getId() : null;

        // Update quantity if provided
        if (updateDTO.getQuantityOnHand() != null) {
            // Validate quantity
            if (updateDTO.getQuantityOnHand() < 0) {
                throw new IllegalArgumentException(
                    "Quantity cannot be negative: " + updateDTO.getQuantityOnHand()
                );
            }
            inventory.setQuantityOnHand(updateDTO.getQuantityOnHand());
        }

        // Update expiration date if provided
        if (updateDTO.getExpirationDate() != null) {
            // Validate expiration date is not in the past
            if (updateDTO.getExpirationDate().getTime() < System.currentTimeMillis()) {
                throw new IllegalArgumentException(
                    "Expiration date cannot be in the past: " + updateDTO.getExpirationDate()
                );
            }
            inventory.setExpirationDate(updateDTO.getExpirationDate());
        }

        // Update warehouse shelf if provided
        if (updateDTO.getWarehouseShelfId() != null) {
            // Verify shelf exists
            @SuppressWarnings("null")
            Warehouse_Shelf newShelf = warehouseShelfRepository.findById(updateDTO.getWarehouseShelfId())
                .orElseThrow(() -> new EntityNotFoundException(
                    "Warehouse shelf with ID " + updateDTO.getWarehouseShelfId() + " not found"
                ));

            // Verify new shelf is in the same warehouse
            if (newShelf.getWarehouse().getId() != inventory.getWarehouse().getId()) {
                throw new IllegalArgumentException(
                    "Cannot relocate inventory to shelf in different warehouse. " +
                    "Current warehouse: " + inventory.getWarehouse().getId() + 
                    ", Target warehouse: " + newShelf.getWarehouse().getId()
                );
            }

            inventory.setWarehouseShelf(newShelf);
        }

        // Check UNIQUE constraint: no duplicate (warehouse, shelf, product, expiration)
        Optional<Inventory> existingInventory = inventoryRepository
            .findByWarehouseIdAndWarehouseShelfIdAndProductIdAndExpirationDate(
                inventory.getWarehouse().getId(),
                inventory.getWarehouseShelf() != null ? inventory.getWarehouseShelf().getId() : null,
                inventory.getProduct().getId(),
                inventory.getExpirationDate()
            );

        // If found a different inventory record with same combination, it's a conflict
        if (existingInventory.isPresent() && existingInventory.get().getId() != inventoryId) {
            throw new ConflictException(
                "Inventory already exists at location warehouse/" + 
                inventory.getWarehouse().getId() + "/shelf/" +
                (inventory.getWarehouseShelf() != null ? inventory.getWarehouseShelf().getId() : "null") +
                " with product " + inventory.getProduct().getId() + 
                " and expiration date " + inventory.getExpirationDate()
            );
        }

        // Update timestamp
        inventory.setUpdatedAt(ZonedDateTime.now());

        // Save updated inventory
        inventory = inventoryRepository.save(inventory);

        // Log the activity with old→new values
        StringBuilder changeDetails = new StringBuilder();
        if (updateDTO.getQuantityOnHand() != null && updateDTO.getQuantityOnHand() != oldQuantity) {
            changeDetails.append("Quantity: ").append(oldQuantity).append("→").append(updateDTO.getQuantityOnHand());
        }
        if (updateDTO.getExpirationDate() != null && !updateDTO.getExpirationDate().equals(oldExpirationDate)) {
            if (changeDetails.length() > 0) changeDetails.append(" | ");
            changeDetails.append("Expiration: ").append(oldExpirationDate).append("→").append(updateDTO.getExpirationDate());
        }
        if (updateDTO.getWarehouseShelfId() != null && !updateDTO.getWarehouseShelfId().equals(oldShelfId)) {
            if (changeDetails.length() > 0) changeDetails.append(" | ");
            changeDetails.append("Shelf: ").append(oldShelfId).append("→").append(updateDTO.getWarehouseShelfId());
        }

        if (changeDetails.length() > 0) {
            logActivity("INVENTORY", inventory.getId(), "UPDATE", changeDetails.toString());
        }

        return inventory;
    }

    /**
     * Validates the AddInventoryItemDTO input.
     * 
     * @param dto The DTO to validate
     * @throws IllegalArgumentException if any validation fails
     */
    private void validateAddInventoryInput(AddInventoryItemDTO dto) {
        
        // Validate SKU
        if (dto.getProductSku() == null || dto.getProductSku().trim().isEmpty()) {
            throw new IllegalArgumentException("Product SKU cannot be null or empty");
        }

        // Validate quantity
        if (dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        // Validate warehouse ID
        if (dto.getWarehouseId() <= 0) {
            throw new IllegalArgumentException("Warehouse ID must be greater than 0");
        }
    }

    /**
     * Updates an existing inventory item with validation.
     * 
     * Validation Rules:
     * - Quantity: Must be > 0 and total inventory must not exceed warehouse capacity
     * - Expiration Date: Must be today or in the future
     * - Warehouse Shelf: Must exist and belong to the same warehouse
     * 
    /**
     * Records an activity log entry for inventory operations.
     * 
     * This method creates an audit trail entry for CREATE and UPDATE operations
     * on inventory items, enabling compliance auditing, historical tracking, and debugging.
     * 
     * Example:
     *   logActivity("INVENTORY", 5, "CREATE", "Created new inventory: 100 units of Widget in Warehouse A");
     *   logActivity("INVENTORY", 5, "UPDATE", "Added 50 more units");
     * 
     * @param entityType The type of entity (must be "INVENTORY")
     * @param entityId The ID of the inventory item being tracked
     * @param action The action performed (CREATE or UPDATE)
     * @param details Additional details about the action for audit trail
     * @throws IllegalArgumentException if parameters are invalid
     * 
     * Note: Errors in logging do not affect inventory operations.
     * Logging uses graceful degradation - if activity log save fails,
     * a warning is printed but the inventory operation succeeds.
     */
    private void logActivity(String entityType, int entityId, String action, String details) {
        try {
            // Get current time for the appropriate action-specific column
            ZonedDateTime now = ZonedDateTime.now();
            
            // Create new ActivityLog entry with action-specific timestamp mapping:
            // - CREATE action: populate created_at
            // - UPDATE action: populate updated_at
            ActivityLog activityLog;
            
            if ("CREATE".equalsIgnoreCase(action)) {
                activityLog = new ActivityLog(entityType, action, entityId, now, null, null);
            } else if ("UPDATE".equalsIgnoreCase(action)) {
                activityLog = new ActivityLog(entityType, action, entityId, null, now, null);
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
            // Log error but don't fail the inventory operation
            // In production, replace with proper logging framework (SLF4J, Log4j)
            System.err.println(
                "Warning: Failed to log activity for inventory ID " + entityId 
                + " action " + action + ": " + e.getMessage()
            );
        }
    }

    /**
     * Checks if an inventory item can be deleted (read-only).
     * Does NOT delete the inventory.
     * Used by frontend before showing confirmation dialog.
     * 
     * @param inventoryId the ID of inventory to check
     * @return DeletionCheckResponse with inventory details and eligibility
     * @throws EntityNotFoundException if inventory not found
     */
    @SuppressWarnings("null")
    @Transactional(readOnly = true)
    public DeletionCheckResponse checkDeletionEligibility(Integer inventoryId) {
        
        // Fetch inventory by ID
        Inventory inventory = inventoryRepository.findById(inventoryId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Inventory with ID " + inventoryId + " not found"
            ));
        
        // Build response with inventory details
        String warehouseShelfCode = inventory.getWarehouseShelf() != null ? 
            inventory.getWarehouseShelf().getCode() : null;
        
        return new DeletionCheckResponse(
            inventory.getId(),
            inventory.getQuantityOnHand(),
            inventory.getExpirationDate(),
            inventory.getWarehouse().getName(),
            warehouseShelfCode,
            inventory.getProduct().getName(),
            inventory.getProduct().getSku(),
            true,  // canDelete - always true (no restrictions)
            null   // reason - null if can delete
        );
    }

    /**
     * Deletes an existing inventory item permanently.
     * Should only be called after frontend has shown confirmation dialog.
     * 
     * @param inventoryId the ID of inventory to delete
     * @param deleteDTO optional deletion reason for audit trail
     * @throws EntityNotFoundException if inventory not found
     */
    @SuppressWarnings("null")
    @Transactional
    public void deleteInventoryItem(Integer inventoryId, DeleteInventoryItemDTO deleteDTO) {
        
        // Fetch inventory by ID
        Inventory inventory = inventoryRepository.findById(inventoryId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Inventory with ID " + inventoryId + " not found"
            ));
        
        // Store deletion details for audit logging
        int quantityDeleted = inventory.getQuantityOnHand();
        String productName = inventory.getProduct().getName();
        String productSku = inventory.getProduct().getSku();
        String warehouseName = inventory.getWarehouse().getName();
        String warehouseShelfCode = inventory.getWarehouseShelf() != null ? 
            inventory.getWarehouseShelf().getCode() : "None";
        String reason = deleteDTO != null && deleteDTO.getReason() != null ? 
            deleteDTO.getReason() : "";
        
        // Delete inventory from database
        inventoryRepository.deleteById(inventoryId);
        
        // Log deletion activity with details
        String details = String.format(
            "Deleted %d units of %s (%s) from %s/%s. Reason: %s",
            quantityDeleted,
            productName,
            productSku,
            warehouseName,
            warehouseShelfCode,
            reason
        );
        
        logActivity("INVENTORY", inventoryId, "DELETE", details);
    }

    /**
     * Retrieves all inventory items for a specific warehouse.
     * 
     * @param warehouseId The warehouse ID
     * @return List of ViewInventoryItemDTO for all items in the warehouse
     * @throws EntityNotFoundException if warehouse not found
     */
    @SuppressWarnings("null")
    public List<ViewInventoryItemDTO> viewAllInventoryByWarehouse(Integer warehouseId) {
        // Verify warehouse exists
        warehouseRepository.findById(warehouseId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Warehouse with ID " + warehouseId + " not found"
            ));

        // Get all inventory items in warehouse
        List<Inventory> inventoryItems = inventoryRepository.findByWarehouseId(warehouseId);

        // Convert to DTOs and return
        return inventoryItems.stream()
            .map(this::convertToViewDTO)
            .collect(Collectors.toList());
    }

    /**
     * Searches inventory by product name within a warehouse.
     * 
     * @param warehouseId The warehouse ID
     * @param productName The product name (or partial name) to search for
     * @return List of ViewInventoryItemDTO matching the search criteria
     * @throws EntityNotFoundException if warehouse not found
     * @throws IllegalArgumentException if productName is empty
     */
    @SuppressWarnings("null")
    public List<ViewInventoryItemDTO> searchByProductName(Integer warehouseId, String productName) {
        // Verify warehouse exists
        warehouseRepository.findById(warehouseId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Warehouse with ID " + warehouseId + " not found"
            ));

        // Validate search term
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name search term cannot be empty");
        }

        // Search for inventory items
        List<Inventory> results = inventoryRepository.searchByWarehouseAndProductName(
            warehouseId,
            productName.trim()
        );

        // Convert to DTOs and return
        return results.stream()
            .map(this::convertToViewDTO)
            .collect(Collectors.toList());
    }

    /**
     * Searches inventory by product SKU within a warehouse.
     * 
     * @param warehouseId The warehouse ID
     * @param productSku The product SKU (or partial SKU) to search for
     * @return List of ViewInventoryItemDTO matching the search criteria
     * @throws EntityNotFoundException if warehouse not found
     * @throws IllegalArgumentException if productSku is empty
     */
    @SuppressWarnings("null")
    public List<ViewInventoryItemDTO> searchByProductSku(Integer warehouseId, String productSku) {
        // Verify warehouse exists
        warehouseRepository.findById(warehouseId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Warehouse with ID " + warehouseId + " not found"
            ));

        // Validate search term
        if (productSku == null || productSku.trim().isEmpty()) {
            throw new IllegalArgumentException("Product SKU search term cannot be empty");
        }

        // Search for inventory items
        List<Inventory> results = inventoryRepository.searchByWarehouseAndProductSku(
            warehouseId,
            productSku.trim()
        );

        // Convert to DTOs and return
        return results.stream()
            .map(this::convertToViewDTO)
            .collect(Collectors.toList());
    }

    /**
     * Filters inventory by category within a warehouse.
     * 
     * @param warehouseId The warehouse ID
     * @param categoryId The category ID to filter by
     * @return List of ViewInventoryItemDTO matching the filter criteria
     * @throws EntityNotFoundException if warehouse not found
     * @throws IllegalArgumentException if categoryId is invalid
     */
    @SuppressWarnings("null")
    public List<ViewInventoryItemDTO> filterByCategory(Integer warehouseId, Integer categoryId) {
        // Verify warehouse exists
        warehouseRepository.findById(warehouseId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Warehouse with ID " + warehouseId + " not found"
            ));

        // Validate category ID
        if (categoryId == null || categoryId <= 0) {
            throw new IllegalArgumentException("Category ID must be a positive number");
        }

        // Filter inventory items
        List<Inventory> results = inventoryRepository.filterByWarehouseAndCategory(
            warehouseId,
            categoryId
        );

        // Convert to DTOs and return
        return results.stream()
            .map(this::convertToViewDTO)
            .collect(Collectors.toList());
    }

    /**
     * Performs advanced search with multiple optional filter options.
     * Searches inventory within a warehouse by product name, SKU, and/or category using OR logic.
     * 
     * @param warehouseId The warehouse ID (required)
     * @param productName Product name to search for (optional, can be null or empty)
     * @param productSku Product SKU to search for (optional, can be null or empty)
     * @param categoryId Category ID to filter by (optional, can be null)
     * @return List of ViewInventoryItemDTO matching the search/filter criteria
     * @throws EntityNotFoundException if warehouse not found
     * @throws IllegalArgumentException if no filter parameters provided or categoryId invalid
     */
    @SuppressWarnings("null")
    public List<ViewInventoryItemDTO> advancedSearch(Integer warehouseId, String productName, 
                                                      String productSku, Integer categoryId) {
        // Verify warehouse exists
        warehouseRepository.findById(warehouseId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Warehouse with ID " + warehouseId + " not found"
            ));

        // Validate that at least one filter parameter is provided
        boolean hasProductName = productName != null && !productName.trim().isEmpty();
        boolean hasProductSku = productSku != null && !productSku.trim().isEmpty();
        boolean hasCategory = categoryId != null && categoryId > 0;

        if (!hasProductName && !hasProductSku && !hasCategory) {
            throw new IllegalArgumentException(
                "At least one search/filter parameter (productName, productSku, or categoryId) is required"
            );
        }

        // Perform search with provided filters
        List<Inventory> results = inventoryRepository.searchInventory(
            warehouseId,
            hasProductName ? productName.trim() : null,
            hasProductSku ? productSku.trim() : null,
            hasCategory ? categoryId : null
        );

        // Convert to DTOs and return
        return results.stream()
            .map(this::convertToViewDTO)
            .collect(Collectors.toList());
    }

    /**
     * Converts an Inventory entity to a ViewInventoryItemDTO.
     * Extracts all relevant information from the inventory record including:
     * - Inventory details (quantity, expiration date, timestamps)
     * - Product information (name, SKU, description, ID)
     * - Category information (name, ID) - handles null category
     * - Warehouse information (name, location, ID)
     * - Warehouse shelf information (code, ID) - handles null shelf assignment
     * 
     * @param inventory The inventory entity to convert
     * @return ViewInventoryItemDTO with all details populated
     */
    private ViewInventoryItemDTO convertToViewDTO(Inventory inventory) {
        ViewInventoryItemDTO dto = new ViewInventoryItemDTO();
        
        // Inventory details
        dto.setInventoryId(inventory.getId());
        dto.setQuantityOnHand(inventory.getQuantityOnHand());
        dto.setExpirationDate(inventory.getExpirationDate());
        dto.setCreatedAt(inventory.getCreatedAt());
        dto.setUpdatedAt(inventory.getUpdatedAt());

        // Product details
        if (inventory.getProduct() != null) {
            Product product = inventory.getProduct();
            dto.setProductId(product.getId());
            dto.setProductName(product.getName());
            dto.setProductSku(product.getSku());
            dto.setProductDescription(product.getDescription());

            // Category details (from product) - handle null category
            if (product.getCategory() != null) {
                dto.setCategoryId(product.getCategory().getId());
                dto.setCategoryName(product.getCategory().getName());
            }
        }

        // Warehouse details
        if (inventory.getWarehouse() != null) {
            Warehouse warehouse = inventory.getWarehouse();
            dto.setWarehouseId(warehouse.getId());
            dto.setWarehouseName(warehouse.getName());
            dto.setWarehouseLocation(warehouse.getLocation());
        }

        // Warehouse shelf details - handle null shelf assignment
        if (inventory.getWarehouseShelf() != null) {
            Warehouse_Shelf shelf = inventory.getWarehouseShelf();
            dto.setWarehouseShelfId(shelf.getId());
            dto.setWarehouseShelfCode(shelf.getCode());
        }

        return dto;
    }

    /**
     * Transfers inventory items from one warehouse to another with capacity validation.
     * 
     * This method implements the complete transfer workflow with the following validations:
     * 
     * 1. Validates input parameters (quantity > 0, IDs valid)
     * 2. Verifies both warehouses exist and are active
     * 3. Checks source warehouse has sufficient inventory of the product
     * 4. Calculates current usage in both warehouses
     * 5. Validates destination warehouse has enough capacity for the transfer
     * 6. Reduces inventory in source warehouse
     * 7. Increases inventory in destination warehouse
     * 8. Creates InventoryTransfer record for workflow tracking
     * 9. Logs the transfer as an activity
     * 
     * Capacity Calculation:
     * - Each unit of product counts as 1 capacity point (abstract unit)
     * - Current usage = sum of all product quantities in warehouse
     * - Available capacity = max_capacity - current_usage
     * - Validation: available_capacity >= transfer_quantity
     * 
     * Example:
     * Warehouse A: capacity 1000, current_usage 800 (available: 200)
     * Warehouse B: capacity 500, current_usage 450 (available: 50)
     * Transfer 30 units from B to A: SUCCESS (A has 200 available)
     * Transfer 60 units from B to A: FAIL (A only has 200 available, needs 60)
     * 
     * @param requestDTO Contains productId, quantity, sourceWarehouseId, destinationWarehouseId
     * @return TransferInventoryResponseDTO with transfer details and status
     * @throws EntityNotFoundException if warehouse or product not found
     * @throws IllegalArgumentException if validation fails
     * @throws ConflictException if warehouses are inactive or insufficient inventory/capacity
     */
    @Transactional
    public TransferInventoryResponseDTO transferInventory(TransferInventoryRequestDTO requestDTO) {
        
        // Validate input
        if (requestDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("Transfer quantity must be greater than 0");
        }
        
        if (requestDTO.getSourceWarehouseId() == requestDTO.getDestinationWarehouseId()) {
            throw new IllegalArgumentException("Source and destination warehouses must be different");
        }

        // Fetch and validate source warehouse
        
        Warehouse sourceWarehouse = warehouseRepository.findById(requestDTO.getSourceWarehouseId())
            .orElseThrow(() -> new EntityNotFoundException(
                "Source warehouse with ID " + requestDTO.getSourceWarehouseId() + " not found"
            ));
        
        if (!sourceWarehouse.isActive()) {
            throw new ConflictException(
                "Cannot transfer from inactive warehouse: " + sourceWarehouse.getName()
            );
        }

        // Fetch and validate destination warehouse
        Warehouse destinationWarehouse = warehouseRepository.findById(requestDTO.getDestinationWarehouseId())
            .orElseThrow(() -> new EntityNotFoundException(
                "Destination warehouse with ID " + requestDTO.getDestinationWarehouseId() + " not found"
            ));
        
        if (!destinationWarehouse.isActive()) {
            throw new ConflictException(
                "Cannot transfer to inactive warehouse: " + destinationWarehouse.getName()
            );
        }

        // Fetch and validate product
        Product product = productService.getProductById(requestDTO.getProductId())
            .orElseThrow(() -> new EntityNotFoundException(
                "Product with ID " + requestDTO.getProductId() + " not found"
            ));

        // Check if source warehouse has sufficient inventory
        List<Inventory> sourceInventoryItems = inventoryRepository.findByWarehouseIdAndProductId(
            requestDTO.getSourceWarehouseId(),
            requestDTO.getProductId()
        );
        
        int totalSourceQuantity = sourceInventoryItems.stream()
            .mapToInt(Inventory::getQuantityOnHand)
            .sum();
        
        if (totalSourceQuantity < requestDTO.getQuantity()) {
            throw new ConflictException(
                "Insufficient inventory in source warehouse. Available: " + totalSourceQuantity + 
                ", Requested: " + requestDTO.getQuantity()
            );
        }

        // Calculate current usage in destination warehouse
        Integer destinationUsage = warehouseRepository.getTotalInventoryQuantity(
            requestDTO.getDestinationWarehouseId()
        );
        if (destinationUsage == null) {
            destinationUsage = 0;
        }

        // Check destination warehouse capacity
        int availableCapacity = destinationWarehouse.getCapacity() - destinationUsage;
        if (availableCapacity < requestDTO.getQuantity()) {
            throw new ConflictException(
                "Insufficient capacity in destination warehouse. Available capacity: " + availableCapacity + 
                ", Required: " + requestDTO.getQuantity()
            );
        }

        // Reduce quantity from source warehouse
        // Transfer from all inventory records in source warehouse until quantity is satisfied
        int remainingToTransfer = requestDTO.getQuantity();
        
        for (Inventory sourceInventory : sourceInventoryItems) {
            if (remainingToTransfer <= 0) break;
            
            int quantityToReduce = Math.min(remainingToTransfer, sourceInventory.getQuantityOnHand());
            sourceInventory.setQuantityOnHand(sourceInventory.getQuantityOnHand() - quantityToReduce);
            sourceInventory.setUpdatedAt(ZonedDateTime.now());
            inventoryRepository.save(sourceInventory);
            
            remainingToTransfer -= quantityToReduce;
        }

        // Add quantity to destination warehouse
        // Try to find existing inventory record for this product in destination
        Optional<Inventory> destinationInventoryOpt = inventoryRepository
            .findByWarehouseIdAndProductId(
                requestDTO.getDestinationWarehouseId(),
                requestDTO.getProductId()
            )
            .stream()
            .findFirst();

        Inventory destinationInventory;
        if (destinationInventoryOpt.isPresent()) {
            // Update existing inventory
            destinationInventory = destinationInventoryOpt.get();
            destinationInventory.setQuantityOnHand(
                destinationInventory.getQuantityOnHand() + requestDTO.getQuantity()
            );
            destinationInventory.setUpdatedAt(ZonedDateTime.now());
        } else {
            // Create new inventory record in destination
            destinationInventory = new Inventory();
            destinationInventory.setProduct(product);
            destinationInventory.setWarehouse(destinationWarehouse);
            destinationInventory.setQuantityOnHand(requestDTO.getQuantity());
            destinationInventory.setCreatedAt(ZonedDateTime.now());
            destinationInventory.setUpdatedAt(ZonedDateTime.now());
        }
        
        inventoryRepository.save(destinationInventory);

        // Create InventoryTransfer record for workflow tracking
        InventoryTransfer transfer = new InventoryTransfer();
        transfer.setProduct(product);
        transfer.setQuantity(requestDTO.getQuantity());
        transfer.setSourceWarehouse(sourceWarehouse);
        transfer.setDestinationWarehouse(destinationWarehouse);
        transfer.setStatus("PENDING");
        transfer.setCreatedAt(ZonedDateTime.now());
        
        InventoryTransfer savedTransfer = inventoryTransferRepository.save(transfer);

        // Log transfer activity
        String transferDetails = String.format(
            "Transfer %d units of %s (%s) from %s to %s. Transfer ID: %d",
            requestDTO.getQuantity(),
            product.getName(),
            product.getSku(),
            sourceWarehouse.getName(),
            destinationWarehouse.getName(),
            savedTransfer.getId()
        );
        
        logActivity("INVENTORY", destinationInventory.getId(), "UPDATE", transferDetails);

        // Build and return response DTO
        return new TransferInventoryResponseDTO(
            savedTransfer.getId(),
            product.getId(),
            product.getName(),
            requestDTO.getQuantity(),
            sourceWarehouse.getId(),
            sourceWarehouse.getName(),
            destinationWarehouse.getId(),
            destinationWarehouse.getName(),
            savedTransfer.getStatus(),
            savedTransfer.getCreatedAt()
        );
    }
}
