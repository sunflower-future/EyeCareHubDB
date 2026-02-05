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
public class PolicyPublicResponse {
    private Long id;
    private String type;
    private String title;
    private String slug;
    private String content;
    private LocalDateTime publishedAt;
}
