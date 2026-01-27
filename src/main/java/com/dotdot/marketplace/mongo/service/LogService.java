package com.dotdot.marketplace.mongo.service;

import com.dotdot.marketplace.mongo.model.AppLog;
import com.dotdot.marketplace.mongo.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    @Async
    public void info(String source, String message, Map<String, Object> data) {
        saveLog(message, "INFO", source, data);
    }

    @Async
    public void error(String source, String message, Throwable ex) {
        Map<String, Object> errorData = Map.of(
                "exceptionMessage", ex.getClass().getName(),
                "stackTrace", ex.getMessage() != null ? ex.getMessage() : "No message"
        );
        saveLog(message, "ERROR", source, errorData);
    }

    private void saveLog(String message, String level, String source, Map<String, Object> data) {
        try {
            AppLog log = new AppLog(message, level, source, data);
            logRepository.save(log);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }
}
