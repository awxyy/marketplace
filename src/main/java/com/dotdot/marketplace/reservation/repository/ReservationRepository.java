package com.dotdot.marketplace.reservation.repository;

import com.dotdot.marketplace.reservation.entity.Reservation;
import com.dotdot.marketplace.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByUserIdAndProductIdAndStatus(Long userId, Long productId, ReservationStatus status);

    List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status);

    List<Reservation> findByStatusAndExpiresAtBefore(ReservationStatus status, LocalDateTime expiredBefore);

    @Modifying
    @Query("UPDATE Reservation r SET r.status = :newStatus WHERE r.expiresAt < :expiredBefore AND r.status = :currentStatus")
    int updateExpiredReservations(@Param("expiredBefore") LocalDateTime expiredBefore,
                                  @Param("currentStatus") ReservationStatus currentStatus,
                                  @Param("newStatus") ReservationStatus newStatus);

    @Query("SELECT SUM(r.quantity) FROM Reservation r WHERE r.product.id = :productId AND r.status = :status")
    Integer sumQuantityByProductIdAndStatus(@Param("productId") Long productId, @Param("status") ReservationStatus status);
}