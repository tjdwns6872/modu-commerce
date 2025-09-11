package com.modu.commerce.category.api.admin.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryOneResponse {
    
    private Long id;
    private Long parentId;
    private String name;
    private int depth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean hasChildren;
}
