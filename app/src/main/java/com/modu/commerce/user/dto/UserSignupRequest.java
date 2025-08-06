package com.modu.commerce.user.dto;

import com.modu.commerce.user.RoleEnum;
import com.modu.commerce.user.StatusEnum;
import com.modu.commerce.user.entity.ModuUser;

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

    public ModuUser toEntity(String password, RoleEnum role, StatusEnum status){
        return ModuUser.builder()
                .email(this.email)
                .password(password)
                .nickname(this.nickname)
                .role(role) 
                .status(status)
                .build();
    }
}
