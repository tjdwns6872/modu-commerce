package com.modu.commerce.category.exception;

import com.modu.commerce.common.exception.NotFoundException;

public class CategoryNotFoundException extends NotFoundException{

    public CategoryNotFoundException() {
        super("존재하지 않는 카테고리입니다.");
    }
    
}
