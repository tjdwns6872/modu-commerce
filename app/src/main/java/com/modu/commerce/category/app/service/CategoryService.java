package com.modu.commerce.category.app.service;

import com.modu.commerce.category.api.admin.dto.request.CategoryChildrenListRequest;
import com.modu.commerce.category.api.admin.dto.request.CategoryRequest;
import com.modu.commerce.category.api.admin.dto.response.CategoryChildrenListResponse;
import com.modu.commerce.category.api.admin.dto.response.CategoryListResponse;
import com.modu.commerce.category.api.admin.dto.response.CategoryOneResponse;
import com.modu.commerce.category.domain.spec.CategoryDeleteSpec;
import com.modu.commerce.category.domain.spec.CategoryListSpec;
import com.modu.commerce.category.domain.spec.CategoryOneSpec;
import com.modu.commerce.common.exception.InvalidPageRequest;

public interface CategoryService {
    
    public Long createCategory(CategoryRequest request);
    
    public CategoryOneResponse categoryOne(CategoryOneSpec spec);

    public CategoryListResponse categoryList(CategoryListSpec request) throws InvalidPageRequest;

    public CategoryChildrenListResponse getChildrenList(CategoryChildrenListRequest request, Long id)  throws InvalidPageRequest;

    public void categorySoftDelete(CategoryDeleteSpec request);

    public void categoryRestore(Long id);
}
