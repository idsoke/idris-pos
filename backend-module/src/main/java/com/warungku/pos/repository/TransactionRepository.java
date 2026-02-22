package com.warungku.pos.repository;

import com.warungku.pos.entity.Transaction;
import com.warungku.pos.entity.enums.TransactionStatus;
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
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Optional<Transaction> findByInvoiceNumber(String invoiceNumber);
    
    Page<Transaction> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.createdAt BETWEEN :start AND :end ORDER BY t.createdAt DESC")
    List<Transaction> findByDateRange(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT t FROM Transaction t WHERE t.status = :status ORDER BY t.createdAt DESC")
    List<Transaction> findByStatus(TransactionStatus status);
    
    @Query("SELECT SUM(t.total) FROM Transaction t WHERE t.createdAt BETWEEN :start AND :end AND t.status = 'COMPLETED'")
    BigDecimal sumTotalByDateRange(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.createdAt BETWEEN :start AND :end AND t.status = 'COMPLETED'")
    Long countByDateRange(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT t.cashier.id, t.cashier.name, COUNT(t), SUM(t.total) FROM Transaction t " +
           "WHERE t.createdAt BETWEEN :start AND :end AND t.status = 'COMPLETED' " +
           "GROUP BY t.cashier.id, t.cashier.name")
    List<Object[]> getCashierStats(LocalDateTime start, LocalDateTime end);
}
