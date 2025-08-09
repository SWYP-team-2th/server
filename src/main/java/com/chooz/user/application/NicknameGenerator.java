package com.chooz.user.application;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.user.domain.NicknameAdjectiveRepository;
import com.chooz.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Component
@RequiredArgsConstructor
public class NicknameGenerator {

    private final NicknameAdjectiveRepository nicknameAdjectiveRepository;
    private final UserRepository userRepository;

    public String generate() {
        String prefix = nicknameAdjectiveRepository.findRandomNicknameAdjective()
                .map(adjective -> adjective.getAdjective() + " 츄")
                .orElse("숨겨진 츄");
        return makeNickname(prefix);
    }
    private String makeNickname(String prefix) {
        List<String> nickNames = userRepository.findNicknamesByPrefix(prefix);
        Set<BigInteger> usedSuffixes = getUsedSuffixes(prefix, nickNames);
        return findUsableNickname(prefix, usedSuffixes);
    }
    private Set<BigInteger> getUsedSuffixes(String prefix, List<String> nickNames) {
        Set<BigInteger> usedSuffixes = new TreeSet<>(BigInteger::compareTo);
        for(String nickName : nickNames) {
            String suffix = nickName.substring(prefix.length());
            if(suffix.isEmpty()) {
                usedSuffixes.add(BigInteger.ZERO);
            }else{
                usedSuffixes.add(new BigInteger(suffix));
            }
        }
        return usedSuffixes;
    }
    private String findUsableNickname(String prefix, Set<BigInteger> usedSuffixes) {
        BigInteger suffix = BigInteger.ZERO;
        while (usedSuffixes.contains(suffix)) {
            suffix = suffix.add(BigInteger.ONE);
        }
        return suffix.signum() == 0 ? prefix : prefix + suffix;
    }
}
