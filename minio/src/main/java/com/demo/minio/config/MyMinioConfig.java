package com.demo.minio.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author tfan
 * @Description MinIO配置类,读取配置属性并初始化MinIO客户端
 * @Date 2025/4/17 16:17
 **/

@Configuration
@ConfigurationProperties(prefix = "minio")
@Data
public class MyMinioConfig {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder().endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
