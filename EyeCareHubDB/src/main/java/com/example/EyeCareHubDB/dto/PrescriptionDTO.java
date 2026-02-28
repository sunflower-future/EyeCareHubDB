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
public class PrescriptionDTO {
    private Long id;
    private Long orderItemId;
    private String productName;
    private String variantSku;
    private String customerName;
    private String rightSphere;
    private String rightCylinder;
    private String rightAxis;
    private String rightPD;
    private String leftSphere;
    private String leftCylinder;
    private String leftAxis;
    private String leftPD;
    private String addPower;
    private String notes;
    private String prescriptionImageUrl;
    private String status;
    private String verifiedByStaffEmail;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
