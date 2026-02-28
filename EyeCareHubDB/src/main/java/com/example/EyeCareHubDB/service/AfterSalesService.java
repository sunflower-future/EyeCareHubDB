package com.example.EyeCareHubDB.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.EyeCareHubDB.dto.AfterSalesCaseCreateRequest;
import com.example.EyeCareHubDB.dto.AfterSalesCaseDTO;
import com.example.EyeCareHubDB.entity.Account;
import com.example.EyeCareHubDB.entity.AfterSalesCase;
import com.example.EyeCareHubDB.entity.AuditLog;
import com.example.EyeCareHubDB.entity.Order;
import com.example.EyeCareHubDB.entity.OrderItem;
import com.example.EyeCareHubDB.mapper.AfterSalesCaseMapper;
import com.example.EyeCareHubDB.repository.AfterSalesCaseRepository;
import com.example.EyeCareHubDB.repository.OrderItemRepository;
import com.example.EyeCareHubDB.repository.OrderRepository;
import com.example.EyeCareHubDB.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AfterSalesService {

    private final AfterSalesCaseRepository caseRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AuditLogService auditLogService;
    private final AfterSalesCaseMapper afterSalesCaseMapper;

    public AfterSalesCaseDTO createCase(AfterSalesCaseCreateRequest request) {
        Account currentUser = SecurityUtils.getCurrentAccount();
        if (currentUser == null)
            throw new RuntimeException("Not authenticated");

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + request.getOrderId()));

        OrderItem orderItem = null;
        if (request.getOrderItemId() != null) {
            orderItem = orderItemRepository.findById(request.getOrderItemId())
                    .orElseThrow(() -> new RuntimeException("Order item not found: " + request.getOrderItemId()));
        }

        AfterSalesCase afterCase = afterSalesCaseMapper.toEntity(request);
        afterCase.setOrder(order);
        afterCase.setOrderItem(orderItem);
        afterCase.setAccount(currentUser);
        afterCase.setType(AfterSalesCase.CaseType.valueOf(request.getType()));

        AfterSalesCase saved = caseRepository.save(afterCase);
        auditLogService.log("AfterSalesCase", saved.getId(), AuditLog.AuditAction.CREATE, null, request.getType());
        return afterSalesCaseMapper.toDTO(saved);
    }

    public AfterSalesCaseDTO getCaseById(Long id) {
        return caseRepository.findById(id)
                .map(afterSalesCaseMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("After-sales case not found: " + id));
    }

    public Page<AfterSalesCaseDTO> getMyCases(Pageable pageable) {
        Account currentUser = SecurityUtils.getCurrentAccount();
        if (currentUser == null)
            throw new RuntimeException("Not authenticated");
        return caseRepository.findByAccountId(currentUser.getId(), pageable).map(afterSalesCaseMapper::toDTO);
    }

    public AfterSalesCaseDTO updateCaseStatus(Long id, String newStatus) {
        AfterSalesCase afterCase = caseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found: " + id));

        String oldStatus = afterCase.getStatus().name();
        afterCase.setStatus(AfterSalesCase.CaseStatus.valueOf(newStatus));

        AfterSalesCase updated = caseRepository.save(afterCase);
        auditLogService.log("AfterSalesCase", id, AuditLog.AuditAction.STATUS_CHANGE, oldStatus, newStatus);
        return afterSalesCaseMapper.toDTO(updated);
    }

    public AfterSalesCaseDTO resolveCase(Long id, String resolution) {
        AfterSalesCase afterCase = caseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found: " + id));

        Account staff = SecurityUtils.getCurrentAccount();
        afterCase.setResolution(resolution);
        afterCase.setAssignedStaff(staff);
        afterCase.setStatus(AfterSalesCase.CaseStatus.RESOLVED);
        afterCase.setResolvedAt(LocalDateTime.now());

        AfterSalesCase updated = caseRepository.save(afterCase);
        auditLogService.log("AfterSalesCase", id, AuditLog.AuditAction.STATUS_CHANGE, "N/A", "RESOLVED");
        return afterSalesCaseMapper.toDTO(updated);
    }

    public Page<AfterSalesCaseDTO> getAllCases(String query, String status, String type, Pageable pageable) {
        AfterSalesCase.CaseStatus caseStatus = null;
        AfterSalesCase.CaseType caseType = null;
        if (status != null && !status.isEmpty()) {
            try {
                caseStatus = AfterSalesCase.CaseStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (type != null && !type.isEmpty()) {
            try {
                caseType = AfterSalesCase.CaseType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return caseRepository.searchCases(query, caseStatus, caseType, pageable).map(afterSalesCaseMapper::toDTO);
    }
}
