package com.skillstorm.proj1_demo.dtos;

import java.time.ZonedDateTime;


/**
 * DTO for Warehouse Dashboard - Aggregates warehouse details with key metrics
 */

public class WarehouseDashboardDTO {

    private int id;
    private String name;
    private String location;
    private int maxCapacity;
    private int currentCapacityUsed;
    private double capacityPercentage;
    private int totalItemsCount;
    private boolean isActive;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    // Constructors
    public WarehouseDashboardDTO() {}
    
    public WarehouseDashboardDTO(int id, String name, String location, 
            int maxCapacity, int currentCapacityUsed, 
            double capacityPercentage, int totalItemsCount, 
            boolean isActive, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.maxCapacity = maxCapacity;
        this.currentCapacityUsed = currentCapacityUsed;
        this.capacityPercentage = capacityPercentage;
        this.totalItemsCount = totalItemsCount;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public WarehouseDashboardDTO(String name, String location, int maxCapacity, int currentCapacityUsed,
            double capacityPercentage, int totalItemsCount, boolean isActive, ZonedDateTime createdAt,
            ZonedDateTime updatedAt) {
        this.name = name;
        this.location = location;
        this.maxCapacity = maxCapacity;
        this.currentCapacityUsed = currentCapacityUsed;
        this.capacityPercentage = capacityPercentage;
        this.totalItemsCount = totalItemsCount;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getCurrentCapacityUsed() {
        return currentCapacityUsed;
    }

    public void setCurrentCapacityUsed(int currentCapacityUsed) {
        this.currentCapacityUsed = currentCapacityUsed;
    }

    public double getCapacityPercentage() {
        return capacityPercentage;
    }

    public void setCapacityPercentage(double capacityPercentage) {
        this.capacityPercentage = capacityPercentage;
    }

    public int getTotalItemsCount() {
        return totalItemsCount;
    }

    public void setTotalItemsCount(int totalItemsCount) {
        this.totalItemsCount = totalItemsCount;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "WarehouseDashboardDTO [id=" + id + ", name=" + name + ", location=" + location + ", maxCapacity="
                + maxCapacity + ", currentCapacityUsed=" + currentCapacityUsed + ", capacityPercentage="
                + capacityPercentage + ", totalItemsCount=" + totalItemsCount + ", isActive=" + isActive + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + maxCapacity;
        result = prime * result + currentCapacityUsed;
        long temp;
        temp = Double.doubleToLongBits(capacityPercentage);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + totalItemsCount;
        result = prime * result + (isActive ? 1231 : 1237);
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
        WarehouseDashboardDTO other = (WarehouseDashboardDTO) obj;
        if (id != other.id)
            return false;
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
        if (maxCapacity != other.maxCapacity)
            return false;
        if (currentCapacityUsed != other.currentCapacityUsed)
            return false;
        if (Double.doubleToLongBits(capacityPercentage) != Double.doubleToLongBits(other.capacityPercentage))
            return false;
        if (totalItemsCount != other.totalItemsCount)
            return false;
        if (isActive != other.isActive)
            return false;
        return true;
    }
    

}
