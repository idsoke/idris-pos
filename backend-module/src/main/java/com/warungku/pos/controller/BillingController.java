package com.warungku.pos.controller;

import com.warungku.pos.core.tenant.TenantContext;
import com.warungku.pos.dto.ApiResponse;
import com.warungku.pos.entity.subscription.Invoice;
import com.warungku.pos.entity.subscription.Payment;
import com.warungku.pos.repository.InvoiceRepository;
import com.warungku.pos.repository.PaymentRepository;
import com.warungku.pos.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Billing & Invoice Controller
 */
@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final SubscriptionService subscriptionService;

    /**
     * Get invoices for current tenant
     */
    @GetMapping("/invoices")
    public ResponseEntity<ApiResponse<Page<Invoice>>> getInvoices(
            @PageableDefault(size = 10) Pageable pageable) {
        Long tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(ApiResponse.success(
                invoiceRepository.findByTenantIdOrderByCreatedAtDesc(tenantId, pageable)));
    }

    /**
     * Get invoice by number
     */
    @GetMapping("/invoices/{invoiceNumber}")
    public ResponseEntity<ApiResponse<Invoice>> getInvoice(@PathVariable String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        return ResponseEntity.ok(ApiResponse.success(invoice));
    }

    /**
     * Get payments for an invoice
     */
    @GetMapping("/invoices/{invoiceId}/payments")
    public ResponseEntity<ApiResponse<List<Payment>>> getInvoicePayments(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(ApiResponse.success(paymentRepository.findByInvoiceId(invoiceId)));
    }

    /**
     * Payment webhook callback (from payment gateway)
     */
    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<String>> paymentWebhook(@RequestBody Map<String, Object> payload) {
        String externalId = (String) payload.get("external_id");
        String status = (String) payload.get("status");

        if (externalId != null && status != null) {
            subscriptionService.handlePaymentCallback(externalId, status);
        }

        return ResponseEntity.ok(ApiResponse.success("Webhook processed"));
    }
}
