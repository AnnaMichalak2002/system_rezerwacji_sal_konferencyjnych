package edu.zut.projekt.controller;

import edu.zut.projekt.api.ReservationsApi;
import edu.zut.projekt.config.DataInitializer;
import edu.zut.projekt.exception.ConflictException;
import edu.zut.projekt.exception.ForbiddenException;
import edu.zut.projekt.exception.NotFoundException;
import edu.zut.projekt.model.Reservation;
import edu.zut.projekt.model.ReservationListResponse;
import edu.zut.projekt.model.ReservationResponse;
import edu.zut.projekt.model.ReservationStatus;
import edu.zut.projekt.model.ResponseHeader;
import edu.zut.projekt.store.ReservationStore;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
public class ReservationsController implements ReservationsApi {

    private final ReservationStore reservationStore;

    public ReservationsController(ReservationStore reservationStore) {
        this.reservationStore = reservationStore;
    }

    @Override
    public ResponseEntity<ReservationListResponse> reservationsGet() {
        ensureAdmin();

        ReservationListResponse response = new ReservationListResponse();
        response.setResponseHeader(buildResponseHeader());
        response.setReservations(reservationStore.findAll());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ReservationResponse> reservationsIdGet(UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        boolean isAdmin = isAdmin(authentication);

        UUID currentUserId = getCurrentUserId(email);

        Reservation reservation = reservationStore.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "RESERVATION_NOT_FOUND",
                        "Wskazana rezerwacja nie zostala znaleziona."
                ));

        if (!isAdmin && !reservation.getUserId().equals(currentUserId)) {
            throw new ForbiddenException(
                    "FORBIDDEN",
                    "Uzytkownik nie posiada uprawnien do przegladania wskazanej rezerwacji."
            );
        }

        ReservationResponse response = new ReservationResponse();
        response.setResponseHeader(buildResponseHeader());
        response.setReservation(reservation);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ReservationResponse> reservationsIdCancelPost(UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        boolean isAdmin = isAdmin(authentication);

        UUID currentUserId = getCurrentUserId(email);

        Reservation reservation = reservationStore.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "RESERVATION_NOT_FOUND",
                        "Wskazana rezerwacja nie zostala znaleziona."
                ));

        if (!isAdmin && !reservation.getUserId().equals(currentUserId)) {
            throw new ForbiddenException(
                    "FORBIDDEN",
                    "Uzytkownik nie posiada uprawnien do anulowania wskazanej rezerwacji."
            );
        }

        OffsetDateTime now = OffsetDateTime.now();

        if (ReservationStatus.CANCELLED.equals(reservation.getStatus())) {
            throw new ConflictException(
                    "RESERVATION_ALREADY_CANCELLED",
                    "Nie mozna anulowac rezerwacji, poniewaz zostala juz wczesniej anulowana."
            );
        }

        if (!reservation.getStartDateTime().isAfter(now)) {
            throw new ConflictException(
                    "RESERVATION_CANNOT_BE_CANCELLED",
                    "Nie mozna anulowac rezerwacji, ktora juz sie rozpoczela lub juz sie zakonczyla."
            );
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationStore.save(reservation);

        ReservationResponse response = new ReservationResponse();
        response.setResponseHeader(buildResponseHeader());
        response.setReservation(reservation);

        return ResponseEntity.ok(response);
    }

    private void ensureAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isAdmin(authentication)) {
            throw new ForbiddenException(
                    "FORBIDDEN",
                    "Uzytkownik nie posiada uprawnien do wykonania tej operacji."
            );
        }
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private UUID getCurrentUserId(String email) {
        if ("admin@yourfurniture.pl".equalsIgnoreCase(email)) {
            return DataInitializer.ADMIN_ID;
        }
        return DataInitializer.USER_ID;
    }

    private ResponseHeader buildResponseHeader() {
        ResponseHeader header = new ResponseHeader();
        header.setRequestId(UUID.randomUUID());
        header.setSendDate(OffsetDateTime.now());
        return header;
    }
}