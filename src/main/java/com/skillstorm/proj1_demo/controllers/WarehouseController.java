package com.skillstorm.proj1_demo.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.proj1_demo.dtos.UpdateWarehouseDTO;
import com.skillstorm.proj1_demo.dtos.WarehouseDashboardDTO;
import com.skillstorm.proj1_demo.models.Warehouse;
import com.skillstorm.proj1_demo.services.WarehouseService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



/**
 * REST Controller for warehouse management endpoints.
 * Handles HTTP requests for warehouse operations.
 */
@RestController
@RequestMapping("/api/v1/warehouses")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }


    /**
     * Creates a new warehouse.
     * 
     * HTTP Method: POST
     * Endpoint: POST /api/v1/warehouses
     * 
     * Request Body Format:
     * {
     *     "name": "Main Distribution Center",
     *     "location": "New York, NY",
     *     "capacity": 50000
     * }
     * 
     * Success Response:
     * Status: 201 CREATED
     * Body: {
     *     "id": 1,
     *     "name": "Main Distribution Center",
     *     "location": "New York, NY",
     *     "capacity": 50000,
     *     "isActive": true,
     *     "createdAt": "2025-11-15T10:30:00Z",
     *     "updatedAt": null
     * }
     * 
     * Error Responses:
     * - 400 BAD REQUEST: If validation fails (empty name, negative capacity, etc.)
     * - 409 CONFLICT: If warehouse name already exists
     * - 500 INTERNAL SERVER ERROR: If unexpected error occurs
     * 
     * @param requestBody Map containing name, location, and capacity
     * @return ResponseEntity with created warehouse and 201 status
     */
    @PostMapping
    public ResponseEntity<?> addWarehouse(
        @RequestBody Map<String, Object> requestBody) {

        // Logic to create a new warehouse
        try {

            // Extract parameters from request body
            String name = (String) requestBody.get("name");
            String location = (String) requestBody.get("location");
            Integer capacity = ((Number) requestBody.get("capacity")).intValue();
            
            // Call service to create warehouse
            Warehouse createdWarehouse = warehouseService.createWarehouse(name, 
            location, capacity);

            // Return 201 CREATED response
            return new ResponseEntity<Warehouse>(createdWarehouse, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {

            // Validation error - return 400 BAD REQUEST
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "error", "Validation Error",
                    "message", e.getMessage()
                ));

        } catch (RuntimeException e) {

            // Business logic error (e.g., duplicate name) - return 409 CONFLICT
            if (e.getMessage().contains("already exists")) {
                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                        "error", "Conflict Error",
                        "message", e.getMessage()
                    ));
            }

            // Other runtime errors - return 500 INTERNAL SERVER ERROR
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Internal Server Error",
                    "message", e.getMessage()
                ));
        }
    }


    /**
     * Retrieves all warehouses.
     * 
     * HTTP Method: GET
     * Endpoint: GET /api/v1/warehouses
     * 
     * Success Response:
     * Status: 200 OK
     * Body: [
     *     {
     *         "id": 1,
     *         "name": "Main Distribution Center",
     *         "location": "New York, NY",
     *         "capacity": 50000,
     *         "isActive": true,
     *         "createdAt": "2025-11-15T10:30:00Z",
     *         "updatedAt": null
     *     },
     *     ...
     * ]
     * 
     * @return ResponseEntity containing list of all warehouses
     */
    @GetMapping
    public ResponseEntity<List<Warehouse>> getAllWarehouses() {
        
        try {
            List<Warehouse> warehouses = warehouseService.getAllWarehouses();
            return new ResponseEntity<>(warehouses, HttpStatus.OK);

        } catch (Exception e) {

            return ResponseEntity.internalServerError().header(
                "message", "Something went wrong internally").build();
        }
    }   


    /**
     * Retrieves a specific warehouse by ID.
     * 
     * HTTP Method: GET
     * Endpoint: GET /api/v1/warehouses/id/{id}
     * 
     * Path Parameter:
     * - id: The warehouse ID (integer)
     * 
     * Success Response:
     * Status: 200 OK
     * Body: {
     *     "id": 1,
     *     "name": "Main Distribution Center",
     *     "location": "New York, NY",
     *     "capacity": 50000,
     *     "isActive": true,
     *     "createdAt": "2025-11-15T10:30:00Z",
     *     "updatedAt": null
     * }
     * 
     * Error Response:
     * Status: 404 NOT FOUND (if warehouse doesn't exist)
     * 
     * @param id The warehouse ID
     * @return ResponseEntity containing the warehouse or 404 if not found
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<Warehouse> getWarehouseById(@PathVariable int id) {

        try {
            Optional<Warehouse> warehouse = warehouseService.getWarehouseById(id);

            if (warehouse.isPresent()) {
                return new ResponseEntity<>(warehouse.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().header(
                "message", e.getMessage()).build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError().header(
                "message", "Something went wrong internally").build();
        }
        
        
    }


    /**
     * Get all warehouses with all dashboard metrics combined 
     * 
     * HTTP Method: GET
     * Endpoint: GET /api/v1/warehouses/dashboard
     * 
     * Success Response:
     * Status: 200 OK
     * Body: [
     *     {
     *         "id": 1,
     *         "name": "Main Distribution Center",
     *         "location": "New York, NY",
     *         "maxCapacity": 50000,
     *         "currentCapacityUsed": 32500,
     *         "capacityPercentage": 65.0,
     *         "totalItemsCount": 156,
     *         "isActive": true,
     *         "createdAt": "2025-11-15T10:30:00Z",
     *         "updatedAt": "2025-11-16T14:22:00Z"
     *     }
     * ]
     * 
     * Error Responses:
     * - 500 INTERNAL SERVER ERROR: If database error occurs
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getAllWarehousesForDashboard() {
        try {
            
            List<WarehouseDashboardDTO> dashboards = warehouseService.getAllWarehousesForDashboard();
            return new ResponseEntity<>(dashboards, HttpStatus.OK);
        } catch (Exception e) {

            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Failed to retrieve warehouse dashboard",
                    "message", e.getMessage()
                ));
        }
    }

    /**
     * Get a specific warehouse with its dashboard metrics
     * 
     * HTTP Method: GET
     * Endpoint: GET /api/v1/warehouses/id/{id}/dashboard
     * 
     * Path Parameter:
     * - id: Warehouse ID (integer)
     * 
     * Success Response:
     * Status: 200 OK
     * Body: {
     *     "id": 1,
     *     "name": "Main Distribution Center",
     *     "location": "New York, NY",
     *     "maxCapacity": 50000,
     *     "currentCapacityUsed": 32500,
     *     "capacityPercentage": 65.0,
     *     "totalItemsCount": 156,
     *     "isActive": true,
     *     "createdAt": "2025-11-15T10:30:00Z",
     *     "updatedAt": "2025-11-16T14:22:00Z"
     * }
     * 
     * Error Responses:
     * - 404 NOT FOUND: If warehouse doesn't exist
     * - 500 INTERNAL SERVER ERROR: If database error occurs
     */
    @GetMapping("/id/{id}/dashboard")
    public ResponseEntity<?> getWarehouseDashboardById(@PathVariable int id) {
        try {
            WarehouseDashboardDTO dashboard = warehouseService.getWarehouseDashboardById(id);
            return new ResponseEntity<>(dashboard, HttpStatus.OK);

        } catch (RuntimeException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "error", "Warehouse not found",
                    "message", e.getMessage()
                ));
                
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Failed to retrieve warehouse dashboard",
                    "message", e.getMessage()
                ));
        }
    }


    /**
     * Updates an existing warehouse with provided information (partial update).
     * 
     * HTTP Method: PATCH
     * Endpoint: PATCH /api/v1/warehouses/id/{id}
     * 
     * Path Parameter:
     * - id: The warehouse ID (integer, required)
     * 
     * Request Body:
     * All fields are optional. Only provided fields will be updated.
     * Null or omitted fields are left unchanged.
     * 
     * Example 1 - Update only capacity:
     * {
     *     "capacity": 75000
     * }
     * 
     * Example 2 - Update multiple fields:
     * {
     *     "name": "Main Distribution Center - Expanded",
     *     "location": "New York, NY 10001",
     *     "capacity": 75000,
     *     "isActive": true
     * }
     * 
     * Example 3 - Deactivate warehouse:
     * {
     *     "isActive": false
     * }
     * 
     * Success Response (200 OK):
     * {
     *     "id": 1,
     *     "name": "Main Distribution Center - Expanded",
     *     "location": "New York, NY 10001",
     *     "capacity": 75000,
     *     "isActive": true,
     *     "createdAt": "2025-11-15T10:30:00Z",
     *     "updatedAt": "2025-11-16T15:45:30Z"
     * }
     * 
     * Error Responses:
     * 
     * 400 Bad Request - Invalid Input:
     * {
     *     "error": "Validation Error",
     *     "message": "Warehouse name cannot exceed 255 characters"
     * }
     * 
     * 400 Bad Request - Capacity Too Small:
     * {
     *     "error": "Validation Error",
     *     "message": "New capacity (30000) cannot be less than current usage (32500)"
     * }
     * 
     * 404 Not Found - Warehouse doesn't exist:
     * {
     *     "error": "Not Found",
     *     "message": "Warehouse with ID 999 not found"
     * }
     * 
     * 409 Conflict - Duplicate warehouse name:
     * {
     *     "error": "Conflict Error",
     *     "message": "Warehouse with name 'Hub-B' already exists"
     * }
     * 
     * 500 Internal Server Error:
     * {
     *     "error": "Internal Server Error",
     *     "message": "Database error occurred while updating warehouse"
     * }
     * 
     * @param id The warehouse ID to update
     * @param updateDTO DTO with fields to update
     * @return ResponseEntity with updated warehouse and 200 status
     */
    @PatchMapping("/id/{id}")
    public ResponseEntity<?> updateWarehouse(
        @PathVariable int id,
        @RequestBody UpdateWarehouseDTO updateDTO) {

        try {
            // Call service to update warehouse
            Warehouse updatedWarehouse = warehouseService.updateWarehouse(id, updateDTO);

            // Return 200 OK with updated warehouse
            return new ResponseEntity<Warehouse>(updatedWarehouse, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            // Validation error - return 400 BAD REQUEST
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "error", "Validation Error",
                    "message", e.getMessage()
                ));

        } catch (RuntimeException e) {
            // Check if error is "not found"
            if (e.getMessage().contains("not found")) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "error", "Not Found",
                        "message", e.getMessage()
                    ));
            }

            // Check if error is "already exists" (conflict)
            if (e.getMessage().contains("already exists")) {
                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                        "error", "Conflict Error",
                        "message", e.getMessage()
                    ));
            }

            // Other runtime errors - return 500 INTERNAL SERVER ERROR
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Internal Server Error",
                    "message", e.getMessage()
                ));

        } catch (Exception e) {
            // Unexpected error - return 500 INTERNAL SERVER ERROR
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Internal Server Error",
                    "message", "An unexpected error occurred while updating warehouse"
                ));
        }
    }

    /**
     * Checks if a warehouse can be deleted before attempting deletion.
     * 
     * HTTP Method: GET
     * Endpoint: GET /api/v1/warehouses/id/{id}/deletion-check
     * 
     * Path Parameter:
     * - id: The warehouse ID (integer, required)
     * 
     * Purpose:
     * This endpoint allows the frontend to check if a warehouse is eligible for deletion.
     * Used by confirmation dialogs to show appropriate warnings.
     * 
     * Success Response (200 OK):
     * {
     *     "warehouseId": 1,
     *     "warehouseName": "Main Distribution Center",
     *     "canDelete": true,
     *     "reason": null
     * }
     * 
     * Cannot Delete Response (200 OK):
     * {
     *     "warehouseId": 1,
     *     "warehouseName": "Main Distribution Center",
     *     "canDelete": false,
     *     "reason": "Warehouse contains 45 inventory items"
     * }
     * 
     * Error Responses:
     * - 404 NOT FOUND: If warehouse doesn't exist
     * - 500 INTERNAL SERVER ERROR: If database error occurs
     * 
     * @param id The warehouse ID to check
     * @return ResponseEntity with deletion eligibility information
     */
    @GetMapping("/id/{id}/deletion-check")
    public ResponseEntity<?> checkDeletionEligibility(@PathVariable int id) {
        try {
            Map<String, Object> eligibility = warehouseService.checkDeletionEligibility(id);
            return new ResponseEntity<>(eligibility, HttpStatus.OK);

        } catch (RuntimeException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "error", "Warehouse not found",
                    "message", e.getMessage()
                ));

        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Failed to check deletion eligibility",
                    "message", e.getMessage()
                ));
        }
    }

    /**
     * Deletes a warehouse by ID.
     * 
     * HTTP Method: DELETE
     * Endpoint: DELETE /api/v1/warehouses/id/{id}
     * 
     * Path Parameter:
     * - id: The warehouse ID (integer, required)
     * 
     * Deletion Rules:
     * - Warehouse must exist
     * - Warehouse must be empty (no inventory items)
     * - Warehouse must have no inventory quantity
     * 
     * Request Flow:
     * 1. Frontend calls GET /api/v1/warehouses/id/{id}/deletion-check to verify warehouse can be deleted
     * 2. If canDelete is true, display confirmation dialog to admin
     * 3. If admin confirms, call DELETE /api/v1/warehouses/id/{id}
     * 4. If successful, remove warehouse from UI and display success message
     * 
     * Success Response (204 NO CONTENT):
     * No body returned. HTTP status indicates successful deletion.
     * 
     * Error Responses:
     * 
     * 400 Bad Request - Warehouse contains inventory:
     * {
     *     "error": "Deletion Error",
     *     "message": "Cannot delete warehouse 'W-A' because it contains 45 units of inventory. 
     *                 Please remove all inventory before deletion."
     * }
     * 
     * 404 Not Found - Warehouse doesn't exist:
     * {
     *     "error", "Not Found",
     *     "message": "Warehouse with ID 444 not found"
     * }
     * 
     * 500 Internal Server Error:
     * {
     *     "error": "Internal Server Error",
     *     "message": "An error occurred while deleting warehouse"
     * }
     * 
     * @param id The warehouse ID to delete
     * @return ResponseEntity with 204 NO CONTENT on success or error details
     */
    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> deleteWarehouse(@PathVariable int id) {
        try {
            warehouseService.deleteWarehouse(id);
            
            // Return 204 NO CONTENT on successful deletion
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (RuntimeException e) {
            // Check if error is "not found"
            if (e.getMessage().contains("not found")) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "error", "Not Found",
                        "message", e.getMessage()
                    ));
            }

            // Check if error is "cannot delete" (has inventory)
            if (e.getMessage().contains("Cannot delete")) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "error", "Deletion Error",
                        "message", e.getMessage()
                    ));
            }

            // Other runtime errors - return 500 INTERNAL SERVER ERROR
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Internal Server Error",
                    "message", e.getMessage()
                ));

        } catch (Exception e) {
            // Unexpected error - return 500 INTERNAL SERVER ERROR
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Internal Server Error",
                    "message", "An error occurred while deleting warehouse"
                ));
        }
    }

    

}
                

