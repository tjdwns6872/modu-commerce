package com.modu.commerce.user.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.modu.commerce.security.JwtTokenProvider;
import com.modu.commerce.user.RoleEnum;
import com.modu.commerce.user.StatusEnum;
import com.modu.commerce.user.dto.UserDetailResponse;
import com.modu.commerce.user.dto.UserLoginRequest;
import com.modu.commerce.user.dto.UserLoginResponse;
import com.modu.commerce.user.dto.UserSignupRequest;
import com.modu.commerce.user.entity.ModuUser;
import com.modu.commerce.user.exception.EmailAlreadyExistsException;
import com.modu.commerce.user.exception.InvalidCredentialsException;
import com.modu.commerce.user.exception.StatusException;
import com.modu.commerce.user.exception.UserNotFoundException;
import com.modu.commerce.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserServiceImpl(UserRepository userRepository
                        ,PasswordEncoder passwordEncoder
                        ,JwtTokenProvider jwtTokenProvider){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public String signup(UserSignupRequest request) {
        log.info("UserSignupRequest DATA ==> {}", request.toString());
        boolean existsEmail = userRepository.existsByEmail(request.getEmail());
        if(existsEmail){
            log.error("SIGNUP EMAIL DUPLICATION ERROR");
            throw new EmailAlreadyExistsException();
        }
        ModuUser entity = request.toEntity(passwordEncoder.encode(request.getPassword())
                                        , RoleEnum.USER
                                        , StatusEnum.ACTIVE);

        entity = userRepository.save(entity);
        log.info("SIGNUP SUCCESS");
        return entity.getEmail();
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {

        ModuUser entity = userRepository.findByEmail(request.getEmail())
                            .orElseThrow(InvalidCredentialsException::new);

        StatusException statusException = entity.getStatus().getException();
        if(statusException != null) throw statusException;

        if(!passwordEncoder.matches(request.getPassword(), entity.getPassword())){
            log.error("LOGIN INVALID CREDENTIALS ERROR");
            throw new InvalidCredentialsException();
        }

        String token = jwtTokenProvider.generateToken(entity.getId());
        log.info("LOGIN SUCCESS - email: {}", entity.getEmail());

        log.info("LOGIN SUCCESS");
        UserLoginResponse response = UserLoginResponse.builder()
                    .id(entity.getId())
                    .email(entity.getEmail())
                    .token(token)
                    .build();

        return response;
    }

    @Override
    public UserDetailResponse userInfo(Long userId) {
        log.info("LOOKING FOR USER ID: {}", userId);

        ModuUser entity = userRepository.findById(userId)
                            .orElseThrow(UserNotFoundException::new);

        StatusException statusException = entity.getStatus().getException();
        if(statusException != null) throw statusException;

        UserDetailResponse response = UserDetailResponse.fromEntity(entity);
        log.info("USER INFO RETRIEVAL SUCCESS FOR ID: {}", userId);

        return response;
    }
    
}
