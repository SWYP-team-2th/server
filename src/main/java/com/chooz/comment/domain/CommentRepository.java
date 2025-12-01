package com.chooz.comment.domain;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
            AND (:cursorId is null OR c.id < :cursorId)
        ORDER BY
            c.id DESC
    """)
    Slice<Comment> findByPostId(
            @Param("postId") Long postId,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
    
    int countByPostId(@NotNull Long postId);

    List<Comment> findByPostIdAndDeletedFalse(@NotNull Long postId);

    void deleteAllByPostId(Long postId);
}
