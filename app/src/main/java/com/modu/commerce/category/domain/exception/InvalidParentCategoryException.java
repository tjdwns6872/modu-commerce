package com.modu.commerce.category.domain.exception;

import jakarta.validation.ValidationException;

public class InvalidParentCategoryException extends ValidationException{
    
    public InvalidParentCategoryException(String message){
        super(message);
    }
}
