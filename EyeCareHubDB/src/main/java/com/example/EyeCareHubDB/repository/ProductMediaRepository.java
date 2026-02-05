package com.example.EyeCareHubDB.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.EyeCareHubDB.entity.ProductMedia;

@Repository
public interface ProductMediaRepository extends JpaRepository<ProductMedia, Long> {
    
    List<ProductMedia> findByProductIdOrderByDisplayOrder(Long productId);
    
    List<ProductMedia> findByProductIdAndType(Long productId, ProductMedia.MediaType type);
    
    Optional<ProductMedia> findByProductIdAndIsPrimaryTrue(Long productId);
    
    @Query("SELECT pm FROM ProductMedia pm WHERE pm.product.id = :productId ORDER BY pm.displayOrder")
    List<ProductMedia> findMediaByProductId(@Param("productId") Long productId);
    
    @Query("SELECT pm FROM ProductMedia pm WHERE pm.product.id = :productId AND pm.isPrimary = true")
    Optional<ProductMedia> findPrimaryMediaByProductId(@Param("productId") Long productId);
}
