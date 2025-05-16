package com.chooz.support.fixture;

import com.chooz.comment.domain.Comment;
import com.chooz.image.domain.ImageFile;
import com.chooz.image.presentation.dto.ImageFileDto;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostImage;
import com.chooz.post.domain.Scope;
import com.chooz.post.domain.VoteType;
import com.chooz.user.domain.User;
import com.chooz.vote.domain.Vote;

import java.util.List;

public abstract class FixtureGenerator {

    public static Post createPost(Long userId, Scope scope, ImageFile imageFile1, ImageFile imageFile2, int key) {
        return Post.create(
                userId,
                "description" + key,
                List.of(
                        PostImage.create("뽀또A", imageFile1.getId()),
                        PostImage.create("뽀또B", imageFile2.getId())
                ),
                scope,
                VoteType.SINGLE
        );
    }

    public static Post createPost(Long userId, Scope scope, List<ImageFile> imageFiles, int key) {
        return Post.create(
                userId,
                "description" + key,
                imageFiles.stream()
                        .map(imageFile -> PostImage.create("뽀또"+key, imageFile.getId()))
                        .toList(),
                scope,
                VoteType.SINGLE
        );
    }

    public static Post createMultiplePost(Long userId, Scope scope, ImageFile imageFile1, ImageFile imageFile2, int key) {
        return Post.create(
                userId,
                "description" + key,
                List.of(
                        PostImage.create("뽀또A", imageFile1.getId()),
                        PostImage.create("뽀또B", imageFile2.getId())
                ),
                scope,
                VoteType.MULTIPLE
        );
    }

    public static User createUser(int key) {
        return User.create("nickname" + key, "profileUrl" + key);
    }

    public static ImageFile createImageFile(int key) {
        return ImageFile.create(
                new ImageFileDto(
                        "originalFileName" + key,
                        "imageUrl" + key,
                        "thumbnailUrl" + key
                )
        );
    }

    public static Vote createVote(Long userId, Long postId, Long imageId) {
        return Vote.of(userId, postId, imageId);
    }

    public static Comment createComment(Long userId, Long postId) {
        return new Comment(userId, postId, "내용");
    }
}
