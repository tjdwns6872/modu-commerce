package com.modu.commerce.products.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.modu.commerce.products.domain.entity.ModuProduct;

public interface ProductRepository extends JpaRepository<ModuProduct, Long>{
    
}
