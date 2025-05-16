package com.chooz.post.presentation.dto;

import com.chooz.post.domain.Post;
import com.chooz.post.domain.Status;
import com.chooz.user.domain.User;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        Long id,
        AuthorDto author,
        String description,
        List<PostImageResponse> images,
        String shareUrl,
        boolean isAuthor,
        Status status,
        LocalDateTime createdAt
) {
    public static PostResponse of(Post post, User user, List<PostImageResponse> images, boolean isAuthor) {
        return new PostResponse(
                post.getId(),
                AuthorDto.of(user),
                post.getDescription(),
                images,
                post.getShareUrl(),
                isAuthor,
                post.getStatus(),
                post.getCreatedAt()
        );
    }
}
