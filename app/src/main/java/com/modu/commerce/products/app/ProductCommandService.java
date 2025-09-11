package com.modu.commerce.products.app;

import com.modu.commerce.products.domain.spec.AdminProductSpec;

public interface ProductCommandService {
    
    public void createProduct(AdminProductSpec spec);
}
