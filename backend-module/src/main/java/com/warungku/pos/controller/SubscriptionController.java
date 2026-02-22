package com.warungku.pos.controller;

import com.warungku.pos.dto.ApiResponse;
import com.warungku.pos.dto.subscription.*;
import com.warungku.pos.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Subscription Management Controller
 */
@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    /**
     * Register new tenant with free trial
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> registerTenant(
            @Valid @RequestBody TenantRegistrationRequest request) {
        SubscriptionResponse response = subscriptionService.registerTenant(request);
        return ResponseEntity.ok(ApiResponse.success(
                "Registration successful! Your 7-day free trial has started.", response));
    }

    /**
     * Get current subscription status
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getSubscriptionStatus() {
        return ResponseEntity.ok(ApiResponse.success(subscriptionService.getCurrentSubscription()));
    }

    /**
     * Get available plans
     */
    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<PlanResponse>>> getPlans() {
        return ResponseEntity.ok(ApiResponse.success(subscriptionService.getAvailablePlans()));
    }

    /**
     * Create payment for subscription upgrade/renewal
     */
    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<PaymentResponse>> subscribe(
            @Valid @RequestBody CreatePaymentRequest request) {
        PaymentResponse response = subscriptionService.createSubscriptionPayment(request);
        return ResponseEntity.ok(ApiResponse.success("Payment created. Please complete the payment.", response));
    }

    /**
     * Cancel subscription
     */
    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> cancelSubscription(
            @RequestBody Map<String, String> request) {
        String reason = request.getOrDefault("reason", "User requested cancellation");
        return ResponseEntity.ok(ApiResponse.success(
                "Subscription cancelled. You can continue using the service until the end of your billing period.",
                subscriptionService.cancelSubscription(reason)));
    }
}
