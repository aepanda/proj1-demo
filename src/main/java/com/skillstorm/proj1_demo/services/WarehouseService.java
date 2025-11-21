package com.skillstorm.proj1_demo.services;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillstorm.proj1_demo.dtos.UpdateWarehouseDTO;
import com.skillstorm.proj1_demo.dtos.WarehouseDashboardDTO;
import com.skillstorm.proj1_demo.models.ActivityLog;
import com.skillstorm.proj1_demo.models.Warehouse;
import com.skillstorm.proj1_demo.repositories.ActivityLogRepository;
import com.skillstorm.proj1_demo.repositories.WarehouseRepository;

/**
 * Service class for warehouse management operations.
 * Handles business logic for creating, updating, and managing warehouses.
 */
@Service
public class WarehouseService {


    private final WarehouseRepository warehouseRepository;
    private final ActivityLogRepository activityLogRepository;

    /**
     * Creates a new warehouse with validation.
     * 
     * Validation Rules:
     * - Name: Must not be null, empty, or longer than 255 characters
     * - Location: Must not be null, empty, or longer than 500 characters
     * - Capacity: Must be greater than 0
     * - Unique Name: No warehouse with the same name can exist
     * 
     * @param name The warehouse name (required)
     * @param location The warehouse location (required)
     * @param capacity The maximum capacity in units (required, must be > 0)
     * @return The created Warehouse entity
     * @throws IllegalArgumentException if validation fails
     * @throws RuntimeException if warehouse name already exists
     */
    public WarehouseService(
        WarehouseRepository warehouseRepository, 
        ActivityLogRepository activityLogRepository) {
        this.warehouseRepository = warehouseRepository;
        this.activityLogRepository = activityLogRepository;
    }

    @Transactional
    public Warehouse createWarehouse(String name, String location, Integer capacity) {

        // Input validation
        validateWarehouseInput(name, location, capacity);

        // Check for duplicate warehouse name
        if (warehouseRepository.existsByName(name.trim())) {
            throw new RuntimeException(
                "Warehouse with name '" + name + "' already exists"
            );
        }

        // Create new warehouse
        Warehouse warehouse = new Warehouse();
        warehouse.setName(name.trim());
        warehouse.setLocation(location.trim());
        warehouse.setCapacity(capacity);
        warehouse.setActive(true);  // New warehouses are active by default
        warehouse.setCreatedAt(ZonedDateTime.now());
        
        // Save to database
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        
        // Record activity in audit log
        // This tracks the warehouse creation with its specific ID
        logActivity("WAREHOUSE", savedWarehouse.getId(), "CREATE");
        
        return savedWarehouse;

    }

