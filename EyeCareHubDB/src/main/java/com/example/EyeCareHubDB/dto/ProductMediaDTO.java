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
public class ProductMediaDTO {
    private Long id;
    private Long productId;
    private String type;
    private String url;
    private String altText;
    private String title;
    private Integer displayOrder;
    private Boolean isPrimary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

