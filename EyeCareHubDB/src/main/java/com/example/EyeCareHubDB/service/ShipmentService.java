package com.example.EyeCareHubDB.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.EyeCareHubDB.dto.ShipmentCreateRequest;
import com.example.EyeCareHubDB.dto.ShipmentDTO;
import com.example.EyeCareHubDB.entity.AuditLog;
import com.example.EyeCareHubDB.entity.Order;
import com.example.EyeCareHubDB.entity.Shipment;
import com.example.EyeCareHubDB.mapper.ShipmentMapper;
import com.example.EyeCareHubDB.repository.OrderRepository;
import com.example.EyeCareHubDB.repository.ShipmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ShipmentService {

        private final ShipmentRepository shipmentRepository;
        private final OrderRepository orderRepository;
        private final AuditLogService auditLogService;
        private final ShipmentMapper shipmentMapper;

        public ShipmentDTO createShipment(Long orderId, ShipmentCreateRequest request) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

                Shipment shipment = shipmentMapper.toEntity(request);
                shipment.setOrder(order);
                shipment.setRecipientName(order.getAccount().getCustomer() != null
                                ? order.getAccount().getCustomer().getFirstName() + " "
                                                + order.getAccount().getCustomer().getLastName()
                                : order.getAccount().getEmail());
                shipment.setRecipientPhone(order.getPhoneNumber());
                shipment.setShippingAddress(order.getShippingAddress());

                Shipment saved = shipmentRepository.save(shipment);
                auditLogService.log("Shipment", saved.getId(), AuditLog.AuditAction.CREATE, null,
                                "Created for order " + orderId);
                return shipmentMapper.toDTO(saved);
        }

        public ShipmentDTO getShipmentById(Long id) {
                return shipmentRepository.findById(id)
                                .map(shipmentMapper::toDTO)
                                .orElseThrow(() -> new RuntimeException("Shipment not found: " + id));
        }

        public List<ShipmentDTO> getShipmentsByOrderId(Long orderId) {
                return shipmentRepository.findByOrderId(orderId).stream()
                                .map(shipmentMapper::toDTO)
                                .collect(Collectors.toList());
        }

        public ShipmentDTO updateShipmentStatus(Long id, String newStatus) {
                Shipment shipment = shipmentRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Shipment not found: " + id));

                String oldStatus = shipment.getStatus().name();
                Shipment.ShipmentStatus status = Shipment.ShipmentStatus.valueOf(newStatus);
                shipment.setStatus(status);

                if (status == Shipment.ShipmentStatus.DELIVERED) {
                        shipment.setActualDeliveryDate(java.time.LocalDate.now());
                }

                Shipment updated = shipmentRepository.save(shipment);
                auditLogService.log("Shipment", id, AuditLog.AuditAction.STATUS_CHANGE, oldStatus, newStatus);
                return shipmentMapper.toDTO(updated);
        }

        public ShipmentDTO updateTracking(Long id, String trackingNumber) {
                Shipment shipment = shipmentRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Shipment not found: " + id));
                shipment.setTrackingNumber(trackingNumber);
                return shipmentMapper.toDTO(shipmentRepository.save(shipment));
        }

        public Page<ShipmentDTO> getAllShipments(String query, String status, Pageable pageable) {
                Shipment.ShipmentStatus shipmentStatus = status != null ? Shipment.ShipmentStatus.valueOf(status)
                                : null;
                return shipmentRepository.searchShipments(query, shipmentStatus, pageable).map(shipmentMapper::toDTO);
        }
}
