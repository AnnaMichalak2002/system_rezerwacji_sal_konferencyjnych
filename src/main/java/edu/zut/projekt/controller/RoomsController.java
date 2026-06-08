package edu.zut.projekt.controller;
import edu.zut.projekt.model.RoomPayload;
import edu.zut.projekt.api.RoomsApi;
import edu.zut.projekt.exception.ConflictException;
import edu.zut.projekt.exception.ForbiddenException;
import edu.zut.projekt.exception.NotFoundException;
import edu.zut.projekt.model.CreateRoomRequest;
import edu.zut.projekt.model.ResponseHeader;
import edu.zut.projekt.model.Room;
import edu.zut.projekt.model.RoomListResponse;
import edu.zut.projekt.model.RoomResponse;
import edu.zut.projekt.model.UpdateRoomRequest;
import edu.zut.projekt.model.UpdateRoomStatusRequest;
import edu.zut.projekt.store.RoomStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class RoomsController implements RoomsApi {

    private final RoomStore roomStore;

    public RoomsController(RoomStore roomStore) {
        this.roomStore = roomStore;
    }

    @Override
    public ResponseEntity<RoomListResponse> roomsGet() {
        List<Room> rooms = roomStore.findAll();

        RoomListResponse response = new RoomListResponse();
        response.setResponseHeader(buildResponseHeader());
        response.setRooms(rooms);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<RoomResponse> roomsIdGet(UUID id) {
        Room room = roomStore.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "ROOM_NOT_FOUND",
                        "Wskazana sala konferencyjna nie zostala znaleziona."
                ));

        RoomResponse response = new RoomResponse();
        response.setResponseHeader(buildResponseHeader());
        response.setRoom(room);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<RoomResponse> roomsPost(CreateRoomRequest createRoomRequest) {

        RoomPayload input = createRoomRequest.getRoom();

        if (roomStore.existsByName(input.getName())) {
            throw new ConflictException(
                    "ROOM_NAME_ALREADY_EXISTS",
                    "Sala o podanej nazwie juz istnieje w systemie."
            );
        }

        Room room = new Room();
        room.setId(UUID.randomUUID());
        room.setName(input.getName());
        room.setCapacity(input.getCapacity());
        room.setLocation(input.getLocation());
        room.setFloor(input.getFloor());
        room.setStatus(input.getStatus());
        room.setHasProjector(input.getHasProjector());
        room.setHasWhiteboard(input.getHasWhiteboard());
        room.setHasDesktopComputer(input.getHasDesktopComputer());
        room.setHasSpeakers(input.getHasSpeakers());
        room.setHasVideoConferenceSystem(input.getHasVideoConferenceSystem());

        roomStore.save(room);

        RoomResponse response = new RoomResponse();
        response.setResponseHeader(buildResponseHeader());
        response.setRoom(room);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<RoomResponse> roomsIdPut(UUID id, UpdateRoomRequest updateRoomRequest) {

        Room existingRoom = roomStore.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "ROOM_NOT_FOUND",
                        "Wskazana sala konferencyjna nie zostala znaleziona."
                ));

        RoomPayload input = updateRoomRequest.getRoom();

        if (roomStore.existsByNameAndIdNot(input.getName(), id)) {
            throw new ConflictException(
                    "ROOM_NAME_ALREADY_EXISTS",
                    "Sala o podanej nazwie juz istnieje w systemie."
            );
        }

        existingRoom.setName(input.getName());
        existingRoom.setCapacity(input.getCapacity());
        existingRoom.setLocation(input.getLocation());
        existingRoom.setFloor(input.getFloor());
        existingRoom.setStatus(input.getStatus());
        existingRoom.setHasProjector(input.getHasProjector());
        existingRoom.setHasWhiteboard(input.getHasWhiteboard());
        existingRoom.setHasDesktopComputer(input.getHasDesktopComputer());
        existingRoom.setHasSpeakers(input.getHasSpeakers());
        existingRoom.setHasVideoConferenceSystem(input.getHasVideoConferenceSystem());

        roomStore.save(existingRoom);

        RoomResponse response = new RoomResponse();
        response.setResponseHeader(buildResponseHeader());
        response.setRoom(existingRoom);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<RoomResponse> roomsIdStatusPatch(UUID id, UpdateRoomStatusRequest updateRoomStatusRequest) {

        Room existingRoom = roomStore.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "ROOM_NOT_FOUND",
                        "Wskazana sala konferencyjna nie zostala znaleziona."
                ));

        existingRoom.setStatus(updateRoomStatusRequest.getStatus());

        roomStore.save(existingRoom);

        RoomResponse response = new RoomResponse();
        response.setResponseHeader(buildResponseHeader());
        response.setRoom(existingRoom);

        return ResponseEntity.ok(response);
    }

    private ResponseHeader buildResponseHeader() {
        ResponseHeader header = new ResponseHeader();
        header.setRequestId(UUID.randomUUID());
        header.setSendDate(OffsetDateTime.now());
        return header;
    }
}