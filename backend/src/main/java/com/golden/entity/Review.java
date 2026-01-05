package com.golden.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Review {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer rating; // 1-5星
    private String content;
    private String images; // JSON格式存储多张图片
    private LocalDateTime createdAt;
    
    // 关联查询字段
    private User user;
}

