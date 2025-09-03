package com.modu.commerce.category.service;

import com.modu.commerce.category.dto.CategoryChildrenListRequest;
import com.modu.commerce.category.dto.CategoryChildrenListResponse;
import com.modu.commerce.category.dto.CategoryDeleteSpec;
import com.modu.commerce.category.dto.CategoryListResponse;
import com.modu.commerce.category.dto.CategoryListSpec;
import com.modu.commerce.category.dto.CategoryOneResponse;
import com.modu.commerce.category.dto.CategoryRequest;

public interface CategoryService {
    
    public Long createCategory(CategoryRequest request);
    
    public CategoryOneResponse categoryOne(Long id);

    public CategoryListResponse categoryList(CategoryListSpec request);

    public CategoryChildrenListResponse getChildrenList(CategoryChildrenListRequest request, Long id);

    public void categorySoftDelete(CategoryDeleteSpec request);
}
