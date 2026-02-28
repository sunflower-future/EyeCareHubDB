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
public class PromotionValidateRequest {
    private String code;
    private BigDecimal orderTotal;
}
