package com.chooz.post.application;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import io.seruco.encoding.base62.Base62;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ShareUrlService {

    private final ShareUrlKeyGenerator shareUrlKeyGenerator;

    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final int BASE = ALPHABET.length();

    public String generateShareUrl() {
        String key = shareUrlKeyGenerator.generateKey();
        return encodeBase62(key);
    }

    private String encodeBase62(String key) {
        long number = Long.parseLong(key);

        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.append(ALPHABET.charAt((int) (number % BASE)));
            number /= BASE;
        }
        return sb.reverse().toString();
    }
}
