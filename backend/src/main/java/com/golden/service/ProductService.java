package com.golden.service;

import com.golden.entity.Product;
import com.golden.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductMapper productMapper;
    
    public Map<String, Object> findAll(Long categoryId, String keyword, Integer page, Integer pageSize) {
        Integer offset = (page - 1) * pageSize;
        List<Product> products = productMapper.findAll(categoryId, keyword, offset, pageSize);
        int total = productMapper.count(categoryId, keyword);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", products);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }
    
    public Product findById(Long id) {
        return productMapper.findById(id);
    }
    
    public void create(Product product) {
        product.setStatus(1);
        productMapper.insert(product);
    }
    
    public void update(Product product) {
        productMapper.update(product);
    }
    
    public void delete(Long id) {
        productMapper.delete(id);
    }
}

