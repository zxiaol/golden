package com.golden.service;

import com.golden.entity.Review;
import com.golden.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewMapper reviewMapper;
    
    public Map<String, Object> getReviews(Long productId, Integer page, Integer pageSize) {
        Integer offset = (page - 1) * pageSize;
        List<Review> reviews = reviewMapper.findByProductId(productId, offset, pageSize);
        int total = reviewMapper.countByProductId(productId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", reviews);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }
    
    public void createReview(Review review) {
        reviewMapper.insert(review);
    }
}

