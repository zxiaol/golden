package com.golden.service;

import com.golden.entity.Category;
import com.golden.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryMapper categoryMapper;
    
    public List<Category> findAll() {
        return categoryMapper.findAll();
    }
    
    public List<Category> findByParentId(Long parentId) {
        return categoryMapper.findByParentId(parentId);
    }
}

