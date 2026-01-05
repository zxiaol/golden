package com.golden.service;

import com.golden.dto.LoginRequest;
import com.golden.dto.LoginResponse;
import com.golden.dto.RegisterRequest;
import com.golden.entity.User;
import com.golden.mapper.UserMapper;
import com.golden.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("账户已被禁用");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        
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
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(1);
        
        userMapper.insert(user);
    }
    
    public User getProfile(Long userId) {
        return userMapper.findById(userId);
    }
    
    public void updateProfile(Long userId, User user) {
        user.setId(userId);
        userMapper.update(user);
    }
}

