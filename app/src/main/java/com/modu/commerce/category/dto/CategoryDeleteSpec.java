package com.modu.commerce.category.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class CategoryDeleteSpec {

    private Long id;
    private Long actorId;

}
