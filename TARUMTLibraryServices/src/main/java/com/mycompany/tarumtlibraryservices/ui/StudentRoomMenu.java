package com.mycompany.tarumtlibraryservices.ui;

import com.mycompany.tarumtlibraryservices.service.RoomManager;
import com.mycompany.tarumtlibraryservices.model.Room;
import com.mycompany.tarumtlibraryservices.model.User;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * Boundary class for Student Room Booking.
 * Only allows users with Student role to book and cancel rooms.
 * @author Kiu Khai Yan
 */
public class StudentRoomMenu {
    
    private final RoomManager roomManager;
    private final Scanner scanner = new Scanner(System.in);
    private final User currentUser;
    private Room[] availableRoomsCache;
    
    public StudentRoomMenu(RoomManager roomManager, User user) {
        this.roomManager = roomManager;
        this.currentUser = user;
    }
    
    public void displayMenu() {
        if (!currentUser.getRole().equals(User.ROLE_STUDENT)) {
            System.out.println("\n[ACCESS DENIED] This menu is only available for students.");
            System.out.println("Your role: " + currentUser.getRoleDisplayName());
            return;
        }
        
        int choice = 0;
        do {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("        STUDENT ROOM BOOKING MENU");
            System.out.println("=".repeat(50));
            System.out.println("  Welcome, " + currentUser.getName() + " (" + currentUser.getUserId() + ")");
            System.out.println("-".repeat(50));
            System.out.println("  1. View Available Rooms");
            System.out.println("  2. Book a Room");
            System.out.println("  3. Cancel My Booking");
            System.out.println("  4. View My Bookings");
            System.out.println("  0. Back to Main Menu");
            System.out.println("=".repeat(50));
            System.out.print("Enter choice: ");
            
            try {
                choice = Integer.parseInt(scanner.nextLine());
                processChoice(choice);
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Invalid input. Please enter a number.");
            }
        } while (choice != 0);
    }
    
    private void processChoice(int choice) {
        switch (choice) {
            case 1:
                displayAvailableRooms();
                break;
            case 2:
                performBooking();
                break;
            case 3:
                performCancellation();
                break;
            case 4:
                viewMyBookings();
                break;
            case 0:
                System.out.println("Returning to main menu...");
                break;
            default:
                System.out.println("[ERROR] Invalid choice.");
        }
    }
    
    private void displayAvailableRooms() {
        availableRoomsCache = roomManager.getAvailableRooms();
        
        if (availableRoomsCache.length == 0) {
            System.out.println("\n[INFO] Sorry, no rooms are currently available.");
            return;
        }
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("                    AVAILABLE ROOMS");
        System.out.println("=".repeat(70));
        
        String[] slots = {"09:00-11:00", "11:00-13:00", "13:00-15:00", "15:00-17:00"};
        
        for (int i = 0; i < availableRoomsCache.length; i++) {
            Room room = availableRoomsCache[i];
            
            System.out.println("\n+--------------------------------------------------------------------+");
            System.out.printf("| [%d] %-10s - %-45s |\n", (i + 1), 
                             room.getRoomId().toUpperCase(), 
                             truncate(room.getRoomName(), 45));
            System.out.printf("| Capacity: %-3d people                                             |\n", room.getCapacity());
            System.out.println("|--------------------------------------------------------------------|");
            System.out.println("| Time Slots:                                                        |");
            
            for (int j = 0; j < 4; j++) {
                if (room.isSlotAvailable(j)) {
                    System.out.printf("|   [%d] %-15s : [AVAILABLE]                                  |\n", (j+1), slots[j]);
                } else {
                    String bookedBy = room.getBookedBy(j);
                    if (currentUser.getUserId().equals(bookedBy)) {
                        System.out.printf("|   [%d] %-15s : [BOOKED BY YOU]                            |\n", (j+1), slots[j]);
                    } else {
                        System.out.printf("|   [%d] %-15s : [BOOKED]                                    |\n", (j+1), slots[j]);
                    }
                }
            }
            System.out.println("+--------------------------------------------------------------------+");
        }
    }
    
