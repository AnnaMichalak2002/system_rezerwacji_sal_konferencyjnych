package edu.zut.projekt.config;

import edu.zut.projekt.model.Reservation;
import edu.zut.projekt.model.ReservationStatus;
import edu.zut.projekt.model.Room;
import edu.zut.projekt.model.RoomStatus;
import edu.zut.projekt.store.ReservationStore;
import edu.zut.projekt.store.RoomStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Configuration
public class DataInitializer {

    public static final UUID ADMIN_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    public static final UUID USER_ID  = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

    @Bean
    CommandLineRunner initData(RoomStore roomStore, ReservationStore reservationStore) {
        return args -> {
            roomStore.clear();
            reservationStore.clear();

            Room room1 = new Room();
            //room1.setId(UUID.randomUUID());
            room1.setId(UUID.fromString("00000000-0000-0000-0000-00000000000a"));
            room1.setName("Sala Konferencyjna A");
            room1.setCapacity(12);
            room1.setLocation("Budynek A, skrzydło wschodnie");
            room1.setFloor(2);
            room1.setStatus(RoomStatus.AVAILABLE);
            room1.setHasProjector(true);
            room1.setHasWhiteboard(true);
            room1.setHasDesktopComputer(false);
            room1.setHasSpeakers(true);
            room1.setHasVideoConferenceSystem(false);

            Room room2 = new Room();
            //room2.setId(UUID.randomUUID());
            room2.setId(UUID.fromString("00000000-0000-0000-0000-00000000000b"));
            room2.setName("Sala Konferencyjna B");
            room2.setCapacity(20);
            room2.setLocation("Budynek A, skrzydło zachodnie");
            room2.setFloor(1);
            room2.setStatus(RoomStatus.UNDER_MAINTENANCE);
            room2.setHasProjector(true);
            room2.setHasWhiteboard(true);
            room2.setHasDesktopComputer(true);
            room2.setHasSpeakers(true);
            room2.setHasVideoConferenceSystem(true);

            Room room3 = new Room();
            //room3.setId(UUID.randomUUID());
            room3.setId(UUID.fromString("00000000-0000-0000-0000-00000000000c"));
            room3.setName("Sala Konferencyjna C");
            room3.setCapacity(8);
            room3.setLocation("Budynek B, parter");
            room3.setFloor(0);
            room3.setStatus(RoomStatus.AVAILABLE);
            room3.setHasProjector(false);
            room3.setHasWhiteboard(true);
            room3.setHasDesktopComputer(false);
            room3.setHasSpeakers(false);
            room3.setHasVideoConferenceSystem(false);

            roomStore.save(room1);
            roomStore.save(room2);
            roomStore.save(room3);

            Reservation reservation1 = new Reservation();
            //reservation1.setId(UUID.randomUUID());
            reservation1.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
            reservation1.setRoomId(room1.getId());
            reservation1.setUserId(USER_ID);
            reservation1.setMeetingTitle("Spotkanie projektowe");
            reservation1.setDescription("Omówienie postępu prac");
            reservation1.setStartDateTime(OffsetDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0));
            reservation1.setEndDateTime(OffsetDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0).withNano(0));
            reservation1.setStatus(ReservationStatus.ACTIVE);
            reservation1.setCreatedAt(OffsetDateTime.now());

            Reservation reservation2 = new Reservation();
            //reservation2.setId(UUID.randomUUID());
            reservation2.setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
            reservation2.setRoomId(room3.getId());
            reservation2.setUserId(ADMIN_ID);
            reservation2.setMeetingTitle("Spotkanie administracyjne");
            reservation2.setDescription("Przegląd zasobów");
            reservation2.setStartDateTime(OffsetDateTime.now().plusDays(2).withHour(12).withMinute(0).withSecond(0).withNano(0));
            reservation2.setEndDateTime(OffsetDateTime.now().plusDays(2).withHour(13).withMinute(0).withSecond(0).withNano(0));
            reservation2.setStatus(ReservationStatus.ACTIVE);
            reservation2.setCreatedAt(OffsetDateTime.now());

            reservationStore.save(reservation1);
            reservationStore.save(reservation2);
        };
    }
}