package com.chooz.post.domain;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    private String title;

    private String imageUrl;

    private int orderSeq;

    public PollChoice(Long id, Post post, String title, String imageUrl, int orderSeq) {
        validateNull(title, imageUrl);
        this.id = id;
        this.post = post;
        this.title = title;
        this.imageUrl = imageUrl;
        this.orderSeq = orderSeq;
    }

    public static PollChoice create(String title, String imageUrl, int order) {
        return new PollChoice(null, null, title, imageUrl, order);
    }

    public void setPost(Post post) {
        validateNull(post);
        this.post = post;
    }
}
