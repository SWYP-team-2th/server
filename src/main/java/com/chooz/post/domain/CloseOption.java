package com.chooz.post.domain;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.chooz.common.util.Validator.validateNull;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CloseOption {

    @Enumerated(EnumType.STRING)
    private CloseType closeType;

    private LocalDateTime closedAt;

    private Integer maxVoterCount;

    @Builder
    public CloseOption(CloseType closeType, LocalDateTime closedAt, Integer maxVoterCount) {
        validateNull(closeType);
        this.closeType = closeType;
        this.closedAt = closedAt;
        this.maxVoterCount = maxVoterCount;
    }

    public static CloseOption create(CloseType closeType, LocalDateTime closedAt, Integer maxVoterCount) {
        validateCloseOption(closeType, closedAt, maxVoterCount);
        return new CloseOption(closeType, closedAt, maxVoterCount);
    }

    private static void validateCloseOption(CloseType closeType, LocalDateTime closedAt, Integer maxVoterCount) {
        switch (closeType) {
            case SELF -> validateSelfCloseType(closedAt, maxVoterCount);
            case DATE -> validateDateCloseType(closedAt, maxVoterCount);
            case VOTER -> validateVoterCloseType(closedAt, maxVoterCount);
            default -> throw new BadRequestException(ErrorCode.INVALID_CLOSE_OPTION);
        }
    }

    private static void validateSelfCloseType(LocalDateTime closedAt, Integer maxVoterCount) {
        if (Objects.nonNull(closedAt) || Objects.nonNull(maxVoterCount)) {
            throw new BadRequestException(ErrorCode.INVALID_SELF_CLOSE_OPTION);
        }
    }

    private static void validateVoterCloseType(LocalDateTime closedAt, Integer maxVoterCount) {
        if (Objects.nonNull(closedAt) || Objects.isNull(maxVoterCount)) {
            throw new BadRequestException(ErrorCode.INVALID_VOTER_CLOSE_OPTION);
        }
        if (maxVoterCount < 1 || maxVoterCount > 999) {
            throw new BadRequestException(ErrorCode.INVALID_VOTER_CLOSE_OPTION);
        }
    }

    private static void validateDateCloseType(LocalDateTime closedAt, Integer maxVoterCount) {
        if (Objects.isNull(closedAt) || Objects.nonNull(maxVoterCount)) {
            throw new BadRequestException(ErrorCode.INVALID_DATE_CLOSE_OPTION);
        }
        if (closedAt.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException(ErrorCode.INVALID_DATE_CLOSE_OPTION);
        }
    }
}
