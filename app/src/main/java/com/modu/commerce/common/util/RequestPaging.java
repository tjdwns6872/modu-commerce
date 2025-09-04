package com.modu.commerce.common.util;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class RequestPaging {
    
    @Min(0)
    protected Integer page=0;

    @Min(10)
    @Max(100)
    protected Integer size=20;
}
