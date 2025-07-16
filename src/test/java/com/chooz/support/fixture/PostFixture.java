package com.chooz.support.fixture;

import com.chooz.post.domain.CloseOption;
import com.chooz.post.domain.CloseType;
import com.chooz.post.domain.CommentActive;
import com.chooz.post.domain.PollChoice;
import com.chooz.post.domain.PollOption;
import com.chooz.post.domain.PollType;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.Scope;
import com.chooz.post.domain.Status;

import java.time.LocalDateTime;
import java.util.List;

public class PostFixture {

    public static Post createDefaultPost(Long userId) {
        return createPostBuilder()
                .userId(userId)
                .build();
    }

    public static Post.PostBuilder createPostBuilder() {
        return Post.builder()
                .userId(1L)
                .title("Default title")
                .description("Default post description")
                .imageUrl("https://example.com/image.png")
                .shareUrl("http://example.com/post/1")
                .status(Status.PROGRESS)
                .closeOption(CloseOption.create(CloseType.SELF, null, null))
                .pollOption(PollOption.create(PollType.SINGLE, Scope.PUBLIC, CommentActive.OPEN))
                .pollChoices(List.of(
                        PollChoice.create("Choice A", "http://example.com/image/1"),
                        PollChoice.create("Choice B", "http://example.com/image/1")
                ));
    }

    public static CloseOption.CloseOptionBuilder createCloseOptionBuilder() {
        return CloseOption.builder()
                .closeType(CloseType.SELF)
                .closedAt(null)
                .maxVoterCount(null);
    }

    public static CloseOption createCloseOptionOverDate() {
        return new CloseOption(CloseType.DATE, LocalDateTime.now().minusMinutes(5), null);
    }

    public static PollOption.PollOptionBuilder createPollOptionBuilder() {
        return PollOption.builder()
                .pollType(PollType.SINGLE)
                .scope(Scope.PUBLIC)
                .commentActive(CommentActive.OPEN);
    }
}
