package com.chooz.comment.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
        SELECT c
        FROM Comment c
        WHERE c.postId = :postId
        AND (
           (:cursorId IS NULL AND :priority IS NULL) OR
           (
             CASE WHEN c.userId = :userId THEN 0 ELSE 1 END = :priority AND c.id < :cursorId
           ) OR (
             CASE WHEN c.userId = :userId THEN 0 ELSE 1 END > :priority
           )
         )
        ORDER BY
            CASE WHEN c.userId = :userId THEN 0 ELSE 1 END,
            c.id DESC
    """)
    List<Comment> findCommentsByPostIdWithPriority(
        @Param("postId") Long postId,
        @Param("userId") Long userId,
        @Param("cursorId") Long cursorId,
        @Param("priority") Integer priority,
        Pageable pageable
    );

    List<Comment> findByPostIdAndDeletedFalse(Long postId);

    long countByPostId(Long postId);
}
