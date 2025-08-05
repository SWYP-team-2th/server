package com.chooz.user.application;

import com.chooz.user.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NicknameGeneratorTest {

    @InjectMocks
    NicknameGenerator nicknameGenerator;

    @Mock
    NicknameAdjectiveRepository nicknameAdjectiveRepository;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("닉네임 생성 테스트")
    void generate() throws Exception {
        //given
        given(nicknameAdjectiveRepository.findRandomNicknameAdjective())
                .willReturn(Optional.of(new NicknameAdjective("호기심 많은")));

        //when
        String nickname = nicknameGenerator.generate();

        //then
        Assertions.assertThat(nickname).isEqualTo("호기심 많은 츄");
    }
}
