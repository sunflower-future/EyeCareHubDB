package com.example.EyeCareHubDB.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@Tag(name = "Cart", description = "Shopping Cart Management APIs")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get current user's cart")
    public ResponseEntity<CartDTO> getCart() {
        return ResponseEntity.ok(cartService.getMyCart());
    }

    @PostMapping("/items")
    @Operation(summary = "Add an item to the cart")
    public ResponseEntity<CartDTO> addItemToCart(@RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addItemToCart(request));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update quantity of an item in the cart")
    public ResponseEntity<CartDTO> updateItemQuantity(
            @PathVariable Long itemId,
            @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItemQuantity(itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove an item from the cart")
    public ResponseEntity<CartDTO> removeItemFromCart(@PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(itemId));
    }

    @DeleteMapping
    @Operation(summary = "Clear the entire cart")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }
}
