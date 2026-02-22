package com.warungku.pos.core.tenant;

/**
 * Interface for entities that belong to a specific tenant (outlet).
 * All tenant-aware entities must implement this interface.
 */
public interface TenantAware {
    Long getTenantId();
    void setTenantId(Long tenantId);
}
