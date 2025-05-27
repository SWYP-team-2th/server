package com.chooz.thumbnail.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.chooz.common.util.Validator.validateNull;

@Getter
@Entity
@Table(name = "thumbnails")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Thumbnail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;

    private Long pollChoiceId;

    private String thumbnailUrl;

    public Thumbnail(Long id, Long postId, Long pollChoiceId, String thumbnailUrl) {
        validateNull(postId, pollChoiceId, thumbnailUrl);
        this.id = id;
        this.postId = postId;
        this.pollChoiceId = pollChoiceId;
        this.thumbnailUrl = thumbnailUrl;
    }

    public static Thumbnail create(Long postId, Long pollChoiceId, String thumbnailUrl) {
        return new Thumbnail(null, postId, pollChoiceId, thumbnailUrl);
    }
}
