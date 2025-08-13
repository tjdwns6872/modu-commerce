package com.modu.commerce.category.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class DuplicateCategoryNameUnderSameParent extends DataIntegrityViolationException{
    
    public DuplicateCategoryNameUnderSameParent() {
        super("동일한 상위 카테고리를 가진 이름이 존재합니다.");
    }
}
