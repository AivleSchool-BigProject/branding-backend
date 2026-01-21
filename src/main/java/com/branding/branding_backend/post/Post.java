package com.branding.branding_backend.post;

import com.branding.branding_backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "promotion_posts")
@Getter
@Setter
@NoArgsConstructor

public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "short_description", length = 255, nullable = false)
    private String shortDescription;

    @Column(name = "logo_image_url", length = 255)
    private String logoImageUrl;

    @Column(length = 50)
    private String region;

    @Column(name = "contact_name", length = 50)
    private String contactName;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Lob
    @Column(name = "company_description")
    private String companyDescription;

    @Column(name = "company_size", nullable = false)
    private String companySize;

    @Column(length = 50)
    private String hashtag1;

    @Column(length = 50)
    private String hashtag2;

    @Column(length = 50)
    private String hashtag3;

    @Column(length = 50)
    private String hashtag4;

    @Column(length = 50)
    private String hashtag5;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
