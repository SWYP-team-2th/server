package com.chooz.user.application;

import com.chooz.user.domain.NicknameAdjectiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NicknameGenerator {

    private final NicknameAdjectiveRepository nicknameAdjectiveRepository;

    public String generate() {
        return nicknameAdjectiveRepository.findRandomNicknameAdjective()
                .map(adjective -> adjective.getAdjective() + " 츄")
                .orElse("숨겨진 츄");
    }
}
