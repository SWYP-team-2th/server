package com.chooz.vote.domain;

import com.chooz.post.application.dto.MostVotedPollChoice;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<Vote> findAllByPostIdIn(List<Long> postIds);

    @Query("""
            SELECT COUNT(DISTINCT v.userId)
            FROM Vote v
            WHERE v.postId = :postId
            """)
    long countVoterByPostId(@Param("postId") Long postId);

}
