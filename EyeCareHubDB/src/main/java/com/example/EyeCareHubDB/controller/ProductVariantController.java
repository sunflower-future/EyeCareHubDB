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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.EyeCareHubDB.dto.ProductVariantCreateRequest;
import com.example.EyeCareHubDB.dto.ProductVariantDTO;
import com.example.EyeCareHubDB.dto.ProductVariantUpdateRequest;
import com.example.EyeCareHubDB.dto.VariantStockResponse;
import com.example.EyeCareHubDB.service.ProductVariantService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/variants")
@RequiredArgsConstructor
@Tag(name = "Product Variants", description = "Product Variant Management APIs")
public class ProductVariantController {
    
    private final ProductVariantService variantService;
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductVariantDTO>> getVariantsByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(variantService.getVariantsByProductId(productId));
    }
    
    @GetMapping("/product/{productId}/active")
    public ResponseEntity<List<ProductVariantDTO>> getActiveVariantsByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(variantService.getActiveVariantsByProductId(productId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductVariantDTO> getVariantById(@PathVariable Long id) {
        return ResponseEntity.ok(variantService.getVariantById(id));
    }
    
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductVariantDTO> getVariantBySku(@PathVariable String sku) {
        return ResponseEntity.ok(variantService.getVariantBySku(sku));
    }
    
    @GetMapping("/{id}/stock")
    public ResponseEntity<VariantStockResponse> getStockStatus(@PathVariable Long id) {
        return ResponseEntity.ok(variantService.getStockStatus(id));
    }
    
    @GetMapping("/{id}/has-stock")
    public ResponseEntity<Boolean> hasStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(variantService.hasStock(id, quantity));
    }
    
    @PostMapping("/product/{productId}")
    public ResponseEntity<ProductVariantDTO> createVariant(
            @PathVariable Long productId,
            @RequestBody ProductVariantCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(variantService.createVariant(productId, request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductVariantDTO> updateVariant(
            @PathVariable Long id,
            @RequestBody ProductVariantUpdateRequest request) {
        return ResponseEntity.ok(variantService.updateVariant(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVariant(@PathVariable Long id) {
        variantService.deleteVariant(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/decrement-stock")
    public ResponseEntity<Void> decrementStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        variantService.decrementStock(id, quantity);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{id}/increment-stock")
    public ResponseEntity<Void> incrementStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        variantService.incrementStock(id, quantity);
        return ResponseEntity.ok().build();
    }
}
