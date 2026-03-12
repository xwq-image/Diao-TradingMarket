package com.campus.market.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface FileService {
    String uploadFile(MultipartFile file);
    List<String> uploadMultipleFiles(MultipartFile[] files);
    void deleteFile(String fileName);
}
