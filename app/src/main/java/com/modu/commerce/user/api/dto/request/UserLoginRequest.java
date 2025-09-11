package com.modu.commerce.user.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequest {

    @NotBlank(message = "Email cannot be null or empty")
    private String email;

    @NotBlank(message = "Password cannot be null or empty")
    private String password;
}
