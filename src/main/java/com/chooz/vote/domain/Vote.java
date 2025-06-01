package com.chooz.vote.domain;

import com.chooz.common.domain.BaseEntity;
import com.chooz.post.domain.PollChoice;
import com.chooz.post.domain.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user_votes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;

    private Long pollChoiceId;

    private Long userId;

    @Builder
    private Vote(Long id, Long postId, Long pollChoiceId, Long userId) {
        this.id = id;
        this.postId = postId;
        this.pollChoiceId = pollChoiceId;
        this.userId = userId;
    }

    public static Vote create(Long postId, Long pollChoiceId, Long userId) {
        return new Vote(null, postId, pollChoiceId, userId);
    }

    public boolean isVoter(Long userId) {
        return this.userId.equals(userId);
    }
}
