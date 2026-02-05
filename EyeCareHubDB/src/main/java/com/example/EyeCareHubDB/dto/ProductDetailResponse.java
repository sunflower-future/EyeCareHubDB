package com.example.EyeCareHubDB.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailResponse {
    private Long id;
    private String name;
    private String slug;
    private String sku;
    private CategoryDTO category;
    private String brand;
    private String shortDescription;
    private String fullDescription;
    private BigDecimal basePrice;
    private BigDecimal salePrice;
    private List<ProductVariantDTO> variants;
    private List<ProductMediaDTO> media;
    private Integer viewCount;
    private Integer soldCount;
    private Boolean isActive;
    private Boolean isFeatured;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
