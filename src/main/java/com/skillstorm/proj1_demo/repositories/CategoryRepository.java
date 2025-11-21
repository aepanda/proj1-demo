package com.skillstorm.proj1_demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillstorm.proj1_demo.models.Category;

/**
 * Repository interface for Category entity.
 * Provides database access and query operations for product categories.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    /**
     * Finds a category by its name.
     * 
     * @param name The category name
     * @return Optional containing the category if found
     */
    public Optional<Category> findByName(String name);

    /**
     * Checks if a category exists by name.
     * 
     * @param name The category name
     * @return true if category exists, false otherwise
     */
    public boolean existsByName(String name);
}
