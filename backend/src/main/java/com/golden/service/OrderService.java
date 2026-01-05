package com.golden.service;

import com.golden.entity.CartItem;
import com.golden.entity.Order;
import com.golden.entity.OrderItem;
import com.golden.entity.Product;
import com.golden.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;
    
    @Transactional
    public Order createOrder(Long userId, String shippingAddress) {
        List<CartItem> cartItems = cartItemMapper.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("购物车为空");
        }
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            Product product = productMapper.findById(item.getProductId());
            if (product == null || product.getStock() < item.getQuantity()) {
                throw new RuntimeException("商品库存不足");
            }
            totalAmount = totalAmount.add(product.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }
        
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        order.setTotalAmount(totalAmount);
        order.setStatus(0);
        order.setShippingAddress(shippingAddress);
        orderMapper.insert(order);
        
        for (CartItem item : cartItems) {
            Product product = productMapper.findById(item.getProductId());
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(item.getProductId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setSubtotal(product.getPrice().multiply(new BigDecimal(item.getQuantity())));
            orderItemMapper.insert(orderItem);
            
            // 减少库存
            product.setStock(product.getStock() - item.getQuantity());
            productMapper.update(product);
        }
        
        // 清空购物车
        cartItemMapper.deleteByUserId(userId);
        
        return order;
    }
    
    public Map<String, Object> getOrders(Long userId, Integer page, Integer pageSize) {
        Integer offset = (page - 1) * pageSize;
        List<Order> orders = orderMapper.findByUserId(userId, offset, pageSize);
        int total = orderMapper.countByUserId(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", orders);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }
    
    public Order getOrderDetail(Long id) {
        return orderMapper.findById(id);
    }
    
    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemMapper.findByOrderId(orderId);
    }
    
    public void updateOrderStatus(Long id, Integer status) {
        Order order = orderMapper.findById(id);
        order.setStatus(status);
        orderMapper.update(order);
    }
    
    public Map<String, Object> getAllOrders(Integer page, Integer pageSize) {
        Integer offset = (page - 1) * pageSize;
        List<Order> orders = orderMapper.findAll(offset, pageSize);
        int total = orderMapper.countAll();
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", orders);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }
}

