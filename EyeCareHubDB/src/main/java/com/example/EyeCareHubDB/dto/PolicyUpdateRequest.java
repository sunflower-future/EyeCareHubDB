package com.example.EyeCareHubDB.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyUpdateRequest {
    private String title;
    private String content;
    private Boolean isPublished;
    private Integer displayOrder;
}
