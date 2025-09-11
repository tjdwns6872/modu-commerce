package com.modu.commerce.category.domain.exception;

import jakarta.validation.ValidationException;

public class InvalidCategoryNameException extends ValidationException{
    
    public InvalidCategoryNameException(String message){
        super(message);
    }
}
