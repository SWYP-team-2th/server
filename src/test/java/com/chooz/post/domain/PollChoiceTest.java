package com.chooz.post.domain;

import com.chooz.common.exception.BadRequestException;
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
        PollChoice pollChoice = PollChoice.create(title, imageUrl);

        //then
        assertAll(
                () -> assertThat(pollChoice.getTitle()).isEqualTo(title),
                () -> assertThat(pollChoice.getImageUrl()).isEqualTo(imageUrl)
        );
    }

    @Test
    @DisplayName("투표 선택지 제목은 10자를 초과할 수 없다")
    void createWithTitleExceedingMaxLength() throws Exception {
        //given
        String title = "12345678901"; // 11자
        String imageUrl = "https://example.com/image.jpg";

        //when & then
        assertThrows(BadRequestException.class, () -> {
            PollChoice.create(title, imageUrl);
        });
    }

    @Test
    @DisplayName("투표 선택지 제목은 10자일 때 정상 생성된다")
    void createWithTitleMaxLength() throws Exception {
        //given
        String title = "1234567890"; // 10자
        String imageUrl = "https://example.com/image.jpg";

        //when
        PollChoice pollChoice = PollChoice.create(title, imageUrl);

        //then
        assertThat(pollChoice.getTitle()).isEqualTo(title);
    }
}
