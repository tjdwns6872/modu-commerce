package com.modu.commerce.category.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.modu.commerce.category.dto.CategoryChildrenListRequest;
import com.modu.commerce.category.dto.CategoryChildrenListResponse;
import com.modu.commerce.category.dto.CategoryListRequest;
import com.modu.commerce.category.dto.CategoryListResponse;
import com.modu.commerce.category.dto.CategoryOneResponse;
import com.modu.commerce.category.dto.CategoryRequest;
import com.modu.commerce.category.entity.ModuCategory;
import com.modu.commerce.category.entity.QModuCategory;
import com.modu.commerce.category.exception.CategoryNotFoundException;
import com.modu.commerce.category.exception.DuplicateCategoryNameUnderSameParent;
import com.modu.commerce.category.exception.InvalidCategoryNameException;
import com.modu.commerce.category.exception.InvalidParentCategoryException;
import com.modu.commerce.category.exception.ParentCategoryNotFound;
import com.modu.commerce.category.repository.CategoryPredicate;
import com.modu.commerce.category.repository.CategoryRepository;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;
    private final JPAQueryFactory queryFactory;

    public CategoryServiceImpl(CategoryRepository categoryRepository, JPAQueryFactory queryFactory){
        this.categoryRepository = categoryRepository;
        this.queryFactory = queryFactory;
    }

    @Transactional
    @Override
    public Long createCategory(CategoryRequest request) {
        log.debug("CATEGORY REQUEST : parentId={}, name='{}'", request.getParentId(), request.getName());

        final String name = request.getName() == null ? "" : request.getName().strip();
        if (name.isEmpty()) {
            throw new InvalidCategoryNameException("카테고리명은 공백일 수 없습니다.");
        }

        final Long parentId = request.getParentId();
        if (parentId != null && parentId == 0L) {
            throw new InvalidParentCategoryException("루트 카테고리는 parentId를 null로 보내야 합니다.");
        }

        int depth;
        ModuCategory parent = null;
        if (parentId != null) {
            parent = categoryRepository.findById(parentId)
                    .orElseThrow(ParentCategoryNotFound::new);
            depth = parent.getDepth() + 1;

            if (categoryRepository.existsByParent_IdAndName(parentId, name)) {
                throw new DuplicateCategoryNameUnderSameParent();
            }
        } else {
            depth = 0;
            if (categoryRepository.existsByParent_IsNullAndName(name)) {
                throw new DuplicateCategoryNameUnderSameParent();
            }
        }

        ModuCategory toSave = ModuCategory.builder()
                .parent(parent)
                .name(name)
                .depth(depth)
                .build();

        try {
            ModuCategory saved = categoryRepository.save(toSave);
            log.debug("CATEGORY CREATION SUCCESSFUL id={}", saved.getId());
            return saved.getId();
            
        } catch (DataIntegrityViolationException  e) {
            throw new DuplicateCategoryNameUnderSameParent();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryOneResponse categoryOne(Long id) {
        if (id == null || id == 0) {
            throw new InvalidParentCategoryException("잘못된 카테고리 id입니다.");
        }

        ModuCategory category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);

        Long parentId = (category.getParent() == null) ? null : category.getParent().getId();
        
        return CategoryOneResponse.builder()
                .id(category.getId())
                .parentId(parentId)
                .name(category.getName())
                .depth(category.getDepth())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .hasChildren(categoryRepository.existsByParent_Id(id))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryListResponse categoryList(CategoryListRequest request) {
        log.info("\n{}", request.toString());
        QModuCategory mc = new QModuCategory("mc");
        QModuCategory child = new QModuCategory("child");

        Predicate condition = ExpressionUtils.allOf(
            CategoryPredicate.nameContains(mc, request.getKeyword()),
            CategoryPredicate.parentIdEq(mc, request.getParentId())
        );

        List<CategoryOneResponse> response = queryFactory.select(
                                                    Projections.fields(
                                                        CategoryOneResponse.class,
                                                        mc.id.as("id"),
                                                        mc.parent.id.as("parentId"),
                                                        mc.name.as("name"),
                                                        mc.depth.as("depth"),
                                                        mc.createdAt.as("createdAt"),
                                                        mc.updatedAt.as("updatedAt"),
                                                        ExpressionUtils.as(
                                                            JPAExpressions
                                                                .selectOne()
                                                                .from(child)
                                                                .where(child.parent.id.eq(mc.id))
                                                                .exists(),
                                                            "hasChildren"   
                                                        )
                                                    ))
                                            .from(mc)
                                            .where(condition)
                                            .orderBy(mc.depth.asc(), mc.name.asc())
                                            .offset((long)request.getPage() * (long)request.getSize())
                                            .limit(request.getSize())
                                            .fetch();

        for (CategoryOneResponse dto : response) {
            log.info("category: {}", dto);
        }

        return CategoryListResponse.builder()
                                .list(response)
                                .parentId(request.getParentId())
                                .keyword(request.getKeyword())
                                .page(request.getPage())
                                .size(request.getSize())
                                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryChildrenListResponse getChildrenList(CategoryChildrenListRequest request, Long id) {

        QModuCategory mc = new QModuCategory("mc");
        QModuCategory child = new QModuCategory("child");

        Predicate condition = ExpressionUtils.allOf(
            CategoryPredicate.parentIdEq(mc, id),
            CategoryPredicate.includeDeletedCheck(mc, request.getIncludeDeleted())
        );

        if(!categoryRepository.existsByIdAndDeletedAtIsNull(id)) throw new CategoryNotFoundException();

        Long categoryCount = 0L;

        if(request.getIncludeDeleted()){
            categoryCount = categoryRepository.countByParent_Id(id);
        }else{
            categoryCount = categoryRepository.countByParent_IdAndDeletedAtIsNull(id);
        }


        List<CategoryOneResponse> response = queryFactory.select(
                                                    Projections.fields(
                                                        CategoryOneResponse.class,
                                                        mc.id.as("id"),
                                                        mc.parent.id.as("parentId"),
                                                        mc.name.as("name"),
                                                        mc.depth.as("depth"),
                                                        mc.createdAt.as("createdAt"),
                                                        mc.updatedAt.as("updatedAt"),
                                                        ExpressionUtils.as(
                                                            JPAExpressions
                                                                .selectOne()
                                                                .from(child)
                                                                .where(child.parent.id.eq(mc.id)
                                                                        ,CategoryPredicate.includeDeletedCheck(child, false))
                                                                .exists(),
                                                            "hasChildren"   
                                                        )
                                                    ))
                                            .from(mc)
                                            .where(condition)
                                            // .orderBy(mc.depth.asc(), mc.name.asc())
                                            .orderBy(mc.id.asc())
                                            .offset((long)request.getPage() * (long)request.getSize())
                                            .limit(request.getSize())
                                            .fetch();

        return CategoryChildrenListResponse.builder()
                                            .list(response)
                                            .page(request.getPage())
                                            .size(request.getSize())
                                            .totalCount(categoryCount)
                                            .build();
    }
}