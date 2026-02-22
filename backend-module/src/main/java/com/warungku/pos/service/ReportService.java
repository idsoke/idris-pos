package com.warungku.pos.service;

import com.warungku.pos.dto.report.DailySalesReport;
import com.warungku.pos.repository.SaleItemRepository;
import com.warungku.pos.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;

    public DailySalesReport getDailySalesReport(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        // Basic stats
        BigDecimal totalRevenue = saleRepository.sumGrandTotalByDateRange(start, end);
        BigDecimal totalTax = saleRepository.sumTaxByDateRange(start, end);
        BigDecimal totalDiscount = saleRepository.sumDiscountByDateRange(start, end);
        Long transactionCount = saleRepository.countByDateRange(start, end);

        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        if (totalTax == null) totalTax = BigDecimal.ZERO;
        if (totalDiscount == null) totalDiscount = BigDecimal.ZERO;
        if (transactionCount == null) transactionCount = 0L;

        BigDecimal netRevenue = totalRevenue.subtract(totalTax);
        BigDecimal avgTransaction = transactionCount > 0
                ? totalRevenue.divide(BigDecimal.valueOf(transactionCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Payment method stats
        List<DailySalesReport.PaymentMethodStat> paymentStats = new ArrayList<>();
        List<Object[]> paymentData = saleRepository.getPaymentMethodStats(start, end);
        for (Object[] row : paymentData) {
            BigDecimal methodTotal = (BigDecimal) row[2];
            BigDecimal percentage = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                    ? methodTotal.multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            paymentStats.add(DailySalesReport.PaymentMethodStat.builder()
                    .method(row[0].toString())
                    .count((Long) row[1])
                    .total(methodTotal)
                    .percentage(percentage)
                    .build());
        }

        // Hourly sales
        List<DailySalesReport.HourlyStat> hourlyStats = new ArrayList<>();
        List<Object[]> hourlyData = saleRepository.getHourlySales(start, end);
        for (Object[] row : hourlyData) {
            Integer hour = (Integer) row[0];
            hourlyStats.add(DailySalesReport.HourlyStat.builder()
                    .hour(hour)
                    .timeRange(String.format("%02d:00 - %02d:59", hour, hour))
                    .count((Long) row[1])
                    .total((BigDecimal) row[2])
                    .build());
        }

        // Top products
        List<DailySalesReport.TopProduct> topProducts = new ArrayList<>();
        List<Object[]> productData = saleItemRepository.getTopSellingProducts(start, end);
        int limit = Math.min(10, productData.size());
        for (int i = 0; i < limit; i++) {
            Object[] row = productData.get(i);
            topProducts.add(DailySalesReport.TopProduct.builder()
                    .productId((Long) row[0])
                    .productName((String) row[1])
                    .quantitySold(((Long) row[2]).intValue())
                    .revenue((BigDecimal) row[3])
                    .build());
        }

        // Cashier performance
        List<DailySalesReport.CashierStat> cashierStats = new ArrayList<>();
        List<Object[]> cashierData = saleRepository.getCashierPerformance(start, end);
        for (Object[] row : cashierData) {
            cashierStats.add(DailySalesReport.CashierStat.builder()
                    .cashierId((Long) row[0])
                    .cashierName((String) row[1])
                    .transactionCount((Long) row[2])
                    .totalSales((BigDecimal) row[3])
                    .build());
        }

        // Calculate total items sold
        int totalItemsSold = topProducts.stream()
                .mapToInt(DailySalesReport.TopProduct::getQuantitySold)
                .sum();

        return DailySalesReport.builder()
                .reportDate(date)
                .totalRevenue(totalRevenue)
                .totalTax(totalTax)
                .totalDiscount(totalDiscount)
                .netRevenue(netRevenue)
                .transactionCount(transactionCount)
                .averageTransaction(avgTransaction)
                .totalItemsSold(totalItemsSold)
                .paymentMethods(paymentStats)
                .hourlySales(hourlyStats)
                .topProducts(topProducts)
                .cashierPerformance(cashierStats)
                .build();
    }

    public List<DailySalesReport> getWeeklySalesReport(LocalDate startDate) {
        List<DailySalesReport> reports = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            reports.add(getDailySalesReport(startDate.plusDays(i)));
        }
        return reports;
    }
}
