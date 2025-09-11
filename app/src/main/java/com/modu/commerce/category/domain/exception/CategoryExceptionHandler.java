package com.modu.commerce.category.domain.exception;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.modu.commerce.common.api.response.CommonResponseVO;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "com.modu.commerce.category")
public class CategoryExceptionHandler {
    
    @ExceptionHandler(DuplicateCategoryNameUnderSameParent.class)
    public ResponseEntity<CommonResponseVO<Void>> duplicateCategoryNameUnderSameParent(DuplicateCategoryNameUnderSameParent ex){
        CommonResponseVO<Void> response = CommonResponseVO.<Void>builder()
            .code(HttpStatus.CONFLICT.value())
            .message(ex.getMessage())
            .build();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @ExceptionHandler(ParentCategoryNotFound.class)
    public ResponseEntity<CommonResponseVO<Void>> parentCategoryNotFound(ParentCategoryNotFound ex){
        CommonResponseVO<Void> response = CommonResponseVO.<Void>builder()
            .code(HttpStatus.NOT_FOUND.value())
            .message(ex.getMessage())
            .build();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponseVO<Map<String, String>>> methodArgumentNotValidException(org.springframework.web.bind.MethodArgumentNotValidException ex){
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        CommonResponseVO<Map<String, String>> body = CommonResponseVO.<Map<String, String>>builder()
            .code(HttpStatus.BAD_REQUEST.value())
            .message("요청 값 검증에 실패했습니다.")
            .data(errors)
            .build();
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(InvalidCategoryNameException.class)
    public ResponseEntity<CommonResponseVO<Void>> invalidCategoryNameException(InvalidCategoryNameException ex) {
        CommonResponseVO<Void> body = CommonResponseVO.<Void>builder()
            .code(HttpStatus.BAD_REQUEST.value())
            .message(ex.getMessage())
            .build();
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(InvalidParentCategoryException.class)
    public ResponseEntity<CommonResponseVO<Void>> invalidParentCategoryException(InvalidParentCategoryException ex) {
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
}
