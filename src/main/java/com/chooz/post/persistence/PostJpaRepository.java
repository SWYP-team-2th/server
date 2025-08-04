package com.chooz.post.persistence;

import com.chooz.post.application.dto.PostWithVoteCount;
import com.chooz.post.domain.CommentActive;
import com.chooz.post.domain.Post;
import com.chooz.post.application.dto.FeedDto;
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
            WHERE p.userId = :userId
            AND (:postId IS NULL OR p.id < :postId)
            ORDER BY p.id DESC
            """
    )
    Slice<Post> findByUserId(@Param("userId") Long userId, @Param("postId") Long postId, Pageable pageable);

    @Query("""
            SELECT p
            FROM Post p
            WHERE p.id IN :postIds
            AND (:postId IS NULL OR p.id < :postId)
            ORDER BY p.id DESC
            """
    )
    Slice<Post> findByIdIn(@Param("postIds") List<Long> postIds, @Param("postId") Long postId, Pageable pageable);

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
            SELECT new com.chooz.post.application.dto.FeedDto(
                    p.id,
                   	p.status,
                   	p.title,
                    t.thumbnailUrl,
                   	p.userId,
                   	u.nickname,
                   	u.profileUrl,
                   	cast((select count(distinct v.userId) from Vote v where p.id = v.postId) as long),
                   	cast((select count(*) from Comment c where p.id = c.postId and c.deleted = false) as long),
                    p.createdAt
            )
            FROM Post p
            INNER JOIN User u on p.userId = u.id
            LEFT JOIN Thumbnail t on p.id = t.postId
            WHERE p.deleted = false
            AND p.pollOption.scope = 'PUBLIC'
            AND (:postId IS NULL OR p.id < :postId)
            ORDER BY p.createdAt DESC
            """
    )
    Slice<FeedDto> findFeedByScopeWithUser(@Param("userId") Long userId, @Param("postId") Long postId, Pageable pageable);

    @Query("""
            SELECT p
            FROM Post p
            JOIN FETCH p.pollChoices
            WHERE p.shareUrl = :shareUrl
            """
    )
    Optional<Post> findByShareUrlFetchPollChoices(@Param("shareUrl") String shareUrl);

    @Query("""
            SELECT p
            FROM Post p
            WHERE p.closeOption.closeType = 'DATE'
            AND p.status = 'PROGRESS'
            AND p.closeOption.closedAt <= CURRENT_TIMESTAMP
            """
    )
    List<Post> findPostsNeedToClose();

    @Query("""
            SELECT p.pollOption.commentActive
            FROM Post p
            WHERE p.id = :postId
            """
    )
    Optional<CommentActive> findCommentActiveByPostId(@Param("postId") Long postId);

    @Query("""
        select new com.chooz.post.application.dto.PostWithVoteCount(
                p,
                count(distinct v.userId)
            )
        from Post p
        left join Vote v on v.postId = p.id
        where p.userId = :userId
        and (:postId is null or p.id < :postId)
        group by p
        order by p.id desc
        """
    )
    Slice<PostWithVoteCount> findPostsWithVoteCountByUserId(
            @Param("userId") Long userId,
            @Param("postId") Long postId,
            Pageable pageable
    );

    @Query("""
        select new com.chooz.post.application.dto.PostWithVoteCount(
                p,
                count(distinct v2.userId)
            )
        from Post p
        inner join Vote v on v.postId = p.id and v.userId = :userId
        left join Vote v2 on v2.postId = p.id
        where (:postId is null or p.id < :postId)
        group by p
        order by p.id desc
        """
    )
    Slice<PostWithVoteCount> findVotedPostsWithVoteCount(
            @Param("userId") Long userId,
            @Param("postId") Long postId,
            Pageable pageable
    );
}
