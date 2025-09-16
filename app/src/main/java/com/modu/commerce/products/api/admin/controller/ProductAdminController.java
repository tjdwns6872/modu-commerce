package com.modu.commerce.products.api.admin.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.modu.commerce.common.api.response.CommonResponseVO;
import com.modu.commerce.products.api.admin.dto.request.ProductCreateRequest;
import com.modu.commerce.products.app.ProductCommandService;
import com.modu.commerce.products.domain.spec.AdminProductSpec;

import jakarta.validation.Valid;

@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin")
@RestController
public class ProductAdminController {

    private final ProductCommandService productCommandService;

    public ProductAdminController(ProductCommandService productCommandService){
        this.productCommandService = productCommandService;
    }

    @PostMapping("/products")
    public ResponseEntity<CommonResponseVO<Long>> createProduct(@RequestBody @Valid ProductCreateRequest request){
        AdminProductSpec spec = AdminProductSpec.builder()
            .name(request.getName())
            .slug(request.getSlug())
            .brandId(request.getBrandId())
            .categoryIds(request.getCategoryIds())
            .summary(request.getSummary())
            .descriptionMd(request.getDescriptionMd())
            .visibility(request.getVisibility())
            .build();

        Long productId = productCommandService.createProduct(spec);

        CommonResponseVO<Long> response = CommonResponseVO.<Long>builder()
            .code(HttpStatus.CREATED.value())
            .message("상품을 생성했습니다.")
            .data(productId)
            .build();

        URI location = URI.create("/api/admin/products/" + productId);

        return ResponseEntity.created(location).body(response);
    }
}
