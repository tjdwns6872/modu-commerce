package com.modu.commerce.category.api.admin.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter @Builder @ToString
public class CategoryChildrenListResponse {
    
    private List<CategoryOneResponse> list;

    private int page;
    private int size;
    private Long totalCount;
}
