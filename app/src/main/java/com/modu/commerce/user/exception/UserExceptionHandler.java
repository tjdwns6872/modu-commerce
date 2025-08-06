package com.modu.commerce.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.modu.commerce.common.api.response.CommonResponseVO;

@RestControllerAdvice
public class UserExceptionHandler {
    
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<CommonResponseVO<Void>> emailAlreadyExistsException(EmailAlreadyExistsException ex) {
        CommonResponseVO<Void> response = CommonResponseVO.<Void>builder()
            .code(HttpStatus.BAD_REQUEST.value())
            .message(ex.getMessage())
            .build();
        
        return ResponseEntity.badRequest().body(response);
    }
}
