package com.warungku.pos.entity.subscription;

import com.warungku.pos.entity.BaseEntity;
import com.warungku.pos.entity.Outlet;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions", indexes = {
        @Index(name = "idx_sub_tenant", columnList = "tenant_id"),
        @Index(name = "idx_sub_status", columnList = "status"),
        @Index(name = "idx_sub_end_date", columnList = "end_date")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", insertable = false, updatable = false)
    private Outlet tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SubscriptionStatus status = SubscriptionStatus.TRIAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle", nullable = false, length = 20)
    @Builder.Default
    private BillingCycle billingCycle = BillingCycle.MONTHLY;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "trial_end_date")
    private LocalDateTime trialEndDate;

    @Column(name = "next_billing_date")
    private LocalDateTime nextBillingDate;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;

    @Column(name = "current_period_start")
    private LocalDateTime currentPeriodStart;

    @Column(name = "current_period_end")
    private LocalDateTime currentPeriodEnd;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "auto_renew")
    @Builder.Default
    private Boolean autoRenew = true;

    // Usage tracking
    @Column(name = "outlets_used")
    @Builder.Default
    private Integer outletsUsed = 1;

    @Column(name = "users_used")
    @Builder.Default
    private Integer usersUsed = 1;

    @Column(name = "products_used")
    @Builder.Default
    private Integer productsUsed = 0;

    @Column(name = "transactions_this_month")
    @Builder.Default
    private Integer transactionsThisMonth = 0;

    public boolean isTrialActive() {
        return status == SubscriptionStatus.TRIAL &&
                trialEndDate != null &&
                LocalDateTime.now().isBefore(trialEndDate);
    }

    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE || status == SubscriptionStatus.TRIAL;
    }

    public boolean isTrialExpired() {
        return status == SubscriptionStatus.TRIAL &&
                trialEndDate != null &&
                LocalDateTime.now().isAfter(trialEndDate);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(endDate);
    }

    public int getDaysRemaining() {
        if (isExpired())
            return 0;
        return (int) java.time.Duration.between(LocalDateTime.now(), endDate).toDays();
    }
}
