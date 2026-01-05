package com.golden.controller;

import com.golden.entity.Category;
import com.golden.service.CategoryService;
import com.golden.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    
    @GetMapping
    public Result<List<Category>> findAll() {
        List<Category> categories = categoryService.findAll();
        return Result.success(categories);
    }
    
    @GetMapping("/{parentId}")
    public Result<List<Category>> findByParentId(@PathVariable Long parentId) {
        List<Category> categories = categoryService.findByParentId(parentId);
        return Result.success(categories);
    }
}

