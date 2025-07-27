package com.chooz.user.application;

import com.chooz.user.domain.NicknameAdjectiveRepository;
import com.chooz.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NicknameGenerator {

    private final NicknameAdjectiveRepository nicknameAdjectiveRepository;
    private final UserRepository userRepository;

    public String generate() {
        String nickName = nicknameAdjectiveRepository.findRandomNicknameAdjective()
                .map(adjective -> adjective.getAdjective() + " 츄")
                .orElse("숨겨진 츄");
        return checkDuplicateNickname(nickName);
    }
    private String checkDuplicateNickname(String nickName) {
        int suffix = 1;
        while (userRepository.existsByNickname(nickName)){
            nickName = nickName + suffix;
            suffix++;
        }
        return nickName;
    }
}
