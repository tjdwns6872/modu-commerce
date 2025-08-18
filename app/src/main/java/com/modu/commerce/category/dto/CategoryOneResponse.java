package com.modu.commerce.category.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class CategoryOneResponse {
    
    private Long id;
    private Long parentId;
    private String name;
    private int depth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean hasChildren;
}
