package com.warungku.pos.controller;

import com.warungku.pos.dto.ApiResponse;
import com.warungku.pos.dto.ProductDto;
import com.warungku.pos.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Product Controller.
 * CRUD operations for products - scoped by tenant.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getProducts(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProducts(pageable)));
    }
    
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getAllProducts() {
        return ResponseEntity.ok(ApiResponse.success(productService.getAllProducts()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProduct(id)));
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductsByCategory(categoryId)));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductDto>>> searchProducts(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.success(productService.searchProducts(q)));
    }
    
    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductByBarcode(@PathVariable String barcode) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductByBarcode(barcode)));
    }
    
    @GetMapping("/low-stock")
    // @PreAuthorize("hasRole('ADMIN')") // TODO: Enable in production
    public ResponseEntity<ApiResponse<List<ProductDto>>> getLowStockProducts() {
        return ResponseEntity.ok(ApiResponse.success(productService.getLowStockProducts()));
    }
    
    @PostMapping
    // @PreAuthorize("hasRole('ADMIN')") // TODO: Enable in production
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(@Valid @RequestBody ProductDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Product created", productService.createProduct(dto)));
    }
    
    @PutMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')") // TODO: Enable in production
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
            @PathVariable Long id, 
            @Valid @RequestBody ProductDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Product updated", productService.updateProduct(id, dto)));
    }
    
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')") // TODO: Enable in production
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted", null));
    }
    
    @PatchMapping("/{id}/stock")
    // @PreAuthorize("hasRole('ADMIN')") // TODO: Enable in production
    public ResponseEntity<ApiResponse<ProductDto>> updateStock(
            @PathVariable Long id, 
            @RequestBody Map<String, Integer> request) {
        Integer quantity = request.get("quantity");
        return ResponseEntity.ok(ApiResponse.success(productService.updateStock(id, quantity)));
    }
}
