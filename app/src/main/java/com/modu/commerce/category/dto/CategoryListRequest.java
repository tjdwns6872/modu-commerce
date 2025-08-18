package com.modu.commerce.category.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CategoryListRequest {
    
    private Long parentId;

    private String keyword;

    private int page;

    private int size;
}
