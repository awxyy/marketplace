package com.dotdot.marketplace.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.dotdot.marketplace.mongo.repository")
public class MongoDbConfig {
}
