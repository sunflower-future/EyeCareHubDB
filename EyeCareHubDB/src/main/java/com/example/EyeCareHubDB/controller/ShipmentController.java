package com.example.EyeCareHubDB.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.EyeCareHubDB.dto.ShipmentCreateRequest;
import com.example.EyeCareHubDB.dto.ShipmentDTO;
import com.example.EyeCareHubDB.service.ShipmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
@Tag(name = "Shipments", description = "Quản lý vận đơn: tạo shipment cho đơn hàng, cập nhật tracking/status. "
        + "Status flow: PENDING → PICKED_UP → IN_TRANSIT → OUT_FOR_DELIVERY → DELIVERED | FAILED → RETURNED")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Tạo vận đơn cho đơn hàng (ADMIN/STAFF)", description = "Tạo shipment mới cho order. Tự động lấy recipientName, recipientPhone, shippingAddress từ order. "
            + "Gửi: carrier (hãng vận chuyển), trackingNumber, shippingMethod, shippingFee, estimatedDeliveryDate. "
            + "1 order có thể có nhiều shipments (chia lô giao hàng).")
    public ResponseEntity<ShipmentDTO> createShipment(
            @PathVariable Long orderId,
            @RequestBody ShipmentCreateRequest request) {
        return ResponseEntity.ok(shipmentService.createShipment(orderId, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết vận đơn theo ID", description = "Trả về đầy đủ: carrier, tracking, method, fee, ngày giao dự kiến/thực tế, "
            + "status, thông tin người nhận, mã đơn hàng.")
    public ResponseEntity<ShipmentDTO> getShipmentById(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.getShipmentById(id));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Danh sách vận đơn theo đơn hàng", description = "Trả về tất cả shipments của 1 order. FE dùng khi hiển thị chi tiết đơn hàng, "
            + "tab 'Vận chuyển'. Mỗi shipment có status và tracking riêng.")
    public ResponseEntity<List<ShipmentDTO>> getShipmentsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(shipmentService.getShipmentsByOrderId(orderId));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Cập nhật trạng thái vận đơn (ADMIN/STAFF)", description = "Cập nhật status. Giá trị hợp lệ: PENDING, PICKED_UP, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, FAILED, RETURNED. "
            + "Khi status = DELIVERED, tự động set actualDeliveryDate = today. Có audit log.")
    public ResponseEntity<ShipmentDTO> updateShipmentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(shipmentService.updateShipmentStatus(id, status));
    }

    @PutMapping("/{id}/tracking")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Cập nhật mã tracking vận đơn (ADMIN/STAFF)", description = "Cập nhật mã vận đơn tracking (VD: GHN123456). FE có thể dùng mã này để link sang trang tracking của hãng vận chuyển.")
    public ResponseEntity<ShipmentDTO> updateTracking(
            @PathVariable Long id,
            @RequestParam String trackingNumber) {
        return ResponseEntity.ok(shipmentService.updateTracking(id, trackingNumber));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Tất cả vận đơn (ADMIN/STAFF, phân trang)", description = "Danh sách toàn bộ shipments, hỗ trợ tìm kiếm theo trackingNumber/orderNumber và lọc theo status. "
            + "Phân trang: page (default 0), size (default 20). Sort: mới nhất trước.")
    public ResponseEntity<Page<ShipmentDTO>> getAllShipments(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(shipmentService.getAllShipments(query, status,
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }
}
