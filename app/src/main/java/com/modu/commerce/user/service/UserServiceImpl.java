package com.modu.commerce.user.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.modu.commerce.user.RoleEnum;
import com.modu.commerce.user.StatusEnum;
import com.modu.commerce.user.dto.UserLoginRequest;
import com.modu.commerce.user.dto.UserLoginResponse;
import com.modu.commerce.user.dto.UserSignupRequest;
import com.modu.commerce.user.entity.ModuUser;
import com.modu.commerce.user.exception.EmailAlreadyExistsException;
import com.modu.commerce.user.exception.InvalidCredentialsException;
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
        log.info("UserLoginRequest DATA ==> {}", request.toString());
        Optional<ModuUser> optionalUser = userRepository.findByEmail(request.getEmail());

        ModuUser entity = optionalUser.orElseThrow(InvalidCredentialsException::new);
        log.info("Entity DATA ==> {}", entity.toString());
        UserLoginResponse response = new UserLoginResponse();
        log.info("PASSWORD MATCHES START");
        log.info("PASSWORD MATCHES RESULT===>{}", passwordEncoder.matches(request.getPassword(), entity.getPassword()));
        if(passwordEncoder.matches(request.getPassword(), entity.getPassword())){
            response = UserLoginResponse.builder()
                                            .id(entity.getId())
                                            .email(entity.getEmail())
                                            .build();
        }else{
            log.error("LOGIN INVALID CREDENTIALS ERROR");
            throw new InvalidCredentialsException();
        }
        log.info("PASSWORD MATCHES END");
        log.info("LOGIN SUCCESS");
        return response;
    }
    
}
