package com.modu.commerce.category.service;

import com.modu.commerce.category.dto.CategoryListRequest;
import com.modu.commerce.category.dto.CategoryListResponse;
import com.modu.commerce.category.dto.CategoryOneResponse;
import com.modu.commerce.category.dto.CategoryRequest;

public interface CategoryService {
    
    public Long createCategory(CategoryRequest request);
    
    public CategoryOneResponse categoryOne(Long id);

    public CategoryListResponse categoryList(CategoryListRequest request);
}
