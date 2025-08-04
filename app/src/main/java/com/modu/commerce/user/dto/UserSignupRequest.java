package com.modu.commerce.user.dto;

import lombok.Data;

@Data
public class UserSignupRequest {
    
    private String email;
    private String password;
    private String nickname;
}
