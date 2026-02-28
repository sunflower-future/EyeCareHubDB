package com.example.EyeCareHubDB.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentCreateRequest {
    private String carrier;
    private String trackingNumber;
    private String shippingMethod;
    private BigDecimal shippingFee;
    private LocalDate estimatedDeliveryDate;
    private String notes;
}
