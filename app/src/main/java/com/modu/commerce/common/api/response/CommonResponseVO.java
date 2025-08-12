package com.modu.commerce.common.api.response;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class CommonResponseVO<T> {
    private final int code;
    private final String message;
    private final T data;
    private final String traceId;

    public static <T> CommonResponseVO<T> ok(T data, String traceId){
        return CommonResponseVO.<T>builder()
                    .code(200)
                    .message("OK")
                    .data(data)
                    .traceId(traceId)
                    .build();
    }

    public static <T> CommonResponseVO<T> of(int code, String message, T data, String traceId) {
        return CommonResponseVO.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .traceId(traceId)
                .build();
    }
}