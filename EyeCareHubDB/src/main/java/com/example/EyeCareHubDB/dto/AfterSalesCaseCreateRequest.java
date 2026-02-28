package com.example.EyeCareHubDB.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AfterSalesCaseCreateRequest {
    private Long orderId;
    private Long orderItemId;
    private String type;
    private String reason;
    private String description;
    private String imageUrls;
    private BigDecimal refundAmount;
}
