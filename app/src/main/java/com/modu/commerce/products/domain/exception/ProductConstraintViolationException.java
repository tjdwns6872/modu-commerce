package com.modu.commerce.products.domain.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class ProductConstraintViolationException extends DataIntegrityViolationException{

    public ProductConstraintViolationException() {
        super("상품 저장 중 제약 조건 위반이 발생했습니다.");
    }
    
}
