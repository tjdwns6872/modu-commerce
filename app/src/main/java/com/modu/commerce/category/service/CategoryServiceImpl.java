package com.modu.commerce.category.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.modu.commerce.category.dto.CategoryRequest;
import com.modu.commerce.category.entity.ModuCategory;
import com.modu.commerce.category.exception.DuplicateCategoryNameUnderSameParent;
import com.modu.commerce.category.exception.ParentCategoryNotFound;
import com.modu.commerce.category.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    @Override
    public Long createCategory(CategoryRequest request) {
        log.info("CATEGORY REQUEST : parentId={}, name='{}'", request.getParentId(), request.getName());

        final String name = request.getName() == null ? "" : request.getName().strip();
        if (name.isEmpty()) {
            throw new jakarta.validation.ValidationException("카테고리명은 공백일 수 없습니다.");
        }

        final Long parentId = request.getParentId();
        if (parentId != null && parentId == 0L) {
            throw new jakarta.validation.ValidationException("루트 카테고리는 parentId를 null로 보내야 합니다.");
        }

        long depth = 1L;
        if (parentId != null) {
            ModuCategory parent = categoryRepository.findById(parentId)
                    .orElseThrow(ParentCategoryNotFound::new);
            depth = parent.getDepth() + 1;

            if (categoryRepository.existsByParentIdAndName(parentId, name)) {
                throw new DuplicateCategoryNameUnderSameParent();
            }
        } else {
            if (categoryRepository.existsByParentIdAndName(null, name)) {
                throw new DuplicateCategoryNameUnderSameParent();
            }
        }

        ModuCategory toSave = ModuCategory.builder()
                .parentId(parentId)
                .name(name)
                .depth(depth)
                .build();

        ModuCategory saved = categoryRepository.save(toSave);
        log.info("CATEGORY CREATION SUCCESSFUL id={}", saved.getId());
        return saved.getId();
    }
    
}
