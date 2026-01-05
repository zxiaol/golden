package com.golden.controller;

import com.golden.entity.Product;
import com.golden.service.ProductService;
import com.golden.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    
    @GetMapping
    public Result<Map<String, Object>> findAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = productService.findAll(categoryId, keyword, page, pageSize);
        return Result.success(result);
    }
    
    @GetMapping("/{id}")
    public Result<Product> findById(@PathVariable Long id) {
        Product product = productService.findById(id);
        return Result.success(product);
    }
    
    @PostMapping
    public Result<Void> create(@RequestBody Product product) {
        try {
            productService.create(product);
            return Result.success("创建成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Product product) {
        try {
            product.setId(id);
            productService.update(product);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            productService.delete(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

