package com.modu.commerce.category.exception;

import com.modu.commerce.common.exception.NotFoundException;

public class ParentCategoryNotFound extends NotFoundException{

    public ParentCategoryNotFound() {
        super("상위 카테고리를 찾을 수 없습니다.");
    }
    
}
