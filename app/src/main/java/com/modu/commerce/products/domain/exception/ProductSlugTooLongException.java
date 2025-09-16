package com.modu.commerce.products.domain.exception;

public class ProductSlugTooLongException extends RuntimeException{
    public ProductSlugTooLongException(){
        super("slug은 220자 이하여야 합니다.");
    }
}
