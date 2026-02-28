package com.example.EyeCareHubDB.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryStockDTO {
    private Long id;
    private Long productVariantId;
    private String variantSku;
    private String productName;
    private Long locationId;
    private String locationName;
    private String locationCode;
    private Integer onHandQuantity;
    private Integer reservedQuantity;
    private Integer availableQuantity;
    private LocalDateTime lastStockCheckAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
