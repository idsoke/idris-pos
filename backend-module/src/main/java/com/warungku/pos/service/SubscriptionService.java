package com.warungku.pos.service;

import com.warungku.pos.core.tenant.TenantContext;
import com.warungku.pos.dto.subscription.*;
import com.warungku.pos.entity.Outlet;
import com.warungku.pos.entity.User;
import com.warungku.pos.entity.enums.Role;
import com.warungku.pos.entity.subscription.*;
import com.warungku.pos.exception.BadRequestException;
import com.warungku.pos.exception.ForbiddenException;
import com.warungku.pos.exception.NotFoundException;
import com.warungku.pos.repository.*;
import com.warungku.pos.service.payment.PaymentGateway;
import com.warungku.pos.service.payment.PaymentGatewayFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final OutletRepository outletRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PaymentGatewayFactory paymentGatewayFactory;

    private static final int TRIAL_DAYS = 7;
    private static final BigDecimal PRICE_PER_OUTLET = new BigDecimal("40000");
    private static final BigDecimal TAX_RATE = new BigDecimal("0.11");
    private static final int GRACE_PERIOD_DAYS = 3;

    private final AtomicLong invoiceCounter = new AtomicLong(System.currentTimeMillis() % 10000);

    // ==================== TENANT REGISTRATION ====================

    @Transactional
    public SubscriptionResponse registerTenant(TenantRegistrationRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        // Create outlet (tenant)
        Outlet outlet = Outlet.builder()
                .name(request.getBusinessName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .taxRate(new BigDecimal("0.10"))
                .isActive(true)
                .build();
        outlet = outletRepository.save(outlet);

        // Create admin user
        User admin = User.builder()
                .name(request.getOwnerName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.ADMIN)
                .tenantId(outlet.getId())
                .isActive(true)
                .build();
        userRepository.save(admin);

        // Get trial plan or default plan
        Plan plan = planRepository.findByCode("STARTER")
                .orElseGet(() -> createDefaultPlan());

        // Create subscription with trial
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime trialEnd = now.plusDays(TRIAL_DAYS);

        Subscription subscription = Subscription.builder()
                .tenantId(outlet.getId())
                .plan(plan)
                .status(SubscriptionStatus.TRIAL)
                .billingCycle(BillingCycle.MONTHLY)
                .startDate(now)
                .endDate(trialEnd)
                .trialEndDate(trialEnd)
                .price(BigDecimal.ZERO)
                .autoRenew(true)
                .outletsUsed(1)
                .usersUsed(1)
                .build();
        subscription = subscriptionRepository.save(subscription);

        log.info("New tenant registered: {} with {} day trial", outlet.getName(), TRIAL_DAYS);

        return SubscriptionResponse.fromEntity(subscription);
    }

    // ==================== SUBSCRIPTION STATUS ====================

    public SubscriptionResponse getCurrentSubscription() {
        Long tenantId = TenantContext.getTenantId();
        Subscription subscription = subscriptionRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new NotFoundException("Subscription not found"));
        return SubscriptionResponse.fromEntity(subscription);
    }

    public boolean isSubscriptionActive(Long tenantId) {
        return subscriptionRepository.findActiveByTenantId(tenantId).isPresent();
    }

    public void checkSubscriptionActive() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null)
            return;

        Subscription subscription = subscriptionRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new ForbiddenException("No subscription found. Please subscribe to continue."));

        // Check if subscription is explicitly cancelled or expired
        if ((subscription.getStatus() != SubscriptionStatus.ACTIVE
                && subscription.getStatus() != SubscriptionStatus.TRIAL) ||
                (!subscription.isActive() && !subscription.isTrialActive())) {

            if (subscription.isTrialExpired()) {
                throw new ForbiddenException("Your free trial has ended. Please subscribe to continue.");
            }

            throw new ForbiddenException("Your subscription has expired. Please renew to continue.");
        }
    }

    // ==================== PLAN MANAGEMENT ====================

    public List<PlanResponse> getAvailablePlans() {
        return planRepository.findByIsActiveTrueOrderBySortOrder().stream()
                .map(PlanResponse::fromEntity)
                .toList();
    }

    // ==================== SUBSCRIPTION UPGRADE/RENEW ====================

    @Transactional
    public PaymentResponse createSubscriptionPayment(CreatePaymentRequest request) {
        Long tenantId = TenantContext.getTenantId();

        Subscription subscription = subscriptionRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new NotFoundException("Subscription not found"));

        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new NotFoundException("Plan", request.getPlanId()));

        // Calculate price
        int outlets = subscription.getOutletsUsed();
        BigDecimal basePrice = request.getBillingCycle() == BillingCycle.YEARLY
                ? plan.getYearlyPrice()
                : plan.getMonthlyPrice();

        // Price per outlet
        BigDecimal subtotal = basePrice.multiply(BigDecimal.valueOf(outlets));
        BigDecimal taxAmount = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.add(taxAmount);

        // Create invoice
        Invoice invoice = Invoice.builder()
                .invoiceNumber(generateInvoiceNumber())
                .tenantId(tenantId)
                .subscription(subscription)
                .billingPeriodStart(LocalDateTime.now())
                .billingPeriodEnd(request.getBillingCycle() == BillingCycle.YEARLY
                        ? LocalDateTime.now().plusYears(1)
                        : LocalDateTime.now().plusMonths(1))
                .subtotal(subtotal)
                .taxRate(TAX_RATE)
                .taxAmount(taxAmount)
                .totalAmount(totalAmount)
                .status(InvoiceStatus.PENDING)
                .dueDate(LocalDateTime.now().plusDays(1))
                .planName(plan.getName())
                .billingCycle(request.getBillingCycle().name())
                .build();
        invoice = invoiceRepository.save(invoice);

        // Create payment via gateway
        PaymentGateway gateway = paymentGatewayFactory.getGateway(request.getPaymentMethod());

        PaymentGateway.PaymentRequest paymentRequest = new PaymentGateway.PaymentRequest(
                invoice.getInvoiceNumber(),
                totalAmount,
                "IDR",
                request.getPaymentMethod(),
                subscription.getTenant().getName(),
                subscription.getTenant().getEmail(),
                subscription.getTenant().getPhone(),
                "Subscription: " + plan.getName(),
                "https://api.warungku.com/webhooks/payment",
                "https://app.warungku.com/billing");

        PaymentGateway.PaymentResult result = gateway.createPayment(paymentRequest);

        if (!result.success()) {
            throw new BadRequestException("Failed to create payment: " + result.message());
        }

        // Save payment record
        Payment payment = Payment.builder()
                .paymentNumber(generatePaymentNumber())
                .tenantId(tenantId)
                .invoice(invoice)
                .amount(totalAmount)
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .externalId(result.externalId())
                .paymentGateway(gateway.getName())
                .paymentUrl(result.paymentUrl())
                .qrCode(result.qrCode())
                .vaNumber(result.vaNumber())
                .expiredAt(LocalDateTime.now().plusHours(24))
                .build();
        payment = paymentRepository.save(payment);

        log.info("Payment created: {} for invoice {}", payment.getPaymentNumber(), invoice.getInvoiceNumber());

        return PaymentResponse.fromEntity(payment);
    }

    @Transactional
    public void handlePaymentCallback(String externalId, String status) {
        Payment payment = paymentRepository.findByExternalId(externalId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + externalId));

        if ("PAID".equalsIgnoreCase(status)) {
            // Update payment
            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidAt(LocalDateTime.now());
            paymentRepository.save(payment);

            // Update invoice
            Invoice invoice = payment.getInvoice();
            invoice.setStatus(InvoiceStatus.PAID);
            invoice.setPaidAt(LocalDateTime.now());
            invoice.setPaymentMethod(payment.getPaymentMethod().name());
            invoice.setPaymentReference(payment.getExternalId());
            invoiceRepository.save(invoice);

            // Activate subscription
            Subscription subscription = invoice.getSubscription();
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscription.setCurrentPeriodStart(invoice.getBillingPeriodStart());
            subscription.setCurrentPeriodEnd(invoice.getBillingPeriodEnd());
            subscription.setEndDate(invoice.getBillingPeriodEnd());
            subscription.setNextBillingDate(invoice.getBillingPeriodEnd().minusDays(7));
            subscriptionRepository.save(subscription);

            // Activate tenant
            Outlet outlet = outletRepository.findById(subscription.getTenantId()).orElse(null);
            if (outlet != null) {
                outlet.setIsActive(true);
                outletRepository.save(outlet);
            }

            log.info("Subscription activated for tenant {} until {}",
                    subscription.getTenantId(), subscription.getEndDate());
        } else if ("FAILED".equalsIgnoreCase(status) || "EXPIRED".equalsIgnoreCase(status)) {
            payment.setStatus(PaymentStatus.valueOf(status));
            paymentRepository.save(payment);
        }
    }

    // ==================== CANCEL SUBSCRIPTION ====================

    @Transactional
    public SubscriptionResponse cancelSubscription(String reason) {
        Long tenantId = TenantContext.getTenantId();

        Subscription subscription = subscriptionRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new NotFoundException("Subscription not found"));

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setCancelledAt(LocalDateTime.now());
        subscription.setCancelReason(reason);
        subscription.setAutoRenew(false);
        subscription = subscriptionRepository.save(subscription);

        log.info("Subscription cancelled for tenant {}: {}", tenantId, reason);

        return SubscriptionResponse.fromEntity(subscription);
    }

    // ==================== USAGE TRACKING ====================

    @Transactional
    public void incrementTransactionCount(Long tenantId) {
        subscriptionRepository.findByTenantId(tenantId).ifPresent(sub -> {
            sub.setTransactionsThisMonth(sub.getTransactionsThisMonth() + 1);
            subscriptionRepository.save(sub);
        });
    }

    public void checkTransactionLimit() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null)
            return;

        Subscription subscription = subscriptionRepository.findByTenantId(tenantId).orElse(null);
        if (subscription == null)
            return;

        Integer limit = subscription.getPlan().getMaxTransactionsPerMonth();
        if (limit != null && subscription.getTransactionsThisMonth() >= limit) {
            throw new ForbiddenException("Monthly transaction limit reached. Please upgrade your plan.");
        }
    }

    // ==================== HELPERS ====================

    private Plan createDefaultPlan() {
        Plan plan = Plan.builder()
                .code("STARTER")
                .name("Starter")
                .description("Perfect for small businesses")
                .monthlyPrice(PRICE_PER_OUTLET)
                .yearlyPrice(PRICE_PER_OUTLET.multiply(BigDecimal.valueOf(10))) // 2 months free
                .trialDays(TRIAL_DAYS)
                .maxOutlets(1)
                .maxUsers(3)
                .maxProducts(100)
                .maxTransactionsPerMonth(1000)
                .featureReports(true)
                .featureInventory(true)
                .featureMultiOutlet(false)
                .featureApiAccess(false)
                .isActive(true)
                .sortOrder(1)
                .build();
        return planRepository.save(plan);
    }

    private String generateInvoiceNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        long num = invoiceCounter.incrementAndGet();
        return String.format("INV-%s-%04d", date, num % 10000);
    }

    private String generatePaymentNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long num = System.currentTimeMillis() % 1000000;
        return String.format("PAY-%s-%06d", date, num);
    }
}
