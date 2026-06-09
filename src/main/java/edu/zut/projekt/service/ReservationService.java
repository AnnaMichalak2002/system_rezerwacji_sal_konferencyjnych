package edu.zut.projekt.service;

import edu.zut.projekt.exception.BadRequestException;
import edu.zut.projekt.exception.ConflictException;
import edu.zut.projekt.exception.ForbiddenException;
import edu.zut.projekt.exception.NotFoundException;
import edu.zut.projekt.model.Reservation;
import edu.zut.projekt.model.ReservationPayload;
import edu.zut.projekt.model.ReservationStatus;
import edu.zut.projekt.model.Room;
import edu.zut.projekt.model.RoomStatus;
import edu.zut.projekt.model.User;
import edu.zut.projekt.store.ReservationStore;
import edu.zut.projekt.store.RoomStore;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationService {

    private static final LocalTime WORK_START = LocalTime.of(8, 0);
    private static final LocalTime WORK_END = LocalTime.of(18, 0);

    private final CurrentUserService currentUserService;
    private final ReservationStore reservationStore;
    private final RoomStore roomStore;
    //private final NotificationService notificationService;

    public ReservationService(CurrentUserService currentUserService,
                              ReservationStore reservationStore,
                              RoomStore roomStore) {
                              //NotificationService notificationService) {
        this.currentUserService = currentUserService;
        this.reservationStore = reservationStore;
        this.roomStore = roomStore;
        //this.notificationService = notificationService;
    }

    public Reservation createReservation(ReservationPayload payload) {
        User currentUser = currentUserService.getCurrentUser();

        if (!Boolean.TRUE.equals(currentUser.getActive())) {
            throw new ForbiddenException(
                    "FORBIDDEN",
                    "Uzytkownik nie posiada uprawnien do wykonania tej operacji."
            );
        }

        Room room = roomStore.findById(payload.getRoomId())
                .orElseThrow(() -> new NotFoundException(
                        "ROOM_NOT_FOUND",
                        "Wskazana sala konferencyjna nie zostala znaleziona."
                ));

        if (!RoomStatus.AVAILABLE.equals(room.getStatus())) {
            throw new ConflictException(
                    "ROOM_NOT_AVAILABLE",
                    "Sala nie moze zostac zarezerwowana, poniewaz nie ma statusu AVAILABLE."
            );
        }

        validateTimeRange(payload);
        validateWorkingDayAndHours(payload);
        validateDuration(payload);
        validateUserReservationConflict(currentUser.getId(), payload);
        validateRoomReservationConflict(room.getId(), payload);

        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID());
        reservation.setRoomId(room.getId());
        reservation.setUserId(currentUser.getId());
        reservation.setMeetingTitle(payload.getMeetingTitle());
        reservation.setDescription(payload.getDescription());
        reservation.setStartDateTime(payload.getStartDateTime());
        reservation.setEndDateTime(payload.getEndDateTime());
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setCreatedAt(OffsetDateTime.now());

        reservationStore.save(reservation);
        //notificationService.sendReservationConfirmation(currentUser, reservation);

        return reservation;
    }

    private void validateTimeRange(ReservationPayload payload) {
        if (!payload.getStartDateTime().isBefore(payload.getEndDateTime())) {
            throw new BadRequestException(
                    "INVALID_TIME_RANGE",
                    "Moment rozpoczecia rezerwacji musi byc wczesniejszy niz moment jej zakonczenia."
            );
        }

        if (payload.getStartDateTime().isBefore(OffsetDateTime.now())) {
            throw new BadRequestException(
                    "INVALID_TIME_RANGE",
                    "Moment rozpoczecia rezerwacji musi byc wczesniejszy niz moment jej zakonczenia."
            );
        }
    }

    private void validateWorkingDayAndHours(ReservationPayload payload) {
        DayOfWeek day = payload.getStartDateTime().getDayOfWeek();

        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            throw new BadRequestException(
                    "RESERVATION_OUTSIDE_WORKING_HOURS",
                    "Rezerwacja zostala zaplanowana poza dniami roboczymi lub poza godzinami pracy organizacji."
            );
        }

        LocalTime start = payload.getStartDateTime().toLocalTime();
        LocalTime end = payload.getEndDateTime().toLocalTime();

        if (start.isBefore(WORK_START) || end.isAfter(WORK_END)) {
            throw new BadRequestException(
                    "RESERVATION_OUTSIDE_WORKING_HOURS",
                    "Rezerwacja zostala zaplanowana poza dniami roboczymi lub poza godzinami pracy organizacji."
            );
        }
    }

    private void validateDuration(ReservationPayload payload) {
        long durationMinutes = Duration.between(
                payload.getStartDateTime(),
                payload.getEndDateTime()
        ).toMinutes();

        boolean startAligned = payload.getStartDateTime().getMinute() == 0
                || payload.getStartDateTime().getMinute() == 30;

        boolean endAligned = payload.getEndDateTime().getMinute() == 0
                || payload.getEndDateTime().getMinute() == 30;

        if (durationMinutes < 30 || durationMinutes > 240 || !startAligned || !endAligned) {
            throw new BadRequestException(
                    "INVALID_RESERVATION_DURATION",
                    "Czas trwania rezerwacji jest nieprawidlowy."
            );
        }
    }

    private void validateUserReservationConflict(UUID userId, ReservationPayload payload) {
        List<Reservation> activeReservations = reservationStore.findActiveReservationsByUserId(userId);

        boolean conflict = activeReservations.stream().anyMatch(existing ->
                overlaps(
                        payload.getStartDateTime(),
                        payload.getEndDateTime(),
                        existing.getStartDateTime(),
                        existing.getEndDateTime()
                )
        );

        if (conflict) {
            throw new ConflictException(
                    "USER_ALREADY_HAS_RESERVATION_AT_THIS_TIME",
                    "Uzytkownik posiada juz inna aktywna rezerwacje w tym samym przedziale czasu."
            );
        }
    }

    private void validateRoomReservationConflict(UUID roomId, ReservationPayload payload) {
        List<Reservation> activeReservations = reservationStore.findActiveReservationsByRoomId(roomId);

        boolean conflict = activeReservations.stream().anyMatch(existing ->
                overlaps(
                        payload.getStartDateTime(),
                        payload.getEndDateTime(),
                        existing.getStartDateTime(),
                        existing.getEndDateTime()
                )
        );

        if (conflict) {
            throw new ConflictException(
                    "ROOM_ALREADY_BOOKED",
                    "Sala jest juz zarezerwowana w wybranym terminie."
            );
        }
    }

    private boolean overlaps(OffsetDateTime newStart,
                             OffsetDateTime newEnd,
                             OffsetDateTime existingStart,
                             OffsetDateTime existingEnd) {
        return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
    }
}