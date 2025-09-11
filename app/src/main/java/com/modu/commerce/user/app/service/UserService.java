package com.modu.commerce.user.app.service;

import com.modu.commerce.user.api.dto.request.UserLoginRequest;
import com.modu.commerce.user.api.dto.request.UserSignupRequest;
import com.modu.commerce.user.api.dto.response.UserDetailResponse;
import com.modu.commerce.user.api.dto.response.UserLoginResponse;

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

    /**
     * 회원정보 
     * @param userId
     * @return UserDetailResponse(회원정보)
     */
    public UserDetailResponse userInfo(Long userId); 
}
