package com.modu.commerce.category.api.admin.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.modu.commerce.category.api.admin.dto.request.AdminCategoryListRequest;
import com.modu.commerce.category.api.admin.dto.request.CategoryChildrenListRequest;
import com.modu.commerce.category.api.admin.dto.request.CategoryRequest;
import com.modu.commerce.category.api.admin.dto.response.CategoryChildrenListResponse;
import com.modu.commerce.category.api.admin.dto.response.CategoryListResponse;
import com.modu.commerce.category.api.admin.dto.response.CategoryOneResponse;
import com.modu.commerce.category.app.service.CategoryService;
import com.modu.commerce.category.domain.spec.CategoryDeleteSpec;
import com.modu.commerce.category.domain.spec.CategoryListSpec;
import com.modu.commerce.category.domain.spec.CategoryOneSpec;
import com.modu.commerce.common.api.response.CommonResponseVO;
import com.modu.commerce.common.exception.InvalidPageRequest;
import com.modu.commerce.security.CustomUserDetails;

import jakarta.validation.Valid;

@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin")
@RestController
public class CategoryAdminController {

    private final CategoryService categoryService;

    public CategoryAdminController(CategoryService categoryService){
        this.categoryService = categoryService;
    }
    
    @PostMapping("/categories")
    public ResponseEntity<CommonResponseVO<Long>> createCategory(@RequestBody @Valid CategoryRequest request){
        Long categoryId = categoryService.createCategory(request);

        CommonResponseVO<Long> response = CommonResponseVO.<Long>builder()
            .code(HttpStatus.CREATED.value())
            .message("카테고리를 생성했습니다.")
            .data(categoryId)
            .build();

        URI location = URI.create("/api/admin/categories/" + categoryId);

        return ResponseEntity.created(location).body(response);

    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<CommonResponseVO<CategoryOneResponse>> getOne(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean includeDeleted){
        CategoryOneSpec spec = CategoryOneSpec.builder()
                                .id(id)
                                .includeDeleted(includeDeleted)
                                .build();
        CategoryOneResponse data = categoryService.categoryOne(spec);

        CommonResponseVO<CategoryOneResponse> response = CommonResponseVO.<CategoryOneResponse>builder()
            .code(HttpStatus.OK.value())
            .message("카테고리를 불러왔습니다.")
            .data(data)
            .build();

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<CommonResponseVO<CategoryListResponse>> getList(@ModelAttribute @Valid AdminCategoryListRequest request) throws InvalidPageRequest{
        
        CategoryListSpec spec = CategoryListSpec.builder()
                                .parentId(request.getParentId())
                                .keyword(request.getKeyword())
                                .includeDeleted(request.getIncludeDeleted())
                                .withHasChildren(request.getWithHasChildren())
                                .page(request.getPage())
                                .size(request.getSize())
                                .build();
        
        CategoryListResponse data = categoryService.categoryList(spec);

        CommonResponseVO<CategoryListResponse> response = CommonResponseVO.<CategoryListResponse>builder()
            .code(HttpStatus.OK.value())
            .message("카테고리 목록 조회")
            .data(data)
            .build();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/categories/{id}/children")
    public ResponseEntity<CommonResponseVO<CategoryChildrenListResponse>> getChildrenList(@PathVariable Long id,
                                                                                @ModelAttribute @Valid CategoryChildrenListRequest request) throws InvalidPageRequest{
        CategoryChildrenListResponse data = categoryService.getChildrenList(request, id);
        
        CommonResponseVO<CategoryChildrenListResponse> response = CommonResponseVO.<CategoryChildrenListResponse>builder()
            .code(HttpStatus.OK.value())
            .message("자식 카테고리 목록 조회")
            .data(data)
            .build();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> categorySoftDelete(@AuthenticationPrincipal CustomUserDetails details, @PathVariable Long id){
        
        CategoryDeleteSpec spec = CategoryDeleteSpec.builder()
                                    .id(id)
                                    .actorId(details.getId())
                                    .build();
        categoryService.categorySoftDelete(spec);
        
        return ResponseEntity.status(HttpStatus.NO_CONTENT.value()).body(null);
    }

    @PatchMapping("/categories/{id}/restore")
    public ResponseEntity<Void> categoryRestore(@PathVariable Long id){
        categoryService.categoryRestore(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT.value()).body(null);
    }
}
