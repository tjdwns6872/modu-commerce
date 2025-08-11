package com.modu.commerce.user.exception;

public class WithdrawalAccountException extends StatusException{

    public WithdrawalAccountException() {
        super(410, "탈퇴한 계정입니다.");
    }
    
}
