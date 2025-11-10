package com.chooz.support;

import com.chooz.image.application.S3Client;
import com.chooz.support.mock.AwsS3Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Import(IntegrationTest.IntegrationTestConfig.class)
@ActiveProfiles("test")
@Transactional
@SpringBootTest
public abstract class IntegrationTest {

    @Configuration
    public static class IntegrationTestConfig {

        @Bean
        @Primary
        public S3Client s3ClientMock() {
            return new AwsS3Mock();
        }
    }
}
