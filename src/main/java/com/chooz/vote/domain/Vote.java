package com.chooz.vote.domain;

import com.chooz.common.domain.BaseEntity;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
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

    public static Vote create(Long userId, Long postId, Long pollChoiceId) {
        return new Vote(null, postId, pollChoiceId, userId);
    }

    public void validateVoter(Long userId) {
        if (!this.userId.equals(userId)) {
            throw new BadRequestException(ErrorCode.NOT_VOTER);
        }
    }

    public void updatePollChoiceId(Long pollChoiceId) {
        this.pollChoiceId = pollChoiceId;
    }
}
