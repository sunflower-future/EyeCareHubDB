package com.example.EyeCareHubDB.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryUpdateRequest {
    private String name;
    private String slug;
    private String description;
    private String imageUrl;
    private Long parentId;
    private Integer displayOrder;
    private Boolean isActive;
}
