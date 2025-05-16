package com.chooz.user.application;

import com.chooz.user.domain.NicknameAdjectiveRepository;
import com.chooz.user.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NicknameGenerator {

    private final NicknameAdjectiveRepository nicknameAdjectiveRepository;

    public String generate(Role role) {
        return nicknameAdjectiveRepository.findRandomNicknameAdjective()
                .map(adjective -> adjective.getAdjective() + " " + role.getNickname())
                .orElse("숨겨진 " + role.getNickname());
    }
}
