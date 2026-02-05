package com.example.EyeCareHubDB.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {
    private Long id;
    private List<CartItemDTO> items;
    private BigDecimal totalPrice;
    private Integer totalItems;
}
