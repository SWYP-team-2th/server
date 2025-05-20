package com.chooz.post.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.chooz.common.util.Validator.validateNull;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PollChoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    private String name;

    private Long imageFileId;

    private int voteCount;

    public PollChoice(Long id, Post post, String name, Long imageFileId, int voteCount) {
        this.id = id;
        this.post = post;
        this.name = name;
        this.imageFileId = imageFileId;
        this.voteCount = voteCount;
    }

    public PollChoice(String name, Long imageFileId, int voteCount) {
        this.name = name;
        this.imageFileId = imageFileId;
        this.voteCount = voteCount;
    }

    public static PollChoice create(String name, Long imageFileId) {
        return new PollChoice(name, imageFileId, 0);
    }

    public void setPost(Post post) {
        validateNull(post);
        this.post = post;
    }

    public void increaseVoteCount() {
        this.voteCount++;
    }

    public void decreaseVoteCount() {
        this.voteCount = this.voteCount == 0 ? 0 : this.voteCount - 1;
    }
}
