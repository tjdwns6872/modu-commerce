package com.modu.commerce.user.domain.exception;

public class StatusException extends RuntimeException{

    protected int code;

    public StatusException(int code, String message){
        super(message);
        this.code = code;
    }
    public int getCode(){
        return this.code;
    }
}
