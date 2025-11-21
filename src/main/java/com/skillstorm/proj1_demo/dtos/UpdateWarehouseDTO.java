package com.skillstorm.proj1_demo.dtos;

/**
 * DTO for updating warehouse information.
 * All fields are optional - only provided fields will be updated.
 * This ensures partial updates are supported.
 */
public class UpdateWarehouseDTO {

    private String name;              // Optional: new warehouse name
    private String location;          // Optional: new warehouse location
    private Integer capacity;         // Optional: new maximum capacity
    private Boolean isActive;         // Optional: new active status

    // ========== Constructors ==========
    /**
     * No-argument constructor for JSON deserialization
     */
    public UpdateWarehouseDTO() {
    }

    /**
     * Full-argument constructor
     * 
     * @param name Warehouse name
     * @param location Warehouse location
     * @param capacity Maximum capacity
     * @param isActive Active status
     */
    public UpdateWarehouseDTO(String name, String location,
     Integer capacity, Boolean isActive) {
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.isActive = isActive;
    }

     // ========== Getters ==========

    /**
     * Get warehouse name
     * @return warehouse name or null if not being updated
     */
    public String getName() {
        return name;
    }

    /**
     * Get warehouse location
     * @return warehouse location or null if not being updated
     */
    public String getLocation() {
        return location;
    }

    /**
     * Get warehouse capacity
     * @return warehouse capacity or null if not being updated
     */
    public Integer getCapacity() {
        return capacity;
    }

    /**
     * Get active status
     * @return active status or null if not being updated
     */
    public Boolean getIsActive() {
        return isActive;
    }

    // ========== Setters ==========

    /**
     * Set warehouse name
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set warehouse location
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Set warehouse capacity
     * @param capacity the capacity to set
     */
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    /**
     * Set active status
     * @param isActive the status to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    // ========== toString ==========

    @Override
    public String toString() {
        return "UpdateWarehouseDTO [name=" + name + ", location=" + location + ", capacity=" 
            + capacity + ", isActive=" + isActive + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((capacity == null) ? 0 : capacity.hashCode());
        result = prime * result + ((isActive == null) ? 0 : isActive.hashCode());
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
        UpdateWarehouseDTO other = (UpdateWarehouseDTO) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        if (capacity == null) {
            if (other.capacity != null)
                return false;
        } else if (!capacity.equals(other.capacity))
            return false;
        if (isActive == null) {
            if (other.isActive != null)
                return false;
        } else if (!isActive.equals(other.isActive))
            return false;
        return true;
    }

    

}
