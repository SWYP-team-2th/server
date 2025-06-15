package com.chooz.comment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    boolean existsByCommentIdAndUserId(Long commentId, Long userId);

    List<CommentLike> findByCommentIdInAndUserId(List<Long> commentIds, Long userId);

    Optional<CommentLike> findByCommentIdAndUserId(Long commentId, Long userId);

    @Query("""
            SELECT cl.commentId AS commentId, COUNT(cl) AS likeCount
            FROM CommentLike cl
            WHERE cl.commentId IN :commentIds 
            GROUP BY cl.commentId
    """)
    List<CommentLikeCountProjection> countByCommentIds(@Param("commentIds") List<Long> commentIds);

}
