package com.warungku.pos.service;

import com.warungku.pos.dto.ProductDto;
import com.warungku.pos.entity.Category;
import com.warungku.pos.entity.Product;
import com.warungku.pos.exception.BadRequestException;
import com.warungku.pos.exception.NotFoundException;
import com.warungku.pos.repository.CategoryRepository;
import com.warungku.pos.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    public List<ProductDto> getAllProducts() {
        return productRepository.findAllActive().stream()
                .map(ProductDto::fromEntity)
                .toList();
    }
    
    public Page<ProductDto> getProducts(Pageable pageable) {
        return productRepository.findAllActive(pageable)
                .map(ProductDto::fromEntity);
    }
    
    public ProductDto getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product", id));
        return ProductDto.fromEntity(product);
    }
    
    public List<ProductDto> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(ProductDto::fromEntity)
                .toList();
    }
    
    public List<ProductDto> searchProducts(String query) {
        return productRepository.search(query).stream()
                .map(ProductDto::fromEntity)
                .toList();
    }
    
    public ProductDto getProductByBarcode(String barcode) {
        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new NotFoundException("Product not found with barcode: " + barcode));
        return ProductDto.fromEntity(product);
    }
    
    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        if (productRepository.findBySku(dto.getSku()).isPresent()) {
            throw new BadRequestException("SKU already exists: " + dto.getSku());
        }
        
        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .sku(dto.getSku())
                .price(dto.getPrice())
                .stock(dto.getStock() != null ? dto.getStock() : 0)
                .minStock(dto.getMinStock() != null ? dto.getMinStock() : 5)
                .image(dto.getImage())
                .barcode(dto.getBarcode())
                .isActive(true)
                .build();
        
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category", dto.getCategoryId()));
            product.setCategory(category);
        }
        
        product = productRepository.save(product);
        return ProductDto.fromEntity(product);
    }
    
    @Transactional
    public ProductDto updateProduct(Long id, ProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product", id));
        
        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getStock() != null) product.setStock(dto.getStock());
        if (dto.getMinStock() != null) product.setMinStock(dto.getMinStock());
        if (dto.getImage() != null) product.setImage(dto.getImage());
        if (dto.getBarcode() != null) product.setBarcode(dto.getBarcode());
        if (dto.getIsActive() != null) product.setIsActive(dto.getIsActive());
        
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category", dto.getCategoryId()));
            product.setCategory(category);
        }
        
        product = productRepository.save(product);
        return ProductDto.fromEntity(product);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product", id));
        product.setIsActive(false);
        productRepository.save(product);
    }
    
    @Transactional
    public ProductDto updateStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product", id));
        
        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            throw new BadRequestException("Insufficient stock. Available: " + product.getStock());
        }
        
        product.setStock(newStock);
        product = productRepository.save(product);
        return ProductDto.fromEntity(product);
    }
    
    public List<ProductDto> getLowStockProducts() {
        return productRepository.findLowStock().stream()
                .map(ProductDto::fromEntity)
                .toList();
    }
}
