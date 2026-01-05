package com.golden.service;

import com.golden.entity.CartItem;
import com.golden.mapper.CartItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemMapper cartItemMapper;
    
    public List<CartItem> getCart(Long userId) {
        return cartItemMapper.findByUserId(userId);
    }
    
    public void addToCart(Long userId, Long productId, Integer quantity) {
        CartItem existing = cartItemMapper.findByUserIdAndProductId(userId, productId);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            cartItemMapper.update(existing);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setQuantity(quantity);
            cartItemMapper.insert(cartItem);
        }
    }
    
    
    public void updateCartItem(Long id, Integer quantity) {
        CartItem item = cartItemMapper.findById(id);
        if (item == null) {
            throw new RuntimeException("购物车项不存在");
        }
        item.setQuantity(quantity);
        cartItemMapper.update(item);
    }
    
    public void removeFromCart(Long id) {
        cartItemMapper.delete(id);
    }
    
    public void clearCart(Long userId) {
        cartItemMapper.deleteByUserId(userId);
    }
}

