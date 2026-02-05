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
public class ProductSearchRequest {
    private String keyword;
    private Long categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean isFeatured;
    private Integer page;
    private Integer pageSize;
}
