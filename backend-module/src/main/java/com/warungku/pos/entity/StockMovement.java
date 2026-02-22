package com.warungku.pos.entity;

import com.warungku.pos.entity.enums.MovementType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;

import java.time.LocalDateTime;

import static com.warungku.pos.core.tenant.TenantAspect.TENANT_FILTER_NAME;

@Entity
@Table(name = "stock_movements", indexes = {
    @Index(name = "idx_stock_tenant", columnList = "tenant_id"),
    @Index(name = "idx_stock_product", columnList = "product_id"),
    @Index(name = "idx_stock_date", columnList = "movement_date"),
    @Index(name = "idx_stock_type", columnList = "movement_type")
})
@Filter(name = TENANT_FILTER_NAME)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement extends TenantBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 20)
    private MovementType movementType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "stock_before", nullable = false)
    private Integer stockBefore;

    @Column(name = "stock_after", nullable = false)
    private Integer stockAfter;

    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;

    @Column(name = "reference_type", length = 50)
    private String referenceType;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(length = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
}
