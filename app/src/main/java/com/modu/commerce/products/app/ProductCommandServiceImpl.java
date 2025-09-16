package com.modu.commerce.products.app;

import org.springframework.stereotype.Service;

import com.modu.commerce.products.domain.entity.ModuProduct;
import com.modu.commerce.products.domain.exception.InvalidProductNameException;
import com.modu.commerce.products.domain.exception.ProductConstraintViolationException;
import com.modu.commerce.products.domain.spec.AdminProductSpec;
import com.modu.commerce.products.domain.type.ProductStatus;
import com.modu.commerce.products.infra.BrandReaderJpaAdapter;
import com.modu.commerce.products.infra.CategoryReaderJpaAdapter;
import com.modu.commerce.products.infra.ProductRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductCommandServiceImpl implements ProductCommandService{

    private final BrandReaderJpaAdapter brandReaderJpaAdapter;
    private final CategoryReaderJpaAdapter categoryReaderJpaAdapter;
    private final ProductRepository productRepository;

    public ProductCommandServiceImpl(BrandReaderJpaAdapter brandReaderJpaAdapter
                                    ,CategoryReaderJpaAdapter categoryReaderJpaAdapter
                                    ,ProductRepository productRepository){
        this.brandReaderJpaAdapter = brandReaderJpaAdapter;
        this.categoryReaderJpaAdapter = categoryReaderJpaAdapter;
        this.productRepository = productRepository;
    }

    @Override
    public Long createProduct(AdminProductSpec spec) {
        log.info("\n{}", spec.toString());
        
        final String name = spec.getName() == null ? "" : spec.getName().strip();

        if(name.isEmpty()){
            throw new InvalidProductNameException();
        }

        if(spec.getBrandId() != null){
            brandReaderJpaAdapter.getOneBrand(spec.getBrandId());
        }
        categoryReaderJpaAdapter.getOneCategory(spec.getCategoryIds()[0]);

        ModuProduct toSave = ModuProduct.builder()
                                .brandId(spec.getBrandId())
                                .name(name)
                                .summary(spec.getSummary())
                                .descriptionMd(spec.getDescriptionMd())
                                .status(ProductStatus.DRAFT)
                                .visibility(spec.getVisibility())
                                .sellStartAt(null)
                                .sellEndAt(null)
                                .build();

        try {
            ModuProduct saved = productRepository.save(toSave);
            log.debug("PRODUCT CREATION SUCCESSFUL id={}", saved.getId());
            return saved.getId();
        } catch (Exception e) {
            throw new ProductConstraintViolationException();
        }
    }
    
}
