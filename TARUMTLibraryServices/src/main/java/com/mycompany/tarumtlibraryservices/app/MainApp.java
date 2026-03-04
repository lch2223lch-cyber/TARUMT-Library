/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tarumtlibraryservices.app;

import com.mycompany.tarumtlibraryservices.adt.UserList;
import com.mycompany.tarumtlibraryservices.model.User;
import com.mycompany.tarumtlibraryservices.service.UserManager;
import java.util.Scanner;

/**
 *
 * @author ch
 */

public class MainApp {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        UserList userList = new UserList();
        UserManager userManager = new UserManager(userList); // Create UserManager instance
        int choice = 0;

        do {
            System.out.println("\n=== User Management System ===");
            System.out.println("1. Add User");
            System.out.println("2. View All Users");
            System.out.println("3. Search User");
            System.out.println("4. Update User");
            System.out.println("5. Delete User");
            System.out.println("6. Reports"); // Changed from Exit to Reports
            System.out.println("7. Exit"); // New Exit option
            
            System.out.print("Enter your choice: ");
            String input = sc.nextLine().trim();
            
            if (!input.matches("[1-7]")) { // Updated to accept 1-7
                System.out.println("Please enter a number between 1 and 7.");
                continue;
            }

            choice = Integer.parseInt(input);

            switch (choice) {

                case 1: // CREATE
                    System.out.print("Enter User ID (S001 / A001 / L001): "); // Updated to include L
                    String id = sc.nextLine().trim();

                    if (id.isEmpty() || !id.matches("[SAL]\\d{3}")) { // Updated to include L
                        System.out.println("Invalid ID format. Must start with S, A, or L followed by 3 digits.");
                        break;
                    }

                    String name;
                    while (true) {
                        System.out.print("Enter Name: ");
                        name = sc.nextLine().trim();

                        if (name.isEmpty() || !name.matches("[A-Za-z ]+")) {
                            System.out.println("Name must contain alphabets only. Try again.");
                        } else {
                            break; // valid input, exit loop
                        }
                    }

                    System.out.print("Enter Role: (S for Student / L for Librarian / A for Admin): "); // Updated description
                    String role = sc.nextLine().trim();
                    
                    role = role.toUpperCase();

                    if (role.isEmpty() || !role.matches("[SLA]")) {
                        System.out.println("Role must be S (Student), L (Librarian), or A (Admin).");
                        break;
                    }

                    if (userList.addUser(new User(id, name, role))) {
                        System.out.println("User added successfully.");
                    } else {
                        System.out.println("User ID already exists.");
                    }
                    break;

                case 2: // READ ALL
                    if (userList.isEmpty()) {
                        System.out.println("No users found.");
                    } else {
                        userList.displayAllUsers();
                    }
                    break;

                case 3: // SEARCH
                    System.out.print("Enter User ID to search: ");
                    String searchId = sc.nextLine().trim();

                    if (searchId.isEmpty()) {
                        System.out.println("User ID cannot be empty.");
                        break;
                    }

                    User found = userList.getUserById(searchId);
                    if (found != null) {
                        System.out.println("\nUser Found:");
                        System.out.println("-".repeat(50));
                        System.out.printf("%-15s: %s%n", "User ID", found.getUserId());
                        System.out.printf("%-15s: %s%n", "Name", found.getName());
                        System.out.printf("%-15s: %s (%s)%n", "Role", found.getRoleName(), found.getRole());
                    } else {
                        System.out.println("User not found.");
                    }
                    break;

                case 4: // UPDATE
                    System.out.print("Enter User ID to update: ");
                    String updateId = sc.nextLine().trim();

                    User user = userList.getUserById(updateId);

                    if (user == null) {
                        System.out.println("User not found.");
                        break;
                    }

                    System.out.println("\nCurrent User Details:");
                    System.out.printf("%-15s: %s%n", "Name", user.getName());
                    System.out.printf("%-15s: %s%n", "Role", user.getRoleName());
                    
                    System.out.println("\nEnter new details (press Enter to keep current value):");
                    
                    System.out.print("Enter new name: ");
                    String newName = sc.nextLine().trim();

                    System.out.print("Enter new role (S/L/A): ");
                    String newRole = sc.nextLine().trim().toUpperCase();

                    // Use the updateUser method instead of directly setting
                    if (userList.updateUser(updateId, newName, newRole)) {
                        System.out.println("User updated successfully.");
                    } else {
                        System.out.println("Failed to update user.");
                    }
                    break;

                case 5: // DELETE
                    System.out.print("Enter User ID to delete: ");
                    String deleteId = sc.nextLine().trim();

                    User deleteUser = userList.getUserById(deleteId);
                    if (deleteUser == null) {
                        System.out.println("User not found.");
                        break;
                    }
                    
                    System.out.println("\nUser to delete:");
                    System.out.printf("%-15s: %s%n", "User ID", deleteUser.getUserId());
                    System.out.printf("%-15s: %s%n", "Name", deleteUser.getName());
                    System.out.printf("%-15s: %s%n", "Role", deleteUser.getRoleName());

                    System.out.print("\nConfirm delete? (Y/N): ");
                    String confirm = sc.nextLine();

                    if (confirm.equalsIgnoreCase("Y")) {
                        if (userList.removeUserById(deleteId)) {
                            System.out.println("User deleted successfully.");
                        } else {
                            System.out.println("Failed to delete user.");
                        }
                    } else {
                        System.out.println("Delete cancelled.");
                    }
                    break;

                case 6: // REPORTS - New menu option
                    showReportMenu(sc, userManager, userList);
                    break;

                case 7: // EXIT - Updated from 6 to 7
                    System.out.println("Exiting system...");
                    break;

                default:
                    System.out.println("Invalid choice. Enter 1–7.");
            }

        } while (choice != 7); // Changed from 6 to 7

