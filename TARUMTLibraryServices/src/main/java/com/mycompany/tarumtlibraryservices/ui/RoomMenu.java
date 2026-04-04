package com.mycompany.tarumtlibraryservices.ui;

import com.mycompany.tarumtlibraryservices.service.RoomManager;
import com.mycompany.tarumtlibraryservices.model.Room;
import com.mycompany.tarumtlibraryservices.model.User;
import java.util.Scanner;

/**
 * Boundary class for Room Management (Admin/Librarian).
 * Interacts with users to manage library study rooms.
 * @author Kiu Khai Yan
 */
public class RoomMenu {

    private final RoomManager roomControl;
    private final Scanner scanner;
    private final User currentUser;

    public RoomMenu(RoomManager roomControl, Scanner scanner, User currentUser) {
        this.roomControl = roomControl;
        this.scanner = scanner;
        this.currentUser = currentUser;
    }

    public void displayMenu() {
        int choice = 0;
        do {
            displayMenuOptions();
            
            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (!processChoice(choice)) {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Invalid input. Please enter a number.");
            }
        } while (choice != 0);
    }
    
    private void displayMenuOptions() {
        System.out.println("\n" + "=".repeat(50));
        
        // Different title based on role
        if (currentUser.getRole().equals(User.ROLE_ADMIN)) {
            System.out.println("       ADMINISTRATOR ROOM MANAGEMENT");
        } else if (currentUser.getRole().equals(User.ROLE_LIBRARIAN)) {
            System.out.println("         LIBRARIAN ROOM MANAGEMENT");
        }
        System.out.println("=".repeat(50));
        
        if (currentUser.getRole().equals(User.ROLE_ADMIN)) {
            // Admin full menu
            System.out.println("1. Add New Room");
            System.out.println("2. Remove Room");
            System.out.println("3. Search Room by ID");
            System.out.println("4. Display All Rooms with Details");
            System.out.println("5. View Comprehensive Report");
            System.out.println("6. View All Bookings");
            System.out.println("7. Export Report to File");
            System.out.println("0. Back to Main Menu");
        } else {
            // Librarian menu (without add/remove)
            System.out.println("1. Search Room by ID");
            System.out.println("2. Display All Rooms with Details");
            System.out.println("3. View Comprehensive Report");
            System.out.println("4. View All Bookings");
            System.out.println("5. Export Report to File");
            System.out.println("0. Back to Main Menu");
        }
        System.out.println("=".repeat(50));
    }
    
    private boolean processChoice(int choice) {
        if (currentUser.getRole().equals(User.ROLE_ADMIN)) {
            // Admin choices
            switch (choice) {
                case 1:
                    addNewRoom();
                    return true;
                case 2:
                    removeRoom();
                    return true;
                case 3:
                    searchRoom();
                    return true;
                case 4:
                    displayAllRoomsWithDetails();
                    return true;
                case 5:
                    roomControl.displayComprehensiveReport();
                    return true;
                case 6:
                    roomControl.displayAllBookings();
                    return true;
                case 7:
                    roomControl.exportReportToFile();
                    return true;
                case 0:
                    System.out.println("Returning to main menu...");
                    return false;
                default:
                    System.out.println("[ERROR] Invalid choice. Please enter a number between 0 and 7.");
                    return true;
            }
        } else {
            // Librarian choices
            switch (choice) {
                case 1:
                    searchRoom();
                    return true;
                case 2:
                    displayAllRoomsWithDetails();
                    return true;
                case 3:
                    roomControl.displayComprehensiveReport();
                    return true;
                case 4:
                    roomControl.displayAllBookings();
                    return true;
                case 5:
                    roomControl.exportReportToFile();
                    return true;
                case 0:
                    System.out.println("Returning to main menu...");
                    return false;
                default:
                    System.out.println("[ERROR] Invalid choice. Please enter a number between 0 and 5.");
                    return true;
            }
        }
    }

