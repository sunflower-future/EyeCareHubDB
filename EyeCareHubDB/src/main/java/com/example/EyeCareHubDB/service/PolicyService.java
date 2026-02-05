package com.example.EyeCareHubDB.service;

import com.example.EyeCareHubDB.dto.PolicyDTO;
import com.example.EyeCareHubDB.dto.PolicyCreateRequest;
import com.example.EyeCareHubDB.dto.PolicyUpdateRequest;
import com.example.EyeCareHubDB.dto.PolicyPublicResponse;
import com.example.EyeCareHubDB.entity.Policy;
import com.example.EyeCareHubDB.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PolicyService {
    
    private final PolicyRepository policyRepository;
    
    public List<PolicyDTO> getAllPolicies() {
        return policyRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<PolicyDTO> getPublishedPolicies() {
        return policyRepository.findPublishedPolicies().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public PolicyDTO getPolicyById(Long id) {
        return policyRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Policy not found with id: " + id));
    }
    
    public PolicyDTO getPolicyByType(Policy.PolicyType type) {
        return policyRepository.findByType(type)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Policy not found with type: " + type));
    }
    
    public PolicyPublicResponse getPublishedPolicyByType(Policy.PolicyType type) {
        return policyRepository.findPublishedByType(type)
                .map(this::toPublicResponse)
                .orElseThrow(() -> new RuntimeException("Published policy not found with type: " + type));
    }
    
    public PolicyDTO getPolicyBySlug(String slug) {
        return policyRepository.findBySlug(slug)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Policy not found with slug: " + slug));
    }
    
    public PolicyPublicResponse getPublishedPolicyBySlug(String slug) {
        return policyRepository.findBySlug(slug)
                .filter(Policy::getIsPublished)
                .map(this::toPublicResponse)
                .orElseThrow(() -> new RuntimeException("Published policy not found with slug: " + slug));
    }
    
    public PolicyDTO createPolicy(PolicyCreateRequest request) {
        if (policyRepository.existsBySlug(request.getSlug())) {
            throw new RuntimeException("Policy with slug already exists: " + request.getSlug());
        }
        
        Policy policy = Policy.builder()
                .type(Policy.PolicyType.valueOf(request.getType()))
                .title(request.getTitle())
                .slug(request.getSlug())
                .content(request.getContent())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .isPublished(false)
                .build();
        
        Policy saved = policyRepository.save(policy);
        return toDTO(saved);
    }
    
    public PolicyDTO updatePolicy(Long id, PolicyUpdateRequest request) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found with id: " + id));
        
        if (request.getTitle() != null) {
            policy.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            policy.setContent(request.getContent());
        }
        if (request.getDisplayOrder() != null) {
            policy.setDisplayOrder(request.getDisplayOrder());
        }
        if (request.getIsPublished() != null) {
            policy.setIsPublished(request.getIsPublished());
            if (request.getIsPublished() && policy.getPublishedAt() == null) {
                policy.setPublishedAt(LocalDateTime.now());
            }
        }
        
        Policy updated = policyRepository.save(policy);
        return toDTO(updated);
    }
    
    public void deletePolicy(Long id) {
        if (!policyRepository.existsById(id)) {
            throw new RuntimeException("Policy not found with id: " + id);
        }
        policyRepository.deleteById(id);
    }
    
    public void publishPolicy(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found with id: " + id));
        
        policy.setIsPublished(true);
        policy.setPublishedAt(LocalDateTime.now());
        policyRepository.save(policy);
    }
    
    public void unpublishPolicy(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found with id: " + id));
        
        policy.setIsPublished(false);
        policyRepository.save(policy);
    }
    
    private PolicyDTO toDTO(Policy policy) {
        return PolicyDTO.builder()
                .id(policy.getId())
                .type(policy.getType().name())
                .title(policy.getTitle())
                .slug(policy.getSlug())
                .content(policy.getContent())
                .isPublished(policy.getIsPublished())
                .displayOrder(policy.getDisplayOrder())
                .createdAt(policy.getCreatedAt())
                .updatedAt(policy.getUpdatedAt())
                .publishedAt(policy.getPublishedAt())
                .build();
    }
    
    private PolicyPublicResponse toPublicResponse(Policy policy) {
        return PolicyPublicResponse.builder()
                .id(policy.getId())
                .type(policy.getType().name())
                .title(policy.getTitle())
                .slug(policy.getSlug())
                .content(policy.getContent())
                .publishedAt(policy.getPublishedAt())
                .build();
    }
}
