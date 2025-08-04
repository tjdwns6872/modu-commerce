package com.modu.commerce.user.service;

import org.springframework.stereotype.Service;

import com.modu.commerce.user.dto.UserSignupRequest;
import com.modu.commerce.user.dto.UserSignupResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    @Override
    public UserSignupResponse signup(UserSignupRequest request) {
        return null;
    }
    
}
