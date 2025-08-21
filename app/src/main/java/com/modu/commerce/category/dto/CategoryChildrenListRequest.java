package com.modu.commerce.category.dto;

import com.modu.commerce.common.util.PagingUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class CategoryChildrenListRequest extends PagingUtil{
    
    private Boolean includeDeleted = false;
    private Boolean withHasChildren = true;
}
