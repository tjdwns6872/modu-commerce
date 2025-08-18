package com.modu.commerce.category.exception;

import jakarta.validation.ValidationException;

public class InvalidCategoryNameException extends ValidationException{
    
    public InvalidCategoryNameException(String message){
        super(message);
    }
}
