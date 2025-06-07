package com.chooz.comment.domain;

import com.chooz.common.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static com.chooz.common.util.Validator.validateEmptyString;
import static com.chooz.common.util.Validator.validateNull;

@Entity
@Getter
@Table(name = "comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long postId;

    @NotNull
    private Long userId;

    @NotNull
    private String content;

    @NotNull
    private Boolean edited;

    public Comment(Long postId, Long userId, String content) {
        validateNull(postId, userId);
        validateEmptyString(content);
        this.postId = postId;
        this.userId = userId;
        this.content = content;
    }

    public static Comment create(Long postId, Long userId, String content) {
        return new Comment(null, postId, userId, content, false);
    }

    public void updateComment(String content) {
        validateEmptyString(content);
        this.content = content;
        this.edited = true;
    }
}
