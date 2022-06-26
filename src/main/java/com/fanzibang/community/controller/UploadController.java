package com.fanzibang.community.controller;

import com.fanzibang.community.service.UploadService;
import com.fanzibang.community.utils.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/upload")
@Validated
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @PostMapping("/image")
    public String image(@RequestParam("image") MultipartFile file) {
         return uploadService.uploadImage(file);
    }

    @DeleteMapping("/image")
    public CommonResult image(@Valid @NotEmpty String filename) {
        String data = uploadService.deleteImage(filename);
        return CommonResult.success(data);
    }
}
