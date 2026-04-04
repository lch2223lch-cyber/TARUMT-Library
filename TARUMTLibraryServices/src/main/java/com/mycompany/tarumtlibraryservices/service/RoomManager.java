package com.mycompany.tarumtlibraryservices.service;

import com.mycompany.tarumtlibraryservices.adt.RoomList;
import com.mycompany.tarumtlibraryservices.model.Room;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Control class for Room Management.
 * Implements business logic and orchestrates data flow between UI and ADT.
 * @author Kiu Khai Yan
 */
public class RoomManager {

    private RoomList roomList;

    public RoomManager(RoomList roomList) {
        this.roomList = roomList;
    }

    // ========== BASIC CRUD OPERATIONS ==========
    
    public boolean addRoom(String id, String name, int capacity) {
        String standardId = id.trim().toUpperCase();
        String standardName = capitalizeWords(name.trim());
        
        if (roomList.getRoomById(standardId) != null) {
            return false;
        }
        return roomList.add(new Room(standardId, standardName, capacity));
    }

    public boolean removeRoom(String id) {
        if (id == null || id.trim().isEmpty()) {
            System.out.println("[ERROR] Room ID cannot be empty.");
            return false;
        }
        String standardId = id.trim().toUpperCase();
        return roomList.removeIf(room -> room.getRoomId().equalsIgnoreCase(standardId));
    }

    public void findAndDisplayRoom(String id) {
        if (id == null || id.trim().isEmpty()) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("                   SEARCH RESULT");
            System.out.println("=".repeat(60));
            System.out.println("  Please enter a valid Room ID.");
            System.out.println("=".repeat(60));
            return;
        }
        
