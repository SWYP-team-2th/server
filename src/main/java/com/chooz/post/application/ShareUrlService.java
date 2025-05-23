package com.chooz.post.application;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShareUrlService {

    private final ShareUrlKeyGenerator shareUrlKeyGenerator;
    private final Base62Encryptor base62Encryptor;

    public String generateShareUrl() {
        String key = shareUrlKeyGenerator.generateKey();
        return base62Encryptor.encrypt(key);
    }

    public String getShareUrlKey(String shareUrl) {
        return base62Encryptor.decrypt(shareUrl);
    }
}
