package com.golden.mapper;

import com.golden.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {
    List<Product> findAll(@Param("categoryId") Long categoryId, @Param("keyword") String keyword, 
                         @Param("offset") Integer offset, @Param("limit") Integer limit);
    int count(@Param("categoryId") Long categoryId, @Param("keyword") String keyword);
    Product findById(Long id);
    int insert(Product product);
    int update(Product product);
    int delete(Long id);
}

