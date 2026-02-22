package com.warungku.pos.dto.subscription;

import com.warungku.pos.entity.subscription.BillingCycle;
import com.warungku.pos.entity.subscription.Subscription;
import com.warungku.pos.entity.subscription.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {

    private Long id;
    private Long tenantId;
    private String tenantName;
    private PlanInfo plan;
    private SubscriptionStatus status;
    private BillingCycle billingCycle;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime trialEndDate;
    private LocalDateTime nextBillingDate;
    private BigDecimal price;
    private Boolean autoRenew;
    private Integer daysRemaining;
    private Boolean isActive;
    private Boolean isTrialExpired;
    private UsageInfo usage;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanInfo {
        private Long id;
        private String code;
        private String name;
        private BigDecimal monthlyPrice;
        private BigDecimal yearlyPrice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsageInfo {
        private Integer outletsUsed;
        private Integer maxOutlets;
        private Integer usersUsed;
        private Integer maxUsers;
        private Integer productsUsed;
        private Integer maxProducts;
        private Integer transactionsThisMonth;
        private Integer maxTransactionsPerMonth;
    }

    public static SubscriptionResponse fromEntity(Subscription sub) {
        return SubscriptionResponse.builder()
                .id(sub.getId())
                .tenantId(sub.getTenantId())
                .tenantName(sub.getTenant() != null ? sub.getTenant().getName() : null)
                .plan(PlanInfo.builder()
                        .id(sub.getPlan().getId())
                        .code(sub.getPlan().getCode())
                        .name(sub.getPlan().getName())
                        .monthlyPrice(sub.getPlan().getMonthlyPrice())
                        .yearlyPrice(sub.getPlan().getYearlyPrice())
                        .build())
                .status(sub.getStatus())
                .billingCycle(sub.getBillingCycle())
                .startDate(sub.getStartDate())
                .endDate(sub.getEndDate())
                .trialEndDate(sub.getTrialEndDate())
                .nextBillingDate(sub.getNextBillingDate())
                .price(sub.getPrice())
                .autoRenew(sub.getAutoRenew())
                .daysRemaining(sub.getDaysRemaining())
                .isActive(sub.isActive())
                .isTrialExpired(sub.isTrialExpired())
                .usage(UsageInfo.builder()
                        .outletsUsed(sub.getOutletsUsed())
                        .maxOutlets(sub.getPlan().getMaxOutlets())
                        .usersUsed(sub.getUsersUsed())
                        .maxUsers(sub.getPlan().getMaxUsers())
                        .productsUsed(sub.getProductsUsed())
                        .maxProducts(sub.getPlan().getMaxProducts())
                        .transactionsThisMonth(sub.getTransactionsThisMonth())
                        .maxTransactionsPerMonth(sub.getPlan().getMaxTransactionsPerMonth())
                        .build())
                .build();
    }
}
