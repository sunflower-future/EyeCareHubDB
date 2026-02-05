package com.example.EyeCareHubDB.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.EyeCareHubDB.dto.ProductVariantCreateRequest;
import com.example.EyeCareHubDB.dto.ProductVariantDTO;
import com.example.EyeCareHubDB.dto.ProductVariantUpdateRequest;
import com.example.EyeCareHubDB.dto.VariantStockResponse;
import com.example.EyeCareHubDB.entity.Product;
import com.example.EyeCareHubDB.entity.ProductVariant;
import com.example.EyeCareHubDB.repository.ProductRepository;
import com.example.EyeCareHubDB.repository.ProductVariantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductVariantService {
    
    private final ProductVariantRepository variantRepository;
    private final ProductRepository productRepository;
    
    public List<ProductVariantDTO> getVariantsByProductId(Long productId) {
        return variantRepository.findByProductIdOrderByDisplayOrder(productId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProductVariantDTO> getActiveVariantsByProductId(Long productId) {
        return variantRepository.findByProductIdAndIsActiveTrue(productId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public ProductVariantDTO getVariantById(Long id) {
        return variantRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Product variant not found with id: " + id));
    }
    
    public ProductVariantDTO getVariantBySku(String sku) {
        return variantRepository.findBySku(sku)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Product variant not found with sku: " + sku));
    }
    
    public ProductVariantDTO createVariant(Long productId, ProductVariantCreateRequest request) {
        if (variantRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("Variant with sku already exists: " + request.getSku());
        }
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .sku(request.getSku())
                .color(request.getColor())
                .size(request.getSize())
                .material(request.getMaterial())
                .lensType(request.getLensType())
                .frameMaterial(request.getFrameMaterial())
                .frameShape(request.getFrameShape())
                .additionalPrice(request.getAdditionalPrice())
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .reservedQuantity(0)
                .isActive(true)
                .displayOrder(0)
                .build();
        
        ProductVariant saved = variantRepository.save(variant);
        return toDTO(saved);
    }
    
    public ProductVariantDTO updateVariant(Long id, ProductVariantUpdateRequest request) {
        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product variant not found with id: " + id));
        
        if (request.getColor() != null) {
            variant.setColor(request.getColor());
        }
        if (request.getSize() != null) {
            variant.setSize(request.getSize());
        }
        if (request.getMaterial() != null) {
            variant.setMaterial(request.getMaterial());
        }
        if (request.getLensType() != null) {
            variant.setLensType(request.getLensType());
        }
        if (request.getFrameMaterial() != null) {
            variant.setFrameMaterial(request.getFrameMaterial());
        }
        if (request.getFrameShape() != null) {
            variant.setFrameShape(request.getFrameShape());
        }
        if (request.getAdditionalPrice() != null) {
            variant.setAdditionalPrice(request.getAdditionalPrice());
        }
        if (request.getStockQuantity() != null) {
            variant.setStockQuantity(request.getStockQuantity());
        }
        if (request.getImageUrl() != null) {
            variant.setImageUrl(request.getImageUrl());
        }
        if (request.getIsActive() != null) {
            variant.setIsActive(request.getIsActive());
        }
        if (request.getDisplayOrder() != null) {
            variant.setDisplayOrder(request.getDisplayOrder());
        }
        
        ProductVariant updated = variantRepository.save(variant);
        return toDTO(updated);
    }
    
    public void deleteVariant(Long id) {
        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product variant not found with id: " + id));
        variant.setIsActive(false);
        variantRepository.save(variant);
    }
    
    public VariantStockResponse getStockStatus(Long id) {
        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product variant not found with id: " + id));
        
        return VariantStockResponse.builder()
                .variantId(variant.getId())
                .sku(variant.getSku())
                .stockQuantity(variant.getStockQuantity())
                .reservedQuantity(variant.getReservedQuantity())
                .availableQuantity(variant.getStockQuantity() - variant.getReservedQuantity())
                .build();
    }
    
    public boolean hasStock(Long id, Integer quantity) {
        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product variant not found with id: " + id));
        
        return variant.getStockQuantity() - variant.getReservedQuantity() >= quantity;
    }
    
    public void decrementStock(Long id, Integer quantity) {
        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product variant not found with id: " + id));
        
        if (!hasStock(id, quantity)) {
            throw new RuntimeException("Insufficient stock for variant: " + variant.getSku());
        }
        
        variant.setStockQuantity(variant.getStockQuantity() - quantity);
        variantRepository.save(variant);
    }
    
    public void incrementStock(Long id, Integer quantity) {
        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product variant not found with id: " + id));
        
        variant.setStockQuantity(variant.getStockQuantity() + quantity);
        variantRepository.save(variant);
    }
    
    private ProductVariantDTO toDTO(ProductVariant variant) {
        return ProductVariantDTO.builder()
                .id(variant.getId())
                .productId(variant.getProduct().getId())
                .sku(variant.getSku())
                .color(variant.getColor())
                .size(variant.getSize())
                .material(variant.getMaterial())
                .lensType(variant.getLensType())
                .frameMaterial(variant.getFrameMaterial())
                .frameShape(variant.getFrameShape())
                .additionalPrice(variant.getAdditionalPrice())
                .stockQuantity(variant.getStockQuantity())
                .reservedQuantity(variant.getReservedQuantity())
                .imageUrl(variant.getImageUrl())
                .isActive(variant.getIsActive())
                .displayOrder(variant.getDisplayOrder())
                .createdAt(variant.getCreatedAt())
                .updatedAt(variant.getUpdatedAt())
                .build();
    }
}
