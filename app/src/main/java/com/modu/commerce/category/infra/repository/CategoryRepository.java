package com.modu.commerce.category.infra.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.modu.commerce.category.domain.entity.ModuCategory;

public interface CategoryRepository extends JpaRepository<ModuCategory, Long>{
    
    boolean existsByParent_IdAndName(Long parentId, String name);
    boolean existsByParent_IdAndDeletedAtIsNullAndName(Long parentId, String name);
    boolean existsByParent_IsNullAndName(String name);
    boolean existsByParent_Id(Long parentId);
    boolean existsByIdAndDeletedAtIsNull(Long id);
    Optional<ModuCategory> findByIdAndDeletedAtIsNull(Long id);
    Long countByParent_Id(Long parentId);
    Long countByParent_IdAndDeletedAtIsNull(Long parentId);

    @Query("""
           select c.id
           from ModuCategory c
           where c.id in :ids
             and c.isDeleted = false
           """)
    List<Long> findActiveIdsByIdIn(@Param("ids") Set<Long> ids);
}