    /**
     * Validates warehouse input data.
     * 
     * @param name The warehouse name
     * @param location The warehouse location
     * @param capacity The warehouse capacity
     * @throws IllegalArgumentException if any validation fails
     */
    private void validateWarehouseInput(String name, String location, int capacity) {
        // Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Warehouse name cannot be null or empty"
            );
        }
        if (name.length() > 255) {
            throw new IllegalArgumentException(
                "Warehouse name cannot exceed 255 characters"
            );
        }
        
        // Validate location
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Warehouse location cannot be null or empty"
            );
        }
        if (location.length() > 500) {
            throw new IllegalArgumentException(
                "Warehouse location cannot exceed 500 characters"
            );
        }
        
        // Validate capacity
        if (capacity <= 0) {
            throw new IllegalArgumentException(
                "Warehouse capacity must be greater than 0"
            );
        }
    }

    /**
     * Retrieves a warehouse by ID.
     * 
     * @param id The warehouse ID
     * @return Optional containing the warehouse if found
     */
    public Optional<Warehouse> getWarehouseById(int id) {
        return warehouseRepository.findById(id);
    }

    /**
     * Retrieves all warehouses.
     * 
     * @return List of all warehouses
     */
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    /**
     * Retrieves all active warehouses.
     * 
     * @return List of active warehouses
     */
    public List<Warehouse> getActiveWarehouses() {
        return warehouseRepository.findByIsActive(true);
    }

    /**
     * Records an activity log entry for warehouse operations.
     * 
     * This method creates an audit trail entry for CREATE, UPDATE, DELETE operations
     * on warehouses, enabling compliance auditing, historical tracking, and debugging.
     * 
     * The unique constraint on (entity_type, entity_id, action) ensures only one
     * CREATE, one UPDATE, and one DELETE record per warehouse.
     * 
     * Example:
     *   logActivity("WAREHOUSE", 5, "CREATE");
     *   // Creates: entity_type="WAREHOUSE", entity_id=5, action="CREATE"
     * 
     * @param entityType The type of entity (must be "WAREHOUSE")
     * @param entityId The ID of the warehouse being tracked
     * @param action The action performed (CREATE, UPDATE, or DELETE)
     * @throws IllegalArgumentException if parameters are invalid
     * 
     * Note: Errors in logging do not affect warehouse creation.
     * Logging uses graceful degradation - if activity log save fails,
     * a warning is printed but the warehouse operation succeeds.
     */
    private void logActivity(String entityType, int entityId, String action) {
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
            // Log error but don't fail the warehouse operation
            // In production, replace with proper logging framework (SLF4J, Log4j)
            System.err.println(
                "Warning: Failed to log activity for warehouse ID " + entityId 
                + " action " + action + ": " + e.getMessage()
            );
        }
    }   


    /**
     * Get all warehouses with their dashboard metrics combined.
     * Calculates current capacity usage and returns as DTO
     * 
     * @return List of WarehouseDashboardDTO with all warehouses and their metrics
     */
    @Transactional(readOnly = true)
    public List<WarehouseDashboardDTO> getAllWarehousesForDashboard() {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        List<WarehouseDashboardDTO> dashboardDTOs = new ArrayList<>();
        
        for (Warehouse warehouse : warehouses) {
            WarehouseDashboardDTO dto = buildWarehouseDashboardDTO(warehouse);
            dashboardDTOs.add(dto);
        }
        
        return dashboardDTOs;
    }


    /**
     * Get a single warehouse with dashboard metrics
     * 
     * @param warehouseId The warehouse ID
     * @return WarehouseDashboardDTO with metrics
     * @throws ResourceNotFoundException if warehouse doesn't exist
     */
    @Transactional(readOnly = true)
    public WarehouseDashboardDTO getWarehouseDashboardById(int warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
            .orElseThrow(() -> new RuntimeException(
                "Warehouse with ID " + warehouseId + " not found"
            ));
        
        return buildWarehouseDashboardDTO(warehouse);
    }


    /**
     * Helper method to build a WarehouseDashboardDTO from a Warehouse entity
     * Calculates all metrics
     * 
     * @param warehouse The warehouse entity
     * @return WarehouseDashboardDTO with calculated metrics
     */
    private WarehouseDashboardDTO buildWarehouseDashboardDTO(Warehouse warehouse) {
        // Get total quantity on hand
        Integer totalQuantity = warehouseRepository.getTotalInventoryQuantity(warehouse.getId());
        int currentCapacityUsed = (totalQuantity != null) ? totalQuantity : 0;
        
        // Calculate capacity percentage
        double capacityPercentage = (warehouse.getCapacity() > 0) 
            ? (currentCapacityUsed * 100.0) / warehouse.getCapacity() 
            : 0.0;
        
        // Count total items
        Long itemCount = warehouseRepository.countInventoryItemsByWarehouse(warehouse.getId());
        int totalItems = (itemCount != null) ? itemCount.intValue() : 0;
        
        // Build DTO
        return new WarehouseDashboardDTO(
            warehouse.getId(),
            warehouse.getName(),
            warehouse.getLocation(),
            warehouse.getCapacity(),
            currentCapacityUsed,
            capacityPercentage,
            totalItems,
            warehouse.isActive(),
            warehouse.getCreatedAt(),
            warehouse.getUpdatedAt()
        );
    }


    /**
     * Get warehouses filtered by active status with dashboard metrics
     * 
     * @param isActive Filter by active status
     * @return List of WarehouseDashboardDTO
     */
    @Transactional(readOnly = true)
    public List<WarehouseDashboardDTO> getActiveWarehousesForDashboard(boolean isActive){
        List<Warehouse> warehouses = warehouseRepository.findByIsActive(isActive);
        List<WarehouseDashboardDTO> dashboardDTOs = new ArrayList<>();
        
        for (Warehouse warehouse : warehouses) {
            WarehouseDashboardDTO dto = buildWarehouseDashboardDTO(warehouse);
            dashboardDTOs.add(dto);
        }
        
        return dashboardDTOs;
    }



    /**
     * Updates an existing warehouse with provided information.
     * Only updates fields that are provided in the DTO (null values are ignored).
     * 
     * Business Rules:
     * - Warehouse must exist
     * - If name is being updated: new name must be unique (not used by another warehouse)
     * - If capacity is being updated: new capacity must be >= current inventory usage
     * - updatedAt timestamp is automatically set to current time
     * - All changes are logged in activity log
     * 
     * Validation Failures:
     * - IllegalArgumentException: For validation errors (400 Bad Request)
     * - RuntimeException: For business logic errors (404 Not Found or 409 Conflict)
     * 
     * @param warehouseId The ID of the warehouse to update
     * @param updateDTO DTO containing fields to update (null fields are ignored)
     * @return The updated Warehouse entity
     * @throws RuntimeException if warehouse not found or business rule violated
     * @throws IllegalArgumentException if validation fails
     */
    @Transactional
    public Warehouse updateWarehouse(int warehouseId, UpdateWarehouseDTO updateDTO) {

        // Step 1: Fetch existing warehouse
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
            .orElseThrow(() -> new RuntimeException(
                "Warehouse with ID " + warehouseId + " not found"
            ));

        // Step 2: Validate and update NAME (if provided)
        if (updateDTO.getName() != null) {
            validateAndUpdateName(warehouse, updateDTO.getName());
        }

        // Step 3: Validate and update LOCATION (if provided)
        if (updateDTO.getLocation() != null) {
            validateAndUpdateLocation(warehouse, updateDTO.getLocation());
        }

        // Step 4: Validate and update CAPACITY (if provided)
        if (updateDTO.getCapacity() != null) {
            validateAndUpdateCapacity(warehouse, updateDTO.getCapacity());
        }

        // Step 5: Update ACTIVE STATUS (if provided)
        if (updateDTO.getIsActive() != null) {
            warehouse.setActive(updateDTO.getIsActive());
        }

        // Step 6: Update the timestamp
        warehouse.setUpdatedAt(ZonedDateTime.now());

        // Step 7: Persist changes to database
        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);

        // Step 8: Log the activity
        logActivity("WAREHOUSE", updatedWarehouse.getId(), "UPDATE");

        return updatedWarehouse;
    }

    /**
     * Validates warehouse name and updates if valid.
     * 
     * Validation Rules:
     * - Name cannot be null or empty
     * - Name cannot exceed 255 characters
     * - Name must be unique (cannot be used by another warehouse)
     * 
     * @param warehouse The warehouse entity to update
     * @param newName The new name to validate and set
     * @throws IllegalArgumentException if validation fails
     * @throws RuntimeException if name already exists
     */
    private void validateAndUpdateName(Warehouse warehouse, String newName) {

        // Validate not null or empty
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Warehouse name cannot be null or empty"
            );
        }

        // Validate length
        if (newName.length() > 255) {
            throw new IllegalArgumentException(
                "Warehouse name cannot exceed 255 characters"
            );
        }

        // Validate uniqueness (excluding current warehouse)
        if (warehouseRepository.existsByNameExcludingId(newName.trim(), warehouse.getId())) {
            throw new RuntimeException(
                "Warehouse with name '" + newName + "' already exists"
            );
        }

        // All validations passed - update the name
        warehouse.setName(newName.trim());
    }

    /**
     * Validates warehouse location and updates if valid.
     * 
     * Validation Rules:
     * - Location cannot be null or empty
     * - Location cannot exceed 500 characters
     * 
     * @param warehouse The warehouse entity to update
     * @param newLocation The new location to validate and set
     * @throws IllegalArgumentException if validation fails
     */
    private void validateAndUpdateLocation(Warehouse warehouse, String newLocation) {

        // Validate not null or empty
        if (newLocation == null || newLocation.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Warehouse location cannot be null or empty"
            );
        }

        // Validate length
        if (newLocation.length() > 500) {
            throw new IllegalArgumentException(
                "Warehouse location cannot exceed 500 characters"
            );
        }

        // All validations passed - update the location
        warehouse.setLocation(newLocation.trim());
    }

    /**
     * Validates warehouse capacity and updates if valid.
     * 
     * Validation Rules:
     * - Capacity cannot be null
     * - Capacity must be greater than 0
     * - Capacity cannot be less than current inventory usage
     * 
     * Business Logic:
     * If reducing capacity, system checks current inventory quantity.
     * Cannot reduce capacity below what's currently stored.
     * 
     * Example:
     * - Current warehouse capacity: 50,000 units
     * - Current inventory: 32,500 units
     * - Attempt to reduce capacity to 30,000: REJECT (less than current usage)
     * - Attempt to increase capacity to 75,000: ACCEPT
     * 
     * @param warehouse The warehouse entity to update
     * @param newCapacity The new capacity to validate and set
     * @throws IllegalArgumentException if validation fails
     */
    private void validateAndUpdateCapacity(Warehouse warehouse, Integer newCapacity) {

        // Validate not null
        if (newCapacity == null) {
            throw new IllegalArgumentException(
                "Warehouse capacity cannot be null"
            );
        }

        // Validate greater than 0
        if (newCapacity <= 0) {
            throw new IllegalArgumentException(
                "Warehouse capacity must be greater than 0"
            );
        }

        // Get current inventory usage
        Integer currentUsage = warehouseRepository.getTotalInventoryQuantity(warehouse.getId());
        int currentCapacityUsed = (currentUsage != null) ? currentUsage : 0;

        // Validate new capacity is >= current usage
        if (newCapacity < currentCapacityUsed) {
            throw new IllegalArgumentException(
                "New capacity (" + newCapacity + ") cannot be less than current usage (" 
                + currentCapacityUsed + ")"
            );
        }

        // All validations passed - update the capacity
        warehouse.setCapacity(newCapacity);
    }

    /**
     * Deletes a warehouse by ID.
     * 
     * Deletion Rules:
     * - Warehouse must exist
     * - Warehouse must be empty (no inventory items)
     * - Warehouse must have no active transactions or transfers
     * 
     * Business Logic:
     * Prevents accidental deletion of warehouses that contain inventory or are involved
     * in active transactions. Admins must clear inventory before deletion.
     * 
     * Validation Failures:
     * - RuntimeException: If warehouse not found or has inventory/transactions
     * 
     * Example:
     * - Warehouse with ID 1 exists and is empty: DELETE SUCCESSFUL
     * - Warehouse with ID 1 has 100 items: DELETE REJECTED (has inventory)
     * - Warehouse with ID 999 doesn't exist: DELETE REJECTED (not found)
     * 
     * @param warehouseId The ID of the warehouse to delete
     * @throws RuntimeException if warehouse not found or has inventory
     */
    @Transactional
    public void deleteWarehouse(int warehouseId) {

        // Step 1: Fetch existing warehouse
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
            .orElseThrow(() -> new RuntimeException(
                "Warehouse with ID " + warehouseId + " not found"
            ));

        // Step 2: Check if warehouse has any inventory
        Integer inventoryCount = warehouseRepository.getTotalInventoryQuantity(warehouseId);
        int currentInventory = (inventoryCount != null) ? inventoryCount : 0;

        if (currentInventory > 0) {
            throw new RuntimeException(
                "Cannot delete warehouse '" + warehouse.getName() + "' because it contains "
                + currentInventory + " units of inventory. Please remove all inventory before deletion."
            );
        }

        // Step 3: Check if warehouse has any inventory items (even if quantity is 0)
        Long itemCount = warehouseRepository.countInventoryItemsByWarehouse(warehouseId);
        if (itemCount != null && itemCount > 0) {
            throw new RuntimeException(
                "Cannot delete warehouse '" + warehouse.getName() + "' because it contains inventory items. "
                + "Please remove all inventory items before deletion."
            );
        }

        // Step 4: Delete the warehouse
        warehouseRepository.deleteById(warehouseId);

        // Step 5: Log the deletion activity
        logActivity("WAREHOUSE", warehouseId, "DELETE");
    }

    /**
     * Checks if a warehouse can be deleted.
     * 
     * This method performs validation checks to determine if a warehouse
     * is eligible for deletion without actually deleting it.
     * Useful for UI confirmation dialogs.
     * 
     * Validation Checks:
     * - Warehouse must exist
     * - Warehouse must have no inventory
     * - Warehouse must have no inventory items
     * 
     * @param warehouseId The ID of the warehouse to check
     * @return A map containing:
     *         - "canDelete": boolean indicating if warehouse can be deleted
     *         - "warehouseId": the warehouse ID
     *         - "warehouseName": the warehouse name
     *         - "reason": explanation if cannot be deleted (null if can be deleted)
     * @throws RuntimeException if warehouse not found
     */
    @Transactional(readOnly = true)
    public Map<String, Object> checkDeletionEligibility(int warehouseId) {

        Map<String, Object> result = new java.util.HashMap<>();

        // Fetch warehouse
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
            .orElseThrow(() -> new RuntimeException(
                "Warehouse with ID " + warehouseId + " not found"
            ));

        result.put("warehouseId", warehouseId);
        result.put("warehouseName", warehouse.getName());

        // Check inventory count
        Long itemCount = warehouseRepository.countInventoryItemsByWarehouse(warehouseId);
        if (itemCount != null && itemCount > 0) {
            result.put("canDelete", false);
            result.put("reason", "Warehouse contains " + itemCount + " inventory items");
            return result;
        }

        // Check inventory quantity
        Integer inventoryQuantity = warehouseRepository.getTotalInventoryQuantity(warehouseId);
        int totalQuantity = (inventoryQuantity != null) ? inventoryQuantity : 0;

        if (totalQuantity > 0) {
            result.put("canDelete", false);
            result.put("reason", "Warehouse contains " + totalQuantity + " units of inventory");
            return result;
        }

        // All checks passed
        result.put("canDelete", true);
        result.put("reason", null);
        return result;
    }



}
