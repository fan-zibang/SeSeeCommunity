package com.fanzibang.community.service;

import com.fanzibang.community.utils.CommonResult;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    String uploadImage(MultipartFile file);

    String deleteImage(String filename);

}
