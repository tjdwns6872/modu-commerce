package com.modu.commerce.products.infra;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.modu.commerce.brand.domain.entity.ModuBrand;
import com.modu.commerce.brand.infra.BrandRepository;
import com.modu.commerce.products.domain.exception.BrandInactiveException;
import com.modu.commerce.products.domain.exception.BrandNotFoundException;

@Component
public class BrandReaderJpaAdapter {
    
    private final BrandRepository brandRepository;

    public BrandReaderJpaAdapter(BrandRepository brandRepository){
        this.brandRepository = brandRepository;
    }

    @Transactional(readOnly = true)
    public ModuBrand getOneBrand(Long brandId){
        if(brandId == null || brandId <= 0L){
            throw new BrandNotFoundException(brandId);
        }
        boolean exists = brandRepository.existsById(brandId);
        if(!exists){
            throw new BrandNotFoundException(brandId);
        }

        Optional<ModuBrand> active = brandRepository.findByIdAndDeletedAtIsNull(brandId);
        return active.orElseThrow(() -> new BrandInactiveException(brandId));
    }
}
