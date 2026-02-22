package com.warungku.pos.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;

import java.math.BigDecimal;

import static com.warungku.pos.core.tenant.TenantAspect.TENANT_FILTER_NAME;

/**
 * Product entity - tenant-scoped.
 */
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_sku", columnList = "sku"),
    @Index(name = "idx_product_tenant", columnList = "tenant_id"),
    @Index(name = "idx_product_category", columnList = "category_id")
})
@Filter(name = TENANT_FILTER_NAME)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends TenantBaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false, length = 50)
    private String sku;
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal costPrice;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 0;
    
    @Column(name = "min_stock")
    @Builder.Default
    private Integer minStock = 5;
    
    @Column(columnDefinition = "MEDIUMTEXT")
    private String image;
    
    private String barcode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}
