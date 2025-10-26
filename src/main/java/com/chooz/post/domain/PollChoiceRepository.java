package com.chooz.post.domain;

import com.chooz.post.application.dto.PollChoiceVoteInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollChoiceRepository extends JpaRepository<PollChoice, Long> {

    @Query("""
            select new com.chooz.post.application.dto.PollChoiceVoteInfo(
                    pc.post.id,
                    pc.id,
                    count(v.id),
                    pc.title
            )
            from PollChoice pc
            left join Vote v on pc.id = v.pollChoiceId
            where pc.post.id in :postIds
            group by pc.post.id, pc.id
            """)
    List<PollChoiceVoteInfo> findPollChoiceWithVoteInfo(@Param("postIds") List<Long> postIds);
}
