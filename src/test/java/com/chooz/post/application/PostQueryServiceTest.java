package com.chooz.post.application;

import com.chooz.comment.domain.Comment;
import com.chooz.comment.domain.CommentRepository;
import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.post.domain.*;
import com.chooz.post.presentation.dto.FeedResponse;
import com.chooz.post.presentation.dto.PollChoiceResponse;
import com.chooz.post.presentation.dto.PostResponse;
import com.chooz.support.IntegrationTest;
import com.chooz.thumbnail.domain.ThumbnailRepository;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import com.chooz.vote.domain.Vote;
import com.chooz.vote.domain.VoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static com.chooz.support.fixture.CommentFixture.createDefaultComment;
import static com.chooz.support.fixture.PostFixture.createDefaultPost;
import static com.chooz.support.fixture.PostFixture.createPostBuilder;
import static com.chooz.support.fixture.ThumbnailFixture.createDefaultThumbnail;
import static com.chooz.support.fixture.UserFixture.createDefaultUser;
import static com.chooz.support.fixture.UserFixture.createUserBuilder;
import static com.chooz.support.fixture.VoteFixture.createDefaultVote;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PostQueryServiceTest extends IntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(PostQueryServiceTest.class);
    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ThumbnailRepository thumbnailRepository;

    @Test
    @DisplayName("게시글 조회")
    void findById() throws Exception {
        //given
        User user1 = userRepository.save(createDefaultUser());
        User user2 = userRepository.save(createDefaultUser());
        Post post = postRepository.save(createDefaultPost(user1.getId()));
        Vote vote = voteRepository.save(Vote.create(post.getId(), post.getPollChoices().get(0).getId(), user1.getId()));

        //when
        PostResponse response = postService.findById(user1.getId(), post.getId());

        //then
        List<PollChoiceResponse> pollChoices = response.pollChoices();
        assertAll(
                () -> assertThat(response.id()).isEqualTo(post.getId()),
                () -> assertThat(response.description()).isEqualTo(post.getDescription()),
                () -> assertThat(response.title()).isEqualTo(post.getTitle()),
                () -> assertThat(response.author().nickname()).isEqualTo(user1.getNickname()),
                () -> assertThat(response.author().profileUrl()).isEqualTo(user1.getProfileUrl()),
                () -> assertThat(response.shareUrl()).isEqualTo(post.getShareUrl()),
                () -> assertThat(response.isAuthor()).isTrue(),
                () -> assertThat(response.commentCount()).isEqualTo(0L),
                () -> assertThat(response.voterCount()).isEqualTo(1L),
                () -> assertThat(response.pollOptions()).isNotNull(),
                () -> assertThat(response.closeOptions()).isNotNull(),
                () -> assertThat(pollChoices).hasSize(2),
                () -> assertThat(pollChoices.get(0).imageUrl()).isEqualTo(post.getPollChoices().get(0).getImageUrl()),
                () -> assertThat(pollChoices.get(0).voteId()).isEqualTo(vote.getId()),
                () -> assertThat(pollChoices.get(1).imageUrl()).isEqualTo(post.getPollChoices().get(1).getImageUrl()),
                () -> assertThat(pollChoices.get(1).voteId()).isNull()
        );
    }

    @Test
    @DisplayName("유저가 작성한 게시글 조회 - 커서 null인 경우")
    void findUserPosts() throws Exception {
        //given
        User user = userRepository.save(createDefaultUser());
        List<Post> posts = createPosts(user, 15);
        int size = 10;

        //when
        var response = postService.findUserPosts(user.getId(), null, size);

        //then
        assertAll(
                () -> assertThat(response.data()).hasSize(size),
                () -> assertThat(response.hasNext()).isTrue(),
                () -> assertThat(response.nextCursor()).isEqualTo(posts.get(posts.size() - size).getId())
        );
    }

    @Test
    @DisplayName("유저가 작성한 게시글 조회 - 커서 있는 경우")
    void findUserPosts2() throws Exception {
        //given
        User user = userRepository.save(createDefaultUser());
        List<Post> posts = createPosts(user, 15);
        int size = 10;

        //when
        var response = postService.findUserPosts(user.getId(), posts.get(3).getId(), size);

        //then
        assertAll(
                () -> assertThat(response.data()).hasSize(3),
                () -> assertThat(response.hasNext()).isFalse(),
                () -> assertThat(response.nextCursor()).isEqualTo(posts.get(0).getId())
        );
    }

    @Test
    @DisplayName("유저가 투표한 게시글 조회 - 커서 null인 경우")
    void findVotedPosts() throws Exception {
        //given
        User user = userRepository.save(createDefaultUser());
        List<Post> posts = createPosts(user, 15);
        for (int i = 0; i < 15; i++) {
            Post post = posts.get(i);
            voteRepository.save(Vote.create(post.getId(), post.getPollChoices().get(0).getId(), user.getId()));
        }
        int size = 10;

        //when
        var response = postService.findVotedPosts(user.getId(), null, size);

        //then
        int 전체_15개에서_맨_마지막_데이터_인덱스 = posts.size() - size;
        assertAll(
                () -> assertThat(response.data()).hasSize(size),
                () -> assertThat(response.hasNext()).isTrue(),
                () -> assertThat(response.nextCursor()).isEqualTo(posts.get(전체_15개에서_맨_마지막_데이터_인덱스).getId())
        );
    }

    @Test
    @DisplayName("피드 조회 - 내 게시글 1개, 공개 게시글 15개, 투표 10개, 댓글 20개")
    void findFeed() throws Exception {
        //given
        int size = 20;
        User user1 = userRepository.save(createUserBuilder().build());
        User user2 = userRepository.save(createUserBuilder().build());

        List<Post> publicPosts = createPostsWithScope(user2, Scope.PUBLIC, 15);
        createPostsWithScope(user2, Scope.PRIVATE, 3);
        Post myPost = postRepository.save(createPostBuilder().userId(user1.getId()).build());

        createVotes(user1, publicPosts.getFirst(), 10);
        createComments(user1, publicPosts.getFirst(), 20);

        List<Vote> publicPostVotes = voteRepository.findByPostIdAndDeletedFalse(publicPosts.getFirst().getId());
        List<Comment> publicPostComments = commentRepository.findByPostIdAndDeletedFalse(publicPosts.getFirst().getId());

        //when
        CursorBasePaginatedResponse<FeedResponse> response = postService.findFeed(user1.getId(), null, size);

        //then
        assertAll(
                () -> assertThat(response.data().size()).isEqualTo(16),
                () -> assertThat(response.data().getLast().voterCount()).isEqualTo(1),
                () -> assertThat(response.data().getLast().commentCount()).isEqualTo(publicPostComments.size()),
                () -> assertThat(response.data().getLast().isAuthor()).isFalse(),
                () -> assertThat(response.data().getFirst().isAuthor()).isTrue()
        );
    }

    private List<Post> createPosts(User user, int size) {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < size; i ++) {
            Post post = postRepository.save(createDefaultPost(user.getId()));
            posts.add(post);
            thumbnailRepository.save(createDefaultThumbnail(post.getId(), post.getPollChoices().get(0).getId()));
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

    private void createVotes(User user, Post post, int size) {
        for (int i = 0; i < size; i++) {
            voteRepository.save(createDefaultVote(user.getId(), post.getId(), post.getPollChoices().get(0).getId()));
        }
    }

    private void createComments(User user, Post post, int size) {
        for (int i = 0; i < size; i++) {
            commentRepository.save(createDefaultComment(user.getId(), post.getId()));
        }
    }
}
