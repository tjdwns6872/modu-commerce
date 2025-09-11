package com.modu.commerce.category.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.modu.commerce.category.api.admin.dto.request.CategoryChildrenListRequest;
import com.modu.commerce.category.api.admin.dto.request.CategoryRequest;
import com.modu.commerce.category.api.admin.dto.response.CategoryChildrenListResponse;
import com.modu.commerce.category.api.admin.dto.response.CategoryListResponse;
import com.modu.commerce.category.api.admin.dto.response.CategoryOneResponse;
import com.modu.commerce.category.domain.entity.ModuCategory;
import com.modu.commerce.category.domain.exception.CategoryNotFoundException;
import com.modu.commerce.category.domain.exception.DuplicateCategoryNameUnderSameParent;
import com.modu.commerce.category.domain.exception.DuplicateCategoryNameUnderSameParentOnRestore;
import com.modu.commerce.category.domain.exception.InvalidCategoryNameException;
import com.modu.commerce.category.domain.exception.InvalidParentCategoryException;
import com.modu.commerce.category.domain.exception.ParentCategoryDeletedOnRestore;
import com.modu.commerce.category.domain.exception.ParentCategoryNotFound;
import com.modu.commerce.category.domain.spec.CategoryDeleteSpec;
import com.modu.commerce.category.domain.spec.CategoryListSpec;
import com.modu.commerce.category.domain.spec.CategoryOneSpec;
import com.modu.commerce.category.entity.QModuCategory;
import com.modu.commerce.category.infra.repository.CategoryPredicate;
import com.modu.commerce.category.infra.repository.CategoryRepository;
import com.modu.commerce.common.exception.InvalidPageRequest;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
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
            // (선택) DB 유니크 제약이 있다면 여기서 수렴
            throw new DuplicateCategoryNameUnderSameParent();
        }
    }

    // ---------------------------------------------
    // 단건 조회: 활성만 노출 + 활성 자식 기준 hasChildren
    // ---------------------------------------------
    @Transactional(readOnly = true)
    @Override
    public CategoryOneResponse categoryOne(CategoryOneSpec request) {
        if (request.getId() == null || request.getId() == 0L) throw new InvalidParentCategoryException("잘못된 카테고리 id입니다.");

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
                                               request.getIncludeDeleted() ? null : child.deletedAt.isNull())
                                        .exists(),
                                "hasChildren"
                        )
                ))
                .from(mc)
                .where(mc.id.eq(request.getId()),
                       request.getIncludeDeleted() ? null : mc.deletedAt.isNull())
                .fetchOne();

        if (data == null) throw new CategoryNotFoundException();
        return data;
    }

    // ---------------------------------------------
    // 목록 조회 (Admin): includeDeleted/withHasChildren + orphan 제거
    // ---------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public CategoryListResponse categoryList(CategoryListSpec request) throws InvalidPageRequest {
        log.info("\n{}", request);

        if(request.getPage() == null){
                request.setPage(0);
        }
        if(request.getSize() == null){
                request.setSize(20);
        }

        if(request.getPage() < 0 || (request.getSize() < 10 || request.getSize() > 100)){
                throw new InvalidPageRequest("잘못된 페이지 이동입니다.");
        }

        final QModuCategory c = QModuCategory.moduCategory;
        final QModuCategory p = new QModuCategory("parent");
        final QModuCategory child = new QModuCategory("child");

        final boolean withHasChildren = Boolean.TRUE.equals(request.getWithHasChildren());
        final boolean includeDeleted  = Boolean.TRUE.equals(request.getIncludeDeleted());

        // 1) 공통 where (키워드/부모/삭제 + orphan 제거)
        Predicate condition = ExpressionUtils.allOf(
                CategoryPredicate.nameContains(c, request.getKeyword()),
                CategoryPredicate.parentIdEq(c, request.getParentId()),
                CategoryPredicate.includeDeletedCheck(c, includeDeleted),
                // orphan 제거: 루트이거나, 부모가 활성인 경우만 노출
                ExpressionUtils.or(
                        c.parent.isNull(),
                        JPAExpressions
                                .selectOne()
                                .from(p)
                                .where(p.id.eq(c.parent.id),
                                       p.deletedAt.isNull())
                                .exists()
                )
        );

        List<CategoryOneResponse> list;

        Long totalCount = queryFactory.select(c.id.count()).from(c).where(condition).fetchOne();

        if (withHasChildren) {
            // 2) withHasChildren=true → EXISTS(child where child.parent_id = c.id [and child.deletedAt is null])
            Expression<Boolean> hasChildrenExpr = ExpressionUtils.as(
                    JPAExpressions
                            .selectOne()
                            .from(child)
                            .where(
                                    child.parent.id.eq(c.id),
                                    includeDeleted ? null : child.deletedAt.isNull()
                            )
                            .exists(),
                    "hasChildren"
            );

            list = queryFactory
                    .select(Projections.fields(
                            CategoryOneResponse.class,
                            c.id.as("id"),
                            c.parent.id.as("parentId"),
                            c.name.as("name"),
                            c.depth.as("depth"),
                            c.createdAt.as("createdAt"),
                            c.updatedAt.as("updatedAt"),
                            hasChildrenExpr // ★ flag=true에서만 projection 포함
                    ))
                    .from(c)
                    .where(condition)
                    .orderBy(c.sortIndex.asc(), c.id.asc())
                    .offset((long) request.getPage() * (long) request.getSize()) // 0-based
                    .limit(request.getSize())
                    .fetch();
        } else {
            // 3) withHasChildren=false → projection 자체 제거(필드 미포함)
            list = queryFactory
                    .select(Projections.fields(
                            CategoryOneResponse.class,
                            c.id.as("id"),
                            c.parent.id.as("parentId"),
                            c.name.as("name"),
                            c.depth.as("depth"),
                            c.createdAt.as("createdAt"),
                            c.updatedAt.as("updatedAt")
                    ))
                    .from(c)
                    .where(condition)
                    .orderBy(c.sortIndex.asc(), c.id.asc())
                    .offset((long) request.getPage() * (long) request.getSize())
                    .limit(request.getSize())
                    .fetch();
        }

        return CategoryListResponse.builder()
                .list(list)
                .parentId(request.getParentId())
                .keyword(request.getKeyword())
                .page(request.getPage())
                .size(request.getSize())
                .totalCount(totalCount)
                .build();
    }

    // ---------------------------------------------
    // 자식 목록 (Admin): includeDeleted/withHasChildren 모두 반영
    // ---------------------------------------------
    @Transactional(readOnly = true)
    @Override
    public CategoryChildrenListResponse getChildrenList(CategoryChildrenListRequest request, Long id) throws InvalidPageRequest {

        if(request.getPage() == null){
                request.setPage(0);
        }
        if(request.getSize() == null){
                request.setSize(20);
        }

        if(request.getPage() < 0 || (request.getSize() < 10 || request.getSize() > 100)){
                throw new InvalidPageRequest("잘못된 페이지 이동입니다.");
        }

        final QModuCategory c = QModuCategory.moduCategory;
        final QModuCategory child = new QModuCategory("child");

        // 부모 활성 선검증 (존재 + 삭제 안됨)
        boolean parentActive = categoryRepository.existsByIdAndDeletedAtIsNull(id);
        if (!parentActive) throw new CategoryNotFoundException();

        final boolean includeDeleted  = Boolean.TRUE.equals(request.getIncludeDeleted());
        final boolean withHasChildren = Boolean.TRUE.equals(request.getWithHasChildren());

        // 목록 조건: parent_id 매칭 + includeDeleted 여부
        Predicate condition = ExpressionUtils.allOf(
                c.parent.id.eq(id),
                CategoryPredicate.includeDeletedCheck(c, includeDeleted)
        );

        // totalCount: 동일 조건으로 계산
        Long totalCount = queryFactory.select(c.id.count()).from(c).where(condition).fetchOne();
        
        List<CategoryOneResponse> list;

        if (withHasChildren) {
            // 손자 존재 여부까지 판단(가시성 규칙 동일 적용)
            Expression<Boolean> hasChildrenExpr = ExpressionUtils.as(
                    JPAExpressions
                            .selectOne()
                            .from(child)
                            .where(
                                    child.parent.id.eq(c.id),
                                    includeDeleted ? null : child.deletedAt.isNull()
                            )
                            .exists(),
                    "hasChildren"
            );

            list = queryFactory
                    .select(Projections.fields(
                            CategoryOneResponse.class,
                            c.id.as("id"),
                            c.parent.id.as("parentId"),
                            c.name.as("name"),
                            c.depth.as("depth"),
                            c.createdAt.as("createdAt"),
                            c.updatedAt.as("updatedAt"),
                            hasChildrenExpr
                    ))
                    .from(c)
                    .where(condition)
                    .orderBy(c.sortIndex.asc(), c.id.asc())
                    .offset((long) request.getPage() * (long) request.getSize())
                    .limit(request.getSize())
                    .fetch();

        } else {
            list = queryFactory
                    .select(Projections.fields(
                            CategoryOneResponse.class,
                            c.id.as("id"),
                            c.parent.id.as("parentId"),
                            c.name.as("name"),
                            c.depth.as("depth"),
                            c.createdAt.as("createdAt"),
                            c.updatedAt.as("updatedAt")
                    ))
                    .from(c)
                    .where(condition)
                    .orderBy(c.sortIndex.asc(), c.id.asc())
                    .offset((long) request.getPage() * (long) request.getSize())
                    .limit(request.getSize())
                    .fetch();
        }

        return CategoryChildrenListResponse.builder()
                .list(list)
                .page(request.getPage())
                .size(request.getSize())
                .totalCount(totalCount)
                .build();
    }

    @Transactional
    @Override
    public void categorySoftDelete(CategoryDeleteSpec request) {
        if (request.getId() == null || request.getId() == 0L) throw new InvalidParentCategoryException("잘못된 카테고리 id입니다.");

        final QModuCategory mc = QModuCategory.moduCategory;

        LocalDateTime now = LocalDateTime.now();

        log.debug("CATEGROY DELETE ID : {}", request.getId());
        log.debug("CATEGROY DELETE ACTORID : {}", request.getActorId());
        long affected = queryFactory
                .update(mc)
                .set(mc.deletedAt, now)
                .set(mc.deletedBy, request.getActorId())
                .where(mc.id.eq(request.getId()), mc.deletedAt.isNull())
                .execute();

        if (affected == 1) {
            // 정상 삭제 → 204 (컨트롤러에서 no body로 반환)
            log.info("CATEGROY DELETE SUCCESS");
            return;
        }
    
        // 2) 영향 없음 → 존재 여부로 분기
        boolean exists = categoryRepository.existsById(request.getId());
        if (!exists) {
            // 미존재 → 404
            throw new CategoryNotFoundException();
        }
    }

    @Transactional
    @Override
    public void categoryRestore(Long id) {
        if (id == null || id == 0L) throw new InvalidParentCategoryException("잘못된 카테고리 id입니다.");

        final QModuCategory mc = QModuCategory.moduCategory;

        ModuCategory target = queryFactory
                .selectFrom(mc)
                .where(mc.id.eq(id))
                .fetchOne();

        if (target == null) throw new CategoryNotFoundException();

        if (target.getDeletedAt() == null) {
                log.info("CATEGORY RESTORE NO-OP id={}", id);
                return;
        }

        final Long parentId = (target.getParent() == null) ? null : target.getParent().getId();
        final String name = target.getName();

        if (parentId != null) {
                boolean parentActive = queryFactory
                        .selectOne()
                        .from(mc)
                        .where(mc.id.eq(parentId),
                               mc.deletedAt.isNull())
                        .fetchFirst() != null;

                if (!parentActive) {
                    throw new ParentCategoryDeletedOnRestore("부모가 삭제되어 복원할 수 없습니다.");
                }
        }

        boolean dupExists;
        if (parentId == null) {
                dupExists = queryFactory
                        .selectOne()
                        .from(mc)
                        .where(mc.deletedAt.isNull(),
                               mc.parent.isNull(),
                               mc.name.eq(name))
                        .fetchFirst() != null;
        } else {
            dupExists = queryFactory
                    .selectOne()
                    .from(mc)
                    .where(mc.deletedAt.isNull(),
                           mc.parent.id.eq(parentId),
                           mc.name.eq(name))
                    .fetchFirst() != null;
        }

        if (dupExists) {
            throw new DuplicateCategoryNameUnderSameParentOnRestore("동일한 이름의 활성 카테고리가 이미 존재합니다.");
        }

        long affected = queryFactory
            .update(mc)
            .setNull(mc.deletedAt)
            .setNull(mc.deletedBy)
            .where(mc.id.eq(id),
                   mc.deletedAt.isNotNull())
            .execute();

        if (affected == 1) {
            log.info("CATEGORY RESTORE SUCCESS id={}", id);
            return;
        }

        boolean exists = categoryRepository.existsById(id);
        if (!exists) throw new CategoryNotFoundException();
    }
}
