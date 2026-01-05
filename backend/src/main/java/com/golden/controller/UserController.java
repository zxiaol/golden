package com.golden.controller;

import com.golden.entity.User;
import com.golden.service.UserService;
import com.golden.util.JwtUtil;
import com.golden.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    private Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        return null;
    }
    
    @GetMapping("/profile")
    public Result<User> getProfile(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        User user = userService.getProfile(userId);
        user.setPassword(null); // 不返回密码
        return Result.success(user);
    }
    
    @PutMapping("/profile")
    public Result<Void> updateProfile(HttpServletRequest request, @RequestBody User user) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        try {
            userService.updateProfile(userId, user);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

