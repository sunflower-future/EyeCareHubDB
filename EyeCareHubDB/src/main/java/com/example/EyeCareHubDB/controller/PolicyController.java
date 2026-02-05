package com.example.EyeCareHubDB.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.EyeCareHubDB.dto.PolicyCreateRequest;
import com.example.EyeCareHubDB.dto.PolicyDTO;
import com.example.EyeCareHubDB.dto.PolicyPublicResponse;
import com.example.EyeCareHubDB.dto.PolicyUpdateRequest;
import com.example.EyeCareHubDB.entity.Policy;
import com.example.EyeCareHubDB.service.PolicyService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
@Tag(name = "Policies", description = "Policy Management APIs")
public class PolicyController {
    
    private final PolicyService policyService;
    
    @GetMapping
    public ResponseEntity<List<PolicyDTO>> getAllPolicies() {
        return ResponseEntity.ok(policyService.getAllPolicies());
    }
    
    @GetMapping("/published")
    public ResponseEntity<List<PolicyDTO>> getPublishedPolicies() {
        return ResponseEntity.ok(policyService.getPublishedPolicies());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PolicyDTO> getPolicyById(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.getPolicyById(id));
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<PolicyDTO> getPolicyByType(@PathVariable String type) {
        return ResponseEntity.ok(policyService.getPolicyByType(Policy.PolicyType.valueOf(type)));
    }
    
    @GetMapping("/public/type/{type}")
    public ResponseEntity<PolicyPublicResponse> getPublishedPolicyByType(@PathVariable String type) {
        return ResponseEntity.ok(policyService.getPublishedPolicyByType(Policy.PolicyType.valueOf(type)));
    }
    
    @GetMapping("/slug/{slug}")
    public ResponseEntity<PolicyDTO> getPolicyBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(policyService.getPolicyBySlug(slug));
    }
    
    @GetMapping("/public/slug/{slug}")
    public ResponseEntity<PolicyPublicResponse> getPublishedPolicyBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(policyService.getPublishedPolicyBySlug(slug));
    }
    
    @PostMapping
    public ResponseEntity<PolicyDTO> createPolicy(@RequestBody PolicyCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(policyService.createPolicy(request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PolicyDTO> updatePolicy(
            @PathVariable Long id,
            @RequestBody PolicyUpdateRequest request) {
        return ResponseEntity.ok(policyService.updatePolicy(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }
}
