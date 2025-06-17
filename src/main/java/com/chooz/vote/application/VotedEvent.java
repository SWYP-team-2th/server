package com.chooz.vote.application;

public record VotedEvent(Long postId, Long pollChoiceId, Long voterId) {
}
