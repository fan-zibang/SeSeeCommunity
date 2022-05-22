package com.fanzibang.community.controller;

import cn.hutool.core.io.FileUtil;
import com.fanzibang.community.constant.ReturnCode;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.utils.QiniuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
@Validated
public class UploadController {

    @Autowired
    private QiniuUtil qiniuUtil;

    @Value("${oss.qiniu.url}")
    private String url;

    @PostMapping("/image")
    public String image(@RequestParam("image") MultipartFile file) {
        // 文件原始名
        String originalFilename = file.getOriginalFilename();
        // 唯一的文件名称
        String filename = UUID.randomUUID().toString() + "." + FileUtil.extName(originalFilename);
        // 上传文件
        boolean isUpload = qiniuUtil.upload(file, filename);
        if (!isUpload){
            Asserts.fail(ReturnCode.RC301);
        }
        return url + filename;
    }

    @DeleteMapping("/image")
    public String image(@Valid @NotEmpty String filename) {
        boolean isDelete = qiniuUtil.delete(filename);
        if (!isDelete) {
            Asserts.fail(ReturnCode.RC302);
        }
        return filename;
    }
}
