package com.modu.commerce.user.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modu.commerce.common.api.response.CommonResponseVO;
import com.modu.commerce.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

import static com.modu.commerce.common.web.TraceIdFilter.TRACE_ID; // ★ traceId 키 상수
// import static com.modu.commerce.common.web.TraceIdFilter.TRACE_HEADER; // 필요하면 사용

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper; // ★ 빈 주입 (new 제거)

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 기본 보안 옵션
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults()) // ★ CORS 활성화
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 권한 규칙
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // ★ Preflight 허용
                .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/api/v1/users/signup",
                    "/api/v1/users/login",
                    "/actuator/health"
                ).permitAll()
                .requestMatchers( "/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )

            // 예외 처리: JSON + traceId
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) ->
                    writeJson(res, HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요합니다."))
                .accessDeniedHandler((req, res, ex) ->
                    writeJson(res, HttpServletResponse.SC_FORBIDDEN, "해당 리소스에 대한 권한이 없습니다."))
            )

            // JWT 필터
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void writeJson(HttpServletResponse res, int status, String message) throws java.io.IOException {
        String traceId = MDC.get(TRACE_ID); // ★ "traceId" 상수 사용
        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        var body = CommonResponseVO.of(status, message, null, traceId);
        res.getWriter().write(objectMapper.writeValueAsString(body));
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
