package com.chooz.post.domain;

import com.chooz.post.presentation.dto.PollChoiceResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollChoiceRepository extends JpaRepository<PollChoice, Long> {

    @Query("""
            SELECT new com.chooz.post.presentation.dto.PollChoiceResponse(
                    pc.id,
                    pc.name,
                    i.imageUrl,
                    i.thumbnailUrl,
                    (SELECT v.id FROM Vote v WHERE v.pollChoiceId = pc.id AND v.userId = :userId)
            )
            FROM PollChoice pc
            INNER JOIN ImageFile i ON pc.imageFileId = i.id
            WHERE pc.post.id = :postId 
            ORDER BY pc.id ASC
            """
    )
    List<PollChoiceResponse> findByPostId(@Param("userId") Long userId, @Param("postId") Long postId);
}
