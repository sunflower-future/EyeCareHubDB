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
    private String accountEmail;
    private String customerName;
    private String orderNumber;
    private String orderType;
    private String status;
    private BigDecimal subtotalPrice;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal totalPrice;
    private String promotionCode;
    private String paymentMethod;
    private String paymentStatus;
    private String paidAt;
    private String shippingAddress;
    private String phoneNumber;
    private String notes;
    private String createdAt;
    private String updatedAt;
    private List<OrderItemDTO> items;

    private Integer totalItemCount;
    private Integer totalQuantity;
}
