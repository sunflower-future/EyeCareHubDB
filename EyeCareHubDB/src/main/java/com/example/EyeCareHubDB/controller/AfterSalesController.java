package com.example.EyeCareHubDB.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.EyeCareHubDB.dto.AfterSalesCaseCreateRequest;
import com.example.EyeCareHubDB.dto.AfterSalesCaseDTO;
import com.example.EyeCareHubDB.service.AfterSalesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/after-sales")
@RequiredArgsConstructor
@Tag(name = "After-Sales", description = "Quản lý hậu mãi: đổi trả (RETURN), bảo hành (WARRANTY), hoàn tiền (REFUND). "
                + "Status flow: OPEN → IN_REVIEW → APPROVED/REJECTED → RESOLVED → CLOSED")
public class AfterSalesController {

        private final AfterSalesService afterSalesService;

        @PostMapping
        @Operation(summary = "Tạo yêu cầu hậu mãi", description = "Customer tạo yêu cầu hậu mãi cho đơn hàng đã giao (DELIVERED). "
                        + "type: RETURN (đổi trả), WARRANTY (bảo hành), REFUND (hoàn tiền). "
                        + "Gửi: orderId (bắt buộc), orderItemId (nếu chỉ 1 item), reason, description, imageUrls (ảnh lỗi). "
                        + "Status mặc định: OPEN. Tự lấy account từ token JWT.")
        public ResponseEntity<AfterSalesCaseDTO> createCase(@RequestBody AfterSalesCaseCreateRequest request) {
                return ResponseEntity.ok(afterSalesService.createCase(request));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Chi tiết case hậu mãi", description = "Trả về đầy đủ: orderNumber, productName, accountEmail, type, reason, "
                        + "description, imageUrls, resolution, refundAmount, status, assignedStaff, timestamps.")
        public ResponseEntity<AfterSalesCaseDTO> getCaseById(@PathVariable Long id) {
                return ResponseEntity.ok(afterSalesService.getCaseById(id));
        }

        @GetMapping("/my-cases")
        @Operation(summary = "Cases hậu mãi của tôi (phân trang)", description = "Trả về tất cả after-sales cases của current user. "
                        + "FE dùng cho trang 'Yêu cầu hậu mãi' / 'My Returns' của customer.")
        public ResponseEntity<Page<AfterSalesCaseDTO>> getMyCases(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size) {
                return ResponseEntity.ok(afterSalesService.getMyCases(
                                PageRequest.of(page, size, Sort.by("createdAt").descending())));
        }

        @PutMapping("/{id}/status")
        @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
        @Operation(summary = "Cập nhật trạng thái case (ADMIN/STAFF)", description = "Status hợp lệ: OPEN, IN_REVIEW, APPROVED, REJECTED, RESOLVED, CLOSED. "
                        + "Flow chuẩn: OPEN → IN_REVIEW → APPROVED → RESOLVED → CLOSED. "
                        + "Hoặc: OPEN → IN_REVIEW → REJECTED → CLOSED. Có audit log.")
        public ResponseEntity<AfterSalesCaseDTO> updateCaseStatus(
                        @PathVariable Long id,
                        @RequestParam String status) {
                return ResponseEntity.ok(afterSalesService.updateCaseStatus(id, status));
        }

        @PutMapping("/{id}/resolve")
        @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
        @Operation(summary = "Giải quyết case hậu mãi (ADMIN/STAFF)", description = "Staff viết resolution (cách giải quyết), tự động set status = RESOLVED, "
                        + "ghi nhận resolvedAt và assignedStaff. "
                        + "VD resolution: 'Đã gửi sản phẩm thay thế, mã vận đơn: GHN123456'.")
        public ResponseEntity<AfterSalesCaseDTO> resolveCase(
                        @PathVariable Long id,
                        @RequestBody Map<String, String> body) {
                return ResponseEntity.ok(afterSalesService.resolveCase(id, body.get("resolution")));
        }

        @GetMapping("/admin")
        @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
        @Operation(summary = "Tất cả cases hậu mãi (ADMIN/STAFF, phân trang + filter)", description = "Filter: status (OPEN/IN_REVIEW/APPROVED/REJECTED/RESOLVED/CLOSED), type (RETURN/WARRANTY/REFUND), query (tìm reason/email/orderNumber).")
        public ResponseEntity<Page<AfterSalesCaseDTO>> getAllCases(
                        @RequestParam(required = false) String query,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String type,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size) {
                return ResponseEntity.ok(afterSalesService.getAllCases(query, status, type,
                                PageRequest.of(page, size, Sort.by("createdAt").descending())));
        }
}
