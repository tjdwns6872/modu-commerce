package com.modu.commerce.products.domain.exception;

public class ProductNameTooLongException extends RuntimeException{
    public ProductNameTooLongException(){
        super("상품명은 200자 이하여야 합니다.");
    }
}
