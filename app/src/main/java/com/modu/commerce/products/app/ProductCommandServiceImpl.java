package com.modu.commerce.products.app;

import org.springframework.stereotype.Service;

import com.modu.commerce.products.domain.spec.AdminProductSpec;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductCommandServiceImpl implements ProductCommandService{

    @Override
    public void createProduct(AdminProductSpec spec) {
        log.info("\n{}", spec.toString());
        
    }
    
}