        sc.close();
    }
    
    //reports submenu
    private static void showReportMenu(Scanner sc, UserManager userManager, UserList userList) {
        int reportChoice = 0;
        
        do {
            System.out.println("\n=== REPORTS MENU ===");
            System.out.println("1. Full User Report");
            System.out.println("2. Student Report");
            System.out.println("3. Librarian Report");
            System.out.println("4. Admin Report");
            System.out.println("5. Search Users by Name");
            System.out.println("6. Export Report to File");
            System.out.println("7. Back to Main Menu");
            
            System.out.print("Enter your choice: ");
            String input = sc.nextLine().trim();
            
            if (!input.matches("[1-7]")) {
                System.out.println("Please enter a number between 1 and 7.");
                continue;
            }
            
            reportChoice = Integer.parseInt(input);
            
            switch (reportChoice) {
                case 1:
                    userManager.generateUserReport();
                    break;
                    
                case 2:
                    userManager.generateRoleBasedReport("S");
                    break;
                    
                case 3:
                    userManager.generateRoleBasedReport("L");
                    break;
                    
                case 4:
                    userManager.generateRoleBasedReport("A");
                    break;
                    
                case 5:
                    System.out.print("Enter name to search: ");
                    String keyword = sc.nextLine().trim();
                    if (!keyword.isEmpty()) {
                        userManager.generateNameSearchReport(keyword);
                    } else {
                        System.out.println("Search keyword cannot be empty.");
                    }
                    break;
                    
                case 6:
                    System.out.println("\nExport Options:");
                    System.out.println("1. Export Full Report");
                    System.out.println("2. Export Student Report");
                    System.out.println("3. Export Librarian Report");
                    System.out.println("4. Export Admin Report");
                    System.out.print("Choose report type to export: ");
                    
                    String exportInput = sc.nextLine().trim();
                    if (exportInput.matches("[1-4]")) {
                        String reportType = "";
                        switch (exportInput) {
                            case "1":
                                reportType = "Full Report";
                                break;
                            case "2":
                                reportType = "Student Report";
                                break;
                            case "3":
                                reportType = "Librarian Report";
                                break;
                            case "4":
                                reportType = "Admin Report";
                                break;
                        }
                        userManager.exportReportToFile(reportType);
                    } else {
                        System.out.println("Invalid option.");
                    }
                    break;
                    
                case 7:
                    System.out.println("Returning to main menu...");
                    break;
            }
            
        } while (reportChoice != 7);
    }
}