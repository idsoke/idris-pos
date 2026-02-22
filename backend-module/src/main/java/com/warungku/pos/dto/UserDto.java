package com.warungku.pos.dto;

import com.warungku.pos.entity.User;
import com.warungku.pos.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private Long tenantId;
    private String outletName;
    private String avatar;
    
    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .tenantId(user.getTenantId())
                .outletName(user.getOutlet() != null ? user.getOutlet().getName() : null)
                .avatar(user.getAvatar())
                .build();
    }
}
