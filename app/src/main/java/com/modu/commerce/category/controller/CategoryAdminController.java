package com.modu.commerce.category.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.modu.commerce.category.dto.CategoryRequest;
import com.modu.commerce.category.service.CategoryService;
import com.modu.commerce.common.api.response.CommonResponseVO;

import jakarta.validation.Valid;

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

        return ResponseEntity.status(response.getCode()).body(response);

    }
}
