package com.modu.commerce.category.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.modu.commerce.category.entity.ModuCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {

    @Positive(message = "parentId는 양수여야 합니다.")
    private Long parentId;

    @NotBlank(message = "카테고리명(name)은 비어 있을 수 없습니다.")
    @Size(max = 100, message = "카테고리명은 100자 이하여야 합니다.")
    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long depth = 1L;

    public ModuCategory toEntity(){
        return new ModuCategory(null, parentId, name, depth, null, null);
    }
}
