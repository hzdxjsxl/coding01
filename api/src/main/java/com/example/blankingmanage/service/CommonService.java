package com.example.blankingmanage.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class CommonService {
    @Autowired
    private MinioClient minioClient;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.bucket-name}")
    private String bucketName;
    /**
     * 高性能流式上传 (MinIO 版)
     */
    public String uploadFile(MultipartFile file) throws Exception {
        // 1. 获取后缀
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 2. 防御装甲 1：UUID 防覆盖
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        // 3. 防御装甲 2：按日期分片 (material/2026/04/21/xxx.jpg)
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String objectName = "material/" + datePath + "/" + uuid + extension;

        // 4. 极限性能流式直传
        try (InputStream inputStream = file.getInputStream()) {

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            // 🌟🌟🌟 防御装甲 3：极其致命的 Content-Type！
                            // 如果不加这句，MinIO 默认把图片当成二进制流，前端访问 URL 时会直接触发下载，而不是在网页里显示图片！
                            .contentType(file.getContentType())
                            .build()
            );
            log.info("MinIO 上传成功，路径: {}", objectName);
        }

        // 5. 拼接出浏览器可直接访问的 URL 返回给前端
        // 格式：http://127.0.0.1:9000/flange-sys/material/2026/04/21/xxx.jpg
        return endpoint + "/" + bucketName + "/" + objectName;
    }
}