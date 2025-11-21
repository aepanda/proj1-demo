package com.skillstorm.proj1_demo.repositories;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillstorm.proj1_demo.models.ActivityLog;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Integer> {

    /**
     * Find all activity logs for a specific entity type.
     * 
     * Example: Find all WAREHOUSE logs
     * @param entityType The type of entity (WAREHOUSE, INVENTORY, PRODUCT, ALERT)
     * @return List of activity logs for that entity type
     */
    public List<ActivityLog> findByEntityType(String entityType);

    /**
     * Find all activity logs for a specific entity instance.
     * 
     * Example: Find all logs for warehouse ID #5
     * @param entityId The ID of the specific entity
     * @return List of activity logs for that entity ID
     */
    public List<ActivityLog> findByEntityId(int entityId);
    
    /**
     * Find all activity logs for a specific action type.
     * 
     * Example: Find all CREATE operations
     * @param action The action type (CREATE, UPDATE, DELETE)
     * @return List of activity logs for that action
     */
    public List<ActivityLog> findByAction(String action);
    
    /**
     * Find all activity logs for a specific entity type and ID combination.
     * 
     * Example: Find all logs for warehouse ID #5
     * @param entityType The type of entity
     * @param entityId The ID of the entity instance
     * @return List of activity logs matching the combination
     */
    public List<ActivityLog> findByEntityTypeAndEntityId(String entityType,
        int entityId);
    
    /**
     * Find all activity logs for a specific entity type, ID, and action.
     * 
     * Example: Find the CREATE log for warehouse ID #5
     * @param entityType The type of entity
     * @param entityId The ID of the entity instance
     * @param action The action performed
     * @return List of activity logs matching all criteria
     */
    public List<ActivityLog> findByEntityTypeAndEntityIdAndAction(String entityType,
        int entityId, String action);
    
    /**
     * Find all activity logs created within a specific time range.
     * 
     * @param startTime The start of the time range
     * @param endTime The end of the time range
     * @return List of activity logs created within the time range
     */
    public List<ActivityLog> findByCreatedAtBetween(ZonedDateTime startTime, 
        ZonedDateTime endTime);
    
    /**
     * Find all active (non-deleted) activity logs.
     * 
     * @return List of activity logs where deleted_at is NULL
     */
    public List<ActivityLog> findByDeletedAtIsNull();

}
