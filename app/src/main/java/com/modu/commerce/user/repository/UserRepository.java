package com.modu.commerce.user.repository;

import org.springframework.stereotype.Repository;

import com.modu.commerce.user.entity.ModuUser;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<ModuUser, Long>{
    
    int countByEmail(String email);

    boolean existsByEmail(String email);
}
