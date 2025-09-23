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
        switch (closeType) {
            case SELF -> {
                return new CloseOption(closeType, null, null);
            }
            case DATE -> {
                validateDateCloseType(closedAt);
                return new CloseOption(closeType, closedAt, null);
            }
            case VOTER -> {
                validateVoterCloseType(maxVoterCount);
                return new CloseOption(closeType, closedAt, maxVoterCount);
            }
            default -> throw new BadRequestException(ErrorCode.INVALID_CLOSE_OPTION);
        }
    }

    private static void validateVoterCloseType(Integer maxVoterCount) {
        if (Objects.isNull(maxVoterCount) || (maxVoterCount < 1 || maxVoterCount > 999)) {
            throw new BadRequestException(ErrorCode.INVALID_VOTER_CLOSE_OPTION);
        }
    }

    private static void validateDateCloseType(LocalDateTime closedAt) {
        if (Objects.isNull(closedAt) || closedAt.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException(ErrorCode.INVALID_DATE_CLOSE_OPTION);
        }
    }
}
