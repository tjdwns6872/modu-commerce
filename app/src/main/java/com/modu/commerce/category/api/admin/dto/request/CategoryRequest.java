package com.modu.commerce.category.api.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryRequest {

    @Positive(message = "parentId는 null(루트) 또는 양수(자식)만 허용합니다.")
    private Long parentId;

    @NotBlank(message = "카테고리명(name)은 비어 있을 수 없습니다.")
    @Size(max = 100, message = "카테고리명은 100자 이하여야 합니다.")
    private String name;
}
