package com.example.EyeCareHubDB.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionCreateRequest {
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
}
