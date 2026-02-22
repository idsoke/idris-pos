package com.warungku.pos.scheduler;

import com.warungku.pos.entity.Outlet;
import com.warungku.pos.entity.subscription.Invoice;
import com.warungku.pos.entity.subscription.InvoiceStatus;
import com.warungku.pos.entity.subscription.Subscription;
import com.warungku.pos.entity.subscription.SubscriptionStatus;
import com.warungku.pos.repository.InvoiceRepository;
import com.warungku.pos.repository.OutletRepository;
import com.warungku.pos.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled jobs for subscription management
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final OutletRepository outletRepository;
    private final InvoiceRepository invoiceRepository;

    private static final int GRACE_PERIOD_DAYS = 3;

    /**
     * Check for expired trials - runs daily at midnight
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void checkExpiredTrials() {
        log.info("Running expired trials check...");

        LocalDateTime now = LocalDateTime.now();
        List<Subscription> expiredTrials = subscriptionRepository.findExpiredTrials(now);

        for (Subscription subscription : expiredTrials) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            subscriptionRepository.save(subscription);

            // Deactivate tenant
            deactivateTenant(subscription.getTenantId());

            log.info("Trial expired for tenant {}: {}", 
                    subscription.getTenantId(), 
                    subscription.getTenant() != null ? subscription.getTenant().getName() : "Unknown");
        }

        log.info("Expired trials check completed. {} subscriptions expired.", expiredTrials.size());
    }

    /**
     * Check for expired subscriptions - runs daily at 1 AM
     */
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void checkExpiredSubscriptions() {
        log.info("Running expired subscriptions check...");

        LocalDateTime now = LocalDateTime.now();
        List<Subscription> expiredSubs = subscriptionRepository.findExpiredSubscriptions(now);

        for (Subscription subscription : expiredSubs) {
            // Move to past due with grace period
            subscription.setStatus(SubscriptionStatus.PAST_DUE);
            subscription.setEndDate(now.plusDays(GRACE_PERIOD_DAYS));
            subscriptionRepository.save(subscription);

            log.info("Subscription past due for tenant {}, grace period until {}", 
                    subscription.getTenantId(), 
                    subscription.getEndDate());
        }

        // Check past due subscriptions that exceeded grace period
        LocalDateTime gracePeriodEnd = now.minusDays(GRACE_PERIOD_DAYS);
        List<Subscription> pastDueExpired = subscriptionRepository.findPastDueExpired(gracePeriodEnd);

        for (Subscription subscription : pastDueExpired) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            subscriptionRepository.save(subscription);

            deactivateTenant(subscription.getTenantId());

            log.info("Subscription expired (grace period ended) for tenant {}", 
                    subscription.getTenantId());
        }

        log.info("Expired subscriptions check completed. {} past due, {} expired.", 
                expiredSubs.size(), pastDueExpired.size());
    }

    /**
     * Check for overdue invoices - runs daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void checkOverdueInvoices() {
        log.info("Running overdue invoices check...");

        LocalDateTime now = LocalDateTime.now();
        List<Invoice> overdueInvoices = invoiceRepository.findOverdueInvoices(now);

        for (Invoice invoice : overdueInvoices) {
            invoice.setStatus(InvoiceStatus.OVERDUE);
            invoiceRepository.save(invoice);

            log.info("Invoice {} marked as overdue for tenant {}", 
                    invoice.getInvoiceNumber(), invoice.getTenantId());
        }

        log.info("Overdue invoices check completed. {} invoices marked.", overdueInvoices.size());
    }

    /**
     * Reset monthly transaction counts - runs on 1st of every month
     */
    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void resetMonthlyTransactionCounts() {
        log.info("Resetting monthly transaction counts...");
        subscriptionRepository.resetMonthlyTransactionCounts();
        log.info("Monthly transaction counts reset completed.");
    }

    /**
     * Send renewal reminders - runs daily at 9 AM
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void sendRenewalReminders() {
        log.info("Checking for renewal reminders...");

        LocalDateTime sevenDaysFromNow = LocalDateTime.now().plusDays(7);
        List<Subscription> dueForRenewal = subscriptionRepository.findDueForRenewal(sevenDaysFromNow);

        for (Subscription subscription : dueForRenewal) {
            // TODO: Send email/notification reminder
            log.info("Renewal reminder needed for tenant {} (expires {})", 
                    subscription.getTenantId(), 
                    subscription.getEndDate());
        }

        log.info("Renewal reminders check completed. {} tenants need reminders.", dueForRenewal.size());
    }

    private void deactivateTenant(Long tenantId) {
        outletRepository.findById(tenantId).ifPresent(outlet -> {
            outlet.setIsActive(false);
            outletRepository.save(outlet);
            log.info("Tenant {} deactivated due to expired subscription", tenantId);
        });
    }
}
