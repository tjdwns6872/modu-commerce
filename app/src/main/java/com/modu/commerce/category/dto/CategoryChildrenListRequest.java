package com.modu.commerce.category.dto;

import com.modu.commerce.common.util.PagingUtil;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
public class CategoryChildrenListRequest extends PagingUtil{
    
    @Builder.Default
    private Boolean includeDeleted = false;

    @Builder.Default
    private Boolean withHasChildren = true;
}
