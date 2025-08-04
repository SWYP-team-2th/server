package com.chooz.post.persistence;

import com.chooz.post.application.dto.PostWithVoteCount;
import com.chooz.post.domain.CommentActive;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import com.chooz.post.application.dto.FeedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final PostJpaRepository postJpaRepository;
    private final PostQueryDslRepository postQueryDslRepository;

    @Override
    public Optional<Post> findById(Long postId) {
        return postJpaRepository.findByIdAndDeletedFalse(postId);
    }

    @Override
    public Post save(Post post) {
        return postJpaRepository.save(post);
    }

    @Override
    public Slice<Post> findAllByUserId(Long userId, Long postId, Pageable pageable) {
        return postQueryDslRepository.findByUserId(userId, postId, pageable);
    }

    @Override
    public Optional<Post> findByIdFetchPollChoices(Long postId) {
        return postJpaRepository.findByIdFetchPollChoices(postId);
    }

    @Override
    public Optional<Post> findByIdFetchPollChoicesWithLock(Long postId) {
        return postJpaRepository.findByIdFetchPollChoicesWithLock(postId);
    }

    @Override
    public Slice<FeedDto> findFeed(Long postId, Pageable pageable) {
        return postQueryDslRepository.findFeed(postId, pageable);
    }

    @Override
    public Optional<Post> findByShareUrlFetchPollChoices(String shareUrl) {
        return postJpaRepository.findByShareUrlFetchPollChoices(shareUrl);
    }

    @Override
    public List<Post> findPostsNeedToClose() {
        return postJpaRepository.findPostsNeedToClose();
    }

    @Override
    public Optional<CommentActive> findCommentActiveByPostId(Long postId) {
        return postJpaRepository.findCommentActiveByPostId(postId);
    }

    @Override
    public Slice<PostWithVoteCount> findPostsWithVoteCountByUserId(Long userId, Long postId, Pageable pageable) {
        return postQueryDslRepository.findPostsWithVoteCountByUserId(userId, postId, pageable);
    }

    @Override
    public Slice<PostWithVoteCount> findVotedPostsWithVoteCount(Long userId, Long postId, Pageable pageable) {
        return postQueryDslRepository.findVotedPostsWithVoteCount(userId, postId, pageable);
    }
}
