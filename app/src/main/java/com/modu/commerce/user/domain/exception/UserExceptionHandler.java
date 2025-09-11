package com.modu.commerce.user.domain.exception;

import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.modu.commerce.common.api.response.CommonResponseVO;
import com.modu.commerce.common.exception.UnauthorizedException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "com.modu.commerce.user")
public class UserExceptionHandler {
    
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<CommonResponseVO<Void>> emailAlreadyExistsException(EmailAlreadyExistsException ex) {
        CommonResponseVO<Void> response = CommonResponseVO.<Void>builder()
            .code(HttpStatus.BAD_REQUEST.value())
            .message(ex.getMessage())
            .build();
        
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<CommonResponseVO<Void>> unauthorizedException(UnauthorizedException ex){
        CommonResponseVO<Void> response = CommonResponseVO.<Void>builder()
            .code(HttpStatus.UNAUTHORIZED.value())
            .message(ex.getMessage())
            .build();
        
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @ExceptionHandler(StatusException.class)
    public ResponseEntity<CommonResponseVO<Void>> statusException(StatusException ex){
        CommonResponseVO<Void> response = CommonResponseVO.<Void>builder()
            .code(ex.getCode())
            .message(ex.getMessage())
            .build();
        
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CommonResponseVO<Void>> notFoundException(NotFoundException ex){
        CommonResponseVO<Void> response = CommonResponseVO.<Void>builder()
            .code(HttpStatus.NOT_FOUND.value())
            .message(ex.getMessage())
            .build();
        
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
