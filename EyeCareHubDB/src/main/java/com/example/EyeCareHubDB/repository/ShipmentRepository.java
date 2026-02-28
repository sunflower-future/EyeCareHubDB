package com.example.EyeCareHubDB.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.EyeCareHubDB.entity.Shipment;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    List<Shipment> findByOrderId(Long orderId);

    List<Shipment> findByStatus(Shipment.ShipmentStatus status);

    Page<Shipment> findAll(Pageable pageable);

    @Query("SELECT s FROM Shipment s WHERE " +
            "(:status IS NULL OR s.status = :status) AND " +
            "(:query IS NULL OR s.trackingNumber LIKE %:query% OR s.order.orderNumber LIKE %:query%)")
    Page<Shipment> searchShipments(@Param("query") String query,
            @Param("status") Shipment.ShipmentStatus status,
            Pageable pageable);
}
