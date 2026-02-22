package com.warungku.pos.service;

import com.warungku.pos.dto.UserDto;
import com.warungku.pos.dto.auth.AuthResponse;
import com.warungku.pos.dto.auth.LoginRequest;
import com.warungku.pos.dto.auth.RegisterRequest;
import com.warungku.pos.entity.Outlet;
import com.warungku.pos.entity.User;
import com.warungku.pos.exception.BadRequestException;
import com.warungku.pos.exception.NotFoundException;
import com.warungku.pos.repository.OutletRepository;
import com.warungku.pos.repository.UserRepository;
import com.warungku.pos.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final OutletRepository outletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        
        Outlet outlet = outletRepository.findById(request.getTenantId())
                .orElseThrow(() -> new NotFoundException("Outlet", request.getTenantId()));
        
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole())
                .tenantId(outlet.getId())
                .isActive(true)
                .build();
        
        user = userRepository.save(user);
        log.info("New user registered: {} for tenant: {}", user.getEmail(), user.getTenantId());
        
        return generateAuthResponse(user);
    }
    
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        User user = userRepository.findByEmailAndActive(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        log.info("User logged in: {} for tenant: {}", user.getEmail(), user.getTenantId());
        
        return generateAuthResponse(user);
    }
    
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }
        
        String email = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmailAndActive(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        return generateAuthResponse(user);
    }
    
    private AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtService.generateToken(
                user.getId(),
                user.getTenantId(),
                user.getRole().name(),
                user.getEmail()
        );
        
        String refreshToken = jwtService.generateRefreshToken(
                user.getId(),
                user.getTenantId(),
                user.getEmail()
        );
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration / 1000)
                .user(UserDto.fromEntity(user))
                .build();
    }
}
