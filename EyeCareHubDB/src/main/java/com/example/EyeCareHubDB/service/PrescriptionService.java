package com.example.EyeCareHubDB.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.EyeCareHubDB.dto.PrescriptionCreateRequest;
import com.example.EyeCareHubDB.dto.PrescriptionDTO;
import com.example.EyeCareHubDB.entity.Account;
import com.example.EyeCareHubDB.entity.AuditLog;
import com.example.EyeCareHubDB.entity.OrderItem;
import com.example.EyeCareHubDB.entity.Prescription;
import com.example.EyeCareHubDB.mapper.PrescriptionMapper;
import com.example.EyeCareHubDB.repository.OrderItemRepository;
import com.example.EyeCareHubDB.repository.PrescriptionRepository;
import com.example.EyeCareHubDB.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final OrderItemRepository orderItemRepository;
    private final AuditLogService auditLogService;
    private final PrescriptionMapper prescriptionMapper;

    public PrescriptionDTO createPrescription(Long orderItemId, PrescriptionCreateRequest request) {
        if (prescriptionRepository.existsByOrderItemId(orderItemId)) {
            throw new RuntimeException("Prescription already exists for order item: " + orderItemId);
        }

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("Order item not found: " + orderItemId));

        Prescription prescription = prescriptionMapper.toEntity(request);
        prescription.setOrderItem(orderItem);

        Prescription saved = prescriptionRepository.save(prescription);
        auditLogService.log("Prescription", saved.getId(), AuditLog.AuditAction.CREATE, null,
                "Created for orderItem " + orderItemId);
        return prescriptionMapper.toDTO(saved);
    }

    public PrescriptionDTO getPrescriptionById(Long id) {
        return prescriptionRepository.findById(id)
                .map(prescriptionMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Prescription not found: " + id));
    }

    public List<PrescriptionDTO> getPrescriptionsByOrderId(Long orderId) {
        return prescriptionRepository.findByOrderItemOrderId(orderId).stream()
                .map(prescriptionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PrescriptionDTO updatePrescription(Long id, PrescriptionCreateRequest request) {
        Prescription p = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found: " + id));

        prescriptionMapper.updateEntity(p, request);

        Prescription updated = prescriptionRepository.save(p);
        auditLogService.log("Prescription", id, AuditLog.AuditAction.UPDATE, null, null);
        return prescriptionMapper.toDTO(updated);
    }

    public PrescriptionDTO verifyPrescription(Long id, boolean approved) {
        Prescription p = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found: " + id));

        Account staff = SecurityUtils.getCurrentAccount();
        String oldStatus = p.getStatus().name();
        p.setStatus(approved ? Prescription.PrescriptionStatus.VERIFIED : Prescription.PrescriptionStatus.REJECTED);
        p.setVerifiedByStaff(staff);
        p.setVerifiedAt(LocalDateTime.now());

        Prescription updated = prescriptionRepository.save(p);
        auditLogService.log("Prescription", id, AuditLog.AuditAction.STATUS_CHANGE, oldStatus,
                updated.getStatus().name());
        return prescriptionMapper.toDTO(updated);
    }
}
