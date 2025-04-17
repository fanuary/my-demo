package com.demo.minio.service.impl;

import com.demo.minio.config.MyMinioConfig;
import com.demo.minio.service.MinioService;
import io.minio.*;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * @Author tfan
 * @Description
 * @Date 2025/4/17 18:42
 **/

@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {


    private final MinioClient minioClient;

    private final MyMinioConfig myMinioConfig;


    @Override
    public String uploadFile(MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        String fileName = UUID.randomUUID().toString()
                + originalFilename.substring(originalFilename.lastIndexOf("."));
        // 检查存储桶是否存在
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(myMinioConfig.getBucketName()).build());
        if (!bucketExists) {
            // 桶不存在，创建桶
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(myMinioConfig.getBucketName()).build());
        }
        // 上传文件
        minioClient.putObject(PutObjectArgs.builder().bucket(myMinioConfig.getBucketName()).object(fileName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType()).build());

        return myMinioConfig.getEndpoint()
                + "/" + myMinioConfig.getBucketName() + "/" + fileName;
    }

    @Override
    public InputStream downloadFile(String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.getObject(GetObjectArgs.builder().bucket(myMinioConfig.getBucketName()).object(fileName).build());
    }

    @Override
    public void deleteFile(String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(myMinioConfig.getBucketName()).object(fileName).build());
    }
}
