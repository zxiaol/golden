package com.golden.service;

import com.golden.dto.LoginRequest;
import com.golden.dto.LoginResponse;
import com.golden.dto.RegisterRequest;
import com.golden.entity.User;
import com.golden.mapper.UserMapper;
import com.golden.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public LoginResponse login(LoginRequest request) {
        logger.debug("开始验证用户登录: username={}", request.getUsername());
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("用户登录验证失败: username={}, reason=用户名或密码错误", request.getUsername());
            throw new RuntimeException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            logger.warn("用户登录失败: username={}, reason=账户已被禁用", request.getUsername());
            throw new RuntimeException("账户已被禁用");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        logger.debug("生成JWT Token成功: userId={}, username={}", user.getId(), user.getUsername());
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setPhone(user.getPhone());
        userInfo.setAvatar(user.getAvatar());
        response.setUser(userInfo);
        
        return response;
    }
    
    public void register(RegisterRequest request) {
        logger.debug("开始注册新用户: username={}, email={}", request.getUsername(), request.getEmail());
        if (userMapper.findByUsername(request.getUsername()) != null) {
            logger.warn("用户注册失败: username={}, reason=用户名已存在", request.getUsername());
            throw new RuntimeException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(1);
        
        userMapper.insert(user);
        logger.info("新用户注册成功: userId={}, username={}, email={}", user.getId(), user.getUsername(), user.getEmail());
    }
    
    public User getProfile(Long userId) {
        logger.debug("获取用户信息: userId={}", userId);
        User user = userMapper.findById(userId);
        if (user == null) {
            logger.warn("用户不存在: userId={}", userId);
        }
        return user;
    }
    
    public void updateProfile(Long userId, User user) {
        logger.info("更新用户信息: userId={}, username={}", userId, user.getUsername());
        user.setId(userId);
        userMapper.update(user);
        logger.info("用户信息更新成功: userId={}", userId);
    }
}

