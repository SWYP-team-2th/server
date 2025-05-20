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
        String name = "뽀또A";
        long imageFileId = 1L;

        //when
        PollChoice pollChoice = PollChoice.create(name, imageFileId);

        //then
        assertAll(
                () -> assertThat(pollChoice.getName()).isEqualTo(name),
                () -> assertThat(pollChoice.getImageFileId()).isEqualTo(imageFileId),
                () -> assertThat(pollChoice.getVoteCount()).isEqualTo(0)
        );
    }
}
