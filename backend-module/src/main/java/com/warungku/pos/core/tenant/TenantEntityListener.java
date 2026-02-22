package com.warungku.pos.core.tenant;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;

/**
 * JPA Entity Listener that automatically sets tenant_id on persist and update.
 * Ensures all tenant-aware entities have the correct tenant_id.
 */
@Slf4j
public class TenantEntityListener {
    
    @PrePersist
    public void setTenantOnCreate(Object entity) {
        if (entity instanceof TenantAware tenantAware) {
            Long tenantId = TenantContext.getTenantId();
            if (tenantId != null && tenantAware.getTenantId() == null) {
                log.debug("Setting tenant_id {} on new entity {}", tenantId, entity.getClass().getSimpleName());
                tenantAware.setTenantId(tenantId);
            }
        }
    }
    
    @PreUpdate
    public void validateTenantOnUpdate(Object entity) {
        if (entity instanceof TenantAware tenantAware) {
            Long currentTenant = TenantContext.getTenantId();
            Long entityTenant = tenantAware.getTenantId();
            
            if (currentTenant != null && entityTenant != null && !currentTenant.equals(entityTenant)) {
                throw new SecurityException("Cross-tenant update attempt detected! " +
                    "Current tenant: " + currentTenant + ", Entity tenant: " + entityTenant);
            }
        }
    }
}
