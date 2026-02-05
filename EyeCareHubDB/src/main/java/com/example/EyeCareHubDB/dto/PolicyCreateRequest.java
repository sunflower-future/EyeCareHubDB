package com.example.EyeCareHubDB.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyCreateRequest {
    private String type;
    private String title;
    private String slug;
    private String content;
    private Integer displayOrder;
}
