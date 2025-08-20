package com.modu.commerce.category.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter @Builder @ToString
public class CategoryListResponse {

    private List<CategoryOneResponse> list;

    private Long parentId;
    private String keyword;
    private int page;
    private int size;
}
