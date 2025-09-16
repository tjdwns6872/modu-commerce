package com.modu.commerce.products.domain.exception;

import com.modu.commerce.common.exception.NotFoundException;

public class CategoryNotFoundException extends NotFoundException{

    public CategoryNotFoundException(Long id) {
        super(String.format("카테고리(id=%d)가 존재하지 않습니다.", id));
    }
    
}
