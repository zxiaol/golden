package com.golden.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long id;
    private Long userId;
    private String orderNo;
    private BigDecimal totalAmount;
    private Integer status; // 0-待支付 1-已支付 2-已发货 3-已完成 4-已取消
    private String shippingAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

