package com.golden.controller;

import com.golden.entity.Review;
import com.golden.service.ReviewService;
import com.golden.util.JwtUtil;
import com.golden.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final JwtUtil jwtUtil;
    
    private Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        return null;
    }
    
    @GetMapping("/product/{productId}")
    public Result<Map<String, Object>> getReviews(@PathVariable Long productId,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = reviewService.getReviews(productId, page, pageSize);
        return Result.success(result);
    }
    
    @PostMapping
    public Result<Void> createReview(HttpServletRequest request, @RequestBody Review review) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        try {
            review.setUserId(userId);
            reviewService.createReview(review);
            return Result.success("评价成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

