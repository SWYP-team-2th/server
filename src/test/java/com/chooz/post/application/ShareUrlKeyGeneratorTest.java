package com.chooz.post.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ShareUrlKeyGeneratorTest {

    ShareUrlKeyGenerator shareUrlKeyGenerator;

    @BeforeEach
    void setUp() {
        shareUrlKeyGenerator = new ShareUrlKeyGenerator(Clock.systemDefaultZone());
    }

    @Test
    @DisplayName("키 생성")
    void createKey() throws Exception {
        //given

        //when
        String key = shareUrlKeyGenerator.generateKey();

        //then
        System.out.println("key = " + key);
        assertThat(key).isNotNull();
    }
}
