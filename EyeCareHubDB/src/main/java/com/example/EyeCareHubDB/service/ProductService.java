package com.example.EyeCareHubDB.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.EyeCareHubDB.dto.ProductCreateRequest;
import com.example.EyeCareHubDB.dto.ProductDTO;
import com.example.EyeCareHubDB.dto.ProductUpdateRequest;
import com.example.EyeCareHubDB.entity.Category;
import com.example.EyeCareHubDB.entity.Product;
import com.example.EyeCareHubDB.repository.CategoryRepository;
import com.example.EyeCareHubDB.repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    public List<ProductDTO> getAllProducts() {
        return productRepository.findByIsActiveTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }
    
    public ProductDTO getProductBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Product not found with slug: " + slug));
    }
    
    public List<ProductDTO> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProductDTO> getFeaturedProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findByIsFeaturedTrue(pageable).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProductDTO> getPopularProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findPopularProducts(pageable).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProductDTO> getProductsOnSale(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findProductsOnSale(pageable).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProductDTO> searchProducts(String keyword, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return productRepository.searchByName(keyword, pageable).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public ProductDTO createProduct(ProductCreateRequest request) {
        if (productRepository.existsBySlug(request.getSlug())) {
            throw new RuntimeException("Product with slug already exists: " + request.getSlug());
        }
        
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + request.getCategoryId()));
        
        Product product = Product.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .sku(request.getSku())
                .category(category)
                .brand(request.getBrand())
                .shortDescription(request.getShortDescription())
                .fullDescription(request.getFullDescription())
                .basePrice(request.getBasePrice())
                .salePrice(request.getSalePrice())
                .isActive(true)
                .isFeatured(false)
                .viewCount(0)
                .soldCount(0)
                .build();
        
        Product saved = productRepository.save(product);
        return toDTO(saved);
    }
    
    public ProductDTO updateProduct(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getSlug() != null && !request.getSlug().equals(product.getSlug())) {
            if (productRepository.existsBySlug(request.getSlug())) {
                throw new RuntimeException("Product with slug already exists: " + request.getSlug());
            }
            product.setSlug(request.getSlug());
        }
        if (request.getSku() != null) {
            product.setSku(request.getSku());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }
        if (request.getBrand() != null) {
            product.setBrand(request.getBrand());
        }
        if (request.getShortDescription() != null) {
            product.setShortDescription(request.getShortDescription());
        }
        if (request.getFullDescription() != null) {
            product.setFullDescription(request.getFullDescription());
        }
        if (request.getBasePrice() != null) {
            product.setBasePrice(request.getBasePrice());
        }
        if (request.getSalePrice() != null) {
            product.setSalePrice(request.getSalePrice());
        }
        if (request.getIsActive() != null) {
            product.setIsActive(request.getIsActive());
        }
        if (request.getIsFeatured() != null) {
            product.setIsFeatured(request.getIsFeatured());
        }
        
        Product updated = productRepository.save(product);
        return toDTO(updated);
    }
    
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        product.setIsActive(false);
        productRepository.save(product);
    }
    
    public void incrementViewCount(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);
    }
    
    private ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .sku(product.getSku())
                .categoryId(product.getCategory().getId())
                .brand(product.getBrand())
                .shortDescription(product.getShortDescription())
                .fullDescription(product.getFullDescription())
                .basePrice(product.getBasePrice())
                .salePrice(product.getSalePrice())
                .isActive(product.getIsActive())
                .isFeatured(product.getIsFeatured())
                .viewCount(product.getViewCount())
                .soldCount(product.getSoldCount())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
