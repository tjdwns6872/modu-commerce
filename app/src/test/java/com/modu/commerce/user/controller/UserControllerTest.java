package com.modu.commerce.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modu.commerce.user.config.SecurityConfig;
import com.modu.commerce.user.dto.UserSignupRequest;
import com.modu.commerce.user.service.UserService;

@WebMvcTest(UserController.class) //테스트 대상 컨트롤러 지정
@Import(SecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;   // HTTP 요청 흉내내는 객체

    @Autowired
    private ObjectMapper objectMapper;  // DTO를 JSON 문자열로 변환할 때 사용

    @MockitoBean
    private UserService userService;     // UserService를 Mock으로 지정

    @Test
    void testSignup() throws Exception {

        // 1) 데이터 생성
        UserSignupRequest request = new UserSignupRequest();
        request.setEmail("test@example.com");
        request.setPassword("pw1234");
        request.setNickname("nick");

        // 2) Service Mock 동작 정의: 회원가입 성공 시 이메일 반환
        when(userService.signup(any(UserSignupRequest.class))).thenReturn(request.getEmail());

        mockMvc.perform(post("/api/v1/users/signup")     // 실제 API URL과 맞춰야 함
                .contentType(MediaType.APPLICATION_JSON)       // 요청 본문 타입 지정
                .content(objectMapper.writeValueAsString(request))  // JSON 바디 문자열 전송
                .with(csrf()))
                .andExpect(status().isCreated())            // 응답 HTTP 상태 201 기대
                .andExpect(jsonPath("$.code").value(201))   // JSON 데이터 내 code 필드 검증
                .andExpect(jsonPath("$.message").value("회원가입 되었습니다."))  // 메시지 검증
                .andExpect(jsonPath("$.data").value("test@example.com"));  // 반환된 이메일 검증
    }
}
