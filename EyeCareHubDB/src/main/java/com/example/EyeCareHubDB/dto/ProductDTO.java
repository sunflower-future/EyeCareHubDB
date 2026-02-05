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
public class ProductDTO {
    private Long id;
    private String name;
    private String slug;
    private String sku;
    private Long categoryId;
    private String brand;
    private String shortDescription;
    private String fullDescription;
    private BigDecimal basePrice;
    private BigDecimal salePrice;
    private Boolean isActive;
    private Boolean isFeatured;
    private Integer viewCount;
    private Integer soldCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

