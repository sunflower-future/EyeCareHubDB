package com.example.EyeCareHubDB.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.EyeCareHubDB.dto.PromotionCreateRequest;
import com.example.EyeCareHubDB.dto.PromotionDTO;
import com.example.EyeCareHubDB.dto.PromotionValidateRequest;
import com.example.EyeCareHubDB.dto.PromotionValidateResponse;
import com.example.EyeCareHubDB.service.PromotionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
@Tag(name = "Promotions", description = "Quản lý mã khuyến mãi: tạo, validate, áp dụng giảm giá. " +
        "Hỗ trợ 3 loại: PERCENTAGE (giảm %), FIXED_AMOUNT (giảm cố định), FREE_SHIPPING (miễn phí ship)")
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping("/validate")
    @Operation(summary = "Validate & preview mã giảm giá", description = "Kiểm tra mã giảm giá có hợp lệ không (đúng code, còn hạn, chưa hết lượt, đủ giá trị đơn tối thiểu). "
            + "Trả về số tiền giảm thực tế và tổng tiền sau khi áp dụng. FE dùng API này khi user nhập mã vào ô promotion.")
    public ResponseEntity<PromotionValidateResponse> validatePromotion(@RequestBody PromotionValidateRequest request) {
        return ResponseEntity.ok(promotionService.validatePromotion(request));
    }

    @GetMapping("/active")
    @Operation(summary = "Danh sách promotions đang hoạt động", description = "Trả về tất cả promotions đang active, còn trong thời hạn, và chưa hết lượt sử dụng. "
            + "FE có thể show danh sách này cho user chọn áp dụng.")
    public ResponseEntity<List<PromotionDTO>> getActivePromotions() {
        return ResponseEntity.ok(promotionService.getActivePromotions());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết promotion theo ID", description = "Trả về đầy đủ thông tin promotion bao gồm: code, tên, loại giảm giá, giá trị, "
            + "thời hạn, số lượt đã dùng/giới hạn, trạng thái isValid (computed).")
    public ResponseEntity<PromotionDTO> getPromotionById(@PathVariable Long id) {
        return ResponseEntity.ok(promotionService.getPromotionById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Danh sách tất cả promotions (ADMIN/STAFF)", description = "Trả về toàn bộ promotions bao gồm cả inactive. Chỉ ADMIN/STAFF mới xem được.")
    public ResponseEntity<List<PromotionDTO>> getAllPromotions() {
        return ResponseEntity.ok(promotionService.getAllPromotions());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo promotion mới (ADMIN)", description = "Tạo mã khuyến mãi mới. discountType: PERCENTAGE / FIXED_AMOUNT / FREE_SHIPPING. "
            + "Code tự động uppercase. applicableCategoryIds/applicableProductIds: comma-separated IDs.")
    public ResponseEntity<PromotionDTO> createPromotion(@RequestBody PromotionCreateRequest request) {
        return ResponseEntity.ok(promotionService.createPromotion(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật promotion (ADMIN)", description = "Cập nhật thông tin promotion. Chỉ gửi các field cần thay đổi, field null sẽ giữ nguyên.")
    public ResponseEntity<PromotionDTO> updatePromotion(@PathVariable Long id,
            @RequestBody PromotionCreateRequest request) {
        return ResponseEntity.ok(promotionService.updatePromotion(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa promotion (soft delete) (ADMIN)", description = "Soft delete: đặt isActive = false. Promotion vẫn còn trong DB nhưng không thể sử dụng.")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.ok().build();
    }
}
