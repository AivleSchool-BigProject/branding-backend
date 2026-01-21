package com.branding.branding_backend.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PostDetailResponse {

    private Long postId;

    private String companyName;
    private String shortDescription;
    private String logoImageUrl;
    private String region;

    private String contactName;
    private String contactEmail;
    private List<String> hashtags;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
