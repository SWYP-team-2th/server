package com.chooz.vote.persistence;

import com.chooz.vote.domain.Vote;
import com.chooz.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class VoteRepositoryImpl implements VoteRepository {

    private final VoteJpaRepository voteRepository;

    @Override
    public Vote save(Vote vote) {
        return voteRepository.save(vote);
    }

    @Override
    public List<Vote> findByUserIdAndPostId(Long userId, Long postId) {
        return voteRepository.findByUserIdAndPostIdAndDeletedFalse(userId, postId);
    }

    @Override
    public List<Vote> findAllByPostId(Long postId) {
        return voteRepository.findAllByPostIdAndDeletedFalse(postId);
    }

    @Override
    public List<Vote> findByPostIdAndDeletedFalse(Long id) {
        return voteRepository.findByPostIdAndDeletedFalse(id);
    }

    @Override
    public long countVoterByPostId(Long postId) {
        return voteRepository.countVoterByPostId(postId);
    }

    @Override
    public void deleteAll(List<Vote> votes) {
        voteRepository.deleteAll(votes);
    }
}
