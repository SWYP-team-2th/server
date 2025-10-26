package com.chooz.support.fixture;

import com.chooz.thumbnail.domain.Thumbnail;
import com.chooz.vote.domain.Vote;

public class ThumbnailFixture {

    public static Thumbnail createDefaultThumbnail(Long postId, Long pollChoiceId) {
        return Thumbnail.create(postId, pollChoiceId, "http://example.com/image");
    }

    public static Thumbnail.ThumbnailBuilder createThumbnailBuilder() {
        return Thumbnail.builder()
                .postId(1L)
                .pollChoiceId(1L)
                .thumbnailUrl("http://example.com/image");
    }
}
