package com.mycompany.tarumtlibraryservices.adt;

import com.mycompany.tarumtlibraryservices.model.Room;
import java.util.function.Predicate;

/**
 * Custom implementation for Managing Room entities using the Team's GenericList
 * ADT. Consistent with requirements for "TARUMT Library Services" application.
 *
 * @author Kiu Khai Yan
 */
public class RoomList extends GenericList<Room> {

    public RoomList() {
        super();
    }

    public RoomList(String fileName) {
        super(fileName);
    }

    /**
     * Specific search to find a room by its ID. Demonstrates use of Predicate
     * as seen in GenericList.
     */
    public Room getRoomById(String roomId) {
        return findFirst(room -> room.getRoomId().equalsIgnoreCase(roomId));
    }

    /**
     * Filters and returns all currently available rooms.
     */
    public Room[] getAvailableRooms() {
        return findAll(Room::isAvailable, Room[]::new);
    }

    /**
     * Get all rooms as array
     */
    public Room[] getAllRooms() {
        return toArray(Room[]::new);
    }

    // ========== FILE PERSISTENCE OVERRIDES ==========
    @Override
    protected String saveElement(Room room) {
        // Save all room data including slot information
        StringBuilder sb = new StringBuilder();
        sb.append(room.getRoomId()).append("|");
        sb.append(room.getRoomName()).append(",");
        sb.append(room.getCapacity()).append(",");

        // Save slot availability status (4 slots)
        for (int i = 0; i < 4; i++) {
            sb.append(room.isSlotAvailable(i));
            if (i < 3) {
                sb.append(",");
            }
        }
        sb.append("|");

        // Save booked by user IDs (4 slots)
        for (int i = 0; i < 4; i++) {
            String bookedBy = room.getBookedBy(i);
            sb.append(bookedBy != null ? bookedBy : "");
            if (i < 3) {
                sb.append(",");
            }
        }

        return sb.toString();
    }

    @Override
    protected Room parseElement(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length < 2) {
                return null;
            }

            // Parse room basic info
            String[] basicInfo = parts[1].split(",");
            if (basicInfo.length < 2) {
                return null;
            }

            Room room = new Room(parts[0], basicInfo[0], Integer.parseInt(basicInfo[1]));

            // Parse slot availability if available
            if (basicInfo.length >= 6) { // roomName, capacity, + 4 slots
                for (int i = 0; i < 4; i++) {
                    boolean isAvailable = Boolean.parseBoolean(basicInfo[2 + i]);
                    if (!isAvailable) {
                        // Temporarily lock the slot (will be overridden if we have user ID)
                        room.lockSlot(i, "temp");
                    }
                }
            }

            // Parse booked by user IDs if available
            if (parts.length >= 3 && !parts[2].isEmpty()) {
                String[] bookedBy = parts[2].split(",");
                for (int i = 0; i < Math.min(4, bookedBy.length); i++) {
                    if (!bookedBy[i].isEmpty() && !bookedBy[i].equals("null") && !bookedBy[i].equals("temp")) {
                        // First unlock if it was temporarily locked
                        if (!room.isSlotAvailable(i)) {
                            room.unlockSlot(i);
                        }
                        // Lock with the actual user
                        room.lockSlot(i, bookedBy[i]);
                    }
                }
            }

            return room;
        } catch (Exception e) {
            System.err.println("Error parsing room data: " + e.getMessage());
            return null;
        }
    }

    // Override loadFromFile to make it public
    @Override
    public void loadFromFile() {
        super.loadFromFile();
    }

    // Override saveToFile to make it public if needed
    @Override
    public void saveToFile() {
        super.saveToFile();
    }
}
