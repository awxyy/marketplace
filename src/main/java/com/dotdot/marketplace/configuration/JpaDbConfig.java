package com.dotdot.marketplace.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.dotdot.marketplace",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX
                , pattern = "com.dotdot.marketplace.mongo.repository.*"
        )
)
public class JpaDbConfig {
}
