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

    /* ================= Multipart 업로드 ================= */
    public String upload(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        String fileName = "posts/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

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

    /* ================= Logo 업로드 ================= */
    public String upload(DownloadedImage image) {

        String extension = extractExtension(image.getContentType());
        String fileName = "logos/" + UUID.randomUUID() + extension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.getBytes().length);
        metadata.setContentType(image.getContentType());

        amazonS3.putObject(
                bucket,
                fileName,
                new ByteArrayInputStream(image.getBytes()),
                metadata
        );

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    /* ================= 삭제 ================= */
    public void delete(String imageUrl) {
        String fileName = imageUrl.substring(imageUrl.indexOf(".com/") + 5);
        amazonS3.deleteObject(bucket, fileName);
    }

    /* ================= 내부 ================= */
    private String extractExtension(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            case "image/webp" -> ".webp";
            case "image/svg+xml" -> ".svg";
            default -> throw new IllegalArgumentException("지원하지 않는 이미지 타입: " + contentType);
        };
    }
}