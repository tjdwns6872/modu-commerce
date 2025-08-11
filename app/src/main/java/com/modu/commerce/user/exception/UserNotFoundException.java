package com.modu.commerce.user.exception;

import com.modu.commerce.common.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException{

    public UserNotFoundException() {
        super("존재하지 않는 회원입니다.");
    }
    
}
