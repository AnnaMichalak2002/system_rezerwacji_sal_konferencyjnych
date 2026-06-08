package edu.zut.projekt.store;

import edu.zut.projekt.model.Room;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoomStore {

    private final Map<UUID, Room> rooms = new ConcurrentHashMap<>();

    public List<Room> findAll() {
        return new ArrayList<>(rooms.values());
    }

    public Optional<Room> findById(UUID id) {
        return Optional.ofNullable(rooms.get(id));
    }

    public Room save(Room room) {
        rooms.put(room.getId(), room);
        return room;
    }

    public boolean existsByName(String name) {
        return rooms.values().stream()
                .anyMatch(room -> room.getName().equalsIgnoreCase(name));
    }

    public boolean existsByNameAndIdNot(String name, UUID id) {
        return rooms.values().stream()
                .anyMatch(room ->
                        room.getName().equalsIgnoreCase(name)
                                && !room.getId().equals(id)
                );
    }

    public void clear() {
        rooms.clear();
    }
}