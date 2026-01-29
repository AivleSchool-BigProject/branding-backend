package com.branding.branding_backend.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /* ================= 업로드 ================= */
    public String upload(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        String fileName = createFileName(file.getOriginalFilename());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            amazonS3.putObject(bucket, fileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    /* ================= 삭제 ================= */
    public void delete(String imageUrl) {

        String fileName = extractFileName(imageUrl);
        amazonS3.deleteObject(bucket, fileName);
    }

    /* ================= 내부 메서드 ================= */
    private String createFileName(String originalFilename) {
        return "posts/" + UUID.randomUUID() + "_" + originalFilename;
    }

    private String extractFileName(String imageUrl) {
        return imageUrl.substring(imageUrl.indexOf(".com/") + 5);
    }

    //Logo url 업로드
    public String upload(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new IllegalArgumentException("이미지 데이터가 비어 있습니다.");
        }

        String fileName = "logos/" + UUID.randomUUID() + ".png";

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageBytes.length);
        metadata.setContentType("image/png");

        amazonS3.putObject(bucket, fileName, new ByteArrayInputStream(imageBytes), metadata);

        return amazonS3.getUrl(bucket, fileName).toString();
    }
}