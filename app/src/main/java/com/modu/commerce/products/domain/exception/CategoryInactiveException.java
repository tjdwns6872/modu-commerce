package com.modu.commerce.products.domain.exception;

public class CategoryInactiveException extends RuntimeException{
    public CategoryInactiveException(Long id){
        super(String.format("카테고리(id=%d)는 비활성 상태입니다.", id));
    }
}
