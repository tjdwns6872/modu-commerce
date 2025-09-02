package com.modu.commerce.category.dto;

import com.modu.commerce.common.util.RequestPaingUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class AdminCategoryListRequest extends RequestPaingUtil{
    
    private Long parentId;
    private String keyword;

    private Boolean includeDeleted = false;
    private Boolean withHasChildren = true;
}
