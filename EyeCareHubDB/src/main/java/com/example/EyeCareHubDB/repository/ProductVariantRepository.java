package com.example.EyeCareHubDB.repository;

import com.example.EyeCareHubDB.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    
    List<ProductVariant> findByProductId(Long productId);
    
    List<ProductVariant> findByProductIdAndIsActiveTrue(Long productId);
    
    Optional<ProductVariant> findBySku(String sku);
    
    boolean existsBySku(String sku);
    
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId ORDER BY pv.displayOrder")
    List<ProductVariant> findByProductIdOrderByDisplayOrder(@Param("productId") Long productId);
    
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.sku = :sku AND pv.isActive = true")
    Optional<ProductVariant> findActiveBySkup(@Param("sku") String sku);
    
    @Query("SELECT COALESCE(SUM(pv.stockQuantity), 0) FROM ProductVariant pv WHERE pv.product.id = :productId")
    Integer getTotalStockByProductId(@Param("productId") Long productId);
}
