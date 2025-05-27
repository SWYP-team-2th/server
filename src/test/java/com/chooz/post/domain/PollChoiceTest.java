package com.chooz.post.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PollChoiceTest {

    @Test
    @DisplayName("게시글 이미지 생성")
    void create() throws Exception {
        //given
        String title = "title";
        String imageUrl = "https://example.com/image.jpg";

        //when
        PollChoice pollChoice = PollChoice.create(title, imageUrl, 0);

        //then
        assertAll(
                () -> assertThat(pollChoice.getTitle()).isEqualTo(title),
                () -> assertThat(pollChoice.getImageUrl()).isEqualTo(imageUrl),
                () -> assertThat(pollChoice.getOrderSeq()).isEqualTo(0)
        );
    }
}
