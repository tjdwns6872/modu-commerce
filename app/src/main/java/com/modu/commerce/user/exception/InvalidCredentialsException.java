package com.modu.commerce.user.exception;

public class InvalidCredentialsException extends RuntimeException{
    
    public InvalidCredentialsException() {
        super("이메일 또는 비밀번호가 틀렸습니다.");
    }

}
