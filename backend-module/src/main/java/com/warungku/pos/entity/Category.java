package com.warungku.pos.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;

import static com.warungku.pos.core.tenant.TenantAspect.TENANT_FILTER_NAME;

/**
 * Category entity - tenant-scoped.
 */
@Entity
@Table(name = "categories", indexes = {
    @Index(name = "idx_category_tenant", columnList = "tenant_id")
})
@Filter(name = TENANT_FILTER_NAME)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Category extends TenantBaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    private String icon;
    
    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}
