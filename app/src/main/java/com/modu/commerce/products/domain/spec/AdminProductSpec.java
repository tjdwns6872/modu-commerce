package com.modu.commerce.products.domain.spec;

import com.modu.commerce.products.domain.type.ProductStatus;
import com.modu.commerce.products.domain.type.ProductVisibility;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminProductSpec {
    
    private String name;
    private String slug;
    private Long brandId;
    private Long[] categoryIds;
    private String summary;
    private String descriptionMd;
    private ProductVisibility visibility;
    private ProductStatus status;

    private String sellStartAt;
    private String sellEndAt;
}
