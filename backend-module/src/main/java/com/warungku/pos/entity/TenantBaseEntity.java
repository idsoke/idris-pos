package com.warungku.pos.entity;

import com.warungku.pos.core.tenant.TenantAware;
import com.warungku.pos.core.tenant.TenantAspect;
import com.warungku.pos.core.tenant.TenantEntityListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

/**
 * Base entity for multi-tenant entities.
 * Automatically filters data by tenant_id.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(TenantEntityListener.class)
@FilterDef(
    name = TenantAspect.TENANT_FILTER_NAME,
    parameters = @ParamDef(name = TenantAspect.TENANT_PARAMETER, type = Long.class),
    defaultCondition = "tenant_id = :tenantId"
)
@Filter(name = TenantAspect.TENANT_FILTER_NAME)
public abstract class TenantBaseEntity extends BaseEntity implements TenantAware {
    
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
}
