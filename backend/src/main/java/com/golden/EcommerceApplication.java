package com.golden;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.golden.mapper")
public class EcommerceApplication {
    private static final Logger logger = LoggerFactory.getLogger(EcommerceApplication.class);
    
    public static void main(String[] args) {
        logger.info("========================================");
        logger.info("  货架电商网站后端服务启动中...");
        logger.info("========================================");
        SpringApplication.run(EcommerceApplication.class, args);
        logger.info("========================================");
        logger.info("  后端服务启动成功！");
        logger.info("  服务地址: http://localhost:8080");
        logger.info("========================================");
    }
}

