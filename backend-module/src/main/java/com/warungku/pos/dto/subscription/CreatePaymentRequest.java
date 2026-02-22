package com.warungku.pos.dto.subscription;

import com.warungku.pos.entity.subscription.BillingCycle;
import com.warungku.pos.entity.subscription.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePaymentRequest {

    @NotNull(message = "Plan ID is required")
    private Long planId;

    @NotNull(message = "Billing cycle is required")
    private BillingCycle billingCycle;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}
