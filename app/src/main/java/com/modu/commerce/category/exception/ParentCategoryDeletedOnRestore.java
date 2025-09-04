package com.modu.commerce.category.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ParentCategoryDeletedOnRestore extends RuntimeException{
    public ParentCategoryDeletedOnRestore(String message){
        super(message);
    }
}
