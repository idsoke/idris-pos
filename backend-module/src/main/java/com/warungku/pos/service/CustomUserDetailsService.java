package com.warungku.pos.service;

import com.warungku.pos.entity.User;
import com.warungku.pos.repository.UserRepository;
import com.warungku.pos.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndActive(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        
        return UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .tenantId(user.getTenantId())
                .role(user.getRole().name())
                .active(user.getIsActive())
                .build();
    }
}
