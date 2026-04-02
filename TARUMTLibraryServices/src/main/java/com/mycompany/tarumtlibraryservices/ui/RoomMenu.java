package com.mycompany.tarumtlibraryservices.ui;

import com.mycompany.tarumtlibraryservices.service.RoomManager;
import com.mycompany.tarumtlibraryservices.model.Room;
import java.util.Scanner;

/**
 * Boundary class for Room Management (Admin).
 * Interacts with users to manage library study rooms.
 * @author Kiu Khai Yan
 */
public class RoomMenu {

    private final RoomManager roomControl;
    private final Scanner scanner = new Scanner(System.in);

    public RoomMenu(RoomManager roomControl) {
        this.roomControl = roomControl;
    }

    public void displayMenu() {
        int choice = 0;
        do {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("       ADMINISTRATOR ROOM MANAGEMENT");
            System.out.println("=".repeat(50));
            System.out.println("  1. Add New Room");
            System.out.println("  2. Remove Room");
            System.out.println("  3. Search Room by ID");
            System.out.println("  4. Display All Rooms with Details");
            System.out.println("  5. View Comprehensive Report");
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
                addNewRoom();
                break;
            case 2:
                removeRoom();
                break;
            case 3:
                searchRoom();
                break;
            case 4:
                displayAllRoomsWithDetails();
                break;
            case 5:
                roomControl.displayComprehensiveReport();
                break;
            case 0:
                System.out.println("Returning to main menu...");
                break;
            default:
                System.out.println("[ERROR] Invalid choice.");
        }
    }

    private void addNewRoom() {
        System.out.print("Enter Room ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Room Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Capacity: ");
        int capacity = Integer.parseInt(scanner.nextLine());

        if (roomControl.addRoom(id, name, capacity)) {
            System.out.println("[SUCCESS] Room added successfully.");
        } else {
            System.out.println("[ERROR] Failed to add room (ID might already exist).");
        }
    }

    private void removeRoom() {
        System.out.print("Enter Room ID to remove: ");
        String id = scanner.nextLine();
        if (roomControl.removeRoom(id)) {
            System.out.println("[SUCCESS] Room removed successfully.");
        } else {
            System.out.println("[ERROR] Room not found.");
        }
    }

    private void searchRoom() {
        System.out.print("Enter Room ID to search: ");
        String id = scanner.nextLine();
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
                
                // Count booked slots for this room
                int roomBookedSlots = 0;
                for (int i = 0; i < 4; i++) {
                    if (!room.isSlotAvailable(i)) {
                        roomBookedSlots++;
                    }
                }
                totalBookedSlots += roomBookedSlots;
                
                System.out.println("\n+--------------------------------------------------------------------+");
                System.out.printf("| ROOM: %-60s |\n", room.getRoomId() + " - " + room.getRoomName());
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
        
        // Summary
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
}