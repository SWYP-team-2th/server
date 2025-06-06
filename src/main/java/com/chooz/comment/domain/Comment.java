package com.chooz.comment.domain;

import com.chooz.common.domain.BaseEntity;
import com.chooz.post.domain.Post;
import com.chooz.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private int edited = 0;

    public void updateContent(String content) {
        this.content = content;
        this.edited = 1;
    }

    public static Comment of(String content, User user, Post post) {
        return Comment.builder()
                .content(content)
                .user(user)
                .post(post)
                .build();
    }
}
