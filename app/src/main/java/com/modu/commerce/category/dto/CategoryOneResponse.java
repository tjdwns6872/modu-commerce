package com.modu.commerce.category.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryOneResponse {
    
    private Long id;
    private Long parentId;
    private String name;
    private int depth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean hasChildren;
}
