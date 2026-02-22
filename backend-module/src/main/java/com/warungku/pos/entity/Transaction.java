package com.warungku.pos.entity;

import com.warungku.pos.entity.enums.PaymentMethod;
import com.warungku.pos.entity.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.warungku.pos.core.tenant.TenantAspect.TENANT_FILTER_NAME;

/**
 * Transaction entity - tenant-scoped.
 */
@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_trx_tenant", columnList = "tenant_id"),
    @Index(name = "idx_trx_invoice", columnList = "invoice_number"),
    @Index(name = "idx_trx_date", columnList = "created_at")
})
@Filter(name = TENANT_FILTER_NAME)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction extends TenantBaseEntity {
    
    @Column(name = "invoice_number", nullable = false, unique = true, length = 50)
    private String invoiceNumber;
    
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;
    
    @Column(nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal tax = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal total;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;
    
    @Column(name = "cash_received", precision = 14, scale = 2)
    private BigDecimal cashReceived;
    
    @Column(name = "cash_change", precision = 14, scale = 2)
    private BigDecimal cashChange;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.COMPLETED;
    
    @Column(length = 500)
    private String notes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_id", nullable = false)
    private User cashier;
    
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TransactionItem> items = new ArrayList<>();
    
    public void addItem(TransactionItem item) {
        items.add(item);
        item.setTransaction(this);
    }
}
