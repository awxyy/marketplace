package com.dotdot.marketplace.mongo.repository;
import com.dotdot.marketplace.mongo.model.AppLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogRepository extends MongoRepository<AppLog, String> {
    List<AppLog> findByLevel(String level);
    List<AppLog> findByTimestampAfter(LocalDateTime date);
}
