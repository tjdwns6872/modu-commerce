package com.modu.commerce.products.infra;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.modu.commerce.category.domain.entity.ModuCategory;
import com.modu.commerce.category.infra.repository.CategoryRepository;
import com.modu.commerce.products.domain.exception.CategoryInactiveException;
import com.modu.commerce.products.domain.exception.CategoryNotFoundException;

@Component
public class CategoryReaderJpaAdapter {
    
    private final CategoryRepository categoryRepository;

    public CategoryReaderJpaAdapter(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public ModuCategory getOneCategory(Long categoryId){
        if(categoryId == null || categoryId <= 0L){
            throw new CategoryNotFoundException(categoryId);
        }
        boolean exists = categoryRepository.existsById(categoryId);
        if(!exists){
            throw new CategoryNotFoundException(categoryId);
        }

        Optional<ModuCategory> active = categoryRepository.findByIdAndDeletedAtIsNull(categoryId);
        return active.orElseThrow(() -> new CategoryInactiveException(categoryId));
    }
}
