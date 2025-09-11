package com.modu.commerce.user.api.dto.response;

import com.modu.commerce.user.domain.entity.ModuUser;
import com.modu.commerce.user.domain.type.UserRole;
import com.modu.commerce.user.domain.type.UserStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDetailResponse {
    
    private Long userId;
    private String email;
    private String nickname;
    private UserRole role;
    private UserStatus status;

    public static UserDetailResponse fromEntity(ModuUser user){
        return new UserDetailResponse(user.getId(), user.getEmail(), user.getNickname(), user.getRole(), user.getStatus());
    }
}
