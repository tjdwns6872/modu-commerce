package com.modu.commerce.products.domain.exception;

import jakarta.validation.ValidationException;

public class InvalidProductNameException extends ValidationException{
    
    public InvalidProductNameException(){
        super("상품명(name)은 비어 있을 수 없습니다.");
    }
}
