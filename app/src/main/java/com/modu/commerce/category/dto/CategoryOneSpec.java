package com.modu.commerce.category.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class CategoryOneSpec {
    private Long id;
    private Boolean includeDeleted;
}
