package com.modu.commerce.category.service;

import java.util.List;
import java.util.Objects;

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
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final JPAQueryFactory queryFactory;

    public CategoryServiceImpl(CategoryRepository categoryRepository, JPAQueryFactory queryFactory){
        this.categoryRepository = categoryRepository;
        this.queryFactory = queryFactory;
    }

    // ---------------------------------------------
    // 생성: 부모 활성 선검증 + 활성만 대상 중복명 검증
    // ---------------------------------------------
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

        final QModuCategory mc = QModuCategory.moduCategory;
        ModuCategory parent = null;
        int depth;

        if (parentId != null) {
            // 부모: 존재 + 활성(DELETED_AT IS NULL) 검증
            parent = queryFactory
                        .selectFrom(mc)
                        .where(mc.id.eq(parentId),
                               mc.deletedAt.isNull())
                        .fetchOne();
            if (parent == null) throw new ParentCategoryNotFound();

            depth = parent.getDepth() + 1;

            // 활성만 기준 중복명 검증
            boolean dupExists = queryFactory
                    .selectOne()
                    .from(mc)
                    .where(mc.deletedAt.isNull(),
                           mc.name.eq(name),
                           mc.parent.id.eq(parentId))
                    .fetchFirst() != null;
            if (dupExists) throw new DuplicateCategoryNameUnderSameParent();

        } else {
            depth = 0;

            // 루트에서 활성만 기준 중복명 검증
            boolean dupExists = queryFactory
                    .selectOne()
                    .from(mc)
                    .where(mc.deletedAt.isNull(),
                           mc.name.eq(name),
                           mc.parent.isNull())
                    .fetchFirst() != null;
            if (dupExists) throw new DuplicateCategoryNameUnderSameParent();
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

        } catch (DataIntegrityViolationException e) {
            // (선택) DB에 (parent_id, NAME, IS_DELETED) 유니크를 도입했다면 여기로 수렴
            throw new DuplicateCategoryNameUnderSameParent();
        }
    }

    // ---------------------------------------------
    // 단건 조회: 활성만 노출 + 활성 자식 기준 hasChildren
    // ---------------------------------------------
    @Transactional(readOnly = true)
    @Override
    public CategoryOneResponse categoryOne(Long id) {
        if (id == null || id == 0L) throw new InvalidParentCategoryException("잘못된 카테고리 id입니다.");

        final QModuCategory mc = QModuCategory.moduCategory;
        final QModuCategory child = new QModuCategory("child");

        CategoryOneResponse data = queryFactory
                .select(Projections.fields(
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
                                        .where(child.parent.id.eq(mc.id),
                                               child.deletedAt.isNull())
                                        .exists(),
                                "hasChildren"
                        )
                ))
                .from(mc)
                .where(mc.id.eq(id),
                       mc.deletedAt.isNull())
                .fetchOne();

        if (data == null) throw new CategoryNotFoundException();
        return data;
    }

    // ---------------------------------------------
    // 목록 조회: 기본 활성만 (includeDeleted 옵션 없음 = 활성만)
    // ---------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public CategoryListResponse categoryList(CategoryListRequest request) {
        log.info("\n{}", request);

        final QModuCategory mc = QModuCategory.moduCategory;
        final QModuCategory child = new QModuCategory("child");

        Predicate condition = ExpressionUtils.allOf(
                CategoryPredicate.nameContains(mc, request.getKeyword()),
                CategoryPredicate.parentIdEq(mc, request.getParentId()),
                mc.deletedAt.isNull() // 기본 활성만
        );

        List<CategoryOneResponse> list = queryFactory
                .select(Projections.fields(
                        CategoryOneResponse.class,
                        mc.id.as("id"),
                        mc.parent.id.as("parentId"),
                        mc.name.as("name"),
                        mc.depth.as("depth"),
                        mc.createdAt.as("createdAt"),
                        mc.updatedAt.as("updatedAt"),
                        // hasChildren: 활성 자식 기준
                        ExpressionUtils.as(
                                JPAExpressions
                                        .selectOne()
                                        .from(child)
                                        .where(child.parent.id.eq(mc.id),
                                               child.deletedAt.isNull())
                                        .exists(),
                                "hasChildren"
                        )
                ))
                .from(mc)
                .where(condition)
                .orderBy(mc.depth.asc(), mc.name.asc())
                .offset((long) request.getPage() * (long) request.getSize())
                .limit(request.getSize())
                .fetch();

        return CategoryListResponse.builder()
                .list(list)
                .parentId(request.getParentId())
                .keyword(request.getKeyword())
                .page(request.getPage())
                .size(request.getSize())
                .build();
    }

    // ---------------------------------------------
    // 자식 목록: includeDeleted / withHasChildren 모두 반영
    // ---------------------------------------------
    @Transactional(readOnly = true)
    @Override
    public CategoryChildrenListResponse getChildrenList(CategoryChildrenListRequest request, Long id) {

        final QModuCategory mc = QModuCategory.moduCategory;
        final QModuCategory child = new QModuCategory("child");

        // 부모 활성 선검증 (존재 + 삭제 안됨)
        boolean parentActive = categoryRepository.existsByIdAndDeletedAtIsNull(id);
        if (!parentActive) throw new CategoryNotFoundException();

        // 목록 조건: parent_id 매칭 + includeDeleted 여부
        Predicate condition = ExpressionUtils.allOf(
                CategoryPredicate.parentIdEq(mc, id),
                CategoryPredicate.includeDeletedCheck(mc, request.getIncludeDeleted())
        );

        // totalCount: includeDeleted 반영
        long totalCount;
        if (Boolean.TRUE.equals(request.getIncludeDeleted())) {
            totalCount = queryFactory
                    .select(mc.id.count())
                    .from(mc)
                    .where(mc.parent.id.eq(id))
                    .fetchOne();
        } else {
            totalCount = queryFactory
                    .select(mc.id.count())
                    .from(mc)
                    .where(mc.parent.id.eq(id),
                           mc.deletedAt.isNull())
                    .fetchOne();
        }

        // withHasChildren=false면 exists 서브쿼리 스킵 → 성능 최적화
        boolean withHasChildren = !Objects.isNull(request.getWithHasChildren()) && request.getWithHasChildren();

        var hasChildrenExpr = withHasChildren
                ? ExpressionUtils.as(
                        JPAExpressions
                                .selectOne()
                                .from(child)
                                .where(child.parent.id.eq(mc.id),
                                       // hasChildren 판단은 항상 "활성 자식" 기준
                                       child.deletedAt.isNull())
                                .exists(),
                        "hasChildren")
                : ExpressionUtils.as(Expressions.FALSE, "hasChildren");

        List<CategoryOneResponse> list = queryFactory
                .select(Projections.fields(
                        CategoryOneResponse.class,
                        mc.id.as("id"),
                        mc.parent.id.as("parentId"),
                        mc.name.as("name"),
                        mc.depth.as("depth"),
                        mc.createdAt.as("createdAt"),
                        mc.updatedAt.as("updatedAt"),
                        hasChildrenExpr
                ))
                .from(mc)
                .where(condition)
                .orderBy(mc.id.asc()) // 임시 정렬
                .offset((long) request.getPage() * (long) request.getSize())
                .limit(request.getSize())
                .fetch();

        return CategoryChildrenListResponse.builder()
                .list(list)
                .page(request.getPage())
                .size(request.getSize())
                .totalCount(totalCount)
                .build();
    }
}
