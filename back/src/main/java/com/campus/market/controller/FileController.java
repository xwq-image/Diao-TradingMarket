package com.campus.market.controller;

import com.campus.market.common.Result;
import com.campus.market.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadFile(file);
        return Result.success(url);
    }

    @PostMapping("/upload-multiple")
    public Result<List<String>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        List<String> urls = fileService.uploadMultipleFiles(files);
        return Result.success(urls);
    }

    @DeleteMapping("/delete")
    public Result<Void> deleteFile(@RequestParam String fileName) {
        fileService.deleteFile(fileName);
        return Result.success();
    }
}
