package com.modu.commerce.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.modu.commerce.common.api.response.CommonResponseVO;
import com.modu.commerce.user.dto.UserSignupRequest;
import com.modu.commerce.user.service.UserService;

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
}
