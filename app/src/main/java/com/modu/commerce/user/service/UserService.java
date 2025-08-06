package com.modu.commerce.user.service;

import com.modu.commerce.user.dto.UserSignupRequest;

public interface UserService {

    /**
     * 회원가입 기능
     * @param userSignupRequest
     */
    public String signup(UserSignupRequest userSignupRequest);
}
