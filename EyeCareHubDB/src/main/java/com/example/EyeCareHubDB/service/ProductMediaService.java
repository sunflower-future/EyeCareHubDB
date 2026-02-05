package com.example.EyeCareHubDB.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.EyeCareHubDB.dto.ProductMediaCreateRequest;
import com.example.EyeCareHubDB.dto.ProductMediaDTO;
import com.example.EyeCareHubDB.dto.ProductMediaUpdateRequest;
import com.example.EyeCareHubDB.entity.Product;
import com.example.EyeCareHubDB.entity.ProductMedia;
import com.example.EyeCareHubDB.repository.ProductMediaRepository;
import com.example.EyeCareHubDB.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductMediaService {
    
    private final ProductMediaRepository mediaRepository;
    private final ProductRepository productRepository;
    
    public List<ProductMediaDTO> getMediaByProductId(Long productId) {
        return mediaRepository.findByProductIdOrderByDisplayOrder(productId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public ProductMediaDTO getMediaById(Long id) {
        return mediaRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Product media not found with id: " + id));
    }
    
    public ProductMediaDTO getPrimaryMedia(Long productId) {
        return mediaRepository.findPrimaryMediaByProductId(productId)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("No primary media found for product: " + productId));
    }
    
    public List<ProductMediaDTO> getImagesByProductId(Long productId) {
        return mediaRepository.findByProductIdAndType(productId, ProductMedia.MediaType.IMAGE).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public ProductMediaDTO addMedia(Long productId, ProductMediaCreateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        ProductMedia media = ProductMedia.builder()
                .product(product)
                .type(ProductMedia.MediaType.valueOf(request.getType() != null ? request.getType() : "IMAGE"))
                .url(request.getUrl())
                .altText(request.getAltText())
                .title(request.getTitle())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .isPrimary(request.getIsPrimary() != null ? request.getIsPrimary() : false)
                .build();
        
        ProductMedia saved = mediaRepository.save(media);
        return toDTO(saved);
    }
    
    public ProductMediaDTO updateMedia(Long id, ProductMediaUpdateRequest request) {
        ProductMedia media = mediaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product media not found with id: " + id));
        
        if (request.getType() != null) {
            media.setType(ProductMedia.MediaType.valueOf(request.getType()));
        }
        if (request.getUrl() != null) {
            media.setUrl(request.getUrl());
        }
        if (request.getAltText() != null) {
            media.setAltText(request.getAltText());
        }
        if (request.getTitle() != null) {
            media.setTitle(request.getTitle());
        }
        if (request.getDisplayOrder() != null) {
            media.setDisplayOrder(request.getDisplayOrder());
        }
        if (request.getIsPrimary() != null) {
            media.setIsPrimary(request.getIsPrimary());
        }
        
        ProductMedia updated = mediaRepository.save(media);
        return toDTO(updated);
    }
    
    public void deleteMedia(Long id) {
        if (!mediaRepository.existsById(id)) {
            throw new RuntimeException("Product media not found with id: " + id);
        }
        mediaRepository.deleteById(id);
    }
    
    public void setPrimaryMedia(Long productId, Long mediaId) {
        ProductMedia media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Product media not found with id: " + mediaId));
        
        if (!media.getProduct().getId().equals(productId)) {
            throw new RuntimeException("Media does not belong to this product");
        }
        
        mediaRepository.findPrimaryMediaByProductId(productId).ifPresent(primaryMedia -> {
            primaryMedia.setIsPrimary(false);
            mediaRepository.save(primaryMedia);
        });
        
        media.setIsPrimary(true);
        mediaRepository.save(media);
    }
    
    private ProductMediaDTO toDTO(ProductMedia media) {
        return ProductMediaDTO.builder()
                .id(media.getId())
                .productId(media.getProduct().getId())
                .type(media.getType().name())
                .url(media.getUrl())
                .altText(media.getAltText())
                .title(media.getTitle())
                .displayOrder(media.getDisplayOrder())
                .isPrimary(media.getIsPrimary())
                .createdAt(media.getCreatedAt())
                .updatedAt(media.getUpdatedAt())
                .build();
    }
}
