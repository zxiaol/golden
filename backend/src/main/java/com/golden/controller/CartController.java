package com.golden.controller;

import com.golden.entity.CartItem;
import com.golden.service.CartService;
import com.golden.util.JwtUtil;
import com.golden.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final JwtUtil jwtUtil;
    
    private Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        return null;
    }
    
    @GetMapping
    public Result<List<CartItem>> getCart(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        List<CartItem> cartItems = cartService.getCart(userId);
        return Result.success(cartItems);
    }
    
    @PostMapping
    public Result<Void> addToCart(HttpServletRequest request, 
                                  @RequestParam Long productId,
                                  @RequestParam(defaultValue = "1") Integer quantity) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        try {
            cartService.addToCart(userId, productId, quantity);
            return Result.success("添加成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public Result<Void> updateCartItem(HttpServletRequest request, @PathVariable Long id, @RequestParam Integer quantity) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        try {
            cartService.updateCartItem(id, quantity);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> removeFromCart(HttpServletRequest request, @PathVariable Long id) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        try {
            cartService.removeFromCart(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

