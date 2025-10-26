package com.chooz.post.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.chooz.common.util.Validator.validateNull;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PollOption {

    @Enumerated(EnumType.STRING)
    private PollType pollType;

    @Enumerated(EnumType.STRING)
    private Scope scope;

    @Enumerated(EnumType.STRING)
    private CommentActive commentActive;

    @Builder
    private PollOption(PollType pollType, Scope scope, CommentActive commentActive) {
        this.pollType = pollType;
        this.scope = scope;
        this.commentActive = commentActive;
    }

    public static PollOption create(PollType pollType, Scope scope, CommentActive commentActive) {
        validateNull(pollType, scope, commentActive);
        return new PollOption(pollType, scope, commentActive);
    }
}
