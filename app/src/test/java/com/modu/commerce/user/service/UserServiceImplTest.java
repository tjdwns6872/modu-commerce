package com.modu.commerce.user.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.modu.commerce.user.api.dto.request.UserLoginRequest;
import com.modu.commerce.user.api.dto.request.UserSignupRequest;
import com.modu.commerce.user.api.dto.response.UserLoginResponse;
import com.modu.commerce.user.app.service.UserServiceImpl;
import com.modu.commerce.user.domain.entity.ModuUser;
import com.modu.commerce.user.domain.exception.EmailAlreadyExistsException;
import com.modu.commerce.user.domain.exception.InvalidCredentialsException;
import com.modu.commerce.user.domain.type.UserRole;
import com.modu.commerce.user.domain.type.UserStatus;
import com.modu.commerce.user.infra.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testSignupNormal() {
        UserSignupRequest request = new UserSignupRequest();
        request.setEmail("test@example.com");
        request.setPassword("pw1234");
        request.setNickname("nick");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPw");
        ModuUser savedUser = ModuUser.builder()
            .email(request.getEmail())
            .password("encodedPw")
            .nickname("nick")
            .role(UserRole.USER)
            .status(UserStatus.ACTIVE)
            .build();
        when(userRepository.save(any(ModuUser.class))).thenReturn(savedUser);

        String email = userService.signup(request);

        assertEquals("test@example.com", email);
        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(passwordEncoder, times(1)).encode("pw1234");
        verify(userRepository, times(1)).save(any(ModuUser.class));
    }

    @Test
    void signupEmailException() {
        UserSignupRequest request = new UserSignupRequest();
        request.setEmail("duplicate@example.com");

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.signup(request));
        verify(userRepository, times(1)).existsByEmail("duplicate@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void testLoginSuccess() {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("12@naver.com"); 
        request.setPassword("test1123123234"); 

        ModuUser userEntity = ModuUser.builder()
            .id(1L)
            .email("1234@naver.com")
            .password("$2a$10$NXtsa0jQ9exSw4sYlohvfOpZMeDmSlSKl/Jk7PepZsMH7qwpSkR9u")
            .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(request.getPassword(), userEntity.getPassword())).thenReturn(true);

        UserLoginResponse response = userService.login(request);

        assertEquals(userEntity.getId(), response.getId());
        assertEquals(userEntity.getEmail(), response.getEmail());
    }

    @Test
    void testLoginFail() {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("1234@naver.com");
        request.setPassword("test1234");

        ModuUser userEntity = ModuUser.builder()
            .id(1L)
            .email("1234@naver.com")
            .password("$2a$10$NXtsa0jQ9exSw4sYlohvfOpZMeDmSlSKl/Jk7PepZsMH7qwpSkR9u")
            .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(request.getPassword(), userEntity.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.login(request));
    }
    
}