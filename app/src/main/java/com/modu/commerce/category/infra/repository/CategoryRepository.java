package com.modu.commerce.category.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.modu.commerce.category.domain.entity.ModuCategory;

public interface CategoryRepository extends JpaRepository<ModuCategory, Long>{
    
    boolean existsByParent_IdAndName(Long parentId, String name);
    boolean existsByParent_IdAndDeletedAtIsNullAndName(Long parentId, String name);
    boolean existsByParent_IsNullAndName(String name);
    boolean existsByParent_Id(Long parentId);
    boolean existsByIdAndDeletedAtIsNull(Long id);
    Long countByParent_Id(Long parentId);
    Long countByParent_IdAndDeletedAtIsNull(Long parentId);
}
