package com.chooz.comment.domain;

import com.chooz.comment.presentation.dto.CommentLikeCountProjection;
import com.chooz.user.domain.User;
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

    Optional<CommentLike> findByCommentIdInAndUserId(Long commentId, Long userId);

    @Query("""
            SELECT cl.comment.id AS commentId, COUNT(cl) AS likeCount
            FROM CommentLike cl
            WHERE cl.comment.id IN :commentIds 
            GROUP BY cl.comment.id
    """)
    List<CommentLikeCountProjection> countByCommentIds(@Param("commentIds") List<Long> commentIds);


    Long user(User user);
}
