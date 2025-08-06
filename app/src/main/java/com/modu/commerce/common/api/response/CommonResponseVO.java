package com.modu.commerce.common.api.response;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class CommonResponseVO<T> {
    private int code;
    private String message;
    private T data;
}