    private void addNewRoom() {
        System.out.print("Enter Room ID (e.g., R001): ");
        String id = scanner.nextLine().trim().toUpperCase();
        
        System.out.print("Enter Room Name: ");
        String name = scanner.nextLine().trim();
        
        System.out.print("Enter Capacity: ");
        int capacity;
        try {
            capacity = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid capacity. Please enter a number.");
            return;
        }
        
        if (roomControl.addRoom(id, name, capacity)) {
            System.out.println("[SUCCESS] Room added successfully with ID: " + id);
        } else {
            System.out.println("[ERROR] Failed to add room (ID might already exist).");
        }
    }

    private void removeRoom() {
        System.out.print("Enter Room ID to remove: ");
        String id = scanner.nextLine().trim().toUpperCase();
        if (id.isEmpty()) {
            System.out.println("[ERROR] Room ID cannot be empty.");
            return;
        }
        if (roomControl.removeRoom(id)) {
            System.out.println("[SUCCESS] Room removed successfully.");
        } else {
            System.out.println("[ERROR] Room not found.");
        }
    }

    private void searchRoom() {
        System.out.print("Enter Room ID to search: ");
        String id = scanner.nextLine().trim().toUpperCase();
        if (id.isEmpty()) {
            System.out.println("[ERROR] Room ID cannot be empty.");
            return;
        }
        roomControl.findAndDisplayRoom(id);
    }

    private void displayAllRoomsWithDetails() {
        Room[] allRooms = roomControl.getAllRooms();
        String[] slots = {"09:00-11:00", "11:00-13:00", "13:00-15:00", "15:00-17:00"};
        
        if (allRooms.length == 0) {
            System.out.println("\n[INFO] No rooms registered in the system.");
            return;
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                    COMPLETE ROOM LIST");
        System.out.println("=".repeat(80));
        
        int totalRooms = allRooms.length;
        int availableRooms = 0;
        int bookedRooms = 0;
        int totalBookedSlots = 0;
        int totalSlots = totalRooms * 4;
        
        for (Room room : allRooms) {
            if (room != null) {
                if (room.isAvailable()) {
                    availableRooms++;
                } else {
                    bookedRooms++;
                }
                
                int roomBookedSlots = 0;
                for (int i = 0; i < 4; i++) {
                    if (!room.isSlotAvailable(i)) {
                        roomBookedSlots++;
                    }
                }
                totalBookedSlots += roomBookedSlots;
                
                System.out.println("\n+--------------------------------------------------------------------+");
                System.out.printf("| ROOM: %-60s |\n", room.getRoomId().toUpperCase() + " - " + room.getRoomName());
                System.out.printf("| CAPACITY: %-58s |\n", room.getCapacity() + " people");
                System.out.println("|--------------------------------------------------------------------|");
                System.out.println("| TIME SLOTS:                                                        |");
                
                for (int i = 0; i < 4; i++) {
                    if (room.isSlotAvailable(i)) {
                        System.out.printf("|   [%d] %-15s : [AVAILABLE]                                  |\n", (i+1), slots[i]);
                    } else {
                        String bookedBy = room.getBookedBy(i);
                        if (bookedBy != null && !bookedBy.isEmpty()) {
                            System.out.printf("|   [%d] %-15s : [BOOKED BY] %-30s |\n", (i+1), slots[i], bookedBy);
                        } else {
                            System.out.printf("|   [%d] %-15s : [BOOKED]                                    |\n", (i+1), slots[i]);
                        }
                    }
                }
                System.out.printf("| BOOKED SLOTS: %d/4                                                |\n", roomBookedSlots);
                System.out.println("+--------------------------------------------------------------------+");
            }
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                          SUMMARY REPORT");
        System.out.println("=".repeat(80));
        System.out.printf("  %-30s : %d%n", "Total Rooms Managed", totalRooms);
        System.out.printf("  %-30s : %d%n", "Rooms with Available Slots", availableRooms);
        System.out.printf("  %-30s : %d%n", "Fully Booked Rooms", bookedRooms);
        System.out.printf("  %-30s : %d / %d%n", "Total Booked Slots", totalBookedSlots, totalSlots);
        System.out.printf("  %-30s : %.2f%%%n", "Overall Occupancy Rate", 
                         (totalSlots > 0 ? ((double) totalBookedSlots / totalSlots) * 100 : 0));
        System.out.println("=".repeat(80));
        System.out.println("  Generated on: " + java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("=".repeat(80));
    }
    
    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
}