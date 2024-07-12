package com.ls.lease.web.admin.service.impl;

import com.ls.lease.common.minio.MinioProperties;
import com.ls.lease.web.admin.service.FileService;
import io.minio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 图片上传业务
 */
@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinioProperties properties;

    @Override
    public String upload(MultipartFile file) {
        try {
            //判断桶是否存在
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(properties.getBucketName())
                    .build()
            );

            if (!bucketExists) {
                //创建桶
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(properties.getBucketName())
                        .build()
                );
                //设置桶的访问权限
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                        .bucket(properties.getBucketName())
                        .config(createBucketPolicyConfig(properties.getBucketName()))
                        .build());
            }
//            minioClient.uploadObject() 上传本地图片
            //设置文件名
            String filename = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
            //上传文件
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(properties.getBucketName())
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .object(filename)
                    .contentType(file.getContentType())//设置响应体，内容类型，不然这种处理方式时字符流，浏览器访问字符流的文件会下载，而不是查看。
                    .build()
            );
            //拼接url
//            String url = properties.getEndpoint() +"/"+properties.getBucketName()+"/"+filename;
            String url = String.join("/", properties.getEndpoint(), properties.getBucketName(), filename);

            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String createBucketPolicyConfig(String bucketName) {

        return """
                {
                  "Statement" : [ {
                    "Action" : "s3:GetObject",
                    "Effect" : "Allow",
                    "Principal" : "*",
                    "Resource" : "arn:aws:s3:::%s/*"
                  } ],
                  "Version" : "2012-10-17"
                }
                """.formatted(bucketName);
    }
}
