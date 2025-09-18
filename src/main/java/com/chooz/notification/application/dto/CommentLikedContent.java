package com.chooz.notification.application.dto;

import lombok.Getter;

@Getter
public class CommentLikedContent extends NotificationContent {

    private final Long commentAuthorId;

    public CommentLikedContent(
            String title,
            String body,
            String thumbnailUrl,
            String profileImageUrl,
            Long commentAuthorId
    ) {
        super(title, body, thumbnailUrl, profileImageUrl);
        this.commentAuthorId = commentAuthorId;
    }
}
