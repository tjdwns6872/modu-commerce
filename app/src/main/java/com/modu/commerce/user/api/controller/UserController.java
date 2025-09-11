package com.modu.commerce.user.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.modu.commerce.common.api.response.CommonResponseVO;
import com.modu.commerce.security.CustomUserDetails;
import com.modu.commerce.user.api.dto.request.UserLoginRequest;
import com.modu.commerce.user.api.dto.request.UserSignupRequest;
import com.modu.commerce.user.api.dto.response.UserDetailResponse;
import com.modu.commerce.user.api.dto.response.UserLoginResponse;
import com.modu.commerce.user.app.service.UserService;

import jakarta.validation.Valid;

@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }
    
    @PostMapping("/signup")
    public ResponseEntity<CommonResponseVO<String>> signup(@RequestBody @Valid UserSignupRequest request){

        String email = userService.signup(request);

        CommonResponseVO<String> response = CommonResponseVO.<String>builder()
            .code(HttpStatus.CREATED.value())
            .message("회원가입 되었습니다.")
            .data(email)
            .build();

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponseVO<UserLoginResponse>> login(@RequestBody @Valid UserLoginRequest request){

        UserLoginResponse result = userService.login(request);

        CommonResponseVO<UserLoginResponse> response = CommonResponseVO.<UserLoginResponse>builder()
            .code(HttpStatus.OK.value())
            .message("로그인 되었습니다.")
            .data(result)
            .build();

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/detail")
    public ResponseEntity<CommonResponseVO<UserDetailResponse>> userInfo(@AuthenticationPrincipal CustomUserDetails details){
        UserDetailResponse result = userService.userInfo(details.getId());

        CommonResponseVO<UserDetailResponse> response = CommonResponseVO.<UserDetailResponse>builder()
            .code(HttpStatus.OK.value())
            .message("회원정보를 성공적으로 가져왔습니다.")
            .data(result)
            .build();

        return ResponseEntity.status(response.getCode()).body(response);
    }
}
