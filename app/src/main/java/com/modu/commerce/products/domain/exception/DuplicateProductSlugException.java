package com.modu.commerce.products.domain.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class DuplicateProductSlugException extends DataIntegrityViolationException{
    public DuplicateProductSlugException(){
        super("이미 존재하는 Slug 입니다.");
    }
}
