package com.chooz.vote.persistence;

import com.chooz.vote.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteJpaRepository extends JpaRepository<Vote, Long> {
    List<Vote> findByUserIdAndPostIdAndDeletedFalse(Long userId, Long postId);

    List<Vote> findAllByPostIdAndDeletedFalse(Long postId);

    List<Vote> findByPostIdAndDeletedFalse(Long id);

    void deleteAllByPostId(Long postId);
}
