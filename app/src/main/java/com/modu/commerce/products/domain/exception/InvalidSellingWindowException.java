package com.modu.commerce.products.domain.exception;

import jakarta.validation.ValidationException;

public class InvalidSellingWindowException extends ValidationException{
    
    public InvalidSellingWindowException(){
        super("판매 시작일은 종료일보다 이전이어야 합니다.");
    }
}
