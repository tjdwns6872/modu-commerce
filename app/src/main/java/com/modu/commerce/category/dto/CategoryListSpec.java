package com.modu.commerce.category.dto;

import com.modu.commerce.common.util.PagingUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
public class CategoryListSpec extends PagingUtil{
    
    private Long parentId;
    private String keyword;

    private Boolean includeDeleted;
    private Boolean withHasChildren;
}
