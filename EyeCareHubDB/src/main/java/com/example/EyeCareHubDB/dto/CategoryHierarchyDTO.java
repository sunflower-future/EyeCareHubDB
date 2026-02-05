package com.example.EyeCareHubDB.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryHierarchyDTO {
    private Long id;
    private String name;
    private String slug;
    private String imageUrl;
    private Integer displayOrder;
    private List<CategoryHierarchyDTO> subCategories;
}
