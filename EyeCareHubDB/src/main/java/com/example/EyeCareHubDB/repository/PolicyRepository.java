package com.example.EyeCareHubDB.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.EyeCareHubDB.entity.Policy;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    
    Optional<Policy> findByType(Policy.PolicyType type);
    
    Optional<Policy> findBySlug(String slug);
    
    List<Policy> findByIsPublishedTrue();
    
    List<Policy> findByIsPublishedTrueOrderByDisplayOrder();
    
    @Query("SELECT p FROM Policy p WHERE p.isPublished = true ORDER BY p.displayOrder")
    List<Policy> findPublishedPolicies();
    
    @Query("SELECT p FROM Policy p WHERE p.type = :type AND p.isPublished = true")
    Optional<Policy> findPublishedByType(@Param("type") Policy.PolicyType type);
    
    boolean existsByType(Policy.PolicyType type);
    
    boolean existsBySlug(String slug);
}
