package com.example.EyeCareHubDB.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.EyeCareHubDB.dto.PrescriptionCreateRequest;
import com.example.EyeCareHubDB.dto.PrescriptionDTO;
import com.example.EyeCareHubDB.service.PrescriptionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/prescriptions")
@RequiredArgsConstructor
@Tag(name = "Prescriptions", description = "Quản lý đơn kính (toa thuốc mắt) cho đơn hàng loại PRESCRIPTION. "
        + "Bao gồm thông số kính: Sphere, Cylinder, Axis, PD cho mắt phải (OD) và mắt trái (OS). "
        + "Staff xác minh (verify/reject) trước khi chuyển sang bước cắt kính.")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping("/order-item/{orderItemId}")
    @Operation(summary = "Tạo đơn kính cho order item", description = "Tạo prescription mới gắn với 1 OrderItem (unique, mỗi item chỉ có 1 prescription). "
            + "Gửi thông số kính: rightSphere, rightCylinder, rightAxis, rightPD, "
            + "leftSphere, leftCylinder, leftAxis, leftPD, addPower. "
            + "Có thể upload ảnh toa (prescriptionImageUrl). Status mặc định: PENDING.")
    public ResponseEntity<PrescriptionDTO> createPrescription(
            @PathVariable Long orderItemId,
            @RequestBody PrescriptionCreateRequest request) {
        return ResponseEntity.ok(prescriptionService.createPrescription(orderItemId, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết đơn kính theo ID", description = "Trả về đầy đủ thông tin prescription: thông số kính OD/OS, "
            + "productName, variantSku, status, staff xác minh, timestamps.")
    public ResponseEntity<PrescriptionDTO> getPrescriptionById(@PathVariable Long id) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(id));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Danh sách đơn kính theo đơn hàng", description = "Trả về tất cả prescriptions của 1 đơn hàng (1 đơn có thể có nhiều item cần prescription). "
            + "FE dùng API này khi hiển thị chi tiết đơn hàng loại PRESCRIPTION.")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptionsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByOrderId(orderId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông số đơn kính", description = "Cập nhật thông tin prescription. Chỉ gửi field cần thay đổi. "
            + "Chỉ cập nhật được khi status chưa VERIFIED.")
    public ResponseEntity<PrescriptionDTO> updatePrescription(@PathVariable Long id,
            @RequestBody PrescriptionCreateRequest request) {
        return ResponseEntity.ok(prescriptionService.updatePrescription(id, request));
    }

    @PutMapping("/{id}/verify")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Staff xác minh đơn kính (ADMIN/STAFF)", description = "Staff kiểm tra và xác minh thông số kính. approved=true → VERIFIED, approved=false → REJECTED. "
            + "Sau khi VERIFIED, đơn hàng có thể chuyển sang bước CUT_LENS trong fulfillment. "
            + "Ghi nhận staff nào verify và thời gian verify.")
    public ResponseEntity<PrescriptionDTO> verifyPrescription(
            @PathVariable Long id,
            @RequestParam boolean approved) {
        return ResponseEntity.ok(prescriptionService.verifyPrescription(id, approved));
    }
}
