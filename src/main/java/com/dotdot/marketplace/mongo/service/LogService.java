package com.dotdot.marketplace.mongo.service;

import com.dotdot.marketplace.mongo.model.AppLog;
import com.dotdot.marketplace.mongo.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    @Async
    public void info(String source, String message, Map<String, Object> data) {
        saveLog("INFO", source, message, data);
    }

    @Async
    public void error(String ssource, String messsage, Throwable ex) {
        Map<String, Object> errorData = Map.of(
                "exceptionMessage", ex.getClass().getName(),
                "stackTrace", ex.getMessage() != null ? ex.getMessage() : "No message"
        );
        saveLog("ERROR", ssource, messsage, errorData);
    }

    private void saveLog(String level, String source, String message, Map<String, Object> data) {
        try {
            AppLog log = new AppLog(level, source, message, data);
            logRepository.save(log);
        } catch (Exception e) {

            System.err.println("Failed to save log: " + e.getMessage());
        }
    }
}
