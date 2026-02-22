package com.warungku.pos.dto.subscription;

import com.warungku.pos.entity.subscription.Payment;
import com.warungku.pos.entity.subscription.PaymentMethod;
import com.warungku.pos.entity.subscription.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private String paymentNumber;
    private Long invoiceId;
    private String invoiceNumber;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String paymentUrl;
    private String qrCode;
    private String vaNumber;
    private LocalDateTime expiredAt;
    private LocalDateTime paidAt;

    public static PaymentResponse fromEntity(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentNumber(payment.getPaymentNumber())
                .invoiceId(payment.getInvoice().getId())
                .invoiceNumber(payment.getInvoice().getInvoiceNumber())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .paymentUrl(payment.getPaymentUrl())
                .qrCode(payment.getQrCode())
                .vaNumber(payment.getVaNumber())
                .expiredAt(payment.getExpiredAt())
                .paidAt(payment.getPaidAt())
                .build();
    }
}
