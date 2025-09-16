package com.modu.commerce.brand.infra;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.modu.commerce.brand.domain.entity.ModuBrand;

public interface BrandRepository extends JpaRepository<ModuBrand, Long> {
    boolean existsById(Long id);
    Optional<ModuBrand> findByIdAndDeletedAtIsNull(Long id);
}
