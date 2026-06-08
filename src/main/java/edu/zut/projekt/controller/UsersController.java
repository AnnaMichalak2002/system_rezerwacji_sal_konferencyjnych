package edu.zut.projekt.controller;

import edu.zut.projekt.api.UsersApi;
import edu.zut.projekt.model.ReservationListResponse;
import edu.zut.projekt.model.ResponseHeader;
import edu.zut.projekt.model.UserResponse;
import edu.zut.projekt.service.CurrentUserService;
import edu.zut.projekt.store.ReservationStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
public class UsersController implements UsersApi {

    private final ReservationStore reservationStore;
    private final CurrentUserService currentUserService;

    public UsersController(ReservationStore reservationStore, CurrentUserService currentUserService) {
        this.reservationStore = reservationStore;
        this.currentUserService = currentUserService;
    }

    @Override
    public ResponseEntity<UserResponse> usersMeGet() {
        UserResponse response = new UserResponse();
        response.setResponseHeader(buildResponseHeader());
        response.setUser(currentUserService.getCurrentUser());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ReservationListResponse> usersMeReservationsGet() {
        ReservationListResponse response = new ReservationListResponse();
        response.setResponseHeader(buildResponseHeader());
        response.setReservations(reservationStore.findByUserId(currentUserService.getCurrentUserId()));

        return ResponseEntity.ok(response);
    }

    private ResponseHeader buildResponseHeader() {
        ResponseHeader header = new ResponseHeader();
        header.setRequestId(UUID.randomUUID());
        header.setSendDate(OffsetDateTime.now());
        return header;
    }
}