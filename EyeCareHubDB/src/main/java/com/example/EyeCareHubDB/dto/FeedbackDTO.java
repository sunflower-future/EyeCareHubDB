package com.example.EyeCareHubDB.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackDTO {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private Long orderItemId;
    private Long accountId;
    private String accountEmail;
    private String customerName;
    private Long productId;
    private String productName;
    private String productSlug;
    private Integer rating;
    private String title;
    private String comment;
    private String imageUrls;
    private Boolean isVerifiedPurchase;
    private String staffReply;
    private LocalDateTime staffReplyAt;
    private String repliedByStaffEmail;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
