package com.example.EyeCareHubDB.dto;

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
public class FulfillmentTaskDTO {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private String taskType;
    private Long assigneeId;
    private String assigneeEmail;
    private String status;
    private Integer priority;
    private String notes;
    private LocalDate dueDate;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
