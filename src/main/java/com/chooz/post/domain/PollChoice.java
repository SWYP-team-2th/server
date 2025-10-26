package com.chooz.post.domain;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.chooz.common.util.Validator.validateNull;

@Getter
@Entity
@Table(name = "poll_choices")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PollChoice {

    private static final int MAX_TITLE_LENGTH = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    private String title;

    private String imageUrl;

    public PollChoice(Long id, Post post, String title, String imageUrl) {
        validateNull(title, imageUrl);
        validateTitleLength(title);
        this.id = id;
        this.post = post;
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public static PollChoice create(String title, String imageUrl) {
        return new PollChoice(null, null, title, imageUrl);
    }

    public void setPost(Post post) {
        validateNull(post);
        this.post = post;
    }

    private void validateTitleLength(String title) {
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new BadRequestException(ErrorCode.POLL_CHOICE_TITLE_LENGTH_EXCEEDED);
        }
    }
}
