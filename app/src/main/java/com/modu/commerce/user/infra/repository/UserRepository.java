package com.modu.commerce.user.infra.repository;

import org.springframework.stereotype.Repository;

import com.modu.commerce.user.domain.entity.ModuUser;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<ModuUser, Long>{
    
    Optional<ModuUser> findByEmail(String email);

    boolean existsByEmail(String email);
}
