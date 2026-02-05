package com.example.EyeCareHubDB.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.EyeCareHubDB.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findBySlug(String slug);
    
    Optional<Product> findBySku(String sku);
    
    List<Product> findByCategoryId(Long categoryId);
    
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    
    List<Product> findByIsActiveTrue();
    
    Page<Product> findByIsActiveTrue(Pageable pageable);
    
    Page<Product> findByIsFeaturedTrue(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.isActive = true")
    Page<Product> searchByName(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.isActive = true ORDER BY p.viewCount DESC")
    List<Product> findPopularProductsByCategory(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.viewCount DESC")
    Page<Product> findPopularProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.salePrice IS NOT NULL ORDER BY p.updatedAt DESC")
    Page<Product> findProductsOnSale(Pageable pageable);
    
    boolean existsBySlug(String slug);
    
    boolean existsBySku(String sku);
}
