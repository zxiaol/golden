package com.golden.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Long categoryId;
    private String imageUrls; // JSON格式存储多张图片
    private Integer status; // 0-下架 1-上架
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

