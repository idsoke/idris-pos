package com.warungku.pos.security.jwt;

import com.warungku.pos.core.tenant.TenantContext;
import com.warungku.pos.security.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT Authentication Filter.
 * Extracts JWT from Authorization header, validates it, and sets up security context.
 * Also sets TenantContext for multi-tenant data filtering.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            // For development: set default tenant if no auth (TODO: remove in production)
            if (TenantContext.getTenantId() == null) {
                TenantContext.setTenantId(1L); // Default to tenant 1
                TenantContext.setUserId(1L);   // Default to user 1
            }
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            final String jwt = authHeader.substring(BEARER_PREFIX.length());
            
            if (jwtService.isTokenValid(jwt)) {
                final String username = jwtService.extractUsername(jwt);
                final Long userId = jwtService.extractUserId(jwt);
                final Long tenantId = jwtService.extractTenantId(jwt);
                final String role = jwtService.extractRole(jwt);
                
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Set tenant context for data filtering
                    TenantContext.setTenantId(tenantId);
                    TenantContext.setUserId(userId);
                    
                    // Create user principal with tenant info
                    UserPrincipal principal = UserPrincipal.builder()
                            .id(userId)
                            .email(username)
                            .tenantId(tenantId)
                            .role(role)
                            .build();
                    
                    // Create authentication token
                    List<SimpleGrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + role)
                    );
                    
                    UsernamePasswordAuthenticationToken authToken = 
                            new UsernamePasswordAuthenticationToken(principal, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    log.debug("Authenticated user: {} with role: {} for tenant: {}", 
                            username, role, tenantId);
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
}
