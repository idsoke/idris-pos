package com.warungku.pos.service.payment;

import com.warungku.pos.entity.subscription.Payment;
import com.warungku.pos.entity.subscription.PaymentMethod;

import java.math.BigDecimal;

/**
 * Abstract Payment Gateway Interface
 * Implement this for Midtrans, Xendit, or other payment providers
 */
public interface PaymentGateway {

    /**
     * Get the gateway name
     */
    String getName();

    /**
     * Check if this gateway supports the payment method
     */
    boolean supports(PaymentMethod method);

    /**
     * Create a payment request
     */
    PaymentResult createPayment(PaymentRequest request);

    /**
     * Check payment status
     */
    PaymentResult checkStatus(String externalId);

    /**
     * Cancel/expire a payment
     */
    PaymentResult cancelPayment(String externalId);

    /**
     * Payment Request DTO
     */
    record PaymentRequest(
            String orderId,
            BigDecimal amount,
            String currency,
            PaymentMethod method,
            String customerName,
            String customerEmail,
            String customerPhone,
            String description,
            String callbackUrl,
            String redirectUrl
    ) {}

    /**
     * Payment Result DTO
     */
    record PaymentResult(
            boolean success,
            String externalId,
            String status,
            String paymentUrl,
            String qrCode,
            String vaNumber,
            String message,
            String rawResponse
    ) {
        public static PaymentResult success(String externalId, String status, String paymentUrl) {
            return new PaymentResult(true, externalId, status, paymentUrl, null, null, "Success", null);
        }

        public static PaymentResult successWithQr(String externalId, String status, String qrCode) {
            return new PaymentResult(true, externalId, status, null, qrCode, null, "Success", null);
        }

        public static PaymentResult successWithVa(String externalId, String status, String vaNumber) {
            return new PaymentResult(true, externalId, status, null, null, vaNumber, "Success", null);
        }

        public static PaymentResult failure(String message) {
            return new PaymentResult(false, null, "FAILED", null, null, null, message, null);
        }
    }
}
