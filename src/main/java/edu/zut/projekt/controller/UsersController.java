package edu.zut.projekt.controller;

import edu.zut.projekt.api.UsersApi;
import edu.zut.projekt.config.DataInitializer;
import edu.zut.projekt.model.*;
import edu.zut.projekt.store.ReservationStore;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
public class UsersController implements UsersApi {

    private final ReservationStore reservationStore;

    public UsersController(ReservationStore reservationStore) {
        this.reservationStore = reservationStore;
    }

    @Override
    public ResponseEntity<UserResponse> usersMeGet() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = new User();
        user.setId(getCurrentUserId(email));
        user.setEmail(email);
        user.setActive(true);

        if ("admin@yourfurniture.pl".equalsIgnoreCase(email)) {
            user.setFirstName("Admin");
            user.setLastName("Systemowy");
            user.setRole(UserRole.ADMIN);
        } else {
            user.setFirstName("Jan");
            user.setLastName("Kowalski");
            user.setRole(UserRole.USER);
        }

        UserResponse response = new UserResponse();
        response.setResponseHeader(buildResponseHeader());
        response.setUser(user);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ReservationListResponse> usersMeReservationsGet() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID currentUserId = getCurrentUserId(authentication.getName());

        ReservationListResponse response = new ReservationListResponse();
        response.setResponseHeader(buildResponseHeader());
        response.setReservations(reservationStore.findByUserId(currentUserId));

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