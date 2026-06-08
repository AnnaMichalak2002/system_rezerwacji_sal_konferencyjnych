package edu.zut.projekt.controller;
import edu.zut.projekt.exception.ForbiddenException;
import edu.zut.projekt.api.ReservationsApi;
import edu.zut.projekt.config.DataInitializer;
import edu.zut.projekt.exception.NotFoundException;
import edu.zut.projekt.model.Reservation;
import edu.zut.projekt.model.ReservationListResponse;
import edu.zut.projekt.model.ReservationResponse;
import edu.zut.projekt.model.ResponseHeader;
import edu.zut.projekt.store.ReservationStore;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class ReservationsController implements ReservationsApi {

    private final ReservationStore reservationStore;

    public ReservationsController(ReservationStore reservationStore) {
        this.reservationStore = reservationStore;
    }

    @Override
    public ResponseEntity<ReservationListResponse> reservationsGet() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new ForbiddenException(
                    "FORBIDDEN",
                    "Uzytkownik nie posiada uprawnien do wykonania tej operacji."
            );
        }

        ReservationListResponse response = new ReservationListResponse();
        response.setResponseHeader(buildResponseHeader());
        response.setReservations(reservationStore.findAll());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ReservationResponse> reservationsIdGet(UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        UUID currentUserId = getCurrentUserId(email);

        Reservation reservation = reservationStore.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "RESERVATION_NOT_FOUND",
                        "Wskazana rezerwacja nie zostala znaleziona."
                ));

        if (!isAdmin && !reservation.getUserId().equals(currentUserId)) {
            throw new ForbiddenException(
                    "FORBIDDEN",
                    "Uzytkownik nie posiada uprawnien do wykonania tej operacji."
            );
        }

        ReservationResponse response = new ReservationResponse();
        response.setResponseHeader(buildResponseHeader());
        response.setReservation(reservation);

        return ResponseEntity.ok(response);
    }

    private ResponseHeader buildResponseHeader() {
        ResponseHeader header = new ResponseHeader();
        header.setRequestId(UUID.randomUUID());
        header.setSendDate(OffsetDateTime.now());
        return header;
    }

    private UUID getCurrentUserId(String email) {
        if ("admin@yourfurniture.pl".equalsIgnoreCase(email)) {
            return DataInitializer.ADMIN_ID;
        }
        return DataInitializer.USER_ID;
    }
}