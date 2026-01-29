package com.branding.branding_backend.s3;

import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class ImageDownloader {

    public byte[] download(String imageUrl) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // ✅ 1. HTTP 상태 코드 검증
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("이미지 다운로드 실패. HTTP status=" + status);
            }

            // ✅ 2. Content-Type 검증
            String contentType = connection.getContentType();
            if (contentType == null || !contentType.startsWith("image")) {
                throw new RuntimeException("이미지가 아닙니다. contentType=" + contentType);
            }

            try (InputStream in = connection.getInputStream();
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                return out.toByteArray();
            }

        } catch (Exception e) {
            throw new RuntimeException("이미지 다운로드 실패: " + imageUrl, e);
        } finally {
            // ✅ 3. 연결 정리
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}