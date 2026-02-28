package com.example.EyeCareHubDB.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentDTO {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private String carrier;
    private String trackingNumber;
    private String shippingMethod;
    private BigDecimal shippingFee;
    private LocalDate estimatedDeliveryDate;
    private LocalDate actualDeliveryDate;
    private String status;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
