package com.example.EyeCareHubDB.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.EyeCareHubDB.dto.AddToCartRequest;
import com.example.EyeCareHubDB.dto.CartDTO;
import com.example.EyeCareHubDB.dto.UpdateCartItemRequest;
import com.example.EyeCareHubDB.service.CartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Quản lý giỏ hàng: thêm/sửa/xóa sản phẩm, auto-tính tổng. "
        + "Mỗi user có 1 cart duy nhất. Cart tự tạo khi user thêm item đầu tiên.")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Lấy giỏ hàng hiện tại", description = "Trả về giỏ hàng của user đang login. Bao gồm danh sách items (productName, variantSku, "
            + "unitPrice hiện tại, snapshotPrice lúc thêm, priceChanged nếu giá đổi, "
            + "inStock + availableStock), totalPrice, totalItems. "
            + "FE dùng priceChanged=true để hiển thị cảnh báo 'Giá sản phẩm đã thay đổi'.")
    public ResponseEntity<CartDTO> getCart() {
        return ResponseEntity.ok(cartService.getMyCart());
    }

    @PostMapping("/items")
    @Operation(summary = "Thêm sản phẩm vào giỏ", description = "Thêm 1 product variant vào giỏ. Gửi: productVariantId, quantity. "
            + "Nếu variant đã có trong giỏ → cộng dồn quantity. "
            + "Tự lưu snapshotPrice (giá tại thời điểm thêm vào giỏ) và snapshotProductName. "
            + "Check stock trước khi thêm, throw lỗi nếu hết hàng.")
    public ResponseEntity<CartDTO> addItemToCart(@RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addItemToCart(request));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Cập nhật số lượng item trong giỏ", description = "Cập nhật quantity cho 1 cart item. Check stock trước khi cập nhật. "
            + "Nếu quantity = 0, nên dùng API DELETE thay thế.")
    public ResponseEntity<CartDTO> updateItemQuantity(
            @PathVariable Long itemId,
            @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItemQuantity(itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Xóa 1 item khỏi giỏ", description = "Xóa hoàn toàn 1 item ra khỏi giỏ. Kiểm tra item thuộc giỏ của user hiện tại.")
    public ResponseEntity<CartDTO> removeItemFromCart(@PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(itemId));
    }

    @DeleteMapping
    @Operation(summary = "Xóa toàn bộ giỏ hàng", description = "Clear hết items trong giỏ. Giỏ vẫn tồn tại, chỉ xóa items.")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }
}
