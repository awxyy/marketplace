package com.dotdot.marketplace.reservation.sheduler;

import com.dotdot.marketplace.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReservationCleanupScheduler {

    private final ReservationService reservationService;

    @Scheduled(fixedRate = 300000)
    public void cleanupExpiredReservations() {
        log.debug("Starting cleanup of expired reservations");
        try {
            int cleanedCount = reservationService.cleanupExpiredReservations();
            if (cleanedCount > 0) {
                log.info("Cleaned up {} expired reservations", cleanedCount);
            }
        } catch (Exception e) {
            log.error("Error during reservation cleanup", e);
        }
    }
}
