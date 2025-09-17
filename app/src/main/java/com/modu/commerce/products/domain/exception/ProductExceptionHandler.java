package com.modu.commerce.products.domain.exception;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.modu.commerce.common.api.response.CommonResponseVO;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "com.modu.commerce.products")
public class ProductExceptionHandler {
    
    @ExceptionHandler(InvalidProductNameException.class)
    public ResponseEntity<CommonResponseVO<Void>> invalidProductNameException(InvalidProductNameException ex) {
        CommonResponseVO<Void> body = CommonResponseVO.<Void>builder()
            .code(HttpStatus.BAD_REQUEST.value())
            .message(ex.getMessage())
            .build();
        return ResponseEntity.badRequest().body(body);
    }
    @ExceptionHandler(InvalidSellingWindowException.class)
    public ResponseEntity<CommonResponseVO<Void>> invalidSellingWindowException(InvalidSellingWindowException ex) {
        CommonResponseVO<Void> body = CommonResponseVO.<Void>builder()
            .code(HttpStatus.BAD_REQUEST.value())
            .message(ex.getMessage())
            .build();
        return ResponseEntity.badRequest().body(body);
    }
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<CommonResponseVO<Void>> categoryNotFoundException(CategoryNotFoundException ex) {
        CommonResponseVO<Void> body = CommonResponseVO.<Void>builder()
            .code(HttpStatus.NOT_FOUND.value())
            .message(ex.getMessage())
            .build();
        return ResponseEntity.status(body.getCode()).body(body);
    }
    @ExceptionHandler(CategoryInactiveException.class)
    public ResponseEntity<CommonResponseVO<Void>> categoryInactiveException(CategoryInactiveException ex) {
        CommonResponseVO<Void> body = CommonResponseVO.<Void>builder()
            .code(HttpStatus.BAD_REQUEST.value())
            .message(ex.getMessage())
            .build();
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(BrandNotFoundException.class)
    public ResponseEntity<CommonResponseVO<Void>> brandNotFoundException(BrandNotFoundException ex) {
        CommonResponseVO<Void> body = CommonResponseVO.<Void>builder()
            .code(HttpStatus.NOT_FOUND.value())
            .message(ex.getMessage())
            .build();
        return ResponseEntity.status(body.getCode()).body(body);
    }
    @ExceptionHandler(BrandInactiveException.class)
    public ResponseEntity<CommonResponseVO<Void>> brandInactiveException(BrandInactiveException ex) {
        CommonResponseVO<Void> body = CommonResponseVO.<Void>builder()
            .code(HttpStatus.BAD_REQUEST.value())
            .message(ex.getMessage())
            .build();
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ProductConstraintViolationException.class)
    public ResponseEntity<CommonResponseVO<Void>> productConstraintViolationException(ProductConstraintViolationException ex){
        CommonResponseVO<Void> body = CommonResponseVO.<Void>builder()
            .code(HttpStatus.CONFLICT.value())
            .message(ex.getMessage())
            .build();
        return ResponseEntity.status(body.getCode()).body(body);
    }
    @ExceptionHandler(DuplicateProductSlugException.class)
    public ResponseEntity<CommonResponseVO<Void>> duplicateProductSlugException(DuplicateProductSlugException ex){
        CommonResponseVO<Void> body = CommonResponseVO.<Void>builder()
            .code(HttpStatus.CONFLICT.value())
            .message(ex.getMessage())
            .build();
        return ResponseEntity.status(body.getCode()).body(body);
    }

    @ExceptionHandler(ProductSlugTooLongException.class)
    public ResponseEntity<CommonResponseVO<Void>> productSlugTooLongException(ProductSlugTooLongException ex) {
        CommonResponseVO<Void> body = CommonResponseVO.<Void>builder()
            .code(HttpStatus.BAD_REQUEST.value())
            .message(ex.getMessage())
            .build();
        return ResponseEntity.badRequest().body(body);
    }
    @ExceptionHandler(ProductNameTooLongException.class)
    public ResponseEntity<CommonResponseVO<Void>> productNameTooLongException(ProductNameTooLongException ex) {
        CommonResponseVO<Void> body = CommonResponseVO.<Void>builder()
            .code(HttpStatus.BAD_REQUEST.value())
            .message(ex.getMessage())
            .build();
        return ResponseEntity.badRequest().body(body);
    }
}
