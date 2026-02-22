package com.warungku.pos.core.tenant;

import com.warungku.pos.exception.ForbiddenException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

/**
 * Aspect that enables Hibernate filter for multi-tenant data access.
 * Automatically filters all queries by tenant_id.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TenantAspect {
    
    private final EntityManager entityManager;
    
    public static final String TENANT_FILTER_NAME = "tenantFilter";
    public static final String TENANT_PARAMETER = "tenantId";
    
    @Around("execution(* com.warungku.pos.repository.*.*(..))")
    public Object enableTenantFilter(ProceedingJoinPoint joinPoint) throws Throwable {
        Long tenantId = TenantContext.getTenantId();
        
        if (tenantId != null) {
            Session session = entityManager.unwrap(Session.class);
            
            if (session.getEnabledFilter(TENANT_FILTER_NAME) == null) {
                log.debug("Enabling tenant filter for tenant_id: {}", tenantId);
                session.enableFilter(TENANT_FILTER_NAME)
                       .setParameter(TENANT_PARAMETER, tenantId);
            }
        }
        
        return joinPoint.proceed();
    }
}
