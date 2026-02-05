package com.example.EyeCareHubDB.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantStockResponse {
    private Long variantId;
    private String sku;
    private Integer stockQuantity;
    private Integer reservedQuantity;
    private Integer availableQuantity;
}
