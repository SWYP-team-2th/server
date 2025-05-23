package com.chooz.post.application;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.assertj.core.api.Assertions.*;

class Base62EncryptorTest {

    Base62Encryptor base62Encryptor;

    @BeforeEach
    void setUp() throws Exception {
        base62Encryptor = new Base62Encryptor("asdfd", "1541235432");
    }

    @Test
    @DisplayName("암호화 및 복호화")
    void encryptAndDecrypt() {
        // given
        String plainText = "15411";

        // when
        String encryptedText = base62Encryptor.encrypt(plainText);
        System.out.println("encryptedText = " + encryptedText);
        String decryptedText = base62Encryptor.decrypt(encryptedText);

        // then
        assertThat(decryptedText).isEqualTo(plainText);
    }

    @Test
    @DisplayName("암호화 및 복호화 - 다른 키")
    void encryptAndDecrypt_differentKey() throws Exception {
        // given
        String plainText = "Hello, World!";
        Base62Encryptor differentBase62Encryptor = new Base62Encryptor("different", "234562");
        String encryptedText = differentBase62Encryptor.encrypt(plainText);

        // when then
        assertThatThrownBy(() -> base62Encryptor.decrypt(encryptedText))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INVALID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("복호화 - 이상한 토큰")
    void decrypt_invalidToken() {
        // given
        String invalid = "invalidToken";

        // when then
        assertThatThrownBy(() -> base62Encryptor.decrypt(invalid))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INVALID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("복호화 - empty string")
    void decrypt_emptyString() {
        // given
        String invalid = "";

        // when then
        assertThatThrownBy(() -> base62Encryptor.decrypt(invalid))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INVALID_TOKEN.getMessage());
    }
}
