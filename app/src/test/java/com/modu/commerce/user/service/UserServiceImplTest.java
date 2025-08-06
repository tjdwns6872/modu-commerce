package com.modu.commerce.user.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.modu.commerce.user.RoleEnum;
import com.modu.commerce.user.StatusEnum;
import com.modu.commerce.user.dto.UserSignupRequest;
import com.modu.commerce.user.entity.ModuUser;
import com.modu.commerce.user.exception.EmailAlreadyExistsException;
import com.modu.commerce.user.repository.UserRepository;

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
            .role(RoleEnum.USER)
            .status(StatusEnum.ACTIVE)
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
}

