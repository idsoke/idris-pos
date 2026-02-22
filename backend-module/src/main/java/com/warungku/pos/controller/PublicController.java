package com.warungku.pos.controller;

import com.warungku.pos.dto.ApiResponse;
import com.warungku.pos.dto.subscription.PlanResponse;
import com.warungku.pos.dto.subscription.SubscriptionResponse;
import com.warungku.pos.dto.subscription.TenantRegistrationRequest;
import com.warungku.pos.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Public endpoints (no authentication required)
 */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final SubscriptionService subscriptionService;

    /**
     * Get available subscription plans
     */
    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<PlanResponse>>> getPlans() {
        return ResponseEntity.ok(ApiResponse.success(subscriptionService.getAvailablePlans()));
    }

    /**
     * Register new tenant (public signup)
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> register(
            @Valid @RequestBody TenantRegistrationRequest request) {
        SubscriptionResponse response = subscriptionService.registerTenant(request);
        return ResponseEntity.ok(ApiResponse.success(
                "Registration successful! Your 7-day free trial has started.", response));
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "status", "UP",
                "service", "Warungku POS API",
                "version", "1.0.0"
        )));
    }
}
