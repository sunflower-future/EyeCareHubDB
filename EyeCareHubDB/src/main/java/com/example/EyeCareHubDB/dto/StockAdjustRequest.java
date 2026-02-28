package com.example.EyeCareHubDB.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAdjustRequest {
    private Long productVariantId;
    private Long locationId;
    private Integer onHandQuantity;
    private Integer reservedQuantity;
}
