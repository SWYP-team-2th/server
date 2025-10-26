package com.chooz.vote.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.chooz.vote.domain.QVote.vote;

@Repository
@RequiredArgsConstructor
public class VoteQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public Long countVoterByPostId(Long postId) {
        return queryFactory.select(vote.userId.countDistinct())
                .from(vote)
                .where(vote.postId.eq(postId))
                .where(vote.deleted.eq(false))
                .fetchOne();
    }

}
