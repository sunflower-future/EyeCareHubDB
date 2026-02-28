package com.example.EyeCareHubDB.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.EyeCareHubDB.entity.InventoryLocation;

@Repository
public interface InventoryLocationRepository extends JpaRepository<InventoryLocation, Long> {

    List<InventoryLocation> findByIsActiveTrue();

    List<InventoryLocation> findByType(InventoryLocation.LocationType type);

    boolean existsByCode(String code);
}
