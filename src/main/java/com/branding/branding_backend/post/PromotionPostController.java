package com.branding.branding_backend.post;

import com.branding.branding_backend.post.dto.PostCreateRequest;
import com.branding.branding_backend.post.dto.PostDetailResponse;
import com.branding.branding_backend.post.dto.PostListResponse;
import com.branding.branding_backend.post.dto.PostUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/brands/posts")
public class PromotionPostController {

    private final PromotionPostService postService;

    /* ================= 홍보물 목록 조회 ================= */
    @GetMapping
    public List<PostListResponse> getPostList() {
        return postService.getPostList();
    }

    /* ================= 홍보물 상세 조회 ================= */
    @GetMapping("/{postId}")
    public PostDetailResponse getPostDetail(@PathVariable Long postId) {
        return postService.getPostDetail(postId);
    }

    /* ================= 홍보물 등록 ================= */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long createPost(
            @RequestPart("data") PostCreateRequest request,
            @RequestPart("image") MultipartFile image,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return postService.createPost(userId, request, image);
    }

    /* ================= 홍보물 수정 ================= */
    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updatePost(
            @PathVariable Long postId,
            @RequestPart("data") PostUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        postService.updatePost(postId, userId, request, image);
    }

    /* ================= 홍보물 삭제 ================= */
    @DeleteMapping("/{postId}")
    public void deletePost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        postService.deletePost(postId, userId);
    }
}