package com.golden.mapper;

import com.golden.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> findAll();
    List<Category> findByParentId(Long parentId);
    Category findById(Long id);
}

