package com.modu.commerce.user.service;

import com.modu.commerce.user.dto.UserLoginRequest;
import com.modu.commerce.user.dto.UserLoginResponse;
import com.modu.commerce.user.dto.UserSignupRequest;

public interface UserService {

    /**
     * 회원가입 기능
     * @param userSignupRequest
     * @return 유저 이메일
     */
    public String signup(UserSignupRequest userSignupRequest);

    /**
     * 일반 로그인 기능
     * @param userLoginRequest
     */
    public UserLoginResponse login(UserLoginRequest userLoginRequest);
}
