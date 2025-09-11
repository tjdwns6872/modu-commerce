package com.modu.commerce.category.domain.spec;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class CategoryOneSpec {
    private Long id;
    private Boolean includeDeleted;
}
