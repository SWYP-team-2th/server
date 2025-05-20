package com.chooz.post.domain;

import com.chooz.common.domain.BaseEntity;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.common.exception.InternalServerException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Getter
@Entity
@ToString(exclude = "pollChoices")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Scope scope;

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PollChoice> pollChoices = new ArrayList<>();

    private String shareUrl;

    private VoteType voteType;

    public Post(
            Long id,
            Long userId,
            String description,
            Status status,
            Scope scope,
            List<PollChoice> pollChoices,
            String shareUrl,
            VoteType voteType
    ) {
        validateDescription(description);
        validatePollChoices(pollChoices);
        this.id = id;
        this.description = description;
        this.userId = userId;
        this.status = status;
        this.scope = scope;
        this.pollChoices = pollChoices;
        pollChoices.forEach(image -> image.setPost(this));
        this.shareUrl = shareUrl;
        this.voteType = voteType;
    }

    private void validatePollChoices(List<PollChoice> images) {
        if (images.size() < 2 || images.size() > 9) {
            throw new BadRequestException(ErrorCode.INVALID_POLL_CHOICE_COUNT);
        }
    }

    private void validateDescription(String description) {
        if (description.length() > 100) {
            throw new BadRequestException(ErrorCode.DESCRIPTION_LENGTH_EXCEEDED);
        }
    }

    public static Post create(Long userId, String description, List<PollChoice> images, Scope scope, VoteType voteType) {
        return new Post(null, userId, description, Status.PROGRESS, scope, images, null, voteType);
    }

    public PollChoice getBestPickedImage() {
        return pollChoices.stream()
                .max(Comparator.comparing(PollChoice::getVoteCount))
                .orElseThrow(() -> new InternalServerException(ErrorCode.POLL_CHOICE_NOT_FOUND));
    }

    public void vote(Long imageId) {
        PollChoice image = pollChoices.stream()
                .filter(pollChoice -> pollChoice.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(ErrorCode.POLL_CHOICE_NOT_FOUND));
        image.increaseVoteCount();
    }

    public void cancelVote(Long imageId) {
        PollChoice image = pollChoices.stream()
                .filter(pollChoice -> pollChoice.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new InternalServerException(ErrorCode.POLL_CHOICE_NOT_FOUND));
        image.decreaseVoteCount();
    }

    public void close(Long userId) {
        if (!isAuthor(userId)) {
            throw new BadRequestException(ErrorCode.NOT_POST_AUTHOR);
        }
        if (status == Status.CLOSED) {
            throw new BadRequestException(ErrorCode.POST_ALREADY_CLOSED);
        }
        this.status = Status.CLOSED;
    }

    public boolean isAuthor(Long userId) {
        return this.userId.equals(userId);
    }

    public void validateProgress() {
        if (!this.status.equals(Status.PROGRESS)) {
            throw new BadRequestException(ErrorCode.POST_ALREADY_CLOSED);
        }
    }

    public void setShareUrl(String shareUrl) {
        if (Objects.nonNull(this.shareUrl)) {
            throw new InternalServerException(ErrorCode.SHARE_URL_ALREADY_EXISTS);
        }
        this.shareUrl = shareUrl;
    }

    public void toggleScope(Long userId) {
        if (!isAuthor(userId)) {
            throw new BadRequestException(ErrorCode.NOT_POST_AUTHOR);
        }
        this.scope = scope.equals(Scope.PRIVATE) ? Scope.PUBLIC : Scope.PRIVATE;
    }
}
