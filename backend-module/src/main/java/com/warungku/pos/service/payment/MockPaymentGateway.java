package com.warungku.pos.service.payment;

import com.warungku.pos.entity.subscription.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock Payment Gateway for Development/Testing
 * Simulates Midtrans/Xendit behavior
 */
@Slf4j
@Service
public class MockPaymentGateway implements PaymentGateway {

    private final Map<String, PaymentResult> payments = new ConcurrentHashMap<>();

    @Override
    public String getName() {
        return "MOCK_GATEWAY";
    }

    @Override
    public boolean supports(PaymentMethod method) {
        return true; // Mock supports all methods
    }

    @Override
    public PaymentResult createPayment(PaymentRequest request) {
        String externalId = "MOCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        log.info("Mock payment created: {} for {} {}", externalId, request.currency(), request.amount());

        PaymentResult result;
        
        switch (request.method()) {
            case QRIS -> {
                // Generate mock QR code (Base64 placeholder)
                String qrCode = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==";
                result = PaymentResult.successWithQr(externalId, "PENDING", qrCode);
            }
            case VIRTUAL_ACCOUNT, BANK_TRANSFER -> {
                // Generate mock VA number
                String vaNumber = "8888" + String.format("%012d", System.currentTimeMillis() % 1000000000000L);
                result = PaymentResult.successWithVa(externalId, "PENDING", vaNumber);
            }
            case EWALLET_OVO, EWALLET_GOPAY, EWALLET_DANA, EWALLET_SHOPEEPAY -> {
                // Generate mock deep link
                String paymentUrl = "https://mock-payment.warungku.com/pay/" + externalId;
                result = PaymentResult.success(externalId, "PENDING", paymentUrl);
            }
            default -> {
                String paymentUrl = "https://mock-payment.warungku.com/checkout/" + externalId;
                result = PaymentResult.success(externalId, "PENDING", paymentUrl);
            }
        }

        payments.put(externalId, result);
        return result;
    }

    @Override
    public PaymentResult checkStatus(String externalId) {
        PaymentResult stored = payments.get(externalId);
        if (stored == null) {
            return PaymentResult.failure("Payment not found: " + externalId);
        }
        return stored;
    }

    @Override
    public PaymentResult cancelPayment(String externalId) {
        payments.remove(externalId);
        return new PaymentResult(true, externalId, "CANCELLED", null, null, null, "Payment cancelled", null);
    }

    /**
     * Simulate payment completion (for testing)
     */
    public PaymentResult simulatePaymentSuccess(String externalId) {
        PaymentResult stored = payments.get(externalId);
        if (stored == null) {
            return PaymentResult.failure("Payment not found");
        }

        PaymentResult paid = new PaymentResult(
                true,
                externalId,
                "PAID",
                stored.paymentUrl(),
                stored.qrCode(),
                stored.vaNumber(),
                "Payment successful",
                null
        );
        payments.put(externalId, paid);
        log.info("Mock payment {} marked as PAID", externalId);
        return paid;
    }
}
