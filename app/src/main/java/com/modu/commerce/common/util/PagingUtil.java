package com.modu.commerce.common.util;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PagingUtil {
    
    @Min(0)
    private int page=0;

    @Min(10)
    @Max(50)
    private int size=10;
}
