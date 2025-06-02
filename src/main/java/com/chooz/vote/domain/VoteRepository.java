package com.chooz.vote.domain;

import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findByUserIdAndPostId(Long userId, Long postId);

    Slice<Vote> findByUserId(Long userId);

    Optional<Vote> findByUserIdAndPollChoiceId(Long voterId, Long pollChoiceId);

    List<Vote> findAllByPostId(Long postId);

    List<Vote> findByPostIdAndDeletedFalse(Long id);
}
