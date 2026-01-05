package com.golden.mapper;

import com.golden.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartItemMapper {
    List<CartItem> findByUserId(Long userId);
    CartItem findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    CartItem findById(Long id);
    int insert(CartItem cartItem);
    int update(CartItem cartItem);
    int delete(Long id);
    int deleteByUserId(Long userId);
}

