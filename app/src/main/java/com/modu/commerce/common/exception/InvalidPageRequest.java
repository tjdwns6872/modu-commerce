package com.modu.commerce.common.exception;

import org.apache.coyote.BadRequestException;

public class InvalidPageRequest extends BadRequestException{
    public InvalidPageRequest(String message){
        super(message);
    }
}
