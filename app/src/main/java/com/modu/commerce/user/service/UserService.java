package com.modu.commerce.user.service;

import com.modu.commerce.user.dto.UserSignupRequest;
import com.modu.commerce.user.dto.UserSignupResponse;

public interface UserService {

    public UserSignupResponse signup(UserSignupRequest request);
}
