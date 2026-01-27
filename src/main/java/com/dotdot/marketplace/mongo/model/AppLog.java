package com.dotdot.marketplace.mongo.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Data
@Document(collection = "app_logs")
public class AppLog {
    @Id
    private String id;
    private String message;
    private String level;
    @Indexed(expireAfter = "30d")
    private LocalDateTime timestamp;
    private String source;

    private Map<String,Object> metadata;

    public AppLog() {
        this.timestamp = LocalDateTime.now();
    }
    public AppLog(String message, String level, String source, Map<String,Object> metadata) {
        this.message = message;
        this.level = level;
        this.timestamp = LocalDateTime.now();
        this.source = source;
        this.metadata = metadata;
    }
}
