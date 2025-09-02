package com.modu.commerce.common.util;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class RequestPaingUtil {
    
    @Min(0)
    protected int page=0;

    @Min(10)
    @Max(50)
    protected int size=10;
}
