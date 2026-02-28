package com.example.EyeCareHubDB.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AfterSalesCaseDTO {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private Long orderItemId;
    private String productName;
    private Long accountId;
    private String accountEmail;
    private String type;
    private String reason;
    private String description;
    private String imageUrls;
    private String resolution;
    private BigDecimal refundAmount;
    private String status;
    private Long assignedStaffId;
    private String assignedStaffEmail;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
