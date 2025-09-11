package com.modu.commerce.user.domain.type;

import com.modu.commerce.user.domain.exception.DormantAccountException;
import com.modu.commerce.user.domain.exception.StatusException;
import com.modu.commerce.user.domain.exception.WithdrawalAccountException;

public enum UserStatus {
    ACTIVE(200, "로그인에 성공했습니다."){
        @Override
        public StatusException getException(){
            return null;
        }
    }
    , INACTIVE(403, "휴면계정 입니다."){
        @Override
        public StatusException getException(){
            return new DormantAccountException();
        }
    }
    , WITHDRAWN(410, "탈퇴된 계정입니다."){
        @Override
        public StatusException getException(){
            return new WithdrawalAccountException();
        }
    };

    private final int code;
    private final String message;

    UserStatus(int code, String message){
        this.message = message;
        this.code = code;
    }

    public String getMessage(){
        return message;
    }

    public int getCode(){
        return code;
    }

    public abstract StatusException getException();
}
