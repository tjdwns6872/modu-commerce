package com.modu.commerce.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.modu.commerce.user.RoleEnum;
import com.modu.commerce.user.StatusEnum;
import com.modu.commerce.user.dto.UserSignupRequest;
import com.modu.commerce.user.entity.ModuUser;
import com.modu.commerce.user.exception.EmailAlreadyExistsException;
import com.modu.commerce.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository
                        ,PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String signup(UserSignupRequest request) {
        log.info("UserSignupRequest DATA ==> {}", request.toString());
        boolean existsEmail = userRepository.existsByEmail(request.getEmail());
        if(existsEmail){
            throw new EmailAlreadyExistsException();
        }
        ModuUser entity = request.toEntity(passwordEncoder.encode(request.getPassword())
                                        , RoleEnum.USER
                                        , StatusEnum.ACTIVE);

        entity = userRepository.save(entity);
        return entity.getEmail();
    }
    
}