        String standardId = id.trim().toUpperCase();
        Room room = roomList.getRoomById(standardId);
        if (room != null) {
            displayRoomReport(room);
        } else {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("                   SEARCH RESULT");
            System.out.println("=".repeat(60));
            System.out.println("  Room ID " + standardId + " not found in the system.");
            System.out.println("=".repeat(60));
        }
    }


    // ========== REPORT METHODS ==========
    
    private void displayRoomReport(Room room) {
        String[] slots = {"09:00-11:00", "11:00-13:00", "13:00-15:00", "15:00-17:00"};
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("                    ROOM DETAILS REPORT");
        System.out.println("=".repeat(70));
        
        System.out.println("\n  [ROOM INFORMATION]");
        System.out.println("  " + "-".repeat(50));
        System.out.printf("  %-20s : %s%n", "Room ID", room.getRoomId().toUpperCase());
        System.out.printf("  %-20s : %s%n", "Room Name", room.getRoomName());
        System.out.printf("  %-20s : %d people%n", "Capacity", room.getCapacity());
        System.out.printf("  %-20s : %s%n", "Overall Status", room.isAvailable() ? "AVAILABLE" : "FULLY BOOKED");
        
        System.out.println("\n  [TIME SLOT AVAILABILITY]");
        System.out.println("  " + "-".repeat(50));
        System.out.printf("  %-20s %-15s %s%n", "Slot", "Time Period", "Status");
        System.out.println("  " + "-".repeat(50));
        
        int bookedSlots = 0;
        for (int i = 0; i < 4; i++) {
            String status;
            if (room.isSlotAvailable(i)) {
                status = "[AVAILABLE]";
            } else {
                String bookedBy = room.getBookedBy(i);
                status = "[BOOKED BY] " + (bookedBy != null ? bookedBy : "Unknown");
                bookedSlots++;
            }
            System.out.printf("  Slot %-13d %-15s %s%n", (i+1), slots[i], status);
        }
        
        System.out.println("\n  [BOOKING SUMMARY]");
        System.out.println("  " + "-".repeat(50));
        System.out.printf("  %-20s : %d / 4 slots%n", "Booked Slots", bookedSlots);
        System.out.printf("  %-20s : %d slots%n", "Available Slots", (4 - bookedSlots));
        System.out.printf("  %-20s : %.1f%%%n", "Occupancy Rate", (bookedSlots / 4.0) * 100);
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("  Generated on: " + LocalDateTime.now().format(dtf));
        System.out.println("=".repeat(70));
    }

    public void displayComprehensiveReport() {
        int totalRooms = roomList.getSize();
        int totalSlots = totalRooms * 4;
        int totalBookings = 0;
        int availableRooms = 0;
        
        Room[] allRooms = getAllRooms();
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                    COMPREHENSIVE ROOM REPORT");
        System.out.println("=".repeat(80));
        
        System.out.println("\n  [1] SUMMARY STATISTICS");
        System.out.println("  " + "-".repeat(70));
        
        for (Room room : allRooms) {
            if (room.isAvailable()) {
                availableRooms++;
            }
            for (int i = 0; i < 4; i++) {
                if (!room.isSlotAvailable(i)) {
                    totalBookings++;
                }
            }
        }
        
        double occupancyRate = totalSlots > 0 ? (totalBookings * 100.0 / totalSlots) : 0;
        
        System.out.printf("  %-25s : %4d%n", "Total Rooms", totalRooms);
        System.out.printf("  %-25s : %4d%n", "Total Time Slots", totalSlots);
        System.out.printf("  %-25s : %4d (%5.1f%%)%n", "Booked Slots", totalBookings, occupancyRate);
        System.out.printf("  %-25s : %4d (%5.1f%%)%n", "Available Slots", (totalSlots - totalBookings), (100 - occupancyRate));
        System.out.printf("  %-25s : %4d%n", "Rooms with Availability", availableRooms);
        System.out.printf("  %-25s : %4d%n", "Fully Booked Rooms", (totalRooms - availableRooms));
        
        System.out.println("\n  [2] ROOM DETAILS");
        System.out.println("  " + "-".repeat(70));
        
        String[] slots = {"09:00-11:00", "11:00-13:00", "13:00-15:00", "15:00-17:00"};
        
        for (int idx = 0; idx < allRooms.length; idx++) {
            Room room = allRooms[idx];
            int roomBookings = 0;
            for (int i = 0; i < 4; i++) {
                if (!room.isSlotAvailable(i)) {
                    roomBookings++;
                }
            }
            
            System.out.println("\n  +--------------------------------------------------------------------+");
            System.out.printf("  | Room %-2d : %-10s - %-45s |\n", (idx + 1), 
                             room.getRoomId().toUpperCase(), 
                             truncate(room.getRoomName(), 45));
            System.out.printf("  | Capacity : %-3d people  |  Booked Slots : %d/4  (%.0f%%)%n", 
                             room.getCapacity(), roomBookings, (roomBookings * 100.0 / 4));
            System.out.println("  |--------------------------------------------------------------------|");
            
            System.out.print("  | Available : ");
            boolean hasAvailable = false;
            for (int i = 0; i < 4; i++) {
                if (room.isSlotAvailable(i)) {
                    System.out.printf("[%s] ", slots[i]);
                    hasAvailable = true;
                }
            }
            if (!hasAvailable) {
                System.out.print("NONE");
            }
            System.out.println();
            
            if (roomBookings > 0) {
                System.out.print("  | Booked by  : ");
                boolean first = true;
                for (int i = 0; i < 4; i++) {
                    if (!room.isSlotAvailable(i)) {
                        String bookedBy = room.getBookedBy(i);
                        if (!first) System.out.print(", ");
                        System.out.printf("%s [%s]", slots[i], (bookedBy != null ? bookedBy : "?"));
                        first = false;
                    }
                }
                System.out.println();
            }
            
            System.out.println("  +--------------------------------------------------------------------+");
        }
        
        System.out.println("\n  [3] RECOMMENDATIONS");
        System.out.println("  " + "-".repeat(70));
        
        if (totalBookings == 0) {
            System.out.println("  -> No bookings yet. Encourage students to use the facilities.");
        } else if (occupancyRate < 30) {
            System.out.printf("  -> Low occupancy rate (%.1f%%). Consider promoting room booking services.%n", occupancyRate);
        } else if (occupancyRate > 70) {
            System.out.printf("  -> High demand detected (%.1f%% occupancy). Consider adding more study rooms.%n", occupancyRate);
        } else {
            System.out.printf("  -> Moderate occupancy rate (%.1f%%). Good usage of facilities.%n", occupancyRate);
        }
        
        if (availableRooms == 0) {
            System.out.println("  -> All rooms are fully booked. Suggest students to book in advance.");
        } else if (availableRooms == totalRooms) {
            System.out.println("  -> All rooms have availability. Good time to promote room usage.");
        } else {
            System.out.printf("  -> %d room(s) have available slots. Students can still book.%n", availableRooms);
        }
        
        int[] slotBookings = new int[4];
        for (Room room : allRooms) {
            for (int i = 0; i < 4; i++) {
                if (!room.isSlotAvailable(i)) {
                    slotBookings[i]++;
                }
            }
        }
        
        int peakSlot = 0;
        for (int i = 1; i < 4; i++) {
            if (slotBookings[i] > slotBookings[peakSlot]) {
                peakSlot = i;
            }
        }
        
        if (slotBookings[peakSlot] > 0) {
            System.out.printf("  -> Peak booking time : %s (%d rooms booked)%n", 
                             slots[peakSlot], slotBookings[peakSlot]);
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("  Generated on: " + LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("=".repeat(80));
    }
    
    // ========== EXPORT REPORT TO FILE ==========
    
    public void exportReportToFile() {
        String filename = "RoomReport_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=".repeat(80));
            writer.println("                    COMPREHENSIVE ROOM REPORT");
            writer.println("=".repeat(80));
            writer.println("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println();
            
            // Summary Statistics
            int totalRooms = roomList.getSize();
            int totalSlots = totalRooms * 4;
            int totalBookings = 0;
            int availableRooms = 0;
            
            Room[] allRooms = getAllRooms();
            for (Room room : allRooms) {
                if (room.isAvailable()) {
                    availableRooms++;
                }
                for (int i = 0; i < 4; i++) {
                    if (!room.isSlotAvailable(i)) {
                        totalBookings++;
                    }
                }
            }
            
            double occupancyRate = totalSlots > 0 ? (totalBookings * 100.0 / totalSlots) : 0;
            
            writer.println("[1] SUMMARY STATISTICS");
            writer.println("-".repeat(50));
            writer.printf("Total Rooms        : %d%n", totalRooms);
            writer.printf("Total Time Slots   : %d%n", totalSlots);
            writer.printf("Booked Slots       : %d (%.1f%%)%n", totalBookings, occupancyRate);
            writer.printf("Available Slots    : %d (%.1f%%)%n", (totalSlots - totalBookings), (100 - occupancyRate));
            writer.printf("Rooms with Availability : %d%n", availableRooms);
            writer.printf("Fully Booked Rooms : %d%n", (totalRooms - availableRooms));
            
            writer.println("\n[2] ROOM DETAILS");
            writer.println("-".repeat(50));
            
            String[] slots = {"09:00-11:00", "11:00-13:00", "13:00-15:00", "15:00-17:00"};
            for (Room room : allRooms) {
                writer.printf("%nRoom: %s - %s%n", room.getRoomId().toUpperCase(), room.getRoomName());
                writer.printf("Capacity: %d people%n", room.getCapacity());
                int roomBookings = 0;
                for (int i = 0; i < 4; i++) {
                    if (room.isSlotAvailable(i)) {
                        writer.printf("  %s: AVAILABLE%n", slots[i]);
                    } else {
                        String bookedBy = room.getBookedBy(i);
                        writer.printf("  %s: BOOKED BY %s%n", slots[i], bookedBy != null ? bookedBy : "Unknown");
                        roomBookings++;
                    }
                }
                writer.printf("Booked Slots: %d/4 (%.0f%%)%n", roomBookings, (roomBookings * 100.0 / 4));
            }
            
            writer.println("\n[3] RECOMMENDATIONS");
            writer.println("-".repeat(50));
            
            if (totalBookings == 0) {
                writer.println("-> No bookings yet. Encourage students to use the facilities.");
            } else if (occupancyRate < 30) {
                writer.printf("-> Low occupancy rate (%.1f%%). Consider promoting room booking services.%n", occupancyRate);
            } else if (occupancyRate > 70) {
                writer.printf("-> High demand detected (%.1f%% occupancy). Consider adding more study rooms.%n", occupancyRate);
            } else {
                writer.printf("-> Moderate occupancy rate (%.1f%%). Good usage of facilities.%n", occupancyRate);
            }
            
            writer.println("\n" + "=".repeat(80));
            writer.println("END OF REPORT");
            
            System.out.println("[SUCCESS] Report exported to: " + filename);
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to export report: " + e.getMessage());
        }
    }
    
    /**
     * Display all bookings across all rooms (for Librarian/Admin)
     */
    public void displayAllBookings() {
        Room[] allRooms = getAllRooms();
        String[] slots = {"09:00-11:00", "11:00-13:00", "13:00-15:00", "15:00-17:00"};
        boolean hasBookings = false;
        int totalBookings = 0;

        System.out.println("\n" + "=".repeat(80));
        System.out.println("                    ALL ROOM BOOKINGS");
        System.out.println("=".repeat(80));

        for (Room room : allRooms) {
            boolean roomHasBookings = false;

            for (int i = 0; i < 4; i++) {
                if (!room.isSlotAvailable(i)) {
                    if (!roomHasBookings) {
                        System.out.println("\n+--------------------------------------------------------------------+");
                        System.out.printf("| Room: %-60s |\n", room.getRoomId().toUpperCase() + " - " + room.getRoomName());
                        System.out.println("|--------------------------------------------------------------------|");
                        roomHasBookings = true;
                    }
                    String bookedBy = room.getBookedBy(i);
                    System.out.printf("| %-15s : BOOKED BY: %-30s |\n", slots[i], bookedBy != null ? bookedBy : "Unknown");
                    hasBookings = true;
                    totalBookings++;
                }
            }
            if (roomHasBookings) {
                System.out.println("+--------------------------------------------------------------------+");
            }
        }

        if (!hasBookings) {
            System.out.println("\n  No bookings found.");
        } else {
            System.out.println("\n  Total Bookings: " + totalBookings);
        }
        System.out.println("=".repeat(80));
    }

    /**
     * Cancel any booking (for Librarian/Admin)
     */
    public boolean cancelAnyBooking(String roomId, int slotIndex) {
        Room room = roomList.getRoomById(roomId);
        if (room != null && !room.isSlotAvailable(slotIndex)) {
            String bookedBy = room.getBookedBy(slotIndex);
            room.unlockSlot(slotIndex);
            saveData();
            System.out.println("[SUCCESS] Booking cancelled for user: " + bookedBy);
            return true;
        }
        System.out.println("[ERROR] Booking not found or slot already available.");
        return false;
    }

    /**
     * Cancel any booking by Book ID and User ID (alternative method)
     */
    public boolean cancelAnyBookingByDetails(String roomId, String userId) {
        Room room = roomList.getRoomById(roomId);
        if (room == null) {
            System.out.println("[ERROR] Room not found.");
            return false;
        }

        for (int i = 0; i < 4; i++) {
            if (!room.isSlotAvailable(i) && userId.equals(room.getBookedBy(i))) {
                room.unlockSlot(i);
                saveData();
                System.out.println("[SUCCESS] Booking cancelled for room: " + roomId + " for user: " + userId);
                return true;
            }
        }
        System.out.println("[ERROR] No booking found for user " + userId + " in room " + roomId);
        return false;
    }
    // ========== HELPER METHODS ==========
    
    private String capitalizeWords(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        String[] words = text.split(" ");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1).toLowerCase())
                      .append(" ");
            }
        }
        
        return result.toString().trim();
    }
    
    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
    
    // ========== METHODS FOR STUDENT MENU ==========
    
    public Room[] getAvailableRooms() {
        return roomList.getAvailableRooms();
    }
    
    public Room getRoomById(String roomId) {
        if (roomId == null || roomId.trim().isEmpty()) {
            return null;
        }
        String standardId = roomId.trim().toUpperCase();
        return roomList.getRoomById(standardId);
    }
    
    public Room getRoomByIndex(int index) {
        return roomList.get(index);
    }
    
    public Room[] getAllRooms() {
        return roomList.getAllRooms();
    }
    
    public void saveData() {
        roomList.saveToFile();
    }
}