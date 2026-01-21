package com.branding.branding_backend.post;

import com.branding.branding_backend.post.dto.PostCreateRequest;
import com.branding.branding_backend.post.dto.PostDetailResponse;
import com.branding.branding_backend.post.dto.PostListResponse;
import com.branding.branding_backend.post.dto.PostUpdateRequest;
import com.branding.branding_backend.s3.S3Uploader;
import com.branding.branding_backend.user.User;
import com.branding.branding_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionPostService {

    private final PromotionPostRepository postRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    /* ================= 홍보물 등록 ================= */
    public Long createPost(
            Long userId,
            PostCreateRequest request,
            MultipartFile image
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        String imageUrl = s3Uploader.upload(image);

        PromotionPost post = PromotionPost.create(user, request, imageUrl);
        return postRepository.save(post).getPostId();
    }

    /* ================= 목록 조회 ================= */
    @Transactional(readOnly = true)
    public List<PostListResponse> getPostList() {

        List<PromotionPost> posts = postRepository.findAllByOrderByUpdatedAtDesc();
        List<PostListResponse> result = new ArrayList<>();

        for (PromotionPost post : posts) {
            result.add(PostListResponse.from(post));
        }

        return result;
    }

    /* ================= 상세 조회 ================= */
    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId) {

        PromotionPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        return PostDetailResponse.from(post);
    }

    /* ================= 수정 ================= */
    public void updatePost(
            Long postId,
            Long userId,
            PostUpdateRequest request,
            MultipartFile image
    ) {
        PromotionPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        post.update(request);

        if (image != null && !image.isEmpty()) {

            if (post.getLogoImageUrl() != null) {
                s3Uploader.delete(post.getLogoImageUrl());
            }

            String newImageUrl = s3Uploader.upload(image);
            post.updateImage(newImageUrl);
        }
    }

    /* ================= 삭제 ================= */
    public void deletePost(Long postId, Long userId) {

        PromotionPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        if (post.getLogoImageUrl() != null) {
            s3Uploader.delete(post.getLogoImageUrl());
        }

        postRepository.delete(post);
    }
}