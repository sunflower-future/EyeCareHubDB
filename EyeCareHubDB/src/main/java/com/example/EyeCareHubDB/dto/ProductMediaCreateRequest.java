package com.example.EyeCareHubDB.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMediaCreateRequest {
    private String type;
    private String url;
    private String altText;
    private String title;
    private Integer displayOrder;
    private Boolean isPrimary;
}
