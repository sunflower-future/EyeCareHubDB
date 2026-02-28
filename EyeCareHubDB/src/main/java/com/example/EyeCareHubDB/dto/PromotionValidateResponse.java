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
public class PromotionValidateResponse {
    private boolean valid;
    private String code;
    private String name;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal calculatedDiscount;
    private BigDecimal orderTotalAfterDiscount;
    private String message;
}
