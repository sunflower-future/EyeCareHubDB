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

import com.example.EyeCareHubDB.dto.CategoryCreateRequest;
import com.example.EyeCareHubDB.dto.CategoryDTO;
import com.example.EyeCareHubDB.dto.CategoryUpdateRequest;
import com.example.EyeCareHubDB.service.CategoryService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category Management APIs")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }
    
    @GetMapping("/slug/{slug}")
    public ResponseEntity<CategoryDTO> getCategoryBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(categoryService.getCategoryBySlug(slug));
    }
    
    @GetMapping("/root")
    public ResponseEntity<List<CategoryDTO>> getRootCategories() {
        return ResponseEntity.ok(categoryService.getRootCategories());
    }
    
    @GetMapping("/{parentId}/sub-categories")
    public ResponseEntity<List<CategoryDTO>> getSubCategories(@PathVariable Long parentId) {
        return ResponseEntity.ok(categoryService.getSubCategories(parentId));
    }
    
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryUpdateRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
