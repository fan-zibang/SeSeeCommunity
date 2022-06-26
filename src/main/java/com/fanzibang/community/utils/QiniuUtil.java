package com.fanzibang.community.utils;

import com.alibaba.fastjson.JSON;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class QiniuUtil {

    private static final Logger logger = LoggerFactory.getLogger(QiniuUtil.class);

    /**
     * 七牛域名domain
     */
    @Value("${oss.qiniu.url}")
    private String url;
    /**
     * 七牛ACCESS_KEY
     */
    @Value("${oss.qiniu.access-key}")
    private String accessKey;
    /**
     * 七牛SECRET_KEY
     */
    @Value("${oss.qiniu.secret-key}")
    private String secretKey;
    /**
     * 七牛空间名
     */
    @Value("${oss.qiniu.bucket}")
    private String bucket;

    public boolean upload(MultipartFile file,String filename) {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.huanan());
        UploadManager uploadManager = new UploadManager(cfg);
        try {
            byte[] uploadBytes = file.getBytes();
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            Response response = uploadManager.put(uploadBytes, filename, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            return true;
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            return false;
        }
    }

    public boolean delete(String filename) {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.huanan());
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(bucket, filename);
            return true;
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            logger.error(ex.getMessage(),ex);
            return false;
        }
    }
}
