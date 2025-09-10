package com.dotdot.marketplace.reservation.service;

import com.dotdot.marketplace.exception.ReservationNotFoundException;
import com.dotdot.marketplace.product.entity.Product;
import com.dotdot.marketplace.product.repository.ProductRepository;
import com.dotdot.marketplace.reservation.entity.Reservation;
import com.dotdot.marketplace.reservation.entity.ReservationStatus;
import com.dotdot.marketplace.reservation.repository.ReservationRepository;
import com.dotdot.marketplace.user.entity.User;
import com.dotdot.marketplace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Value("${app.reservation.ttl-minutes:15}")
    private int reservationTtlMinutes;

    @Transactional
    public Reservation createOrUpdateReservation(Long userId, Long productId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        var existingReservation = reservationRepository
                .findByUserIdAndProductIdAndStatus(userId, productId, ReservationStatus.ACTIVE);

        if (existingReservation.isPresent()) {
            return updateReservation(existingReservation.get(), quantity);
        }

        return createNewReservation(user, product, quantity);
    }

    @Transactional
    public Reservation createNewReservation(User user, Product product, Integer quantity) {
        if (product.getAvailableQuantity() < quantity) {
            throw new IllegalArgumentException(
                    String.format("Not enough available stock. Available: %d, Requested: %d",
                            product.getAvailableQuantity(), quantity));
        }

        product.setReservedQuantity(product.getReservedQuantity() + quantity);
        productRepository.save(product);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setProduct(product);
        reservation.setQuantity(quantity);
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(reservationTtlMinutes));
        reservation.setStatus(ReservationStatus.ACTIVE);

        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation updateReservation(Reservation reservation, Integer newQuantity) {
        Product product = reservation.getProduct();
        int oldQuantity = reservation.getQuantity();
        int quantityDiff = newQuantity - oldQuantity;

        if (quantityDiff > 0) {
            if (product.getAvailableQuantity() < quantityDiff) {
                throw new IllegalArgumentException("Not enough available stock for reservation update");
            }
        }

        product.setReservedQuantity(product.getReservedQuantity() + quantityDiff);
        productRepository.save(product);

        reservation.setQuantity(newQuantity);
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(reservationTtlMinutes)); // Extend expiration

        return reservationRepository.save(reservation);
    }

    @Transactional
    public void releaseReservation(Long userId, Long productId) {
        var reservation = reservationRepository
                .findByUserIdAndProductIdAndStatus(userId, productId, ReservationStatus.ACTIVE);
        reservation.ifPresent(this::releaseReservation);
    }

    @Transactional
    public void releaseReservation(Reservation reservation) {
        Product product = reservation.getProduct();

        product.setReservedQuantity(product.getReservedQuantity() - reservation.getQuantity());
        productRepository.save(product);

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        log.info("Released reservation {} for user {} and product {}",
                reservation.getId(), reservation.getUser().getId(), product.getId());
    }

    @Transactional
    public void convertReservationToOrder(Long userId, Long productId) {
        Reservation reservation = reservationRepository
                .findByUserIdAndProductIdAndStatus(userId, productId, ReservationStatus.ACTIVE)
                .orElseThrow(() -> new ReservationNotFoundException(
                        "No active reservation found for userId=" + userId + ", productId=" + productId
                ));

        Product product = reservation.getProduct();

        if (product.getQuantity() < reservation.getQuantity()) {
            throw new RuntimeException("Not enough stock to convert reservation for product: "
                    + product.getName());
        }

        product.setQuantity(product.getQuantity() - reservation.getQuantity());
        product.setReservedQuantity(product.getReservedQuantity() - reservation.getQuantity());
        productRepository.save(product);

        reservation.setStatus(ReservationStatus.CONVERTED_TO_ORDER);
        reservationRepository.save(reservation);
    }

    @Transactional
    public int cleanupExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();

        List<Reservation> expiredReservations = reservationRepository.findByExpiresAtBeforeAndStatus(
                ReservationStatus.ACTIVE, now
        );

        for (Reservation reservation : expiredReservations) {
            Product product = reservation.getProduct();
            product.setReservedQuantity(product.getReservedQuantity() - reservation.getQuantity());
            productRepository.save(product);

            reservation.setStatus(ReservationStatus.EXPIRED);
            reservationRepository.save(reservation);
        }
        int cleanedCount = expiredReservations.size();

        log.info("Cleaned up {} expired reservations", cleanedCount);
        return cleanedCount;
    }

    public List<Reservation> getUserActiveReservations(Long userId) {
        return reservationRepository.findByUserIdAndStatus(userId, ReservationStatus.ACTIVE);
    }
}
