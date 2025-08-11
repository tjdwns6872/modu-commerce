package com.modu.commerce.user.exception;

import com.modu.commerce.common.exception.UnauthorizedException;

public class InvalidCredentialsException extends UnauthorizedException{
    
    public InvalidCredentialsException() {
        super("이메일 또는 비밀번호가 틀렸습니다.");
    }

}
