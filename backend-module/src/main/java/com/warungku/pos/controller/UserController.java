package com.warungku.pos.controller;

import com.warungku.pos.core.tenant.TenantContext;
import com.warungku.pos.dto.ApiResponse;
import com.warungku.pos.dto.UserDto;
import com.warungku.pos.entity.User;
import com.warungku.pos.exception.NotFoundException;
import com.warungku.pos.repository.UserRepository;
import com.warungku.pos.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller.
 * Current user profile and info.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserRepository userRepository;
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new NotFoundException("User", principal.getId()));
        return ResponseEntity.ok(ApiResponse.success(UserDto.fromEntity(user)));
    }
    
    @GetMapping("/me/tenant")
    public ResponseEntity<ApiResponse<Long>> getCurrentTenant() {
        return ResponseEntity.ok(ApiResponse.success(TenantContext.getTenantId()));
    }
}
