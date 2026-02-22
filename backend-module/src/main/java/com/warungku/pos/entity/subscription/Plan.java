package com.warungku.pos.entity.subscription;

import com.warungku.pos.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plans")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Plan extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "monthly_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyPrice;

    @Column(name = "yearly_price", precision = 12, scale = 2)
    private BigDecimal yearlyPrice;

    @Column(length = 3)
    @Builder.Default
    private String currency = "IDR";

    @Column(name = "trial_days")
    @Builder.Default
    private Integer trialDays = 14;

    // Feature Limits
    @Column(name = "max_outlets")
    @Builder.Default
    private Integer maxOutlets = 1;

    @Column(name = "max_users")
    @Builder.Default
    private Integer maxUsers = 3;

    @Column(name = "max_products")
    @Builder.Default
    private Integer maxProducts = 100;

    @Column(name = "max_transactions_per_month")
    private Integer maxTransactionsPerMonth;

    // Feature Flags
    @Column(name = "feature_reports")
    @Builder.Default
    private Boolean featureReports = true;

    @Column(name = "feature_inventory")
    @Builder.Default
    private Boolean featureInventory = true;

    @Column(name = "feature_multi_outlet")
    @Builder.Default
    private Boolean featureMultiOutlet = false;

    @Column(name = "feature_api_access")
    @Builder.Default
    private Boolean featureApiAccess = false;

    @Column(name = "feature_export")
    @Builder.Default
    private Boolean featureExport = true;

    @Column(name = "feature_email_support")
    @Builder.Default
    private Boolean featureEmailSupport = true;

    @Column(name = "feature_priority_support")
    @Builder.Default
    private Boolean featurePrioritySupport = false;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_popular")
    @Builder.Default
    private Boolean isPopular = false;

    @OneToMany(mappedBy = "plan")
    @Builder.Default
    private List<Subscription> subscriptions = new ArrayList<>();
}
