package com.modu.commerce.category.api.admin.dto.request;

import com.modu.commerce.common.util.RequestPaging;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class CategoryChildrenListRequest extends RequestPaging{
    
    private Boolean includeDeleted = false;

    private Boolean withHasChildren = true;
}
