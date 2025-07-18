package com.chooz.vote.application;

import java.util.List;

public record VotedEvent(Long postId, List<Long> pollChoiceIds, Long voterId) {
}
