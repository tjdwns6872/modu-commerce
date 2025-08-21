package com.modu.commerce.category.repository;

import com.modu.commerce.category.entity.QModuCategory;
import com.querydsl.core.types.dsl.BooleanExpression;

public class CategoryPredicate {
    
    public static BooleanExpression idEq(QModuCategory category, Long id){
        if (id == null) return null;
        return category.id.eq(id);
    }

    public static BooleanExpression parentIdEq(QModuCategory category, Long parentId){
        if(parentId == null) return null;
        return category.parent.id.eq(parentId);
    }

    public static BooleanExpression nameEq(QModuCategory category, String name){
        if(name == null || name.isBlank()) return null;
        return category.name.eq(name);
    }

    public static BooleanExpression nameContains(QModuCategory category, String name){
        if(name == null || name.isBlank()) return null;
        return category.name.contains(name);
    }

    public static BooleanExpression includeDeletedCheck(QModuCategory category, boolean includeDeleted){
        if(includeDeleted == true) return null;
        return category.isDeleted.eq(0);
    }
}
