package com.example.EyeCareHubDB.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.EyeCareHubDB.dto.InventoryLocationDTO;
import com.example.EyeCareHubDB.dto.InventoryStockDTO;
import com.example.EyeCareHubDB.dto.StockAdjustRequest;
import com.example.EyeCareHubDB.service.InventoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Quản lý kho hàng và tồn kho: locations (kho/cửa hàng/nhà cung cấp), "
        + "stock theo variant + location, reserve/release stock khi checkout/cancel")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/locations")
    @Operation(summary = "Danh sách tất cả kho hàng đang hoạt động", description = "Trả về danh sách các kho/cửa hàng/nhà cung cấp (type: WAREHOUSE/STORE/SUPPLIER) "
            + "đang active. FE dùng để hiển thị dropdown chọn kho khi quản lý tồn kho.")
    public ResponseEntity<List<InventoryLocationDTO>> getAllLocations() {
        return ResponseEntity.ok(inventoryService.getAllLocations());
    }

    @PostMapping("/locations")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo kho hàng mới (ADMIN)", description = "Tạo location mới. type: WAREHOUSE (kho chính), STORE (cửa hàng), SUPPLIER (nhà cung cấp). "
            + "code phải unique, dùng để định danh nhanh (VD: WH-HCM-01).")
    public ResponseEntity<InventoryLocationDTO> createLocation(@RequestBody InventoryLocationDTO request) {
        return ResponseEntity.ok(inventoryService.createLocation(request));
    }

    @PutMapping("/locations/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật thông tin kho hàng (ADMIN)", description = "Cập nhật tên, địa chỉ, loại, trạng thái hoạt động của kho. Chỉ gửi field cần thay đổi.")
    public ResponseEntity<InventoryLocationDTO> updateLocation(@PathVariable Long id,
            @RequestBody InventoryLocationDTO request) {
        return ResponseEntity.ok(inventoryService.updateLocation(id, request));
    }

    @GetMapping("/stocks/variant/{variantId}")
    @Operation(summary = "Tồn kho theo product variant", description = "Trả về tồn kho của 1 variant tại tất cả các locations. "
            + "Mỗi record chứa: onHandQuantity, reservedQuantity, availableQuantity (= onHand - reserved). "
            + "FE dùng để check stock availability trước khi cho add to cart.")
    public ResponseEntity<List<InventoryStockDTO>> getStocksByVariantId(@PathVariable Long variantId) {
        return ResponseEntity.ok(inventoryService.getStocksByVariantId(variantId));
    }

    @GetMapping("/stocks/location/{locationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Tồn kho theo location (ADMIN/STAFF)", description = "Xem toàn bộ tồn kho tại 1 kho. Bao gồm thông tin variant (SKU, tên sản phẩm) "
            + "và số lượng (onHand, reserved, available).")
    public ResponseEntity<List<InventoryStockDTO>> getStocksByLocationId(@PathVariable Long locationId) {
        return ResponseEntity.ok(inventoryService.getStocksByLocationId(locationId));
    }

    @PutMapping("/stocks/adjust")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Điều chỉnh tồn kho (ADMIN/STAFF)", description = "Cập nhật số lượng onHand và/hoặc reserved cho 1 variant tại 1 location. "
            + "Nếu chưa có record stock cho cặp variant-location, sẽ tự tạo mới. "
            + "Dùng khi nhập hàng, kiểm kê, hoặc điều chỉnh thủ công. Có audit log.")
    public ResponseEntity<InventoryStockDTO> adjustStock(@RequestBody StockAdjustRequest request) {
        return ResponseEntity.ok(inventoryService.adjustStock(request));
    }

    @GetMapping("/stocks/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Cảnh báo tồn kho thấp (ADMIN/STAFF)", description = "Trả về danh sách các stock record có availableQuantity <= threshold (mặc định 10). "
            + "FE dùng để hiển thị warning/notification cho staff.")
    public ResponseEntity<List<InventoryStockDTO>> getLowStock(@RequestParam(defaultValue = "10") int threshold) {
        return ResponseEntity.ok(inventoryService.getLowStock(threshold));
    }
}
