package com.swyp8team2.post.application;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.image.domain.ImageFile;
import com.swyp8team2.image.domain.ImageFileRepository;
import com.swyp8team2.image.presentation.dto.ImageFileDto;
import com.swyp8team2.post.domain.Post;
import com.swyp8team2.post.domain.PostImage;
import com.swyp8team2.post.domain.PostRepository;
import com.swyp8team2.post.presentation.dto.CreatePostRequest;
import com.swyp8team2.post.presentation.dto.PostResponse;
import com.swyp8team2.post.presentation.dto.SimplePostResponse;
import com.swyp8team2.post.presentation.dto.VoteRequestDto;
import com.swyp8team2.post.presentation.dto.VoteResponseDto;
import com.swyp8team2.support.IntegrationTest;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class PostServiceTest extends IntegrationTest {

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ImageFileRepository imageFileRepository;

    @Test
    @DisplayName("게시글 작성")
    void create() throws Exception {
        //given
        long userId = 1L;
        CreatePostRequest request = new CreatePostRequest("description", List.of(
                new VoteRequestDto(1L),
                new VoteRequestDto(2L)
        ));

        //when
        Long postId = postService.create(userId, request);

        //then
        Post post = postRepository.findById(postId).get();
        List<PostImage> images = post.getImages();
        assertAll(
                () -> assertThat(post.getDescription()).isEqualTo("description"),
                () -> assertThat(post.getUserId()).isEqualTo(userId),
                () -> assertThat(images).hasSize(2),
                () -> assertThat(images.get(0).getImageFileId()).isEqualTo(1L),
                () -> assertThat(images.get(0).getName()).isEqualTo("뽀또A"),
                () -> assertThat(images.get(0).getVoteCount()).isEqualTo(0),
                () -> assertThat(images.get(1).getImageFileId()).isEqualTo(2L),
                () -> assertThat(images.get(1).getName()).isEqualTo("뽀또B"),
                () -> assertThat(images.get(1).getVoteCount()).isEqualTo(0)
        );
    }

    @Test
    @DisplayName("게시글 작성 - 이미지가 2개 미만인 경우")
    void create_invalidPostImageCount() throws Exception {
        //given
        long userId = 1L;
        CreatePostRequest request = new CreatePostRequest("description", List.of(
                new VoteRequestDto(1L)
        ));

        //when then
        assertThatThrownBy(() -> postService.create(userId, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INVALID_POST_IMAGE_COUNT.getMessage());
    }

    @Test
    @DisplayName("게시글 작성 - 설명이 100자 넘어가는 경우")
    void create_descriptionCountExceeded() throws Exception {
        //given
        long userId = 1L;
        CreatePostRequest request = new CreatePostRequest("a".repeat(101), List.of(
                new VoteRequestDto(1L),
                new VoteRequestDto(2L)
        ));

        //when then
        assertThatThrownBy(() -> postService.create(userId, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.DESCRIPTION_LENGTH_EXCEEDED.getMessage());
    }

    @Test
    @DisplayName("게시글 조회")
    void findById() throws Exception {
        //given
        User user = createUser(1);
        ImageFile imageFile1 = createImageFile(1);
        ImageFile imageFile2 = createImageFile(2);
        Post post = createPost(user, imageFile1, imageFile2, 1);

        //when
        PostResponse response = postService.findById(post.getId());

        //then
        List<VoteResponseDto> votes = response.votes();
        assertAll(
                () -> assertThat(response.description()).isEqualTo(post.getDescription()),
                () -> assertThat(response.id()).isEqualTo(post.getId()),
                () -> assertThat(response.author().nickname()).isEqualTo(user.getNickname()),
                () -> assertThat(response.author().profileUrl()).isEqualTo(user.getProfileUrl()),
                () -> assertThat(response.shareUrl()).isEqualTo(post.getShareUrl()),
                () -> assertThat(votes).hasSize(2),
                () -> assertThat(votes.get(0).imageUrl()).isEqualTo(imageFile1.getImageUrl()),
                () -> assertThat(votes.get(0).voteCount()).isEqualTo(0),
                () -> assertThat(votes.get(0).voteRatio()).isEqualTo("0.0"),
                () -> assertThat(votes.get(0).voted()).isFalse(),
                () -> assertThat(votes.get(1).imageUrl()).isEqualTo(imageFile2.getImageUrl()),
                () -> assertThat(votes.get(1).voteCount()).isEqualTo(0),
                () -> assertThat(votes.get(1).voteRatio()).isEqualTo("0.0"),
                () -> assertThat(votes.get(1).voted()).isFalse()
        );
    }

    @Test
    @DisplayName("내가 작성한 게시글 조회 - 커서 null인 경우")
    void findMyPosts() throws Exception {
        //given
        User user = createUser(1);
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 30; i += 2) {
            ImageFile imageFile1 = createImageFile(i);
            ImageFile imageFile2 = createImageFile(i + 1);
            posts.add(createPost(user, imageFile1, imageFile2, i));
        }
        int size = 10;

        //when
        var response = postService.findMyPosts(user.getId(), null, size);

        //then
        assertAll(
                () -> assertThat(response.data()).hasSize(size),
                () -> assertThat(response.hasNext()).isTrue(),
                () -> assertThat(response.nextCursor()).isEqualTo(posts.get(posts.size() - size).getId())
        );
    }

    @Test
    @DisplayName("내가 작성한 게시글 조회 - 커서 있는 경우")
    void findMyPosts2() throws Exception {
        //given
        User user = createUser(1);
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 30; i += 2) {
            ImageFile imageFile1 = createImageFile(i);
            ImageFile imageFile2 = createImageFile(i + 1);
            posts.add(createPost(user, imageFile1, imageFile2, i));
        }
        int size = 10;

        //when
        var response = postService.findMyPosts(user.getId(), posts.get(3).getId(), size);

        //then
        assertAll(
                () -> assertThat(response.data()).hasSize(3),
                () -> assertThat(response.hasNext()).isFalse(),
                () -> assertThat(response.nextCursor()).isEqualTo(posts.get(0).getId())
        );
    }

    private Post createPost(User user, ImageFile imageFile1, ImageFile imageFile2, int index) {
        Post post = postRepository.save(Post.create(
                user.getId(),
                "description" + index,
                List.of(
                        PostImage.create("뽀또A", imageFile1.getId()),
                        PostImage.create("뽀또B", imageFile2.getId())
                ),
                "shareUrl" + index
        ));
        return post;
    }

    private User createUser(int index) {
        return userRepository.save(User.create("nickname" + index, "profileUrl" + index));
    }

    private ImageFile createImageFile(int index) {
        return imageFileRepository.save(ImageFile.create(
                new ImageFileDto(
                        "originalFileName" + index,
                        "imageUrl" + index,
                        "thumbnailUrl" + index
                )
        ));
    }
}
