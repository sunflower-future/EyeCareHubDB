package com.example.EyeCareHubDB.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.EyeCareHubDB.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Optional<Category> findBySlug(String slug);
    
    Optional<Category> findByName(String name);
    
    List<Category> findByParentIdOrderByDisplayOrder(Long parentId);
    
    List<Category> findByIsActiveTrue();
    
    List<Category> findByIsActiveTrueOrderByDisplayOrder();
    
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL ORDER BY c.displayOrder")
    List<Category> findRootCategories();
    
    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId ORDER BY c.displayOrder")
    List<Category> findSubCategories(@Param("parentId") Long parentId);
    
    boolean existsBySlug(String slug);
}
