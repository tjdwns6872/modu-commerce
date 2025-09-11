package com.modu.commerce.products.api.admin.dto.request;

import com.modu.commerce.products.domain.type.ProductVisibility;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductCreateRequest {

    @NotBlank(message = "상품명(name)은 비어 있을 수 없습니다.")
    @Size(max = 200, message = "상품명은 200자 이하여야 합니다.")
    private String name;
    
    @Size(max = 220, message = "slug은 220자 이하여야 합니다.")
    private String slug;

    @Positive(message = "brandId는 null(노브랜드) 또는 양수만 허용합니다.")
    private Long brandId;

    private Long[] categoryIds;

    @Size(max = 500, message = "summary은 500자 이하여야 합니다.")
    private String summary;

    private String descriptionMd;

    private ProductVisibility visibility;
}
