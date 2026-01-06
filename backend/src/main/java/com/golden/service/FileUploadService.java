package com.golden.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    @Value("${file.upload.url}")
    private String uploadUrl;
    
    public String uploadImage(MultipartFile file) throws IOException {
        logger.info("开始上传文件: originalFilename={}, size={} bytes", file.getOriginalFilename(), file.getSize());
        
        if (file.isEmpty()) {
            logger.warn("文件上传失败: 文件为空");
            throw new RuntimeException("文件为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;
        
        logger.debug("生成文件名: originalFilename={}, newFilename={}", originalFilename, filename);
        
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            logger.info("创建上传目录: path={}", uploadPath);
            uploadDir.mkdirs();
        }
        
        Path path = Paths.get(uploadPath + filename);
        Files.write(path, file.getBytes());
        
        String fileUrl = uploadUrl + filename;
        logger.info("文件上传成功: filename={}, url={}", filename, fileUrl);
        
        return fileUrl;
    }
}

