package com.warungku.pos.repository;

import com.warungku.pos.entity.subscription.Invoice;
import com.warungku.pos.entity.subscription.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Page<Invoice> findByTenantIdOrderByCreatedAtDesc(Long tenantId, Pageable pageable);

    List<Invoice> findByTenantIdAndStatus(Long tenantId, InvoiceStatus status);

    @Query("SELECT i FROM Invoice i WHERE i.status = 'PENDING' AND i.dueDate < :now")
    List<Invoice> findOverdueInvoices(LocalDateTime now);

    @Query("SELECT MAX(i.invoiceNumber) FROM Invoice i WHERE i.invoiceNumber LIKE :prefix%")
    String findLastInvoiceNumberByPrefix(String prefix);
}
