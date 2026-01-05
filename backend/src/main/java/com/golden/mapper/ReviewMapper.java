package com.golden.mapper;

import com.golden.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper {
    List<Review> findByProductId(@Param("productId") Long productId, 
                                @Param("offset") Integer offset, @Param("limit") Integer limit);
    int countByProductId(Long productId);
    int insert(Review review);
}

