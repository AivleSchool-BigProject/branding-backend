package com.branding.branding_backend.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PostListResponse {

    private Long postId;
    private String companyName;
    private String shortDescription;
    private String logoImageUrl;
    private String region;
    private String companySize;
    private List<String> hashtags;
    private LocalDateTime updatedAt;
}
