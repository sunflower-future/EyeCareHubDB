package com.example.EyeCareHubDB.controller;

import java.util.Map;

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
@Tag(name = "Orders", description = "Quản lý đơn hàng: checkout từ giỏ, mua ngay, update status, confirm payment. "
                + "Status flow: PENDING → CONFIRMED → PROCESSING → READY_FOR_SHIPPING → SHIPPING → DELIVERED | CANCELLED. "
                + "Hỗ trợ 3 loại đơn: IN_STOCK / PREORDER / PRESCRIPTION.")
public class OrderController {

        private final OrderService orderService;

        @PostMapping("/checkout")
        @Operation(summary = "Checkout giỏ hàng → tạo đơn hàng", description = "Convert toàn bộ cart items thành 1 order. Gửi: shippingAddress, phoneNumber, notes, "
                        + "promotionCode (optional, auto validate + apply discount), "
                        + "orderType (IN_STOCK/PREORDER/PRESCRIPTION, default: IN_STOCK), "
                        + "paymentMethod (COD/BANK_TRANSFER/MOMO/VNPAY, optional). "
                        + "Tự trừ stock, tính subtotal - discount + shippingFee = totalPrice. "
                        + "Sau checkout, giỏ hàng sẽ tự clear. paymentStatus mặc định: UNPAID.")
        public ResponseEntity<OrderDTO> checkout(@Valid @RequestBody CheckoutRequest request) {
                return ResponseEntity.ok(orderService.placeOrder(request));
        }

        @PostMapping("/buy-now")
        @Operation(summary = "Mua ngay (không qua giỏ)", description = "Tạo đơn hàng trực tiếp từ 1 product variant + quantity. "
                        + "Gửi: productVariantId, quantity, shippingAddress, phoneNumber, notes. "
                        + "Tự trừ stock, tạo order với 1 item duy nhất. orderType mặc định: IN_STOCK.")
        public ResponseEntity<OrderDTO> buyNow(@Valid @RequestBody com.example.EyeCareHubDB.dto.BuyNowRequest request) {
                return ResponseEntity.ok(orderService.buyNow(request));
        }

        @GetMapping("/{orderId}")
        @Operation(summary = "Chi tiết đơn hàng", description = "Trả về đầy đủ thông tin đơn hàng: orderNumber, orderType, status, "
                        + "subtotalPrice, discountAmount, shippingFee, totalPrice, promotionCode, "
                        + "paymentMethod/Status, customerName, accountEmail, danh sách items (productName, "
                        + "productSlug, imageUrl, quantity, price, subtotal), totalItemCount, totalQuantity. "
                        + "Customer chỉ xem được đơn của mình. ADMIN/STAFF xem được tất cả.")
        public ResponseEntity<OrderDTO> getOrder(@PathVariable Long orderId) {
                return ResponseEntity.ok(orderService.getOrderById(orderId));
        }

        @PutMapping("/{orderId}/cancel")
        @Operation(summary = "Hủy đơn hàng", description = "Chỉ hủy được đơn PENDING (chưa confirm). Tự restore stock cho các items. "
                        + "Customer hủy đơn của mình. ADMIN/STAFF hủy bất kỳ đơn nào. Có audit log.")
        public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long orderId) {
                return ResponseEntity.ok(orderService.cancelOrder(orderId));
        }

        @GetMapping
        @Operation(summary = "Danh sách đơn hàng của tôi (phân trang)", description = "Trả về đơn hàng của user hiện tại. Filter theo status (optional). "
                        + "Phân trang: page (default 0), size (default 10). Mới nhất trước.")
        public ResponseEntity<com.example.EyeCareHubDB.dto.PageResponse<OrderDTO>> getMyOrders(
                        @RequestParam(required = false) String status,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                return ResponseEntity.ok(orderService.getMyOrders(status, page, size));
        }

        @GetMapping("/admin")
        @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
        @Operation(summary = "Tất cả đơn hàng (ADMIN/STAFF, phân trang + filter)", description = "Filter: query (orderNumber/email/phone/name), status, orderType (IN_STOCK/PREORDER/PRESCRIPTION), paymentStatus (UNPAID/PAID/REFUNDED).")
        public ResponseEntity<com.example.EyeCareHubDB.dto.PageResponse<OrderDTO>> getAllOrders(
                        @RequestParam(required = false) String query,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String orderType,
                        @RequestParam(required = false) String paymentStatus,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                return ResponseEntity.ok(orderService.getAllOrdersPaginated(query, status, orderType, paymentStatus,
                                page, size));
        }

        @GetMapping("/admin/statistics")
        @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
        @Operation(summary = "Thống kê đơn hàng (ADMIN/STAFF)", description = "Trả về: totalOrders, totalRevenue (tổng doanh thu, trừ đơn CANCELLED), "
                        + "ordersByStatus (map: {'PENDING': 5, 'CONFIRMED': 10, ...}).")
        public ResponseEntity<com.example.EyeCareHubDB.dto.OrderStatisticsResponse> getStatistics() {
                return ResponseEntity.ok(orderService.getOrderStatistics());
        }

        @PutMapping("/{orderId}/status")
        @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
        @Operation(summary = "Cập nhật trạng thái đơn hàng (ADMIN/STAFF)", description = "Status hợp lệ: PENDING, CONFIRMED, PROCESSING, READY_FOR_SHIPPING, SHIPPING, DELIVERED, CANCELLED. "
                        + "Flow chuẩn: PENDING → CONFIRMED → PROCESSING → READY_FOR_SHIPPING → SHIPPING → DELIVERED. "
                        + "Có audit log ghi nhận thay đổi.")
        public ResponseEntity<OrderDTO> updateStatus(
                        @PathVariable Long orderId,
                        @RequestParam String status) {
                return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
        }

        @PutMapping("/{orderId}/confirm-payment")
        @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
        @Operation(summary = "Xác nhận thanh toán (ADMIN/STAFF)", description = "Gán paymentStatus = PAID, paymentMethod, paidAt. "
                        + "Nếu đơn đang PENDING → tự chuyển sang CONFIRMED. "
                        + "Gửi body: {\"paymentMethod\": \"BANK_TRANSFER\"}")
        public ResponseEntity<OrderDTO> confirmPayment(
                        @PathVariable Long orderId,
                        @RequestBody Map<String, String> body) {
                return ResponseEntity.ok(orderService.confirmPayment(orderId, body.get("paymentMethod")));
        }
}
