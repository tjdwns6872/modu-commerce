package com.modu.commerce.user.api.dto.request;

import com.modu.commerce.user.domain.entity.ModuUser;
import com.modu.commerce.user.domain.type.UserRole;
import com.modu.commerce.user.domain.type.UserStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserSignupRequest {

    @NotBlank(message = "Email cannot be null or empty")
    @Email
    private String email;

    @NotBlank(message = "Password cannot be null or empty")
    private String password;

    private String nickname;

    public ModuUser toEntity(String password, UserRole role, UserStatus status){
        return ModuUser.builder()
                .email(this.email)
                .password(password)
                .nickname(this.nickname)
                .role(role) 
                .status(status)
                .build();
    }
}
