package com.fanzibang.community.service.impl;

import cn.hutool.core.io.FileUtil;
import com.fanzibang.community.constant.ReturnCode;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.service.UploadService;
import com.fanzibang.community.utils.QiniuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class UploadServiceImpl implements UploadService {

    @Autowired
    private QiniuUtil qiniuUtil;

    @Value("${oss.qiniu.url}")
    private String url;

    @Override
    public String uploadImage(MultipartFile file) {
        String imgUrl = null;
        if (file.isEmpty()) {
            Asserts.fail(ReturnCode.RC1003);
        }
        if (file.getContentType().equals(MediaType.IMAGE_JPEG_VALUE) || file.getContentType().equals(MediaType.IMAGE_PNG_VALUE)) {
            // 文件原始名
            String originalFilename = file.getOriginalFilename();
            // 唯一的文件名称
            String filename = UUID.randomUUID() + "." + FileUtil.extName(originalFilename);
            // 上传文件
            boolean isUpload = qiniuUtil.upload(file, filename);
            if (!isUpload) {
                Asserts.fail(ReturnCode.RC1001);
            }
            imgUrl = url + filename;
        } else {
            Asserts.fail(ReturnCode.RC1004);
        }
        return imgUrl;

    }

    @Override
    public String deleteImage(String filename) {
        boolean isDelete = qiniuUtil.delete(filename);
        if (!isDelete) {
            Asserts.fail(ReturnCode.RC1002);
        }
        return null;
    }
}
