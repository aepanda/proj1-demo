package com.skillstorm.proj1_demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.proj1_demo.dtos.AddInventoryItemDTO;
import com.skillstorm.proj1_demo.dtos.DeleteInventoryItemDTO;
import com.skillstorm.proj1_demo.dtos.DeletionCheckResponse;
import com.skillstorm.proj1_demo.dtos.TransferInventoryRequestDTO;
import com.skillstorm.proj1_demo.dtos.TransferInventoryResponseDTO;
import com.skillstorm.proj1_demo.dtos.UpdateInventoryItemDTO;
import com.skillstorm.proj1_demo.dtos.ViewInventoryItemDTO;
import com.skillstorm.proj1_demo.exceptions.ConflictException;
import com.skillstorm.proj1_demo.exceptions.EntityNotFoundException;
import com.skillstorm.proj1_demo.models.Inventory;
import com.skillstorm.proj1_demo.services.InventoryService;

/**
 * REST Controller for inventory management endpoints.
 * Handles HTTP requests for inventory operations.
 * 
 * Role: Admin only - Admins can add items to warehouses
 * 
 * Communication flow:
 * HTTP Request → InventoryController → InventoryService → Repository Layer
 */
@RestController
@RequestMapping("/api/v1/inventories")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Adds a new inventory item to a warehouse.
     * 
     * HTTP Method: POST
     * Endpoint: POST /api/v1/inventory
     * Role Required: ADMIN
     * 
     * Allows admins to add items to a warehouse, specifying:
     * - Item details (name, SKU, description, category)
     * - Quantity
     * - Storage location (warehouse and optional shelf code)
     * 
     * Request Body Format:
     * {
     *     "productSku": "PART-001",
     *     "productName": "High-Performance Processor",
     *     "productDescription": "12-core processor, 3.5GHz base clock",
     *     "categoryId": 1,
     *     "quantity": 100,
     *     "expirationDate": "2026-12-31",
     *     "warehouseId": 1,
     *     "warehouseShelfCode": "RACK-A1"
     * }
     * 
     * Success Response:
     * Status: 201 CREATED
     * Body: Inventory object with product, warehouse, and shelf details
     * 
     * Error Responses:
     * - 400 BAD REQUEST: Validation failed (invalid quantity, missing SKU, etc.)
     * - 404 NOT FOUND: Warehouse or shelf doesn't exist
     * - 409 CONFLICT: Warehouse is inactive
     * - 500 INTERNAL SERVER ERROR: Unexpected error
     * 
     * @param dto The DTO containing inventory item details
     * @return ResponseEntity with created inventory and 201 status, or error response
     */
    @PostMapping
    public ResponseEntity<?> addInventoryItem(@RequestBody AddInventoryItemDTO dto) {

        try {
            // Validate DTO is not null
            if (dto == null) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "error", "Validation Error",
                        "message", "Request body cannot be null"
                    ));
            }

            // Call service to add inventory item
            Inventory createdInventory = inventoryService.addInventoryItem(dto);

            // Return 201 CREATED response
            return new ResponseEntity<>(createdInventory, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            // Validation error - return 400 BAD REQUEST
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "error", "Validation Error",
                    "message", e.getMessage()
                ));

        } catch (RuntimeException e) {
            // Check if it's a not found error
            String message = e.getMessage();
            if (message != null && message.contains("not found")) {
                // Return 404 NOT FOUND
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "error", "Not Found",
                        "message", message
                    ));
            }
            
            // Check if it's a conflict error (inactive warehouse)
            if (message != null && message.contains("inactive")) {
                // Return 409 CONFLICT
                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                        "error", "Conflict",
                        "message", message
                    ));
            }

            // Generic runtime error - return 400 BAD REQUEST
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "error", "Error",
                    "message", message != null ? message : "An error occurred"
                ));

        } catch (Exception e) {
            // Unexpected error - return 500 INTERNAL SERVER ERROR
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Internal Server Error",
                    "message", "An unexpected error occurred",
                    "details", e.getMessage()
                ));
        }
    }

    /**
     * Updates an existing inventory item with partial updates.
     * 
     * HTTP Method: PATCH
     * Endpoint: PATCH /api/v1/inventories/{inventoryId}
     * Role Required: ADMIN
     * 
     * Allows admins to update specific fields of an inventory item:
     * - Quantity on hand (adjust stock levels)
     * - Expiration date (update shelf life)
     * - Warehouse shelf location (relocate inventory)
     * 
     * Only provided fields will be updated (partial update).
     * 
     * Request Body Format (all fields optional):
     * {
     *     "quantityOnHand": 150,
     *     "expirationDate": "2026-12-31",
     *     "warehouseShelfId": 5
     * }
     * 
     * Success Response:
     * Status: 200 OK
     * Body: Updated Inventory object
     * 
     * Error Responses:
     * - 400 BAD REQUEST: Validation failed (negative quantity, past expiration, etc.)
     * - 404 NOT FOUND: Inventory, shelf, or warehouse not found
     * - 409 CONFLICT: Update violates UNIQUE constraint or cross-warehouse relocation
     * - 500 INTERNAL SERVER ERROR: Unexpected error
     * 
     * @param inventoryId the ID of the inventory item to update
     * @param updateDTO the update data (fields can be null for no change)
     * @return ResponseEntity with updated inventory and 200 status, or error response
     */
    @PatchMapping("/id/{inventoryId}")
    public ResponseEntity<?> updateInventoryItem(
            @PathVariable Integer inventoryId,
            @RequestBody UpdateInventoryItemDTO updateDTO) {

        try {
            // Validate path variable
            if (inventoryId == null || inventoryId <= 0) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "error", "Validation Error",
                        "message", "Inventory ID must be a positive number"
                    ));
            }

            // Validate DTO is not null
            if (updateDTO == null) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "error", "Validation Error",
                        "message", "Request body cannot be null"
                    ));
            }

            // Call service to update inventory item
            Inventory updatedInventory = inventoryService.updateInventoryItem(inventoryId, updateDTO);

            // Return 200 OK response
            return new ResponseEntity<>(updatedInventory, HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            // Entity not found - return 404 NOT FOUND
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "error", "Not Found",
                    "message", e.getMessage()
                ));

        } catch (ConflictException e) {
            // Constraint violation - return 409 CONFLICT
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                    "error", "Conflict",
                    "message", e.getMessage()
                ));

        } catch (IllegalArgumentException e) {
            // Validation error - return 400 BAD REQUEST
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "error", "Validation Error",
                    "message", e.getMessage()
                ));

        } catch (Exception e) {
            // Unexpected error - return 500 INTERNAL SERVER ERROR
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Internal Server Error",
                    "message", "An unexpected error occurred",
                    "details", e.getMessage()
                ));
        }
    }

    /**
     * Checks if an inventory item can be deleted (read-only).
     * 
     * HTTP Method: GET
     * Endpoint: GET /api/v1/inventories/{inventoryId}/deletion-check
     * Role Required: ADMIN
     * 
     * Returns inventory details and deletion eligibility for confirmation dialog.
     * Does NOT delete the inventory.
     * 
     * Success Response (200):
     * {
     *     "inventoryId": 5,
     *     "quantity": 100,
     *     "expirationDate": "2026-12-31",
     *     "warehouseName": "Warehouse A",
     *     "warehouseShelfCode": "SHELF-A1",
     *     "productName": "High-Performance Processor",
     *     "productSku": "PROC-001",
     *     "canDelete": true,
     *     "reason": null
     * }
     * 
     * Error Responses:
     * - 404 NOT FOUND: Inventory doesn't exist
     * - 500 INTERNAL SERVER ERROR: Unexpected error
     * 
     * @param inventoryId the ID of the inventory to check for deletion
     * @return ResponseEntity with deletion check details
     */
    @GetMapping("/id/{inventoryId}/deletion-check")
    public ResponseEntity<?> checkDeletionEligibility(@PathVariable Integer inventoryId) {

        try {
            // Validate path variable
            if (inventoryId == null || inventoryId <= 0) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "error", "Validation Error",
                        "message", "Inventory ID must be a positive number"
                    ));
            }

            // Call service to check deletion eligibility
            DeletionCheckResponse checkResponse = inventoryService.checkDeletionEligibility(inventoryId);

            // Return 200 OK response
            return new ResponseEntity<>(checkResponse, HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            // Inventory not found - return 404 NOT FOUND
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "error", "Not Found",
                    "message", e.getMessage()
                ));

        } catch (Exception e) {
            // Unexpected error - return 500 INTERNAL SERVER ERROR
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Internal Server Error",
                    "message", "An error occurred while checking deletion eligibility",
                    "details", e.getMessage()
                ));
        }
    }

    /**
     * Deletes an inventory item permanently.
     * 
     * HTTP Method: DELETE
     * Endpoint: DELETE /api/v1/inventories/{inventoryId}
     * Role Required: ADMIN
     * 
     * Removes inventory from warehouse. Should only be called after frontend
     * has shown confirmation dialog (using deletion-check endpoint).
     * 
     * Request Body Format (optional):
     * {
     *     "reason": "Expired inventory cleanup"
     * }
     * 
     * Success Response (204):
     * HTTP 204 No Content
     * (no body)
     * 
     * Error Responses:
     * - 404 NOT FOUND: Inventory doesn't exist
     * - 500 INTERNAL SERVER ERROR: Unexpected error
     * 
     * @param inventoryId the ID of the inventory to delete
     * @param deleteDTO optional deletion reason for audit trail
     * @return ResponseEntity with 204 status on success
     */
    @DeleteMapping("/id/{inventoryId}")
    public ResponseEntity<?> deleteInventoryItem(
            @PathVariable Integer inventoryId,
            @RequestBody(required = false) DeleteInventoryItemDTO deleteDTO) {

        try {
            // Validate path variable
            if (inventoryId == null || inventoryId <= 0) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "error", "Validation Error",
                        "message", "Inventory ID must be a positive number"
                    ));
            }

            // Call service to delete inventory item
            inventoryService.deleteInventoryItem(inventoryId, deleteDTO);

            // Return 204 NO CONTENT response (no body needed)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (EntityNotFoundException e) {
            // Inventory not found - return 404 NOT FOUND
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "error", "Not Found",
                    "message", e.getMessage()
                ));

        } catch (Exception e) {
            // Unexpected error - return 500 INTERNAL SERVER ERROR
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Internal Server Error",
                    "message", "An error occurred while deleting inventory",
                    "details", e.getMessage()
                ));
        }
    }

    /**
     * Retrieves all inventory items in a warehouse.
     * 
     * HTTP Method: GET
     * Endpoint: GET /api/v1/inventories/warehouses/id/{warehouseId}
     * Role Required: ADMIN
     * 
     * Returns a complete list of all inventory items stored in a specific warehouse,
     * with product, category, and shelf information for each item.
     * Results are sorted by product name, then expiration date.
     * 
     * Success Response (200):
     * [
     *     {
     *         "inventoryId": 1,
     *         "quantityOnHand": 50,
     *         "expirationDate": "2026-12-31",
     *         "productName": "High-Performance Processor",
     *         "productSku": "PROC-001",
     *         "categoryName": "Processors",
     *         "warehouseName": "Warehouse A",
     *         "warehouseShelfCode": "RACK-A1",
     *         ...
     *     }
     * ]
     * 
     * Error Responses:
     * - 400 BAD REQUEST: Invalid warehouseId
     * - 404 NOT FOUND: Warehouse doesn't exist
     * - 500 INTERNAL SERVER ERROR: Unexpected error
     * 
     * @param warehouseId the ID of the warehouse to view inventory from
     * @return ResponseEntity with list of inventory items and 200 status
     */
    @GetMapping("/warehouses/id/{warehouseId}")
    public ResponseEntity<?> viewAllInventory(@PathVariable Integer warehouseId) {
        try {
            // Validate path variable
            if (warehouseId == null || warehouseId <= 0) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "error", "Validation Error",
                        "message", "Warehouse ID must be a positive number"
                    ));
            }

            // Call service to get all inventory items
            List<ViewInventoryItemDTO> inventoryItems = inventoryService.viewAllInventoryByWarehouse(warehouseId);

            // Return 200 OK response
            return new ResponseEntity<>(inventoryItems, HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            // Warehouse not found - return 404 NOT FOUND
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "error", "Not Found",
                    "message", e.getMessage()
                ));

        } catch (Exception e) {
            // Unexpected error - return 500 INTERNAL SERVER ERROR
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Internal Server Error",
                    "message", "An error occurred while retrieving inventory",
                    "details", e.getMessage()
                ));
        }
    }

    /**
     * Searches inventory items by product name within a warehouse.
     * 
     * HTTP Method: GET
     * Endpoint: GET /api/v1/inventories/warehouses/id/{warehouseId}/search/name
     * Role Required: ADMIN
     * 
     * Query Parameter:
     * - name (required): Product name or partial name to search for (case-insensitive)
     * 
     * Returns all inventory items matching the product name search in the specified warehouse.
     * Performs partial matching (e.g., searching "proces" finds "High-Performance Processor").
     * 
     * Example Request:
     * GET /api/v1/inventories/warehouse/1/search/name?name=processor
     * 
     * Success Response (200): Array of matching ViewInventoryItemDTO objects
     * 
     * Error Responses:
     * - 400 BAD REQUEST: Invalid warehouseId or empty name parameter
     * - 404 NOT FOUND: Warehouse doesn't exist
     * - 500 INTERNAL SERVER ERROR: Unexpected error
     * 
     * @param warehouseId the ID of the warehouse to search in
     * @param name the product name to search for (required)
     * @return ResponseEntity with matching inventory items and 200 status
     */
    @GetMapping("/warehouses/id/{warehouseId}/search/name")
    public ResponseEntity<?> searchByProductName(
            @PathVariable Integer warehouseId,
            @RequestParam String name) {
        try {
            // Validate path variable
            if (warehouseId == null || warehouseId <= 0) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "error", "Validation Error",
                        "message", "Warehouse ID must be a positive number"
                    ));
            }

            // Call service to search by product name
            List<ViewInventoryItemDTO> results = inventoryService.searchByProductName(warehouseId, name);

            // Return 200 OK response
            return new ResponseEntity<>(results, HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            // Warehouse not found - return 404 NOT FOUND
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "error", "Not Found",
                    "message", e.getMessage()
                ));

        } catch (IllegalArgumentException e) {
            // Validation error - return 400 BAD REQUEST
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "error", "Validation Error",
                    "message", e.getMessage()
                ));

        } catch (Exception e) {
            // Unexpected error - return 500 INTERNAL SERVER ERROR
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Internal Server Error",
                    "message", "An error occurred while searching inventory",
                    "details", e.getMessage()
                ));
        }
    }

    /**
     * Searches inventory items by product SKU within a warehouse.
     * 
     * HTTP Method: GET
     * Endpoint: GET /api/v1/inventories/warehouse/{warehouseId}/search/sku
     * Role Required: ADMIN
     * 
     * Query Parameter:
     * - sku (required): Product SKU or partial SKU to search for (case-insensitive)
     * 
     * Returns all inventory items matching the product SKU search in the specified warehouse.
     * Performs partial matching (e.g., searching "PROC" finds "PROC-001", "PROC-002").
     * 
     * Example Request:
     * GET /api/v1/inventories/warehouse/1/search/sku?sku=PROC
     * 
     * Success Response (200): Array of matching ViewInventoryItemDTO objects
     * 
     * Error Responses:
     * - 400 BAD REQUEST: Invalid warehouseId or empty sku parameter
     * - 404 NOT FOUND: Warehouse doesn't exist
     * - 500 INTERNAL SERVER ERROR: Unexpected error
     * 
     * @param warehouseId the ID of the warehouse to search in
     * @param sku the product SKU to search for (required)
     * @return ResponseEntity with matching inventory items and 200 status
     */
    @GetMapping("/warehouses/id/{warehouseId}/search/sku")
    public ResponseEntity<?> searchByProductSku(
            @PathVariable Integer warehouseId,
            @RequestParam String sku) {
        try {
            // Validate path variable
            if (warehouseId == null || warehouseId <= 0) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "error", "Validation Error",
                        "message", "Warehouse ID must be a positive number"
                    ));
            }

            // Call service to search by product SKU
            List<ViewInventoryItemDTO> results = inventoryService.searchByProductSku(warehouseId, sku);

            // Return 200 OK response
            return new ResponseEntity<>(results, HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            // Warehouse not found - return 404 NOT FOUND
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "error", "Not Found",
                    "message", e.getMessage()
                ));

        } catch (IllegalArgumentException e) {
            // Validation error - return 400 BAD REQUEST
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "error", "Validation Error",
                    "message", e.getMessage()
                ));

        } catch (Exception e) {
            // Unexpected error - return 500 INTERNAL SERVER ERROR
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Internal Server Error",
                    "message", "An error occurred while searching inventory",
                    "details", e.getMessage()
                ));
        }
    }

    /**
     * Filters inventory items by category within a warehouse.
     * 
     * HTTP Method: GET
     * Endpoint: GET /api/v1/inventories/warehouse/{warehouseId}/filter/category
     * Role Required: ADMIN
     * 
     * Query Parameter:
     * - categoryId (required): Category ID to filter by
     * 
     * Returns all inventory items in a specific category within the specified warehouse.
     * 
     * Example Request:
     * GET /api/v1/inventories/warehouse/1/filter/category?categoryId=2
     * 
     * Success Response (200): Array of matching ViewInventoryItemDTO objects
     * 
     * Error Responses:
     * - 400 BAD REQUEST: Invalid warehouseId or categoryId
     * - 404 NOT FOUND: Warehouse doesn't exist
     * - 500 INTERNAL SERVER ERROR: Unexpected error
     * 
     * @param warehouseId the ID of the warehouse to filter in
     * @param categoryId the category ID to filter by (required)
     * @return ResponseEntity with matching inventory items and 200 status
     */
    @GetMapping("/warehouses/id/{warehouseId}/filter/category")
    public ResponseEntity<?> filterByCategory(
            @PathVariable Integer warehouseId,
            @RequestParam Integer categoryId) {
        try {
            // Validate path variable
            if (warehouseId == null || warehouseId <= 0) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "error", "Validation Error",
                        "message", "Warehouse ID must be a positive number"
                    ));
            }

            // Call service to filter by category
            List<ViewInventoryItemDTO> results = inventoryService.filterByCategory(warehouseId, categoryId);

            // Return 200 OK response
            return new ResponseEntity<>(results, HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            // Warehouse not found - return 404 NOT FOUND
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "error", "Not Found",
                    "message", e.getMessage()
                ));

        } catch (IllegalArgumentException e) {
            // Validation error - return 400 BAD REQUEST
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "error", "Validation Error",
                    "message", e.getMessage()
                ));

        } catch (Exception e) {
            // Unexpected error - return 500 INTERNAL SERVER ERROR
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Internal Server Error",
                    "message", "An error occurred while filtering inventory",
                    "details", e.getMessage()
                ));
        }
    }

    /**
     * Advanced search with multiple optional filter options.
     * 
     * HTTP Method: GET
     * Endpoint: GET /api/v1/inventories/warehouse/{warehouseId}/search
     * Role Required: ADMIN
     * 
     * Query Parameters (at least one required):
     * - name (optional): Product name or partial name to search for
     * - sku (optional): Product SKU or partial SKU to search for
     * - categoryId (optional): Category ID to filter by
     * 
     * Returns all inventory items matching ANY of the provided criteria (OR logic).
     * If multiple parameters are provided, results include items matching any filter.
     * Results are sorted by product name, then expiration date.
     * 
     * Example Requests:
     * GET /api/v1/inventories/warehouse/1/search?name=processor
     * GET /api/v1/inventories/warehouse/1/search?sku=PROC&categoryId=2
     * GET /api/v1/inventories/warehouse/1/search?name=widget&sku=WID&categoryId=3
     * 
     * Success Response (200): Array of matching ViewInventoryItemDTO objects
     * 
     * Error Responses:
     * - 400 BAD REQUEST: Invalid warehouseId, no filter parameters provided, or invalid categoryId
     * - 404 NOT FOUND: Warehouse doesn't exist
     * - 500 INTERNAL SERVER ERROR: Unexpected error
     * 
     * @param warehouseId the ID of the warehouse to search in
     * @param name optional product name to search for
     * @param sku optional product SKU to search for
     * @param categoryId optional category ID to filter by
     * @return ResponseEntity with matching inventory items and 200 status
     */
    @GetMapping("/warehouses/id/{warehouseId}/search/advanced")
    public ResponseEntity<?> advancedSearch(
            @PathVariable Integer warehouseId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) Integer categoryId) {
        try {
            // Validate path variable
            if (warehouseId == null || warehouseId <= 0) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "error", "Validation Error",
                        "message", "Warehouse ID must be a positive number"
                    ));
            }

            // Call service to perform advanced search
            List<ViewInventoryItemDTO> results = inventoryService.advancedSearch(
                warehouseId,
                name,
                sku,
                categoryId
            );

            // Return 200 OK response
            return new ResponseEntity<>(results, HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            // Warehouse not found - return 404 NOT FOUND
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "error", "Not Found",
                    "message", e.getMessage()
                ));

        } catch (IllegalArgumentException e) {
            // Validation error - return 400 BAD REQUEST
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "error", "Validation Error",
                    "message", e.getMessage()
                ));

        } catch (Exception e) {
            // Unexpected error - return 500 INTERNAL SERVER ERROR
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Internal Server Error",
                    "message", "An error occurred while searching inventory",
                    "details", e.getMessage()
                ));
        }
    }

    /**
     * Transfers inventory items from one warehouse to another.
     * 
     * HTTP Method: POST
     * Endpoint: POST /api/v1/inventories/transfer
     * Role Required: ADMIN
     * 
     * Validates:
     * - Source warehouse has sufficient inventory
     * - Destination warehouse has sufficient capacity
     * - Both warehouses are active
     * - Quantity is positive
     * - Source and destination are different
     * 
     * Request Body Format:
     * {
     *     "productId": 5,
     *     "quantity": 50,
     *     "sourceWarehouseId": 1,
     *     "destinationWarehouseId": 2
     * }
     * 
     * Success Response:
     * Status: 201 CREATED
     * Body: TransferInventoryResponseDTO with transfer details
     * Example:
     * {
     *     "transferId": 10,
     *     "productId": 5,
     *     "productName": "GPU Memory Module",
     *     "quantity": 50,
     *     "sourceWarehouseId": 1,
     *     "sourceWarehouseName": "Main Hub",
     *     "destinationWarehouseId": 2,
     *     "destinationWarehouseName": "Regional Center",
     *     "status": "PENDING",
     *     "createdAt": "2025-11-17T14:30:00Z"
     * }
     * 
     * Error Responses:
     * - 400 BAD REQUEST: Validation failed (invalid quantity, same source/dest, etc.)
     * - 404 NOT FOUND: Warehouse or product not found
     * - 409 CONFLICT: Inactive warehouse, insufficient inventory, or insufficient capacity
     * - 500 INTERNAL SERVER ERROR: Unexpected error
     * 
     * @param requestDTO The DTO containing transfer details
     * @return ResponseEntity with created transfer and 201 status, or error response
     */
    @PostMapping("/transfer")
    public ResponseEntity<?> transferInventory(@RequestBody TransferInventoryRequestDTO requestDTO) {
        
        try {
            // Validate DTO is not null
            if (requestDTO == null) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "error", "Validation Error",
                        "message", "Request body cannot be null"
                    ));
            }

            // Call service to transfer inventory
            TransferInventoryResponseDTO result = inventoryService.transferInventory(requestDTO);

            // Return 201 CREATED response
            return new ResponseEntity<>(result, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            // Validation error - return 400 BAD REQUEST
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "error", "Validation Error",
                    "message", e.getMessage()
                ));

        } catch (EntityNotFoundException e) {
            // Entity not found - return 404 NOT FOUND
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "error", "Not Found",
                    "message", e.getMessage()
                ));

        } catch (ConflictException e) {
            // Conflict error (inactive warehouse, insufficient inventory/capacity) - return 409 CONFLICT
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                    "error", "Conflict",
                    "message", e.getMessage()
                ));

        } catch (Exception e) {
            // Unexpected error - return 500 INTERNAL SERVER ERROR
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Internal Server Error",
                    "message", "An error occurred while transferring inventory",
                    "details", e.getMessage()
                ));
        }
    }
}
