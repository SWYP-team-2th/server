package com.chooz.comment.application;

import com.chooz.comment.domain.CommentLikeRepository;
import com.chooz.comment.domain.CommentRepository;
import com.chooz.comment.presentation.dto.CommentCursor;
import com.chooz.comment.presentation.dto.CommentResponse;
import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import com.chooz.support.IntegrationTest;
import com.chooz.support.fixture.CommentFixture;
import com.chooz.support.fixture.PostFixture;
import com.chooz.support.fixture.UserFixture;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class CommentQueryServiceTest extends IntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(CommentQueryServiceTest.class);
    @Autowired
    CommentService commentService;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentLikeRepository commentLikeRepository;

    private Long postId;
    private Long userId;
    private Long myId;

    @Nested
    class 댓글조회 {
        @BeforeEach
        void setup() {
            // 유저, 투표내역, 포스트 저장
            User user = userRepository.save(UserFixture.createDefaultUser());
            userId = user.getId();

            User anothrUser =  userRepository.save(UserFixture.createDefaultUser());
            myId = anothrUser.getId();

            Post post = postRepository.save(PostFixture.createDefaultPost(userId));
            postId = post.getId();

            // 댓글 100개 저장
            //내가 제일 먼저 댓글저장
            for (int i = 0; i < 15; i++) {
                commentRepository.save(CommentFixture.createWithContentComment(postId, myId, "내가 남긴 댓글"));
            }
            for (int i = 0; i < 85; i++) {
                commentRepository.save(CommentFixture.createWithContentComment(postId, userId, "댓글 " + i));
            }
        }

        @Test
        void 커서없이_첫페이지_10개_조회() {
            // when
            // 내가 남긴 댓글을 상위에서 조회하는 것을 테스트 하기 위해 userId를 바꿔서 테스트
            // userId = 1
            // CursorBasePaginatedResponse<CommentResponse> result = CommentService.getComments(postId, userId, null, 10);
            // myId = 2
            CursorBasePaginatedResponse<CommentResponse> result = commentService.getComments(postId, myId, new CommentCursor(null, null), 10);

            // then
            assertThat(result.data()).hasSize(10);
            assertThat(result.hasNext()).isTrue(); // 10개 + 1개 조회되었으므로
            assertThat(result.data().get(0).content()).contains("댓글");
    //        for(CommentResponse cr : result.data()){log.info("[커서 : " +  cr.getId() + ", size : 10개 댓글 조회] : " + cr.content() + ", userId : " + cr.userId());}
        }

        @Test
        void 커서기반_두번쨰페이지_10개_조회() {
            // when
            CursorBasePaginatedResponse<CommentResponse> firstResult = commentService.getComments(postId, myId, new CommentCursor(null, null), 10);

            CursorBasePaginatedResponse<CommentResponse> result =
                    commentService.getComments(
                            postId,
                            myId,
                            new CommentCursor(firstResult.nextCursor(), firstResult.data().getLast().commentCursor().priority()),
                            10
                    );
            // then
            assertAll(
                    () -> assertThat(result.data()).hasSize(10),
                    () -> assertThat(result.hasNext()).isTrue(),
                    () -> assertThat(result.data().get(0).content()).contains("댓글")
            );
    //        for(CommentResponse cr : result.data()){ log.info("[커서 : " +  cr.getId() + ", size : 10개 댓글 조회] : " + cr.content() + ", userId : " + cr.userId());}
        }

        @Test
        void 코멘트ID_삭제로_간격_존재시_조회() {
            for(int i = 95 ; i < 99 ; i++) { //95 ~ 98삭제
                commentRepository.deleteById((long)i);
            }
            //when
            CursorBasePaginatedResponse<CommentResponse> result = commentService.getComments(postId, userId, new CommentCursor(null, null), 10);
            //then
            assertAll(
                    () -> assertThat(result.data()).hasSize(10),
                    () -> assertThat(result.hasNext()).isTrue()
            );
        }
    }
    @Nested
    class 마지막댓글조회 {
        @BeforeEach
        void setup() {
            // 유저, 투표내역, 포스트 저장
            User user = userRepository.save(UserFixture.createDefaultUser());
            userId = user.getId();

            Post post = postRepository.save(PostFixture.createDefaultPost(userId));
            postId = post.getId();

            // 댓글 100개 저장
            for (int i = 0; i < 15; i++) {
                commentRepository.save(CommentFixture.createWithContentComment(postId, userId, "댓글 " + i));
            }
        }

        @Test
        void 마지막페이지_조회시_hasNext_확인 (){
            // when
            CursorBasePaginatedResponse<CommentResponse> firstResult = commentService.getComments(postId, myId, new CommentCursor(null, null), 10);

            CursorBasePaginatedResponse<CommentResponse> result =
                    commentService.getComments(
                            postId,
                            myId,
                            new CommentCursor(firstResult.nextCursor(), firstResult.data().getLast().commentCursor().priority()),
                            10
                    );
            // then
            assertThat(result.hasNext()).isFalse();
        }
    }
}
