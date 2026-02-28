package com.example.EyeCareHubDB.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.EyeCareHubDB.dto.FeedbackCreateRequest;
import com.example.EyeCareHubDB.dto.FeedbackDTO;
import com.example.EyeCareHubDB.dto.FeedbackSummaryDTO;
import com.example.EyeCareHubDB.service.FeedbackService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedbacks", description = "Đánh giá sản phẩm: tạo review (1-5 sao), staff phản hồi, duyệt/ẩn. "
                + "Chỉ DELIVERED orders mới tính verified purchase. Status: PENDING → APPROVED/REJECTED/HIDDEN")
public class FeedbackController {

        private final FeedbackService feedbackService;

        @PostMapping
        @Operation(summary = "Tạo đánh giá sản phẩm", description = "Customer tạo đánh giá cho sản phẩm đã mua. rating: 1-5 sao (bắt buộc). "
                        + "Gửi: orderId, orderItemId, productId, title, comment, imageUrls (comma-separated). "
                        + "Tự kiểm tra verified purchase (đơn hàng DELIVERED + cùng account). "
                        + "Mỗi account chỉ review 1 lần/orderItem. Status mặc định: PENDING.")
        public ResponseEntity<FeedbackDTO> createFeedback(@RequestBody FeedbackCreateRequest request) {
                return ResponseEntity.ok(feedbackService.createFeedback(request));
        }

        @GetMapping("/product/{productId}")
        @Operation(summary = "Đánh giá theo sản phẩm (PUBLIC, phân trang)", description = "Trả về danh sách đánh giá APPROVED của 1 sản phẩm. "
                        + "Bao gồm: customerName, rating, title, comment, imageUrls, isVerifiedPurchase, "
                        + "staffReply (nếu có), timestamps. Phân trang: page, size.")
        public ResponseEntity<Page<FeedbackDTO>> getFeedbacksByProductId(
                        @PathVariable Long productId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                return ResponseEntity.ok(feedbackService.getFeedbacksByProductId(productId,
                                PageRequest.of(page, size, Sort.by("createdAt").descending())));
        }

        @GetMapping("/product/{productId}/summary")
        @Operation(summary = "Tổng hợp đánh giá sản phẩm (PUBLIC)", description = "Trả về: averageRating (trung bình 1 chữ số thập phân, VD: 4.3), "
                        + "totalReviews (tổng số đánh giá), ratingDistribution (map: {5: 10, 4: 5, 3: 2, 2: 1, 1: 0}). "
                        + "FE dùng để hiển thị thanh rating bar trên trang sản phẩm.")
        public ResponseEntity<FeedbackSummaryDTO> getProductFeedbackSummary(@PathVariable Long productId) {
                return ResponseEntity.ok(feedbackService.getProductFeedbackSummary(productId));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Chi tiết đánh giá theo ID", description = "Trả về đầy đủ thông tin feedback: customerName, productName, rating, comment, "
                        + "imageUrls, staffReply, status, isVerifiedPurchase, timestamps.")
        public ResponseEntity<FeedbackDTO> getFeedbackById(@PathVariable Long id) {
                return ResponseEntity.ok(feedbackService.getFeedbackById(id));
        }

        @PutMapping("/{id}")
        @Operation(summary = "Sửa đánh giá (owner only)", description = "Chỉ người tạo feedback mới có thể sửa. Gửi field cần thay đổi: rating, title, comment, imageUrls.")
        public ResponseEntity<FeedbackDTO> updateFeedback(@PathVariable Long id,
                        @RequestBody FeedbackCreateRequest request) {
                return ResponseEntity.ok(feedbackService.updateFeedback(id, request));
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Xóa đánh giá (owner only)", description = "Chỉ người tạo feedback mới có thể xóa. Hard delete.")
        public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
                feedbackService.deleteFeedback(id);
                return ResponseEntity.ok().build();
        }

        @PutMapping("/{id}/reply")
        @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
        @Operation(summary = "Staff phản hồi đánh giá (ADMIN/STAFF)", description = "Staff reply vào feedback. Ghi nhận: staffReply, staffReplyAt, repliedByStaffEmail. "
                        + "FE hiển thị reply bên dưới comment của customer.")
        public ResponseEntity<FeedbackDTO> staffReply(
                        @PathVariable Long id,
                        @RequestBody Map<String, String> body) {
                return ResponseEntity.ok(feedbackService.staffReply(id, body.get("reply")));
        }

        @PutMapping("/{id}/moderate")
        @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
        @Operation(summary = "Duyệt/ẩn đánh giá (ADMIN/STAFF)", description = "Cập nhật status: PENDING → APPROVED (hiển thị public), REJECTED (từ chối), HIDDEN (ẩn). "
                        + "Chỉ feedback APPROVED mới hiển thị ở product page. Có audit log.")
        public ResponseEntity<FeedbackDTO> moderateFeedback(
                        @PathVariable Long id,
                        @RequestParam String status) {
                return ResponseEntity.ok(feedbackService.moderateFeedback(id, status));
        }

        @GetMapping("/admin")
        @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
        @Operation(summary = "Tất cả đánh giá (ADMIN/STAFF, phân trang + filter)", description = "Filter: status (PENDING/APPROVED/REJECTED/HIDDEN), rating (1-5), productId, query (tìm title/comment/email).")
        public ResponseEntity<Page<FeedbackDTO>> getAllFeedbacks(
                        @RequestParam(required = false) String query,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) Integer rating,
                        @RequestParam(required = false) Long productId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size) {
                return ResponseEntity.ok(feedbackService.getAllFeedbacks(query, status, rating, productId,
                                PageRequest.of(page, size, Sort.by("createdAt").descending())));
        }
}
