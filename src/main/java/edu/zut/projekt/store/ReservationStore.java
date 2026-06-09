package edu.zut.projekt.store;

import edu.zut.projekt.model.Reservation;
import edu.zut.projekt.model.ReservationStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class ReservationStore {

    private final Map<UUID, Reservation> reservations = new ConcurrentHashMap<>();

    public List<Reservation> findAll() {
        return new ArrayList<>(reservations.values());
    }

    public Optional<Reservation> findById(UUID id) {
        return Optional.ofNullable(reservations.get(id));
    }

    public Reservation save(Reservation reservation) {
        reservations.put(reservation.getId(), reservation);
        return reservation;
    }

    public List<Reservation> findByUserId(UUID userId) {
        return reservations.values().stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Reservation> findActiveReservationsByUserId(UUID userId) {
        return reservations.values().stream()
                .filter(r -> r.getUserId().equals(userId))
                .filter(r -> ReservationStatus.ACTIVE.equals(r.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Reservation> findActiveReservationsByRoomId(UUID roomId) {
        return reservations.values().stream()
                .filter(r -> r.getRoomId().equals(roomId))
                .filter(r -> ReservationStatus.ACTIVE.equals(r.getStatus()))
                .collect(Collectors.toList());
    }

    public void clear() {
        reservations.clear();
    }
}