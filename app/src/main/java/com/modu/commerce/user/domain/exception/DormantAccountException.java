package com.modu.commerce.user.domain.exception;

public class DormantAccountException extends StatusException{
    
    public DormantAccountException(){
        super(403, "휴면 계정입니다. 고객센터에 문의하세요.");
    }
}
