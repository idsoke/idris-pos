package com.warungku.pos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Outlet entity - represents a tenant in the system.
 * Each outlet is a separate tenant with isolated data.
 */
@Entity
@Table(name = "outlets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Outlet extends BaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 500)
    private String address;
    
    @Column(length = 20)
    private String phone;
    
    private String email;
    
    @Column(name = "tax_rate", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal taxRate = new BigDecimal("0.10");
    
    @Column(length = 3)
    @Builder.Default
    private String currency = "IDR";
    
    @Column(length = 50)
    @Builder.Default
    private String timezone = "Asia/Jakarta";
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    private String logo;
    
    @Column(name = "receipt_header", length = 500)
    private String receiptHeader;
    
    @Column(name = "receipt_footer", length = 500)
    private String receiptFooter;
}
