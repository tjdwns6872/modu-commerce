package com.modu.commerce.category.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.modu.commerce.category.dto.CategoryChildrenListRequest;
import com.modu.commerce.category.dto.CategoryChildrenListResponse;
import com.modu.commerce.category.dto.CategoryListRequest;
import com.modu.commerce.category.dto.CategoryListResponse;
import com.modu.commerce.category.dto.CategoryOneResponse;
import com.modu.commerce.category.dto.CategoryRequest;
import com.modu.commerce.category.service.CategoryService;
import com.modu.commerce.common.api.response.CommonResponseVO;

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
    public ResponseEntity<CommonResponseVO<CategoryOneResponse>> getOne(@PathVariable Long id){
        CategoryOneResponse data = categoryService.categoryOne(id);

        CommonResponseVO<CategoryOneResponse> response = CommonResponseVO.<CategoryOneResponse>builder()
            .code(HttpStatus.OK.value())
            .message("카테고리를 불러왔습니다.")
            .data(data)
            .build();

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<CommonResponseVO<CategoryListResponse>> getList(@ModelAttribute @Valid CategoryListRequest request){
        CategoryListResponse data = categoryService.categoryList(request);

        CommonResponseVO<CategoryListResponse> response = CommonResponseVO.<CategoryListResponse>builder()
            .code(HttpStatus.OK.value())
            .message("카테고리 목록 조회")
            .data(data)
            .build();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/categories/{id}/children")
    public ResponseEntity<CommonResponseVO<CategoryChildrenListResponse>> getChildrenList(@PathVariable Long id,
                                                                                CategoryChildrenListRequest request){
        CategoryChildrenListResponse data = categoryService.getChildrenList(request, id);
        
        CommonResponseVO<CategoryChildrenListResponse> response = CommonResponseVO.<CategoryChildrenListResponse>builder()
            .code(HttpStatus.OK.value())
            .message("자식 카테고리 목록 조회")
            .data(data)
            .build();
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
