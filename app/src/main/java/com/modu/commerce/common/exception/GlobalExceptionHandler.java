package com.modu.commerce.common.exception;

import com.modu.commerce.common.api.response.CommonResponseVO;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import static com.modu.commerce.common.web.TraceIdFilter.TRACE_ID;

@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    private String traceId() { return MDC.get(TRACE_ID); }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponseVO<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
                .body(CommonResponseVO.of(400, msg, null, traceId()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponseVO<Object>> handleConstraintViolation(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
                .body(CommonResponseVO.of(400, msg, null, traceId()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CommonResponseVO<Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(CommonResponseVO.of(409, "데이터 무결성 위반(중복/제약 위반)", null, traceId()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponseVO<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(CommonResponseVO.of(400, ex.getMessage(), null, traceId()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponseVO<Object>> handleUnknown(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponseVO.of(500, "서버 오류가 발생했습니다.", null, traceId()));
    }

    @ExceptionHandler(InvalidPageRequest.class)
    public ResponseEntity<CommonResponseVO<Object>> invalidPageRequest(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponseVO.of(400, "잘못된 페이지 이동입니다.", null, traceId()));
    }
}
