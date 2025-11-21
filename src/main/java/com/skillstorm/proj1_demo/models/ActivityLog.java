package com.skillstorm.proj1_demo.models;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Represents an audit log entry for entity changes.
 * 
 * Tracks CREATE, UPDATE, and DELETE operations on major entities.
 * Uses the activity log's own id combined with entity_type and action
 * to ensure unique audit trail entries.
 * 
 * Supported Entity Types:
 * - WAREHOUSE: Changes to warehouse master data
 * - INVENTORY: Changes to inventory batches
 * - PRODUCT: Changes to product master data
 * - ALERT: Changes to alert records
 * - TRANSFER: Changes to inventory transfer requests
 * 
 * Usage Example:
 * ActivityLog log = new ActivityLog("WAREHOUSE", "CREATE");
 * // When saved with id=1: creates unique entry (WAREHOUSE, 1, CREATE)
 * 
 * @see Warehouse
 * @see Inventory
 * @see Product
 * @see Alert
 * @see InventoryTransfer
 */
@Entity
@Table(name = "activity_log", uniqueConstraints = {
    @UniqueConstraint(
        name = "uq_entity_action", 
        columnNames = {"entity_type", "entity_id", "action"}
    )
})
public class ActivityLog {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Type of entity being tracked.
     * 
     * Valid Values:
     * - WAREHOUSE: Warehouse entity changes
     * - INVENTORY: Inventory batch changes
     * - PRODUCT: Product master data changes
     * - ALERT: Alert record changes
     * - TRANSFER: Transfer request changes
     */
    @Column(name = "entity_type")
    private String entityType;

    /**
     * ID of the entity being tracked.
     */
    @Column(name = "entity_id")
    private int entityId;

    /**
     * Action performed on the entity.
     * 
     * Valid Values:
     * - CREATE: Entity was newly created
     * - UPDATE: Entity was modified
     * - DELETE: Entity was deleted (or soft-deleted)
     */
    @Column
    private String action;

    /**
     * When this activity was recorded.
     * Automatically set to current timestamp on creation.
     */
    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt;

    /**
     * When this activity record was last updated.
     * Null if record has never been updated.
     */
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime updatedAt;

    /**
     * When this activity record was soft-deleted.
     * Null if record is still active.
     */
    @Column(name = "deleted_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime deletedAt;

    /**
     * Validates entity type and action before persisting.
     * Does NOT set timestamps - these are set explicitly by services based on action type.
     * 
     * This lifecycle callback is invoked by JPA before persisting the entity to the database.
     */
    @PrePersist
    private void onPrePersist() {
        // Validate entity type and action
        validateEntityType();
        validateAction();
    }

    /**
     * Validates that entityType is one of the supported values.
     */
    private void validateEntityType() {
        if (entityType == null || entityType.trim().isEmpty()) {
            throw new IllegalArgumentException("entityType cannot be null or empty");
        }
        
        String[] validEntityTypes = {"WAREHOUSE", "INVENTORY", "PRODUCT", "ALERT"};
        boolean isValidType = false;
        
        for (String type : validEntityTypes) {
            if (entityType.equalsIgnoreCase(type)) {
                this.entityType = type.toUpperCase(); // Normalize to uppercase
                isValidType = true;
                break;
            }
        }
        
        if (!isValidType) {
            throw new IllegalArgumentException(
                "entityType must be one of: WAREHOUSE, INVENTORY, PRODUCT, ALERT. "
                + "Got: " + entityType
            );
        }
    }

    /**
     * Validates that action is one of the supported values.
     */
    private void validateAction() {
        if (action == null || action.trim().isEmpty()) {
            throw new IllegalArgumentException("action cannot be null or empty");
        }
        
        String[] validActions = {"CREATE", "UPDATE", "DELETE"};
        boolean isValidAction = false;
        
        for (String act : validActions) {
            if (action.equalsIgnoreCase(act)) {
                this.action = act.toUpperCase(); // Normalize to uppercase
                isValidAction = true;
                break;
            }
        }
        
        if (!isValidAction) {
            throw new IllegalArgumentException(
                "action must be one of: CREATE, UPDATE, DELETE. "
                + "Got: " + action
            );
        }
    }

    // Constructors
    public ActivityLog() {
    }

    /**
     * Constructs an ActivityLog without id (will be auto-generated on save).
     * 
     * @param entityType Type of entity (WAREHOUSE, INVENTORY, PRODUCT, ALERT, TRANSFER)
     * @param action Action performed (CREATE, UPDATE, DELETE)
     * @param entityId ID of the entity being tracked
     */
    public ActivityLog(String entityType, String action, int entityId) {
        this.entityType = entityType;
        this.action = action;
        this.entityId = entityId;
    }

    /**
     * Constructs a complete ActivityLog with id.
     * 
     * @param id The activity log entry id
     * @param entityType Type of entity (WAREHOUSE, INVENTORY, PRODUCT, ALERT, TRANSFER)
     * @param action Action performed (CREATE, UPDATE, DELETE)
     * @param entityId ID of the entity being tracked
     */
    public ActivityLog(int id, String entityType, String action, int entityId) {
        this.id = id;
        this.entityType = entityType;
        this.action = action;
        this.entityId = entityId;
    }

    /**
     * Constructs an ActivityLog with explicit timestamps.
     * Used by services to set action-specific timestamp columns:
     * - CREATE action: sets createdAt, others null
     * - UPDATE action: sets updatedAt, others null
     * - DELETE action: sets deletedAt, others null
     * 
     * @param entityType Type of entity (WAREHOUSE, INVENTORY, PRODUCT, ALERT, TRANSFER)
     * @param action Action performed (CREATE, UPDATE, DELETE)
     * @param entityId ID of the entity being tracked
     * @param createdAt Timestamp for CREATE action (null for other actions)
     * @param updatedAt Timestamp for UPDATE action (null for other actions)
     * @param deletedAt Timestamp for DELETE action (null for other actions)
     */
    public ActivityLog(String entityType, String action, int entityId, 
                      ZonedDateTime createdAt, ZonedDateTime updatedAt, ZonedDateTime deletedAt) {
        this.entityType = entityType;
        this.action = action;
        this.entityId = entityId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getAction() {
        return action;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public int getEntityId() {
        return entityId;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public ZonedDateTime getDeletedAt() {
        return deletedAt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setDeletedAt(ZonedDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public String toString() {
        return "ActivityLog [id=" + id + ", entityType=" + entityType 
            + ", action=" + action + ", createdAt=" + createdAt + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((entityType == null) ? 0 : entityType.hashCode());
        result = prime * result + entityId;
        result = prime * result + ((action == null) ? 0 : action.hashCode());
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
        ActivityLog other = (ActivityLog) obj;
        if (entityType == null) {
            if (other.entityType != null)
                return false;
        } else if (!entityType.equals(other.entityType))
            return false;
        if (entityId != other.entityId)
            return false;
        if (action == null) {
            if (other.action != null)
                return false;
        } else if (!action.equals(other.action))
            return false;
        return true;
    }

    
}
