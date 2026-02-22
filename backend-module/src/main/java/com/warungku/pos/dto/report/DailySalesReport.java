package com.warungku.pos.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailySalesReport {

    private LocalDate reportDate;
    private BigDecimal totalRevenue;
    private BigDecimal totalTax;
    private BigDecimal totalDiscount;
    private BigDecimal netRevenue;
    private Long transactionCount;
    private BigDecimal averageTransaction;
    private Integer totalItemsSold;
    private List<PaymentMethodStat> paymentMethods;
    private List<HourlyStat> hourlySales;
    private List<TopProduct> topProducts;
    private List<CashierStat> cashierPerformance;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentMethodStat {
        private String method;
        private Long count;
        private BigDecimal total;
        private BigDecimal percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HourlyStat {
        private Integer hour;
        private String timeRange;
        private Long count;
        private BigDecimal total;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProduct {
        private Long productId;
        private String productName;
        private Integer quantitySold;
        private BigDecimal revenue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CashierStat {
        private Long cashierId;
        private String cashierName;
        private Long transactionCount;
        private BigDecimal totalSales;
    }
}
