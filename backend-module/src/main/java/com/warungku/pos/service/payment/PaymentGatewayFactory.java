package com.warungku.pos.service.payment;

import com.warungku.pos.entity.subscription.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Factory to select appropriate payment gateway
 */
@Component
@RequiredArgsConstructor
public class PaymentGatewayFactory {

    private final List<PaymentGateway> gateways;
    private final MockPaymentGateway mockPaymentGateway; // Default fallback

    public PaymentGateway getGateway(PaymentMethod method) {
        return gateways.stream()
                .filter(g -> g.supports(method))
                .findFirst()
                .orElse(mockPaymentGateway);
    }

    public PaymentGateway getGatewayByName(String name) {
        return gateways.stream()
                .filter(g -> g.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(mockPaymentGateway);
    }
}
