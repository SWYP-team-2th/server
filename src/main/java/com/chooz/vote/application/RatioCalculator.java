package com.chooz.vote.application;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class RatioCalculator {

    public String calculate(long totalVoteCount, long voteCount) {
        if (totalVoteCount == 0) {
            return "0.0";
        }
        BigDecimal totalCount = new BigDecimal(totalVoteCount);
        BigDecimal count = new BigDecimal(voteCount);
        BigDecimal bigDecimal = count.divide(totalCount, 3, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
        return String.format("%.1f", bigDecimal);
    }

    public String calculate(int totalVoteCount, long voteCount) {
        return calculate((long) totalVoteCount, voteCount);
    }
}
