package com.golden.controller;

import com.golden.util.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页控制器
 * 提供根路径的友好响应
 */
@RestController
public class IndexController {
    
    @GetMapping("/")
    public Result<String> index() {
        return Result.success("货架电商网站后端API服务", "API文档: /api/products, /api/auth/login 等");
    }
}

