package com.chooz.post.persistence;

import com.chooz.post.application.dto.PostWithVoteCount;
import com.chooz.post.application.dto.QFeedDto;
import com.chooz.post.application.dto.QPostWithVoteCount;
import com.chooz.post.domain.Post;
import com.chooz.post.application.dto.FeedDto;
import com.chooz.post.domain.Scope;
import com.chooz.user.domain.QUser;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.chooz.comment.domain.QComment.comment;
import static com.chooz.post.domain.QPost.*;
import static com.chooz.user.domain.QUser.*;
import static com.chooz.vote.domain.QVote.vote;

@Repository
@RequiredArgsConstructor
public class PostQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    Slice<Post> findByUserId(Long userId, Long postId, Pageable pageable) {
        List<Post> results = queryFactory.selectFrom(post)
                .where(
                        post.userId.eq(userId),
                        cursor(postId, post.id),
                        post.deleted.isFalse()
                )
                .orderBy(post.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = isHasNext(pageable, results);

        if (hasNext) {
            results.removeLast();
        }
        return new SliceImpl<>(results, pageable, hasNext);
    }

    private Predicate cursor(Long cursor, NumberPath<Long> id) {
        return cursor != null ? id.lt(cursor) : null;
    }

    private boolean isHasNext(Pageable pageable, List<?> results) {
        return results.size() > pageable.getPageSize();
    }

    /**
     * 피드 관련 데이터 조회
     * @param postId
     * @param pageable
     * @return
     */
    public Slice<FeedDto> findFeed(Long postId, Pageable pageable) {
        List<FeedDto> results = queryFactory
                .select(new QFeedDto(
                        post.id,
                        post.status,
                        post.title,
                        post.imageUrl,
                        post.userId,
                        user.nickname,
                        user.profileUrl,
                        JPAExpressions
                                .select(vote.userId.countDistinct())
                                .from(vote)
                                .where(
                                        vote.postId.eq(post.id),
                                        vote.deleted.isFalse()
                                ),
                        JPAExpressions
                                .select(comment.count())
                                .from(comment)
                                .where(
                                        comment.postId.eq(post.id),
                                        comment.deleted.isFalse()
                                ),
                        post.createdAt
                ))
                .from(post)
                .innerJoin(user).on(post.userId.eq(user.id))
                .where(
                        post.pollOption.scope.eq(Scope.PUBLIC),
                        cursor(postId, post.id),
                        post.deleted.isFalse()
                )
                .orderBy(post.createdAt.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = isHasNext(pageable, results);

        if (hasNext) {
            results.removeLast();
        }
        return new SliceImpl<>(results, pageable, hasNext);
    }

    /**
     * 유저가 작성한 게시글 리스트 조회
     * @param userId
     * @param postId
     * @param pageable
     * @return
     */
    public Slice<PostWithVoteCount> findPostsWithVoteCountByUserId(Long userId, Long postId, Pageable pageable) {
        List<PostWithVoteCount> results = queryFactory
                .select(new QPostWithVoteCount(
                        post,
                        JPAExpressions
                                .select(vote.userId.count())
                                .from(vote)
                                .where(
                                        vote.postId.eq(post.id),
                                        vote.deleted.isFalse()
                                )
                ))
                .from(post)
                .where(
                        post.userId.eq(userId),
                        cursor(postId, post.id),
                        post.deleted.isFalse()
                )
                .orderBy(post.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = isHasNext(pageable, results);

        if (hasNext) {
            results.removeLast();
        }
        return new SliceImpl<>(results, pageable, hasNext);
    }

    /**
     * 유저가 투표한 게시글 리스트 조회
     * @param userId
     * @param postId
     * @param pageable
     * @return
     */
    public Slice<PostWithVoteCount> findVotedPostsWithVoteCount(Long userId, Long postId, Pageable pageable) {
        List<PostWithVoteCount> results = queryFactory
                .select(new QPostWithVoteCount(
                        post,
                        JPAExpressions
                                .select(vote.userId.count())
                                .from(vote)
                                .where(
                                        vote.postId.eq(post.id),
                                        vote.deleted.isFalse()
                                )
                ))
                .from(post)
                .where(
                        post.id.in(
                                JPAExpressions
                                        .select(vote.postId)
                                        .from(vote)
                                        .where(
                                                vote.userId.eq(userId),
                                                vote.deleted.isFalse()
                                        )
                        ),
                        cursor(postId, post.id),
                        post.deleted.isFalse()
                )
                .orderBy(post.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = isHasNext(pageable, results);

        if (hasNext) {
            results.removeLast();
        }
        return new SliceImpl<>(results, pageable, hasNext);
    }
}
