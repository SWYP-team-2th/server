package com.chooz.notification.application.dto;

import lombok.Getter;

@Getter
public class VotedContent extends NotificationContent {

    private final Long postAuthorId;
    private final String postAuthorName;

    public VotedContent(
            String actorName,
            String actorProfileImageUrl,
            String targetThumbnailUrl,
            Long postAuthorId,
            String postAuthorName
    ) {
        super(actorName, targetThumbnailUrl, actorProfileImageUrl);
        this.postAuthorId = postAuthorId;
        this.postAuthorName = postAuthorName;
    }
}
