package com.chooz.post.application;

import com.chooz.comment.domain.Comment;
import com.chooz.comment.domain.CommentRepository;
import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.domain.CommentActive;
import com.chooz.post.domain.PollOption;
import com.chooz.post.domain.PollType;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import com.chooz.post.domain.Scope;
import com.chooz.post.presentation.dto.FeedResponse;
import com.chooz.post.presentation.dto.MyPagePostResponse;
import com.chooz.post.presentation.dto.PollChoiceVoteResponse;
import com.chooz.post.presentation.dto.PostResponse;
import com.chooz.support.IntegrationTest;
import com.chooz.support.fixture.PostFixture;
import com.chooz.support.fixture.UserFixture;
import com.chooz.support.fixture.VoteFixture;
import com.chooz.thumbnail.domain.ThumbnailRepository;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import com.chooz.vote.application.VoteService;
import com.chooz.vote.domain.Vote;
import com.chooz.vote.domain.VoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class PostQueryServiceTest extends IntegrationTest {

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
    @Autowired
    private VoteService voteService;

    @Test
    @DisplayName("게시글 조회")
    void findById() throws Exception {
        //given
        User user1 = userRepository.save(createDefaultUser());
        User user2 = userRepository.save(createDefaultUser());
        Post post = postRepository.save(createDefaultPost(user1.getId()));
        Vote vote = voteRepository.save(VoteFixture.createDefaultVote(user1.getId(), post.getId(), post.getPollChoices().get(0).getId()));

        //when
        PostResponse response = postService.findById(user1.getId(), post.getId(), "shareKey");

        //then
        List<PollChoiceVoteResponse> pollChoices = response.pollChoices();
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
                () -> assertThat(response.pollOption()).isNotNull(),
                () -> assertThat(response.closeOption()).isNotNull(),
                () -> assertThat(pollChoices).hasSize(2),
                () -> assertThat(pollChoices.get(0).imageUrl()).isEqualTo(post.getPollChoices().get(0).getImageUrl()),
                () -> assertThat(pollChoices.get(0).voteId()).isEqualTo(vote.getId()),
                () -> assertThat(pollChoices.get(1).imageUrl()).isEqualTo(post.getPollChoices().get(1).getImageUrl()),
                () -> assertThat(pollChoices.get(1).voteId()).isNull()
        );
    }

    @Test
    @DisplayName("게시글 조회 - 공개 범위 PUBLIC")
    void findById_public() throws Exception {
        //given
        User author = userRepository.save(createDefaultUser());
        User otherUser = userRepository.save(createDefaultUser());
        String shareKey = "shareKey";
        String otherKey = "otherKey";
        Post post = postRepository.save(PostFixture.createPostBuilder()
                .shareUrl(shareKey)
                .userId(author.getId())
                .pollOption(PostFixture.pollOptionBuilder().scope(Scope.PUBLIC).build())
                .build());

        //when then
        assertThatNoException()
                .isThrownBy(() -> postService.findById(author.getId(), post.getId(), shareKey));
        assertThatNoException()
                .isThrownBy(() -> postService.findById(author.getId(), post.getId(), otherKey));
        assertThatNoException()
                .isThrownBy(() -> postService.findById(author.getId(), post.getId(), null));
        assertThatNoException()
                .isThrownBy(() -> postService.findById(otherUser.getId(), post.getId(), shareKey));
        assertThatNoException()
                .isThrownBy(() -> postService.findById(otherUser.getId(), post.getId(), otherKey));
        assertThatNoException()
                .isThrownBy(() -> postService.findById(otherUser.getId(), post.getId(), null));
    }

    @Test
    @DisplayName("게시글 조회 - 공개 범위 PRIVATE")
    void findById_private() throws Exception {
        //given
        User author = userRepository.save(createDefaultUser());
        User otherUser = userRepository.save(createDefaultUser());
        String shareKey = "shareKey";
        String otherKey = "otherKey";
        Post post = postRepository.save(PostFixture.createPostBuilder()
                .shareUrl(shareKey)
                .userId(author.getId())
                .pollOption(PostFixture.pollOptionBuilder().scope(Scope.PRIVATE).build())
                .build());

        //when then
        assertThatNoException()
                .isThrownBy(() -> postService.findById(author.getId(), post.getId(), shareKey));
        assertThatNoException()
                .isThrownBy(() -> postService.findById(author.getId(), post.getId(), otherKey));
        assertThatNoException()
                .isThrownBy(() -> postService.findById(author.getId(), post.getId(), null));
        assertThatNoException()
                .isThrownBy(() -> postService.findById(otherUser.getId(), post.getId(), shareKey));
        assertThatThrownBy(() -> postService.findById(otherUser.getId(), post.getId(), otherKey))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.POST_NOT_REVEALABLE.getMessage());
        assertThatThrownBy(() -> postService.findById(otherUser.getId(), post.getId(), null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.POST_NOT_REVEALABLE.getMessage());
    }

    @Test
    @DisplayName("유저가 작성한 게시글 조회 - 커서 null인 경우")
    void findUserPosts() throws Exception {
        //given
        User user = userRepository.save(createDefaultUser());
        List<Post> posts = createPosts(user, 15);
        int size = 10;

        //when
        var response = postService.findUserPosts(user.getId(), user.getId(), null, size);

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
        var response = postService.findUserPosts(user.getId(), user.getId(), posts.get(3).getId(), size);

        //then
        assertAll(
                () -> assertThat(response.data()).hasSize(3),
                () -> assertThat(response.hasNext()).isFalse(),
                () -> assertThat(response.nextCursor()).isEqualTo(posts.get(0).getId())
        );
    }

    @Test
    @DisplayName("유저가 작성한 게시글 조회 - 중복 투표")
    void findUserPosts_multiple() {
        //given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post1 = postRepository.save(PostFixture.createPostBuilder()
                .userId(user.getId())
                .pollOption(PostFixture.multiplePollOption())
                .build());
        Post post2 = postRepository.save(PostFixture.createPostBuilder()
                .userId(user.getId())
                .pollOption(PostFixture.multiplePollOption())
                .build());
        //유저1 게시글1 선택지 1, 2 복수 투표
        voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post1.getId(), post1.getPollChoices().get(0).getId()));
        voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post1.getId(), post1.getPollChoices().get(1).getId()));

        //유저1 게시글2 선택지 1 단일 투표
        voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post2.getId(), post2.getPollChoices().get(0).getId()));

        //when
        var response = postService.findUserPosts(user.getId(), user.getId(), null, 10);

        //then
        List<MyPagePostResponse> data = response.data();
        assertAll(
                () -> assertThat(response.data()).hasSize(2),
                () -> assertThat(response.hasNext()).isFalse(),

                () -> assertThat(data.getFirst().id()).isEqualTo(post2.getId()),
                () -> assertThat(data.getFirst().title()).isEqualTo(post2.getTitle()),

                () -> assertThat(data.getFirst().postVoteInfo().mostVotedPollChoice().title()).isEqualTo(post2.getPollChoices().get(0).getTitle()),
                () -> assertThat(data.getFirst().postVoteInfo().totalVoterCount()).isEqualTo(1),
                () -> assertThat(data.getFirst().postVoteInfo().mostVotedPollChoice().voteCount()).isEqualTo(1),
                () -> assertThat(data.getFirst().postVoteInfo().mostVotedPollChoice().voteRatio()).isEqualTo("100"),

                () -> assertThat(data.get(1).id()).isEqualTo(post1.getId()),
                () -> assertThat(data.get(1).title()).isEqualTo(post1.getTitle()),

                () -> assertThat(data.get(1).postVoteInfo().mostVotedPollChoice().title()).isEqualTo(post1.getPollChoices().get(0).getTitle()),
                () -> assertThat(data.get(1).postVoteInfo().totalVoterCount()).isEqualTo(2),
                () -> assertThat(data.get(1).postVoteInfo().mostVotedPollChoice().voteCount()).isEqualTo(1),
                () -> assertThat(data.get(1).postVoteInfo().mostVotedPollChoice().voteRatio()).isEqualTo("50")
        );
    }

    @Test
    @DisplayName("유저가 작성한 게시글 조회 - 중복 투표2")
    void findUserPosts_multiple2() {
        //given
        User user = userRepository.save(UserFixture.createDefaultUser());
        User user2 = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createPostBuilder()
                .userId(user.getId())
                .pollOption(PostFixture.multiplePollOption())
                .build());
        //유저1 선택지 1, 2 복수 투표
        voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post.getId(), post.getPollChoices().get(0).getId()));
        voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post.getId(), post.getPollChoices().get(1).getId()));

        //유저2 선택지 1 단일 투표
        voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post.getId(), post.getPollChoices().get(0).getId()));

        //when
        var response = postService.findUserPosts(user.getId(), user.getId(), null, 10);

        //then
        List<MyPagePostResponse> data = response.data();
        assertAll(
                () -> assertThat(response.data()).hasSize(1),
                () -> assertThat(response.hasNext()).isFalse(),

                () -> assertThat(data.getFirst().id()).isEqualTo(post.getId()),
                () -> assertThat(data.getFirst().title()).isEqualTo(post.getTitle()),

                () -> assertThat(data.getFirst().postVoteInfo().mostVotedPollChoice().title()).isEqualTo(post.getPollChoices().get(0).getTitle()),
                () -> assertThat(data.getFirst().postVoteInfo().totalVoterCount()).isEqualTo(3),
                () -> assertThat(data.getFirst().postVoteInfo().mostVotedPollChoice().voteCount()).isEqualTo(2),
                () -> assertThat(data.getFirst().postVoteInfo().mostVotedPollChoice().voteRatio()).isEqualTo("67")
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
            voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post.getId(), post.getPollChoices().get(0).getId()));
        }
        int size = 10;

        //when
        var response = postService.findVotedPosts(user.getId(), user.getId(), null, size);

        //then
        int 전체_15개에서_맨_마지막_데이터_인덱스 = posts.size() - size;
        assertAll(
                () -> assertThat(response.data()).hasSize(size),
                () -> assertThat(response.hasNext()).isTrue(),
                () -> assertThat(response.nextCursor()).isEqualTo(posts.get(전체_15개에서_맨_마지막_데이터_인덱스).getId())
        );
    }

    @Test
    @DisplayName("유저가 투표한 게시글 조회 - 중복 투표")
    void findVotedPosts_multiple() {
        //given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createPostBuilder()
                .userId(user.getId())
                .pollOption(PostFixture.multiplePollOption())
                .build());
        Post post2 = postRepository.save(PostFixture.createPostBuilder()
                .userId(user.getId())
                .pollOption(PostFixture.multiplePollOption())
                .build());
        //유저1 선택지 1, 2 복수 투표
        voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post.getId(), post.getPollChoices().get(0).getId()));
        voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post.getId(), post.getPollChoices().get(1).getId()));

        //유저1 게시글2 투표 후 취소
        voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post2.getId(), post2.getPollChoices().get(1).getId()));
        voteService.vote(user.getId(), post2.getId(), List.of());

        //when
        var response = postService.findVotedPosts(user.getId(), user.getId(), null, 10);

        //then
        List<MyPagePostResponse> data = response.data();
        assertAll(
                () -> assertThat(response.data()).hasSize(1),
                () -> assertThat(response.hasNext()).isFalse(),

                () -> assertThat(data.getFirst().id()).isEqualTo(post.getId()),
                () -> assertThat(data.getFirst().title()).isEqualTo(post.getTitle()),

                () -> assertThat(data.getFirst().postVoteInfo().mostVotedPollChoice().title()).isEqualTo(post.getPollChoices().get(0).getTitle()),
                () -> assertThat(data.getFirst().postVoteInfo().totalVoterCount()).isEqualTo(2),
                () -> assertThat(data.getFirst().postVoteInfo().mostVotedPollChoice().voteCount()).isEqualTo(1),
                () -> assertThat(data.getFirst().postVoteInfo().mostVotedPollChoice().voteRatio()).isEqualTo("50")
        );
    }

    @Test
    @DisplayName("유저가 투표한 게시글 조회 - 중복 투표2")
    void findVotedPosts_multiple2() {
        //given
        User user = userRepository.save(UserFixture.createDefaultUser());
        User user2 = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createPostBuilder()
                .userId(user.getId())
                .pollOption(PostFixture.multiplePollOption())
                .build());
        //유저1 선택지 1, 2 복수 투표
        voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post.getId(), post.getPollChoices().get(0).getId()));
        voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post.getId(), post.getPollChoices().get(1).getId()));

        //유저2 선택지 1 단일 투표
        voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post.getId(), post.getPollChoices().get(0).getId()));

        //when
        var response = postService.findVotedPosts(user.getId(), user.getId(), null, 10);

        //then
        List<MyPagePostResponse> data = response.data();
        assertAll(
                () -> assertThat(response.data()).hasSize(1),
                () -> assertThat(response.hasNext()).isFalse(),

                () -> assertThat(data.getFirst().id()).isEqualTo(post.getId()),
                () -> assertThat(data.getFirst().title()).isEqualTo(post.getTitle()),

                () -> assertThat(data.getFirst().postVoteInfo().mostVotedPollChoice().title()).isEqualTo(post.getPollChoices().get(0).getTitle()),
                () -> assertThat(data.getFirst().postVoteInfo().totalVoterCount()).isEqualTo(3),
                () -> assertThat(data.getFirst().postVoteInfo().mostVotedPollChoice().voteCount()).isEqualTo(2),
                () -> assertThat(data.getFirst().postVoteInfo().mostVotedPollChoice().voteRatio()).isEqualTo("67")
        );
    }

    @Test
    @DisplayName("마이페이지 게시글 공개 범위 - 본인인 경우")
    void scope_author() {
        //given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post publicPost = postRepository.save(PostFixture.createPostBuilder()
                .userId(user.getId())
                .pollOption(PostFixture.pollOptionBuilder()
                        .scope(Scope.PUBLIC)
                        .build())
                .build());
        Post privatePost = postRepository.save(PostFixture.createPostBuilder()
                .userId(user.getId())
                .pollOption(PostFixture.pollOptionBuilder()
                        .scope(Scope.PRIVATE)
                        .build())
                .build());
        //유저1 본인 게시글 1 2 투표
        voteRepository.save(VoteFixture.createDefaultVote(user.getId(), publicPost.getId(), publicPost.getPollChoices().get(0).getId()));

        //when
        var response1 = postService.findVotedPosts(user.getId(), user.getId(), null, 10);
        var response2 = postService.findUserPosts(user.getId(), user.getId(), null, 10);

        //then
        assertThat(response1.data()).hasSize(1);
        assertThat(response2.data()).hasSize(2);
    }

    @Test
    @DisplayName("마이페이지 게시글 공개 범위 - 다른 사람인 경우")
    void scope_otherUser() {
        //given
        User author = userRepository.save(UserFixture.createDefaultUser());
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post publicPost = postRepository.save(PostFixture.createPostBuilder()
                .userId(author.getId())
                .pollOption(PostFixture.pollOptionBuilder()
                        .scope(Scope.PUBLIC)
                        .build())
                .build());
        Post privatePost = postRepository.save(PostFixture.createPostBuilder()
                .userId(author.getId())
                .pollOption(PostFixture.pollOptionBuilder()
                        .scope(Scope.PRIVATE)
                        .build())
                .build());
        //유저1 본인 게시글 1 2 투표
        voteRepository.save(VoteFixture.createDefaultVote(author.getId(), privatePost.getId(), privatePost.getPollChoices().get(0).getId()));

        //when
        var response1 = postService.findVotedPosts(user.getId(), author.getId(), null, 10);
        var response2 = postService.findUserPosts(user.getId(), author.getId(), null, 10);

        //then
        assertThat(response1.data()).hasSize(0);
        assertThat(response2.data()).hasSize(1);
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
        for (int i = 0; i < size; i++) {
            Post post = postRepository.save(createDefaultPost(user.getId()));
            posts.add(post);
            thumbnailRepository.save(createDefaultThumbnail(post.getId(), post.getPollChoices().get(0).getId()));
        }
        return posts;
    }

    private List<Post> createPostsWithScope(User user, Scope scope, int size) {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < size; i++) {
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
