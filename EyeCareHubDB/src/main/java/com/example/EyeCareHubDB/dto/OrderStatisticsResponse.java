package com.example.EyeCareHubDB.dto;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderStatisticsResponse {
    private long totalOrders;
    private BigDecimal totalRevenue;
    private Map<String, Long> ordersByStatus;
}
