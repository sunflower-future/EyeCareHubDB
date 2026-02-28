package com.example.EyeCareHubDB.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.EyeCareHubDB.entity.Prescription;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    Optional<Prescription> findByOrderItemId(Long orderItemId);

    List<Prescription> findByOrderItemOrderId(Long orderId);

    boolean existsByOrderItemId(Long orderItemId);

    List<Prescription> findByStatus(Prescription.PrescriptionStatus status);
}
