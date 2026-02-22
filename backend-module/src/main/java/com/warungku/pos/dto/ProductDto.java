package com.warungku.pos.dto;

import com.warungku.pos.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private Integer stock;
    private Integer minStock;
    private String image;
    private String barcode;
    private Long categoryId;
    private String categoryName;
    private Boolean isActive;
    private Boolean lowStock;
    
    public static ProductDto fromEntity(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .sku(product.getSku())
                .price(product.getPrice())
                .stock(product.getStock())
                .minStock(product.getMinStock())
                .image(product.getImage())
                .barcode(product.getBarcode())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .isActive(product.getIsActive())
                .lowStock(product.getStock() <= product.getMinStock())
                .build();
    }
}
