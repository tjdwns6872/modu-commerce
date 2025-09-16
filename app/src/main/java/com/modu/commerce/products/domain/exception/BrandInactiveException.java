package com.modu.commerce.products.domain.exception;

public class BrandInactiveException extends RuntimeException{
    public BrandInactiveException(Long id){
        super(String.format("브랜드(id=%d)는 비활성 상태입니다.", id));
    }
}