    private void performBooking() {
        availableRoomsCache = roomManager.getAvailableRooms();
        
        if (availableRoomsCache.length == 0) {
            System.out.println("\n[INFO] Sorry, no rooms are currently available for booking.");
            return;
        }
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("                    BOOK A ROOM");
        System.out.println("=".repeat(70));
        
        System.out.println("\nAvailable Rooms:");
        for (int i = 0; i < availableRoomsCache.length; i++) {
            Room room = availableRoomsCache[i];
            System.out.printf("  [%d] %s - %s (Capacity: %d)%n", 
                (i + 1), room.getRoomId().toUpperCase(), room.getRoomName(), room.getCapacity());
        }
        
        System.out.print("\nSelect room number (1-" + availableRoomsCache.length + "): ");
        try {
            int roomNumber = Integer.parseInt(scanner.nextLine()) - 1;
            
            if (roomNumber >= 0 && roomNumber < availableRoomsCache.length) {
                Room selectedRoom = availableRoomsCache[roomNumber];
                String[] slots = {"09:00-11:00", "11:00-13:00", "13:00-15:00", "15:00-17:00"};
                
                System.out.println("\n+--------------------------------------------------------------------+");
                System.out.printf("| Room: %-60s |\n", selectedRoom.getRoomId().toUpperCase() + " - " + selectedRoom.getRoomName());
                System.out.printf("| Capacity: %-58s |\n", selectedRoom.getCapacity() + " people");
                System.out.println("+--------------------------------------------------------------------+");
                
                System.out.println("\nAvailable Time Slots:");
                int availableSlotCount = 0;
                for (int i = 0; i < 4; i++) {
                    if (selectedRoom.isSlotAvailable(i)) {
                        System.out.printf("  [%d] %s%n", (i + 1), slots[i]);
                        availableSlotCount++;
                    }
                }
                
                if (availableSlotCount == 0) {
                    System.out.println("[WARNING] This room has no available slots at the moment.");
                    return;
                }
                
                System.out.print("\nSelect time slot (1-4): ");
                int slotIndex = Integer.parseInt(scanner.nextLine()) - 1;
                
                if (slotIndex >= 0 && slotIndex < 4 && selectedRoom.isSlotAvailable(slotIndex)) {
                    System.out.println("\nBooking Details:");
                    System.out.println("  Room: " + selectedRoom.getRoomId().toUpperCase() + " - " + selectedRoom.getRoomName());
                    System.out.println("  Time: " + slots[slotIndex]);
                    System.out.println("  Student ID: " + currentUser.getUserId());
                    System.out.println("  Student Name: " + currentUser.getName());
                    
                    System.out.print("\nConfirm booking? (Y/N): ");
                    String confirm = scanner.nextLine().toUpperCase();
                    
                    if (confirm.equals("Y")) {
                        selectedRoom.lockSlot(slotIndex, currentUser.getUserId());
                        System.out.println("\n[SUCCESS] Booking successful!");
                        System.out.println("  Your room has been reserved.");
                        System.out.println("  Please arrive on time.");
                        roomManager.saveData();
                    } else {
                        System.out.println("\n[CANCELLED] Booking cancelled.");
                    }
                } else {
                    System.out.println("\n[ERROR] Invalid or unavailable slot selection.");
                }
            } else {
                System.out.println("\n[ERROR] Invalid room number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid input. Please enter a number.");
        }
    }
    
    private void performCancellation() {
        Room[] allRooms = roomManager.getAllRooms();
        String[] slots = {"09:00-11:00", "11:00-13:00", "13:00-15:00", "15:00-17:00"};
        ArrayList<BookingInfo> userBookings = new ArrayList<>();
        
        for (Room room : allRooms) {
            for (int i = 0; i < 4; i++) {
                if (currentUser.getUserId().equals(room.getBookedBy(i))) {
                    userBookings.add(new BookingInfo(room, i, slots[i]));
                }
            }
        }
        
        if (userBookings.isEmpty()) {
            System.out.println("\n[INFO] You have no current bookings to cancel.");
            return;
        }
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("                 CANCEL BOOKING");
        System.out.println("=".repeat(70));
        System.out.println("\nYour Current Bookings:");
        System.out.println("+----+----------------------------------------------------------+");
        System.out.println("| No | Booking Details                                          |");
        System.out.println("+----+----------------------------------------------------------+");
        
        for (int i = 0; i < userBookings.size(); i++) {
            BookingInfo booking = userBookings.get(i);
            System.out.printf("| %2d | %s - %s (%s)%n", 
                (i + 1), 
                booking.room.getRoomId().toUpperCase(), 
                truncate(booking.room.getRoomName(), 35),
                booking.timeSlot);
        }
        System.out.println("+----+----------------------------------------------------------+");
        
        System.out.print("\nSelect booking to cancel (1-" + userBookings.size() + "): ");
        try {
            int bookingNumber = Integer.parseInt(scanner.nextLine()) - 1;
            
            if (bookingNumber >= 0 && bookingNumber < userBookings.size()) {
                BookingInfo booking = userBookings.get(bookingNumber);
                
                System.out.println("\nCancel Booking:");
                System.out.println("  Room: " + booking.room.getRoomId().toUpperCase() + " - " + booking.room.getRoomName());
                System.out.println("  Time: " + booking.timeSlot);
                
                System.out.print("\nAre you sure? (Y/N): ");
                String confirm = scanner.nextLine().toUpperCase();
                
                if (confirm.equals("Y")) {
                    booking.room.unlockSlot(booking.slotIndex);
                    System.out.println("\n[SUCCESS] Booking cancelled successfully!");
                    System.out.println("  The slot is now available for others.");
                    roomManager.saveData();
                } else {
                    System.out.println("\n[CANCELLED] Cancellation cancelled.");
                }
            } else {
                System.out.println("\n[ERROR] Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid input.");
        }
    }
    
    private void viewMyBookings() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("              YOUR CURRENT BOOKINGS");
        System.out.println("=".repeat(70));
        System.out.println("  Student: " + currentUser.getName() + " (" + currentUser.getUserId() + ")");
        System.out.println("-".repeat(70));
        
        Room[] allRooms = roomManager.getAllRooms();
        String[] slots = {"09:00-11:00", "11:00-13:00", "13:00-15:00", "15:00-17:00"};
        boolean hasBookings = false;
        
        for (Room room : allRooms) {
            for (int i = 0; i < 4; i++) {
                if (currentUser.getUserId().equals(room.getBookedBy(i))) {
                    System.out.printf("\n+--------------------------------------------------------------------+\n");
                    System.out.printf("| Room: %-60s |\n", room.getRoomId().toUpperCase() + " - " + room.getRoomName());
                    System.out.printf("| Time: %-60s |\n", slots[i]);
                    System.out.printf("| Capacity: %-57s |\n", room.getCapacity() + " people");
                    System.out.printf("+--------------------------------------------------------------------+\n");
                    hasBookings = true;
                }
            }
        }
        
        if (!hasBookings) {
            System.out.println("\n  You have no current bookings.");
            System.out.println("  Use option 2 to book a room.");
        }
    }
    
    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
    
    private static class BookingInfo {
        Room room;
        int slotIndex;
        String timeSlot;
        
        BookingInfo(Room room, int slotIndex, String timeSlot) {
            this.room = room;
            this.slotIndex = slotIndex;
            this.timeSlot = timeSlot;
        }
    }
}