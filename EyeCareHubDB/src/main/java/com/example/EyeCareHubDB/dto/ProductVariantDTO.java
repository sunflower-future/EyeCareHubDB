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
public class ProductVariantDTO {
    private Long id;
    private Long productId;
    private String sku;
    private String color;
    private String size;
    private String material;
    private String lensType;
    private String frameMaterial;
    private String frameShape;
    private BigDecimal additionalPrice;
    private Integer stockQuantity;
    private Integer reservedQuantity;
    private String imageUrl;
    private Boolean isActive;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

