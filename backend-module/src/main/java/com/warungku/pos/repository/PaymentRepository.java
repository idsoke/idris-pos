package com.warungku.pos.repository;

import com.warungku.pos.entity.subscription.Payment;
import com.warungku.pos.entity.subscription.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentNumber(String paymentNumber);

    Optional<Payment> findByExternalId(String externalId);

    List<Payment> findByInvoiceId(Long invoiceId);

    List<Payment> findByTenantIdAndStatus(Long tenantId, PaymentStatus status);
}
