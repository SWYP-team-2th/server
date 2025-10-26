package com.chooz.support;

import com.chooz.common.config.JpaConfig;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

@Import({JpaConfig.class, RepositoryTest.QueryDslConfig.class})
@DataJpaTest(
        includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class)
)
public abstract class RepositoryTest {

    @TestConfiguration
    public static class QueryDslConfig {

        @PersistenceContext
        private EntityManager entityManager;

        @Bean
        public JPAQueryFactory queryFactory() {
            return new JPAQueryFactory(entityManager);
        }
    }
}
