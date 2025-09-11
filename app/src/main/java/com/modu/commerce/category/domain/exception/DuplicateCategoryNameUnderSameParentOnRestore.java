package com.modu.commerce.category.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateCategoryNameUnderSameParentOnRestore extends RuntimeException {
    public DuplicateCategoryNameUnderSameParentOnRestore(String message) {
        super(message);
    }
}
