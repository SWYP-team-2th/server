package com.chooz.post.domain;

import com.chooz.common.domain.BaseEntity;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.common.exception.InternalServerException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.chooz.common.util.Validator.validateNull;

@Getter
@Entity
@ToString(exclude = "pollChoices")
@Table(name = "posts")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String imageUrl;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PollChoice> pollChoices = new ArrayList<>();

    private String shareUrl;

    @Embedded
    private PollOption pollOption;
    
    @Embedded
    private CloseOption closeOption;

    @Builder
    private Post(
            Long id,
            Long userId,
            String title,
            String description,
            String imageUrl,
            Status status,
            List<PollChoice> pollChoices,
            String shareUrl,
            PollOption pollOption,
            CloseOption closeOption
    ) {
        validateNull(userId, title, description, pollChoices, imageUrl);
        validateTitle(title);
        validateDescription(description);
        validatePollChoices(pollChoices);
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.status = status;
        this.pollChoices = pollChoices;
        pollChoices.forEach(pollChoice -> pollChoice.setPost(this));
        this.shareUrl = shareUrl;
        this.pollOption = pollOption;
        this.closeOption = closeOption;
    }

    public static Post create(
            Long userId,
            String title,
            String description,
            String imageUrl,
            List<PollChoice> pollChoices,
            String shareUrl,
            PollOption pollOption,
            CloseOption closeOption
    ) {
        return new Post(
                null,
                userId,
                title,
                description,
                imageUrl,
                Status.PROGRESS,
                pollChoices,
                shareUrl,
                pollOption,
                closeOption
        );
    }

    private static void validatePollChoices(List<PollChoice> images) {
        if (images.size() < 2 || images.size() > 10) {
            throw new BadRequestException(ErrorCode.INVALID_POLL_CHOICE_COUNT);
        }
    }

    private static void validateDescription(String description) {
        if (description.length() > 100) {
            throw new BadRequestException(ErrorCode.DESCRIPTION_LENGTH_EXCEEDED);
        }
    }
    
    private static void validateTitle(String title) {
        if (StringUtils.hasText(title) && title.length() > 50) {
            throw new BadRequestException(ErrorCode.TITLE_LENGTH_EXCEEDED);
        }
    }

    public void closeByAuthor(Long userId) {
        if (!isAuthor(userId)) {
            throw new BadRequestException(ErrorCode.NOT_POST_AUTHOR);
        }
        close();
    }

    public void close() {
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
        pollOption.toggleScope();
    }

    public void validateCloseDate(Clock clock) {
        if (closeOption.getClosedAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(ErrorCode.CLOSE_DATE_OVER);
        }
    }

    public void validateMaxVoterCount(long voterCount) {
        if (closeOption.getMaxVoterCount() >= voterCount) {
            throw new BadRequestException(ErrorCode.EXCEED_MAX_VOTER_COUNT);
        }
    }

    public boolean isSingleVote() {
        return PollType.SINGLE.equals(pollOption.getPollType());
    }

    public boolean isCloseTypeVoter() {
        return CloseType.VOTER.equals(closeOption.getCloseType());
    }

    public boolean isClosableByVoterCount(long voterCount) {
        return closeOption.getMaxVoterCount() == voterCount;
    }

    public boolean isClosed() {
        return this.status.equals(Status.CLOSED);
    }
}
