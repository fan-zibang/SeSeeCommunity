package com.fanzibang.community.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {

    String upload(MultipartFile file, Integer type);

    String delete(String filename, Integer type);

}
