package com.chooz.vote.application;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class RatioCalculator {

    public String calculate(long totalVoteCount, long voteCount) {
        if (totalVoteCount == 0) {
            return "0";
        }
        BigDecimal totalCount = new BigDecimal(totalVoteCount);
        BigDecimal count = new BigDecimal(voteCount);
        BigDecimal bigDecimal = count.divide(totalCount, 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
        return String.valueOf(bigDecimal.intValue());
    }

    public String calculate(int totalVoteCount, long voteCount) {
        return calculate((long) totalVoteCount, voteCount);
    }
}
