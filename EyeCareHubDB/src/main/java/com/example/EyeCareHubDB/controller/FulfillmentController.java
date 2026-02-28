package com.example.EyeCareHubDB.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.EyeCareHubDB.dto.FulfillmentTaskDTO;
import com.example.EyeCareHubDB.service.FulfillmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/fulfillment")
@RequiredArgsConstructor
@Tag(name = "Fulfillment", description = "Quản lý quy trình xử lý đơn hàng (fulfillment pipeline). "
                + "Tasks được auto-generate theo loại đơn: "
                + "IN_STOCK: QC→PACK→SHIP | "
                + "PREORDER: RECEIVE_PREORDER→QC→PACK→SHIP | "
                + "PRESCRIPTION: CUT_LENS→ASSEMBLE→QC→PACK→SHIP")
public class FulfillmentController {

        private final FulfillmentService fulfillmentService;

        @PostMapping("/generate/{orderId}")
        @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
        @Operation(summary = "Auto-generate fulfillment tasks cho đơn hàng (ADMIN/STAFF)", description = "Tự động tạo danh sách tasks theo orderType. "
                        + "IN_STOCK → 3 tasks (QC, PACK, SHIP). "
                        + "PREORDER → 4 tasks (RECEIVE_PREORDER, QC, PACK, SHIP). "
                        + "PRESCRIPTION → 5 tasks (CUT_LENS, ASSEMBLE, QC, PACK, SHIP). "
                        + "Mỗi task có status mặc định TODO, priority theo thứ tự.")
        public ResponseEntity<List<FulfillmentTaskDTO>> generateTasks(@PathVariable Long orderId) {
                return ResponseEntity.ok(fulfillmentService.generateTasks(orderId));
        }

        @GetMapping("/order/{orderId}")
        @Operation(summary = "Danh sách tasks theo đơn hàng", description = "Trả về tất cả fulfillment tasks của 1 order, sắp xếp theo displayOrder. "
                        + "FE dùng để hiển thị pipeline/progress bar: mỗi task 1 bước, "
                        + "status: TODO (chờ) → IN_PROGRESS (đang xử lý) → DONE (hoàn thành). "
                        + "Bao gồm assignee email, timestamps start/complete.")
        public ResponseEntity<List<FulfillmentTaskDTO>> getTasksByOrderId(@PathVariable Long orderId) {
                return ResponseEntity.ok(fulfillmentService.getTasksByOrderId(orderId));
        }

        @PutMapping("/{taskId}/status")
        @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
        @Operation(summary = "Cập nhật trạng thái task (ADMIN/STAFF)", description = "Status hợp lệ: TODO, IN_PROGRESS, DONE, SKIPPED. "
                        + "Khi chuyển IN_PROGRESS: tự set startedAt. "
                        + "Khi chuyển DONE/SKIPPED: tự set completedAt. "
                        + "Có audit log ghi nhận thay đổi status.")
        public ResponseEntity<FulfillmentTaskDTO> updateTaskStatus(
                        @PathVariable Long taskId,
                        @RequestParam String status) {
                return ResponseEntity.ok(fulfillmentService.updateTaskStatus(taskId, status));
        }

        @PutMapping("/{taskId}/assign")
        @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
        @Operation(summary = "Gán nhân viên xử lý task (ADMIN/STAFF)", description = "Gán 1 Account (STAFF/ADMIN) làm assignee cho task. "
                        + "FE có thể hiển thị dropdown list staff để chọn. "
                        + "Có audit log ghi nhận assignee.")
        public ResponseEntity<FulfillmentTaskDTO> assignTask(
                        @PathVariable Long taskId,
                        @RequestBody Map<String, Long> body) {
                return ResponseEntity.ok(fulfillmentService.assignTask(taskId, body.get("staffId")));
        }

        @GetMapping("/my-tasks")
        @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
        @Operation(summary = "Tasks được gán cho tôi (STAFF)", description = "Trả về tất cả tasks mà current user là assignee. "
                        + "FE dùng cho trang 'My Tasks' / 'Việc của tôi' của staff.")
        public ResponseEntity<List<FulfillmentTaskDTO>> getMyTasks() {
                return ResponseEntity.ok(fulfillmentService.getMyTasks());
        }

        @GetMapping("/admin")
        @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
        @Operation(summary = "Tất cả fulfillment tasks (ADMIN/STAFF, phân trang + filter)", description = "Filter: status (TODO/IN_PROGRESS/DONE/SKIPPED), taskType (QC/PACK/SHIP/CUT_LENS/ASSEMBLE/RECEIVE_PREORDER), assigneeId, query (tìm orderNumber).")
        public ResponseEntity<Page<FulfillmentTaskDTO>> getAllTasks(
                        @RequestParam(required = false) String query,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String taskType,
                        @RequestParam(required = false) Long assigneeId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size) {
                return ResponseEntity.ok(fulfillmentService.getAllTasks(query, status, taskType, assigneeId,
                                PageRequest.of(page, size, Sort.by("createdAt").descending())));
        }
}
