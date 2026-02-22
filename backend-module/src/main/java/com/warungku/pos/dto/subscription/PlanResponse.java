package com.warungku.pos.dto.subscription;

import com.warungku.pos.entity.subscription.Plan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponse {

    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal monthlyPrice;
    private BigDecimal yearlyPrice;
    private String currency;
    private Integer trialDays;
    private Boolean isPopular;
    private Limits limits;
    private Features features;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Limits {
        private Integer maxOutlets;
        private Integer maxUsers;
        private Integer maxProducts;
        private Integer maxTransactionsPerMonth;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Features {
        private Boolean reports;
        private Boolean inventory;
        private Boolean multiOutlet;
        private Boolean apiAccess;
        private Boolean export;
        private Boolean emailSupport;
        private Boolean prioritySupport;
    }

    public static PlanResponse fromEntity(Plan plan) {
        return PlanResponse.builder()
                .id(plan.getId())
                .code(plan.getCode())
                .name(plan.getName())
                .description(plan.getDescription())
                .monthlyPrice(plan.getMonthlyPrice())
                .yearlyPrice(plan.getYearlyPrice())
                .currency(plan.getCurrency())
                .trialDays(plan.getTrialDays())
                .isPopular(plan.getIsPopular())
                .limits(Limits.builder()
                        .maxOutlets(plan.getMaxOutlets())
                        .maxUsers(plan.getMaxUsers())
                        .maxProducts(plan.getMaxProducts())
                        .maxTransactionsPerMonth(plan.getMaxTransactionsPerMonth())
                        .build())
                .features(Features.builder()
                        .reports(plan.getFeatureReports())
                        .inventory(plan.getFeatureInventory())
                        .multiOutlet(plan.getFeatureMultiOutlet())
                        .apiAccess(plan.getFeatureApiAccess())
                        .export(plan.getFeatureExport())
                        .emailSupport(plan.getFeatureEmailSupport())
                        .prioritySupport(plan.getFeaturePrioritySupport())
                        .build())
                .build();
    }
}
