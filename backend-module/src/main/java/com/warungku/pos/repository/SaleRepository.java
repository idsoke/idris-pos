package com.warungku.pos.repository;

import com.warungku.pos.entity.Sale;
import com.warungku.pos.entity.enums.SaleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    Optional<Sale> findByReceiptNumber(String receiptNumber);

    Page<Sale> findAllByOrderBySaleDateDesc(Pageable pageable);

    @Query("SELECT s FROM Sale s WHERE s.saleDate BETWEEN :start AND :end ORDER BY s.saleDate DESC")
    List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT s FROM Sale s WHERE s.status = :status ORDER BY s.saleDate DESC")
    List<Sale> findByStatus(SaleStatus status);

    @Query("SELECT s FROM Sale s WHERE s.cashier.id = :cashierId AND s.saleDate BETWEEN :start AND :end")
    List<Sale> findByCashierAndDateRange(Long cashierId, LocalDateTime start, LocalDateTime end);

    // Report queries
    @Query("SELECT COALESCE(SUM(s.grandTotal), 0) FROM Sale s WHERE s.saleDate BETWEEN :start AND :end AND s.status = 'COMPLETED'")
    BigDecimal sumGrandTotalByDateRange(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.saleDate BETWEEN :start AND :end AND s.status = 'COMPLETED'")
    Long countByDateRange(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COALESCE(SUM(s.taxAmount), 0) FROM Sale s WHERE s.saleDate BETWEEN :start AND :end AND s.status = 'COMPLETED'")
    BigDecimal sumTaxByDateRange(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COALESCE(SUM(s.discountAmount), 0) FROM Sale s WHERE s.saleDate BETWEEN :start AND :end AND s.status = 'COMPLETED'")
    BigDecimal sumDiscountByDateRange(LocalDateTime start, LocalDateTime end);

    @Query("SELECT s.paymentMethod, COUNT(s), SUM(s.grandTotal) FROM Sale s " +
           "WHERE s.saleDate BETWEEN :start AND :end AND s.status = 'COMPLETED' " +
           "GROUP BY s.paymentMethod")
    List<Object[]> getPaymentMethodStats(LocalDateTime start, LocalDateTime end);

    @Query("SELECT HOUR(s.saleDate) as hour, COUNT(s), SUM(s.grandTotal) FROM Sale s " +
           "WHERE s.saleDate BETWEEN :start AND :end AND s.status = 'COMPLETED' " +
           "GROUP BY HOUR(s.saleDate) ORDER BY hour")
    List<Object[]> getHourlySales(LocalDateTime start, LocalDateTime end);

    @Query("SELECT s.cashier.id, s.cashier.name, COUNT(s), SUM(s.grandTotal) FROM Sale s " +
           "WHERE s.saleDate BETWEEN :start AND :end AND s.status = 'COMPLETED' " +
           "GROUP BY s.cashier.id, s.cashier.name")
    List<Object[]> getCashierPerformance(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(s) FROM Sale s WHERE DATE(s.saleDate) = CURRENT_DATE")
    Long countTodaySales();

    @Query("SELECT MAX(s.receiptNumber) FROM Sale s WHERE s.receiptNumber LIKE :prefix%")
    String findLastReceiptNumberByPrefix(String prefix);
}
