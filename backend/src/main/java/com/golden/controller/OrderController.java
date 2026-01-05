package com.golden.controller;

import com.golden.entity.Order;
import com.golden.entity.OrderItem;
import com.golden.service.OrderService;
import com.golden.util.JwtUtil;
import com.golden.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final JwtUtil jwtUtil;
    
    private Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        return null;
    }
    
    @PostMapping
    public Result<Order> createOrder(HttpServletRequest request, @RequestParam String shippingAddress) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        try {
            Order order = orderService.createOrder(userId, shippingAddress);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping
    public Result<Map<String, Object>> getOrders(HttpServletRequest request,
                                                  @RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        Map<String, Object> result = orderService.getOrders(userId, page, pageSize);
        return Result.success(result);
    }
    
    @GetMapping("/{id}")
    public Result<Order> getOrderDetail(@PathVariable Long id) {
        Order order = orderService.getOrderDetail(id);
        return Result.success(order);
    }
    
    @GetMapping("/{id}/items")
    public Result<List<OrderItem>> getOrderItems(@PathVariable Long id) {
        List<OrderItem> items = orderService.getOrderItems(id);
        return Result.success(items);
    }
    
    @PutMapping("/{id}/status")
    public Result<Void> updateOrderStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            orderService.updateOrderStatus(id, status);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/admin/all")
    public Result<Map<String, Object>> getAllOrders(@RequestParam(defaultValue = "1") Integer page,
                                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = orderService.getAllOrders(page, pageSize);
        return Result.success(result);
    }
}

