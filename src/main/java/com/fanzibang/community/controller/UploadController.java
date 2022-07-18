package com.fanzibang.community.controller;

import com.fanzibang.community.service.UploadService;
import com.fanzibang.community.utils.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/file")
@Validated
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @PostMapping("/img")
    public String upload(@RequestParam("img") MultipartFile file,
                         @Valid @Min(value = 1, message = "上传类型：1-头像；2-帖子图片")
                         @Max(value = 2,message = "上传类型：1-头像；2-帖子图片") Integer type) {
         return uploadService.upload(file, type);
    }

    @DeleteMapping("/img")
    public CommonResult delete(@Valid @NotEmpty String filename,
                               @Valid @Min(value = 1, message = "上传类型：1-头像；2-帖子图片")
                               @Max(value = 2,message = "上传类型：1-头像；2-帖子图片") Integer type) {
        String data = uploadService.delete(filename, type);
        return CommonResult.success(data);
    }
}
