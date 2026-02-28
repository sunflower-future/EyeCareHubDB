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
public class CartItemDTO {
    private Long id;
    private Long productVariantId;
    private String productName;
    private String variantSku;
    private String variantColor;
    private String variantSize;
    private String imageUrl;
    private BigDecimal unitPrice;
    private BigDecimal snapshotPrice;
    private Boolean priceChanged;
    private Integer quantity;
    private BigDecimal subtotal;
    private Boolean inStock;
    private Integer availableStock;
}
