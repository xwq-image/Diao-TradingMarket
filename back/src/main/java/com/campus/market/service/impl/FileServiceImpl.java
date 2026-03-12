package com.campus.market.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.campus.market.exception.BusinessException;
import com.campus.market.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload-path}")
    private String uploadPath;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Override
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小不能超过10MB");
        }

        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = FileUtil.extName(originalFilename);

        // 检查文件类型
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BusinessException("不支持的文件类型，仅支持图片格式");
        }

        // 生成新文件名
        String newFileName = IdUtil.simpleUUID() + "." + extension;

        // 确保上传目录存在
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 保存文件
        File destFile = new File(uploadPath + File.separator + newFileName);
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            throw new BusinessException("文件上传失败");
        }

        // 返回文件访问路径
        return "/uploads/" + newFileName;
    }

    @Override
    public List<String> uploadMultipleFiles(MultipartFile[] files) {
        List<String> urls = new ArrayList<>();
        
        if (files == null || files.length == 0) {
            throw new BusinessException("文件不能为空");
        }

        if (files.length > 5) {
            throw new BusinessException("最多只能上传5张图片");
        }

        for (MultipartFile file : files) {
            String url = uploadFile(file);
            urls.add(url);
        }

        return urls;
    }

    @Override
    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        // 从URL中提取文件名
        String realFileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        File file = new File(uploadPath + File.separator + realFileName);

        if (file.exists()) {
            file.delete();
        }
    }
}
