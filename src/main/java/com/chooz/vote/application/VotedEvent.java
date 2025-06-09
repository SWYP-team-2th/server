package com.chooz.vote.application;

public record VotedEvent(Long id, Long pollChoiceId, Long voterId) {
}
