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

import com.example.EyeCareHubDB.dto.ProductMediaCreateRequest;
import com.example.EyeCareHubDB.dto.ProductMediaDTO;
import com.example.EyeCareHubDB.dto.ProductMediaUpdateRequest;
import com.example.EyeCareHubDB.service.ProductMediaService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
@Tag(name = "Product Media", description = "Product Media Management APIs")
public class ProductMediaController {
    
    private final ProductMediaService mediaService;
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductMediaDTO>> getMediaByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(mediaService.getMediaByProductId(productId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductMediaDTO> getMediaById(@PathVariable Long id) {
        return ResponseEntity.ok(mediaService.getMediaById(id));
    }
    
    @GetMapping("/product/{productId}/primary")
    public ResponseEntity<ProductMediaDTO> getPrimaryMedia(@PathVariable Long productId) {
        return ResponseEntity.ok(mediaService.getPrimaryMedia(productId));
    }
    
    @GetMapping("/product/{productId}/images")
    public ResponseEntity<List<ProductMediaDTO>> getImagesByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(mediaService.getImagesByProductId(productId));
    }
    
    @PostMapping("/product/{productId}")
    public ResponseEntity<ProductMediaDTO> addMedia(
            @PathVariable Long productId,
            @RequestBody ProductMediaCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mediaService.addMedia(productId, request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductMediaDTO> updateMedia(
            @PathVariable Long id,
            @RequestBody ProductMediaUpdateRequest request) {
        return ResponseEntity.ok(mediaService.updateMedia(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedia(@PathVariable Long id) {
        mediaService.deleteMedia(id);
        return ResponseEntity.noContent().build();
    }
}
