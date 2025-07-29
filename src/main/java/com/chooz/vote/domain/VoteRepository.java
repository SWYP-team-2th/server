package com.chooz.vote.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findByUserIdAndPostId(Long userId, Long postId);

    List<Vote> findAllByPostId(Long postId);

    List<Vote> findByPostIdAndDeletedFalse(Long id);

    @Query("""
            SELECT COUNT(DISTINCT v.userId)
            FROM Vote v
            WHERE v.postId = :postId
            """)
    long countVoterByPostId(@Param("postId") Long postId);

}
