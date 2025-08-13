package com.modu.commerce.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.modu.commerce.category.entity.ModuCategory;

public interface CategoryRepository extends JpaRepository<ModuCategory, Long>{
    
    boolean existsByParentIdAndName(Long parentId, String name);
}
