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

import com.example.EyeCareHubDB.dto.ProductCreateRequest;
import com.example.EyeCareHubDB.dto.ProductDTO;
import com.example.EyeCareHubDB.dto.ProductUpdateRequest;
import com.example.EyeCareHubDB.service.ProductService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product Management APIs")
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductDTO> getProductBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getProductBySlug(slug));
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }
    
    @GetMapping("/featured")
    public ResponseEntity<List<ProductDTO>> getFeaturedProducts(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(productService.getFeaturedProducts(limit));
    }
    
    @GetMapping("/popular")
    public ResponseEntity<List<ProductDTO>> getPopularProducts(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(productService.getPopularProducts(limit));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(productService.searchProducts(keyword, page, pageSize));
    }
    
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductUpdateRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
