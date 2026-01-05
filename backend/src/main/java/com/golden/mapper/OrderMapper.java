package com.golden.mapper;

import com.golden.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    int insert(Order order);
    Order findById(Long id);
    Order findByOrderNo(String orderNo);
    List<Order> findByUserId(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    int countByUserId(Long userId);
    int update(Order order);
    List<Order> findAll(@Param("offset") Integer offset, @Param("limit") Integer limit);
    int countAll();
}

