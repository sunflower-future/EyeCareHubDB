package com.example.EyeCareHubDB.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.EyeCareHubDB.entity.InventoryStock;

@Repository
public interface InventoryStockRepository extends JpaRepository<InventoryStock, Long> {

    List<InventoryStock> findByProductVariantId(Long productVariantId);

    List<InventoryStock> findByLocationId(Long locationId);

    Optional<InventoryStock> findByProductVariantIdAndLocationId(Long productVariantId, Long locationId);

    @Query("SELECT SUM(s.onHandQuantity) FROM InventoryStock s WHERE s.productVariant.id = :variantId")
    Integer getTotalOnHandByVariantId(@Param("variantId") Long variantId);

    @Query("SELECT s FROM InventoryStock s WHERE (s.onHandQuantity - s.reservedQuantity) <= :threshold")
    List<InventoryStock> findLowStock(@Param("threshold") int threshold);
}
