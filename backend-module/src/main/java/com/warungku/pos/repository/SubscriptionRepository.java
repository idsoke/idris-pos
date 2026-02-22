package com.warungku.pos.repository;

import com.warungku.pos.entity.subscription.Subscription;
import com.warungku.pos.entity.subscription.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByTenantId(Long tenantId);

    Optional<Subscription> findByTenantIdAndStatusIn(Long tenantId, List<SubscriptionStatus> statuses);

    @Query("SELECT s FROM Subscription s WHERE s.tenantId = :tenantId AND (s.status = 'ACTIVE' OR s.status = 'TRIAL')")
    Optional<Subscription> findActiveByTenantId(Long tenantId);

    List<Subscription> findByStatus(SubscriptionStatus status);

    @Query("SELECT s FROM Subscription s WHERE s.status = 'TRIAL' AND s.trialEndDate < :now")
    List<Subscription> findExpiredTrials(LocalDateTime now);

    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND s.endDate < :now")
    List<Subscription> findExpiredSubscriptions(LocalDateTime now);

    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND s.nextBillingDate <= :date AND s.autoRenew = true")
    List<Subscription> findDueForRenewal(LocalDateTime date);

    @Query("SELECT s FROM Subscription s WHERE s.status = 'PAST_DUE' AND s.endDate < :gracePeriodEnd")
    List<Subscription> findPastDueExpired(LocalDateTime gracePeriodEnd);

    @Modifying
    @Query("UPDATE Subscription s SET s.transactionsThisMonth = 0 WHERE s.status IN ('ACTIVE', 'TRIAL')")
    void resetMonthlyTransactionCounts();
}
