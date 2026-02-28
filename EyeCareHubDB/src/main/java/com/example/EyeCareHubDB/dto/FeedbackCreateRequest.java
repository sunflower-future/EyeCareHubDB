package com.example.EyeCareHubDB.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackCreateRequest {
    private Long orderId;
    private Long orderItemId;
    private Long productId;
    private Integer rating;
    private String title;
    private String comment;
    private String imageUrls;
}
