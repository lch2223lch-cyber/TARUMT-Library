package com.mycompany.tarumtlibraryservices.app;

import com.mycompany.tarumtlibraryservices.adt.RoomList;
import com.mycompany.tarumtlibraryservices.adt.UserList;
import com.mycompany.tarumtlibraryservices.service.RoomManager;
import com.mycompany.tarumtlibraryservices.model.Room;
import com.mycompany.tarumtlibraryservices.model.User;
import com.mycompany.tarumtlibraryservices.ui.RoomMenu;
import com.mycompany.tarumtlibraryservices.ui.StudentRoomMenu;
import java.io.File;
import java.util.Scanner;

/**
 * Test class for Room Management System with User Authentication
 * @author Kiu Khai Yan
 */
public class RoomTest {
    
    private static UserList userList;
    private static User currentUser;
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=".repeat(40));
        System.out.println("   TARUMT LIBRARY SERVICES");
        System.out.println("   Room Management System");
        System.out.println("=".repeat(40));
        
        // Initialize user list with filename
        userList = new UserList("users.txt");
        
        // Load users from file - ONLY LOAD ONCE
        userList.loadFromFile();
        
        // Display user loading status
        System.out.println("[SYSTEM] User system initialized.");
        System.out.println("[SYSTEM] Total users loaded: " + userList.getSize());
        
        // Login
        if (!login(scanner)) {
            System.out.println("\n[ERROR] Login failed. Exiting program.");
            return;
        }
        
        // Display welcome message
        System.out.println("\n[WELCOME] " + currentUser.getName() + "!");
        System.out.println("[ROLE] " + currentUser.getRoleDisplayName());
        
        // Initialize room system
        String filename = "rooms.txt";
        File file = new File(filename);
        RoomList roomList = new RoomList(filename);
        RoomManager roomManager = new RoomManager(roomList);
        
        // Display room status
        if (file.exists()) {
            System.out.println("[SYSTEM] Loaded " + roomList.getSize() + " rooms from " + filename);
        } else {
            System.out.println("[SYSTEM] New room data file will be created: " + filename);
        }
        
        roomManager.fixRoomNamesCapitalization();
        
        int choice = 0;
        do {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("              MAIN MENU");
            System.out.println("=".repeat(50));
            System.out.println("  1. Administrator Room Management");
            
            // Only show student menu for students
            if (currentUser.getRole().equals(User.ROLE_STUDENT)) {
                System.out.println("  2. Student Room Booking");
            } else {
                System.out.println("  2. Student Room Booking (Student only)");
            }
            
            System.out.println("  0. Exit & Save");
            System.out.println("=".repeat(50));
            System.out.print("Enter your choice: ");
            
            try {
                choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        // Admin menu - only allow admin or librarian
                        if (currentUser.canManageBooks()) {
                            System.out.println("\n--- Entering Administrator Mode ---");
                            RoomMenu adminMenu = new RoomMenu(roomManager);
                            adminMenu.displayMenu();
                        } else {
                            System.out.println("\n[ACCESS DENIED] Only Administrators and Librarians can access this menu.");
                            System.out.println("Your role: " + currentUser.getRoleDisplayName());
                        }
                        break;
                        
                    case 2:
                        // Student menu - only allow students
                        if (currentUser.getRole().equals(User.ROLE_STUDENT)) {
                            StudentRoomMenu studentMenu = new StudentRoomMenu(roomManager, currentUser);
                            studentMenu.displayMenu();
                        } else {
                            System.out.println("\n[ACCESS DENIED] Room booking is only available for students.");
                            System.out.println("Your role: " + currentUser.getRoleDisplayName());
                            System.out.println("Please login with a student account.");
                        }
                        break;
                        
                    case 3:
                        System.out.println("\n--- Room Statistics ---");
                        displayRoomStatistics(roomManager);
                        break;
                        
                    case 0:
                        System.out.println("\n--- Saving Data ---");
                        roomManager.saveData();
                        System.out.println("[SUCCESS] Data saved successfully");
                        System.out.println("[SUCCESS] Thank you for using TARUMT Library Services!");
                        System.out.println("[SUCCESS] Goodbye, " + currentUser.getName() + "!");
                        break;
                        
                    default:
                        System.out.println("[ERROR] Invalid choice. Please enter 0, 1, 2, or 3.");
                }
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Invalid input. Please enter a number.");
            } catch (Exception e) {
                System.out.println("[ERROR] An error occurred: " + e.getMessage());
            }
        } while (choice != 0);
        
        scanner.close();
    }
    
    private static boolean login(Scanner scanner) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("              USER LOGIN");
        System.out.println("=".repeat(50));
        
        int attempts = 0;
        final int MAX_ATTEMPTS = 3;
        
        while (attempts < MAX_ATTEMPTS) {
            System.out.print("\nUser ID: ");
            String userId = scanner.nextLine().trim();
            
            // Find user by ID
            User user = userList.getUserById(userId);
            
            if (user != null) {
                if (user.isActive()) {
                    currentUser = user;
                    return true;
                } else {
                    System.out.println("[ERROR] Account is deactivated. Please contact administrator.");
                    return false;
                }
            } else {
                attempts++;
                System.out.println("[ERROR] User ID not found. Attempts remaining: " + (MAX_ATTEMPTS - attempts));
                
                // Show available users for testing (remove in production)
                if (attempts < MAX_ATTEMPTS) {
                    System.out.println("\nAvailable users:");
                    // Use a Set to avoid duplicates when displaying
                    java.util.HashSet<String> displayedUsers = new java.util.HashSet<>();
                    userList.forEach(u -> {
                        if (u.isActive() && !displayedUsers.contains(u.getUserId())) {
                            displayedUsers.add(u.getUserId());
                            System.out.println("   * " + u.getUserId() + " (" + u.getRoleDisplayName() + ")");
                        }
                    });
                }
            }
        }
        
        return false;
    }
    
    private static void displayRoomStatistics(RoomManager roomManager) {
        int totalRooms = roomManager.getAllRooms().length;
        int availableRooms = roomManager.getAvailableRooms().length;
        int totalSlots = totalRooms * 4;
        int bookedSlots = 0;
        
        Room[] allRooms = roomManager.getAllRooms();
        for (Room room : allRooms) {
            for (int i = 0; i < 4; i++) {
                if (!room.isSlotAvailable(i)) {
                    bookedSlots++;
                }
            }
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              ROOM STATISTICS");
        System.out.println("=".repeat(60));
        System.out.printf("  %-25s : %d%n", "Total Rooms", totalRooms);
        System.out.printf("  %-25s : %d%n", "Total Time Slots", totalSlots);
        System.out.printf("  %-25s : %d%n", "Booked Slots", bookedSlots);
        System.out.printf("  %-25s : %d%n", "Available Slots", (totalSlots - bookedSlots));
        System.out.printf("  %-25s : %d%n", "Rooms with Availability", availableRooms);
        
        if (totalSlots > 0) {
            double occupancyRate = ((double) bookedSlots / totalSlots) * 100;
            System.out.printf("  %-25s : %.2f%%%n", "Overall Occupancy Rate", occupancyRate);
        }
        System.out.println("=".repeat(60));
        System.out.println("  Generated on: " + java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("=".repeat(60));
    }
}