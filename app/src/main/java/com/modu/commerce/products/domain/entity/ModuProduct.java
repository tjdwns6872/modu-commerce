package com.modu.commerce.products.domain.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.modu.commerce.products.domain.type.ProductStatus;
import com.modu.commerce.products.domain.type.ProductVisibility;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "MODU_PRODUCT")
@Builder
@Data
public class ModuProduct {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "BRAND_ID")
    private Long brandId;

    @Column(name = "NAME", length = 200, nullable = false)
    private String name;

    @Column(name = "SLUG", length = 220, nullable = false)
    private String slug;

    @Column(name = "SUMMARY", length = 500)
    private String summary;

    @Column(name = "DESCRIPTIONMD")
    private String descriptionMd;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Column(name = "VISIBILITY")
    @Enumerated(EnumType.STRING)
    private ProductVisibility visibility;

    @Column(name = "SELL_START_AT")
    private LocalDateTime sellStartAt;

    @Column(name = "SELL_END_AT")
    private LocalDateTime sellEndAt;

    @CreatedDate
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;

    @Column(name = "DELETED_BY")
    private Long deletedBy;
    
    @Column(name = "IS_DELETED", insertable=false, updatable=false)
    private Integer isDeleted;
}
