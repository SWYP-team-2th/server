package com.chooz.vote.domain;

import java.util.List;

public interface VoteRepository {

    Vote save(Vote vote);

    List<Vote> findByUserIdAndPostId(Long userId, Long postId);

    List<Vote> findAllByPostId(Long postId);

    List<Vote> findByPostIdAndDeletedFalse(Long id);

    long countVoterByPostId(Long postId);

    void deleteAll(List<Vote> votes);
}
