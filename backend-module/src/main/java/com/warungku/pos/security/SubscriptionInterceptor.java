package com.warungku.pos.security;

import com.warungku.pos.core.tenant.TenantContext;
import com.warungku.pos.service.SubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

/**
 * Interceptor to check subscription status before processing requests.
 * Blocks transaction APIs if subscription is expired.
 * SUPERADMIN bypasses all subscription checks.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionInterceptor implements HandlerInterceptor {

    private final SubscriptionService subscriptionService;

    // Endpoints that require active subscription
    private static final Set<String> PROTECTED_PATHS = Set.of(
            "/api/sales",
            "/api/products",
            "/api/transactions",
            "/api/reports",
            "/api/stock"
    );

    // Endpoints that are always allowed
    private static final Set<String> ALLOWED_PATHS = Set.of(
            "/api/auth",
            "/api/subscription",
            "/api/plans",
            "/api/billing",
            "/api/users/me",
            "/api/public"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();

        // Skip if no tenant context (not authenticated)
        if (TenantContext.getTenantId() == null) {
            return true;
        }

        // SUPERADMIN bypasses all subscription checks
        if (isSuperAdmin()) {
            return true;
        }

        // Skip allowed paths
        if (isAllowedPath(path)) {
            return true;
        }

        // Check subscription for protected paths
        if (isProtectedPath(path)) {
            try {
                subscriptionService.checkSubscriptionActive();
                
                // Also check transaction limit for sales endpoints
                if (path.startsWith("/api/sales") && "POST".equalsIgnoreCase(request.getMethod())) {
                    subscriptionService.checkTransactionLimit();
                }
            } catch (Exception e) {
                log.warn("Subscription check failed for tenant {}: {}", 
                        TenantContext.getTenantId(), e.getMessage());
                throw e; // Let exception handler deal with it
            }
        }

        return true;
    }

    private boolean isSuperAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"));
    }

    private boolean isProtectedPath(String path) {
        return PROTECTED_PATHS.stream().anyMatch(path::startsWith);
    }

    private boolean isAllowedPath(String path) {
        return ALLOWED_PATHS.stream().anyMatch(path::startsWith);
    }
}
