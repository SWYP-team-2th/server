package com.chooz.post.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
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
    
    private PollOption(PollType pollType, Scope scope, CommentActive commentActive) {
        validateNull(pollType, scope, commentActive);
        this.pollType = pollType;
        this.scope = scope;
        this.commentActive = commentActive;
    }

    public static PollOption create(PollType pollType, Scope scope, CommentActive commentActive) {
        return new PollOption(pollType, scope, commentActive);
    }


    public void toggleScope() {
        this.scope = scope.equals(Scope.PRIVATE) ? Scope.PUBLIC : Scope.PRIVATE;
    }
    
    public void toggleCommentStatus() {
        this.commentActive = commentActive.equals(CommentActive.CLOSED) ? CommentActive.OPEN : CommentActive.CLOSED;
    }
} 