package com.golden.controller;

import com.golden.dto.LoginRequest;
import com.golden.dto.LoginResponse;
import com.golden.dto.RegisterRequest;
import com.golden.service.UserService;
import com.golden.util.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;
    
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("用户注册请求: username={}, email={}", request.getUsername(), request.getEmail());
        try {
            userService.register(request);
            logger.info("用户注册成功: username={}", request.getUsername());
            return Result.success("注册成功", null);
        } catch (Exception e) {
            logger.error("用户注册失败: username={}, error={}", request.getUsername(), e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("用户登录请求: username={}", request.getUsername());
        try {
            LoginResponse response = userService.login(request);
            logger.info("用户登录成功: username={}, userId={}", request.getUsername(), response.getUser().getId());
            return Result.success(response);
        } catch (Exception e) {
            logger.warn("用户登录失败: username={}, reason={}", request.getUsername(), e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}

