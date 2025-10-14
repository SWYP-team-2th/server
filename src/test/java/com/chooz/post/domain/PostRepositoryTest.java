package com.chooz.post.domain;

import com.chooz.post.application.dto.PostWithVoteCount;
import com.chooz.post.application.dto.FeedDto;
import com.chooz.support.RepositoryTest;
import com.chooz.support.fixture.PostFixture;
import com.chooz.support.fixture.UserFixture;
import com.chooz.support.fixture.VoteFixture;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import com.chooz.vote.domain.VoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.chooz.support.fixture.PostFixture.createDefaultPost;
import static com.chooz.support.fixture.PostFixture.createPostBuilder;
import static com.chooz.support.fixture.UserFixture.createDefaultUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

class PostRepositoryTest extends RepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    VoteRepository voteRepository;

    @Test
    @DisplayName("유저가 작성한 게시글 조회 - 게시글이 15개일 경우 15번쨰부터 10개 조회해야 함")
    void findAllByUserId1() throws Exception {
        //given
        long userId = 1L;
        List<Post> posts = createPosts(userId, 15);
        int size = 10;

        //when
        Slice<Post> res = postRepository.findAllByUserId(userId, null, PageRequest.ofSize(size));

        //then
        assertAll(
                () -> assertThat(res.getContent().size()).isEqualTo(size),
                () -> assertThat(res.getContent().get(0).getId()).isEqualTo(posts.get(posts.size() - 1).getId()),
                () -> assertThat(res.getContent().get(1).getId()).isEqualTo(posts.get(posts.size() - 2).getId()),
                () -> assertThat(res.getContent().get(2).getId()).isEqualTo(posts.get(posts.size() - 3).getId()),
                () -> assertThat(res.hasNext()).isTrue()
        );
    }

    @Test
    @DisplayName("유저가 작성한 게시글 조회 - 15개 중에 커서가 5번째 게시글의 id면 4번째부터 0번째까지 조회해야 함")
    void findAllByUserId2() throws Exception {
        //given
        long userId = 1L;
        List<Post> posts = createPosts(userId, 15);
        int size = 10;
        int cursorIndex = 5;

        //when
        Slice<Post> res = postRepository.findAllByUserId(userId, posts.get(cursorIndex).getId(), PageRequest.ofSize(size));

        //then
        assertAll(
                () -> assertThat(res.getContent().size()).isEqualTo(5),
                () -> assertThat(res.getContent().get(0).getId()).isEqualTo(posts.get(cursorIndex - 1).getId()),
                () -> assertThat(res.getContent().get(1).getId()).isEqualTo(posts.get(cursorIndex - 2).getId()),
                () -> assertThat(res.getContent().get(2).getId()).isEqualTo(posts.get(cursorIndex - 3).getId()),
                () -> assertThat(res.hasNext()).isFalse()
        );
    }

    @Test
    @DisplayName("피드 조회")
    void findByScopeAndDeletedFalse() {
        //given
        User user1 = userRepository.save(createDefaultUser());
        User user2 = userRepository.save(createDefaultUser());
        List<Post> myPosts = createPosts(user1.getId(), 5);
        List<Post> privatePosts = createPostsWithScope(user2, Scope.PRIVATE, 5);
        List<Post> publicPosts = createPostsWithScope(user2, Scope.PUBLIC, 5);
        int size = 10;

        //when
        Slice<FeedDto> res = postRepository.findFeed(null, PageRequest.ofSize(size));

        //then
        assertAll(
                () -> assertThat(res.getContent().size()).isEqualTo(size),
                () -> assertThat(res.hasNext()).isFalse()
        );
    }

    @Test
    @DisplayName("마감 시간 지난 진행 중인 게시글 조회")
    void findPostNeedToClose() throws Exception {
        //given
        User user = userRepository.save(createDefaultUser());
        createPosts(user.getId(), 5);
        int expected = 10;
        for (int i = 0; i < expected; i++) {
            Post post = createPostBuilder()
                    .userId(user.getId())
                    .closeOption(
                            PostFixture.createCloseOptionBuilder()
                                    .closeType(CloseType.DATE)
                                    .closedAt(LocalDateTime.now().minusMinutes(i))
                                    .build())
                    .build();
            postRepository.save(post);
        }

        //when
        List<Post> postsNeedToClose = postRepository.findPostsNeedToClose();

        //then
        assertThat(postsNeedToClose).hasSize(expected);
    }

    @Test
    void findPostsWithVoteCountByUserId() throws Exception {
        // given
        User user1 = userRepository.save(UserFixture.createDefaultUser());
        User user2 = userRepository.save(UserFixture.createDefaultUser());
        User user3 = userRepository.save(UserFixture.createDefaultUser());

        // user1 게시글 생성
        Post post1 = postRepository.save(PostFixture.createDefaultPost(user1.getId()));
        Post post2 = postRepository.save(PostFixture.createDefaultPost(user1.getId()));
        Post post3 = postRepository.save(PostFixture.createDefaultPost(user1.getId()));

        // 다른 사용자(user2) 게시글 생성
        Post post4 = postRepository.save(PostFixture.createDefaultPost(user2.getId()));

        List<PollChoice> post1Choices = post1.getPollChoices();
        List<PollChoice> post2Choices = post2.getPollChoices();

        // post1: user1, user2, user3 -> 총 3명 투표
        voteRepository.save(VoteFixture.createDefaultVote(user1.getId(), post1.getId(), post1Choices.get(0).getId()));
        voteRepository.save(VoteFixture.createDefaultVote(user2.getId(), post1.getId(), post1Choices.get(0).getId()));
        voteRepository.save(VoteFixture.createDefaultVote(user3.getId(), post1.getId(), post1Choices.get(1).getId()));

        // post2: user2 -> 총 1명 투표
        voteRepository.save(VoteFixture.createDefaultVote(user2.getId(), post2.getId(), post2Choices.get(0).getId()));

        // post3에는 투표 없음

        // post4: user1, user3 -> 총 2명 투표 (user1의 게시글이 아님)
        List<PollChoice> post4Choices = post4.getPollChoices();
        voteRepository.save(VoteFixture.createDefaultVote(user1.getId(), post4.getId(), post4Choices.get(0).getId()));
        voteRepository.save(VoteFixture.createDefaultVote(user3.getId(), post4.getId(), post4Choices.get(0).getId()));

        // when
        Slice<PostWithVoteCount> result = postRepository.findPostsWithVoteCountByUserId(
                user1.getId(),
                user1.getId(),
                null,
                PageRequest.of(0, 10)
        );

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent()).extracting("post.id", "voteCount")
                .containsExactly(
                        tuple(post3.getId(), 0L),
                        tuple(post2.getId(), 1L),
                        tuple(post1.getId(), 3L)
                );
    }

    @Test
    void findVotedPostsWithVoteCount() throws Exception {
        // given
        User user1 = userRepository.save(UserFixture.createDefaultUser());
        User user2 = userRepository.save(UserFixture.createDefaultUser());
        User user3 = userRepository.save(UserFixture.createDefaultUser());

        // user2 게시글 생성
        Post post1 = postRepository.save(PostFixture.createDefaultPost(user2.getId()));
        Post post2 = postRepository.save(PostFixture.createDefaultPost(user2.getId()));
        Post post3 = postRepository.save(PostFixture.createDefaultPost(user2.getId()));
        Post post4 = postRepository.save(PostFixture.createDefaultPost(user2.getId()));

        List<PollChoice> post1Choices = post1.getPollChoices();
        List<PollChoice> post2Choices = post2.getPollChoices();
        List<PollChoice> post3Choices = post3.getPollChoices();
        List<PollChoice> post4Choices = post4.getPollChoices();

        // post1: user1, user2 -> 총 2명 투표
        voteRepository.save(VoteFixture.createDefaultVote(user1.getId(), post1.getId(), post1Choices.get(0).getId()));
        voteRepository.save(VoteFixture.createDefaultVote(user2.getId(), post1.getId(), post1Choices.get(1).getId()));

        // post2: user1, user3 -> 총 2명 투표
        voteRepository.save(VoteFixture.createDefaultVote(user1.getId(), post2.getId(), post2Choices.get(0).getId()));
        voteRepository.save(VoteFixture.createDefaultVote(user3.getId(), post2.getId(), post2Choices.get(1).getId()));

        // post3: user2, user3 -> 총 2명 투표 (user1은 투표하지 않음)
        voteRepository.save(VoteFixture.createDefaultVote(user2.getId(), post3.getId(), post3Choices.get(0).getId()));
        voteRepository.save(VoteFixture.createDefaultVote(user3.getId(), post3.getId(), post3Choices.get(1).getId()));

        // post4: user1, user2, user3 -> 총 3명 투표
        voteRepository.save(VoteFixture.createDefaultVote(user1.getId(), post4.getId(), post4Choices.get(0).getId()));
        voteRepository.save(VoteFixture.createDefaultVote(user2.getId(), post4.getId(), post4Choices.get(0).getId()));
        voteRepository.save(VoteFixture.createDefaultVote(user3.getId(), post4.getId(), post4Choices.get(1).getId()));

        // when
        Slice<PostWithVoteCount> result = postRepository.findVotedPostsWithVoteCount(
                user1.getId(),
                user1.getId(),
                null,
                PageRequest.of(0, 10)
        );

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent()).extracting("post.id", "voteCount")
                .containsExactly(
                        tuple(post4.getId(), 3L),
                        tuple(post2.getId(), 2L),
                        tuple(post1.getId(), 2L)
                );
    }

    @Test
    @DisplayName("본인의 마이페이지를 보는 경우, hasNext가 true여야 함")
    void findPostsWithVoteCountByUserId_hasNext_true() throws Exception {
        // given
        User user1 = userRepository.save(UserFixture.createDefaultUser());

        // 비공개 3개, 공개 9개
        createPostsWithScope(user1, Scope.PRIVATE, 3);
        List<Post> publicPosts = createPostsWithScope(user1, Scope.PUBLIC, 9);

        voteRepository.save(VoteFixture.createDefaultVote(user1.getId(), publicPosts.getFirst().getId(), publicPosts.getFirst().getPollChoices().getFirst().getId()));

        // when
        Slice<PostWithVoteCount> result = postRepository.findPostsWithVoteCountByUserId(
                user1.getId(),
                user1.getId(),
                null,
                PageRequest.of(0, 10)
        );

        // then
        assertThat(result.getContent()).hasSize(10);
        assertThat(result.hasNext()).isTrue();
    }

    @Test
    @DisplayName("다른 사람의 마이페이지를 보는 경우, hasNext가 false여야 함")
    void findPostsWithVoteCountByUserId_hasNext_false() throws Exception {
        // given
        User user1 = userRepository.save(UserFixture.createDefaultUser());
        User user2 = userRepository.save(UserFixture.createDefaultUser());

        // 비공개 3개, 공개 9개
        createPostsWithScope(user1, Scope.PRIVATE, 3);
        createPostsWithScope(user1, Scope.PUBLIC, 9);

        // when
        Slice<PostWithVoteCount> result = postRepository.findPostsWithVoteCountByUserId(
                user2.getId(),
                user1.getId(),
                null,
                PageRequest.of(0, 10)
        );

        // then
        assertThat(result.getContent()).hasSize(9);
        assertThat(result.hasNext()).isFalse();
    }

    private List<Post> createPosts(long userId, int size) {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            posts.add(postRepository.save(createDefaultPost(userId)));
        }
        return posts;
    }

    private List<Post> createPostsWithScope(User user, Scope scope, int size) {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < size; i ++) {
            Post post = createPostBuilder()
                    .userId(user.getId())
                    .pollOption(PollOption.create(PollType.SINGLE, scope, CommentActive.OPEN))
                    .build();
            posts.add(postRepository.save(post));
        }
        return posts;
    }
}
