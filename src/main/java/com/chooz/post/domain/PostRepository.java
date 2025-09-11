package com.chooz.post.domain;

import com.chooz.post.application.dto.FeedDto;
import com.chooz.post.application.dto.PostWithVoteCount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository {

    Optional<Post> findById(Long postId);

    Post save(Post post);

    Slice<Post> findAllByUserId(Long userId, Long postId, Pageable pageable);

    Optional<Post> findByIdFetchPollChoices(Long postId);

    Optional<Post> findByIdFetchPollChoicesWithLock(Long postId);

    Slice<FeedDto> findFeed(Long postId, Pageable pageable);

    Optional<Post> findByShareUrlFetchPollChoices(String shareUrl);

    List<Post> findPostsNeedToClose();

    Optional<CommentActive> findCommentActiveByPostId(Long postId);

    Slice<PostWithVoteCount> findPostsWithVoteCountByUserId(Long userId, Long postId, Pageable pageable);

    Slice<PostWithVoteCount> findVotedPostsWithVoteCount(Long userId, Long postId, Pageable pageable);

    Optional<Post> findByIdAndUserId(Long postId, Long userId);
}
