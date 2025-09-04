package com.modu.commerce.category.service;

import com.modu.commerce.category.dto.CategoryChildrenListRequest;
import com.modu.commerce.category.dto.CategoryChildrenListResponse;
import com.modu.commerce.category.dto.CategoryDeleteSpec;
import com.modu.commerce.category.dto.CategoryListResponse;
import com.modu.commerce.category.dto.CategoryListSpec;
import com.modu.commerce.category.dto.CategoryOneResponse;
import com.modu.commerce.category.dto.CategoryOneSpec;
import com.modu.commerce.category.dto.CategoryRequest;
import com.modu.commerce.common.exception.InvalidPageRequest;

public interface CategoryService {
    
    public Long createCategory(CategoryRequest request);
    
    public CategoryOneResponse categoryOne(CategoryOneSpec spec);

    public CategoryListResponse categoryList(CategoryListSpec request) throws InvalidPageRequest;

    public CategoryChildrenListResponse getChildrenList(CategoryChildrenListRequest request, Long id)  throws InvalidPageRequest;

    public void categorySoftDelete(CategoryDeleteSpec request);

    public void categoryRestore(Long id);
}
