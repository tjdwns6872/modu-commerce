package com.modu.commerce.common.util;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class PagingUtil {
    
    @Min(0)
    @Builder.Default
    protected Integer page=0;

    @Min(10)
    @Max(100)
    @Builder.Default
    protected Integer size=20;
}
