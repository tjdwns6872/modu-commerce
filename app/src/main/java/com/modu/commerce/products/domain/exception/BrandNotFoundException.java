package com.modu.commerce.products.domain.exception;

import com.modu.commerce.common.exception.NotFoundException;

public class BrandNotFoundException extends NotFoundException{

    public BrandNotFoundException(Long id) {
        super(String.format("브랜드(id=%d)가 존재하지 않습니다.", id));
    }
    
}
