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
public class ProductVariantCreateRequest {
    private String sku;
    private String color;
    private String size;
    private String material;
    private String lensType;
    private String frameMaterial;
    private String frameShape;
    private BigDecimal additionalPrice;
    private Integer stockQuantity;
}
