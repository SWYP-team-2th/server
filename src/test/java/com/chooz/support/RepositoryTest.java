package com.chooz.support;

import com.chooz.common.config.JpaConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

@Import(JpaConfig.class)
@DataJpaTest(
        includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class)
)
public abstract class RepositoryTest {
}
