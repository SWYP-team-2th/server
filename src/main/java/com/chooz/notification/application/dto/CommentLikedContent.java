package com.chooz.notification.application.dto;

import lombok.Getter;

@Getter
public class CommentLikedContent extends NotificationContent {

    private final Long commentAuthorId;
    private final String commentAuthorName;

    public CommentLikedContent(
            String actorName,
            String actorProfileImageUrl,
            String targetThumbnailUrl,
            Long commentAuthorId,
            String commentAuthorName
    ) {
        super(actorName, targetThumbnailUrl, actorProfileImageUrl);
        this.commentAuthorId = commentAuthorId;
        this.commentAuthorName = commentAuthorName;
    }
}
