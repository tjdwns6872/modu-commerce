package com.modu.commerce.user.dto;

import com.modu.commerce.user.RoleEnum;
import com.modu.commerce.user.StatusEnum;
import com.modu.commerce.user.entity.ModuUser;

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
    private RoleEnum role;
    private StatusEnum status;

    public static UserDetailResponse fromEntity(ModuUser user){
        return new UserDetailResponse(user.getId(), user.getEmail(), user.getNickname(), user.getRole(), user.getStatus());
    }
}
