package com.demo.minio;

import com.demo.minio.utils.MinioUtil;
import io.minio.MinioClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MinioApplicationTests {

    @Autowired
    private MinioUtil minioUtil;

    @Test
    void contextLoads() {
        String url = minioUtil.getPresignedObjectUrl("local-test", "4f486cb1-691c-4254-9488-a1421793c1d1.jpg");
        System.out.println(url);
    }

}
