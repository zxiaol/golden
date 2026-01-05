package com.golden.entity;

import lombok.Data;

@Data
public class Category {
    private Long id;
    private String name;
    private Long parentId;
    private Integer sortOrder;
    private Integer status; // 0-禁用 1-启用
}

