package com.modu.commerce.user.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserLoginResponse {
    private Long id;
    private String email;
    private String token;
}
