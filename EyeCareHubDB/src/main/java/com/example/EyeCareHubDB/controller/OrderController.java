package com.example.EyeCareHubDB.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.EyeCareHubDB.dto.CheckoutRequest;
import com.example.EyeCareHubDB.dto.OrderDTO;
import com.example.EyeCareHubDB.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "Endpoints for placing and managing orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    @Operation(summary = "Place an order from the current cart")
    public ResponseEntity<OrderDTO> checkout(@Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(orderService.placeOrder(request));
    }

    @PostMapping("/buy-now")
    @Operation(summary = "Buy a single item directly (Buy Now)")
    public ResponseEntity<OrderDTO> buyNow(@Valid @RequestBody com.example.EyeCareHubDB.dto.BuyNowRequest request) {
        return ResponseEntity.ok(orderService.buyNow(request));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order details by ID")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel an order (Owner only, only if status is PENDING - before confirm)")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    @GetMapping
    @Operation(summary = "Get all orders for the current account (Paginated)")
    public ResponseEntity<com.example.EyeCareHubDB.dto.PageResponse<OrderDTO>> getMyOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getMyOrders(status, page, size));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Get all orders with optional filter/search (Admin/Staff only, Paginated)")
    public ResponseEntity<com.example.EyeCareHubDB.dto.PageResponse<OrderDTO>> getAllOrders(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getAllOrdersPaginated(query, status, page, size));
    }

    @GetMapping("/admin/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Get order statistics (Admin/Staff only)")
    public ResponseEntity<com.example.EyeCareHubDB.dto.OrderStatisticsResponse> getStatistics() {
        return ResponseEntity.ok(orderService.getOrderStatistics());
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Update order status (Admin/Staff only)", description = "PENDING, CONFIRMED, SHIPPING, DELIVERED, CANCELLED")
    public ResponseEntity<OrderDTO> updateStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }
}
