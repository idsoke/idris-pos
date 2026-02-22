package com.warungku.pos.repository;

import com.warungku.pos.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    List<SaleItem> findBySaleId(Long saleId);

    @Query("SELECT si.product.id, si.productName, SUM(si.quantity), SUM(si.subtotal) " +
           "FROM SaleItem si JOIN si.sale s " +
           "WHERE s.saleDate BETWEEN :start AND :end AND s.status = 'COMPLETED' " +
           "GROUP BY si.product.id, si.productName " +
           "ORDER BY SUM(si.quantity) DESC")
    List<Object[]> getTopSellingProducts(LocalDateTime start, LocalDateTime end);

    @Query("SELECT si.product.id, si.productName, SUM(si.quantity), SUM(si.subtotal) " +
           "FROM SaleItem si JOIN si.sale s " +
           "WHERE s.saleDate BETWEEN :start AND :end AND s.status = 'COMPLETED' " +
           "GROUP BY si.product.id, si.productName " +
           "ORDER BY SUM(si.subtotal) DESC")
    List<Object[]> getTopRevenueProducts(LocalDateTime start, LocalDateTime end);
}
