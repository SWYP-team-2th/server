package com.chooz.post.domain;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
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
    
    public CloseOption(CloseType closeType, LocalDateTime closedAt, Integer maxVoterCount) {
        validateNull(closeType);
        validateCloseOption(closeType, closedAt, maxVoterCount);
        this.closeType = closeType;
        this.closedAt = closedAt;
        this.maxVoterCount = maxVoterCount;
    }

    public static CloseOption create(CloseType closeType, LocalDateTime closedAt, Integer maxVoterCount) {
        return new CloseOption(closeType, closedAt, maxVoterCount);
    }

    private void validateCloseOption(CloseType closeType, LocalDateTime closedAt, Integer maxVoterCount) {
        if (CloseType.DATE.equals(closeType) && Objects.isNull(closedAt)) {
            throw new BadRequestException(ErrorCode.INVALID_DATE);
        }
        if (CloseType.VOTER.equals(closeType)) {
            if (Objects.isNull(maxVoterCount)) {
                throw new BadRequestException(ErrorCode.INVALID_MAX_VOTER_COUNT);
            }
            if (maxVoterCount < 1 || maxVoterCount > 999) {
                throw new BadRequestException(ErrorCode.INVALID_MAX_VOTER_COUNT);
            }
        }
    }
}
