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
public class OrderDTO {
    private Long id;
    private Long accountId;
    private String orderNumber;
    private String status;
    private BigDecimal totalPrice;
    private String shippingAddress;
    private String phoneNumber;
    private String notes;
    private String createdAt;
    private List<OrderItemDTO> items;
}
