package com.chooz.post.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PollChoiceNameGeneratorTest {

    PollChoiceNameGenerator pollChoiceNameGenerator;

    @BeforeEach
    void setUp() {
        pollChoiceNameGenerator = new PollChoiceNameGenerator();
    }

    @Test
    @DisplayName("이미지 이름 생성")
    void generate() throws Exception {
        //given

        //when
        String generate1 = pollChoiceNameGenerator.generate();
        String generate2 = pollChoiceNameGenerator.generate();

        //then
        assertThat(generate1).isEqualTo("뽀또A");
        assertThat(generate2).isEqualTo("뽀또B");
    }
}
