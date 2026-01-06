package com.golden.controller;

import com.golden.entity.Product;
import com.golden.service.ProductService;
import com.golden.util.Result;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    
    @GetMapping
    public Result<Map<String, Object>> findAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        logger.info("查询商品列表: categoryId={}, keyword={}, page={}, pageSize={}", categoryId, keyword, page, pageSize);
        Map<String, Object> result = productService.findAll(categoryId, keyword, page, pageSize);
        logger.debug("商品列表查询完成: total={}", result.get("total"));
        return Result.success(result);
    }
    
    @GetMapping("/{id}")
    public Result<Product> findById(@PathVariable Long id) {
        logger.info("查询商品详情: productId={}", id);
        Product product = productService.findById(id);
        if (product == null) {
            logger.warn("商品不存在: productId={}", id);
        }
        return Result.success(product);
    }
    
    @PostMapping
    public Result<Void> create(@RequestBody Product product) {
        logger.info("创建商品: name={}, price={}", product.getName(), product.getPrice());
        try {
            productService.create(product);
            logger.info("商品创建成功: productId={}, name={}", product.getId(), product.getName());
            return Result.success("创建成功", null);
        } catch (Exception e) {
            logger.error("商品创建失败: name={}, error={}", product.getName(), e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Product product) {
        logger.info("更新商品: productId={}, name={}", id, product.getName());
        try {
            product.setId(id);
            productService.update(product);
            logger.info("商品更新成功: productId={}", id);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            logger.error("商品更新失败: productId={}, error={}", id, e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        logger.info("删除商品: productId={}", id);
        try {
            productService.delete(id);
            logger.info("商品删除成功: productId={}", id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            logger.error("商品删除失败: productId={}, error={}", id, e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}

