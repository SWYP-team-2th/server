package com.chooz.post.persistence;

import com.chooz.post.application.dto.PostWithVoteCount;
import com.chooz.post.domain.CommentActive;
import com.chooz.post.domain.Post;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostJpaRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByIdAndDeletedFalse(Long postId);

    @Query("""
            SELECT p
            FROM Post p
            JOIN FETCH p.pollChoices
            WHERE p.id = :postId
            AND p.deleted = false
            """
    )
    Optional<Post> findByIdFetchPollChoices(@Param("postId") Long postId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT p
            FROM Post p
            JOIN FETCH p.pollChoices
            WHERE p.id = :postId
            AND p.deleted = false
            """
    )
    Optional<Post> findByIdFetchPollChoicesWithLock(@Param("postId") Long postId);

    @Query("""
            SELECT p
            FROM Post p
            JOIN FETCH p.pollChoices
            WHERE p.shareUrl = :shareUrl
            AND p.deleted = false
            """
    )
    Optional<Post> findByShareUrlFetchPollChoices(@Param("shareUrl") String shareUrl);

    @Query("""
            SELECT p
            FROM Post p
            WHERE p.closeOption.closeType = 'DATE'
            AND p.status = 'PROGRESS'
            AND p.closeOption.closedAt <= CURRENT_TIMESTAMP
            AND p.deleted = false
            """
    )
    List<Post> findPostsNeedToClose();

    @Query("""
            SELECT p.pollOption.commentActive
            FROM Post p
            WHERE p.id = :postId
            AND p.deleted = false
            """
    )
    Optional<CommentActive> findCommentActiveByPostId(@Param("postId") Long postId);

    @Query("""
        select new com.chooz.post.application.dto.PostWithVoteCount(
                p,
                count(distinct v2.userId)
            )
        from Post p
        inner join Vote v on v.postId = p.id and v.userId = :userId
        left join Vote v2 on v2.postId = p.id
        where (:postId is null or p.id < :postId)
        AND p.deleted = false
        group by p
        order by p.id desc
        """
    )
    Slice<PostWithVoteCount> findVotedPostsWithVoteCount(
            @Param("userId") Long userId,
            @Param("postId") Long postId,
            Pageable pageable
    );

    Optional<Post> findByIdAndUserIdAndDeletedFalse(Long postId, Long userId);
}
