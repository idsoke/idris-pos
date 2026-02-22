package com.warungku.pos.core.tenant;

import lombok.extern.slf4j.Slf4j;

/**
 * Thread-local storage for current tenant context.
 * Stores tenant_id for the current request to enable multi-tenant data filtering.
 */
@Slf4j
public final class TenantContext {
    
    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();
    private static final ThreadLocal<Long> CURRENT_USER = new ThreadLocal<>();
    
    private TenantContext() {}
    
    public static void setTenantId(Long tenantId) {
        log.debug("Setting tenant context: {}", tenantId);
        CURRENT_TENANT.set(tenantId);
    }
    
    public static Long getTenantId() {
        return CURRENT_TENANT.get();
    }
    
    public static void setUserId(Long userId) {
        CURRENT_USER.set(userId);
    }
    
    public static Long getUserId() {
        return CURRENT_USER.get();
    }
    
    public static void clear() {
        log.debug("Clearing tenant context");
        CURRENT_TENANT.remove();
        CURRENT_USER.remove();
    }
    
    public static boolean hasTenant() {
        return CURRENT_TENANT.get() != null;
    }
}
