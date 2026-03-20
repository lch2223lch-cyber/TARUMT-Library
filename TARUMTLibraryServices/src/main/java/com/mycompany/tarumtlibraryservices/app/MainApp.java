/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tarumtlibraryservices.app;

import com.mycompany.tarumtlibraryservices.adt.UserList;
import com.mycompany.tarumtlibraryservices.model.User;
import com.mycompany.tarumtlibraryservices.service.UserManager;
import com.mycompany.tarumtlibraryservices.service.AuthService;
import java.util.Scanner;

/**
 *
 * @author ch
 */
public class MainApp {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        UserList userList = new UserList();
        UserManager userManager = new UserManager(userList);
        AuthService authService = new AuthService(userList);

        // Check if first time setup needed
        if (userList.getSize() == 0) {
            setupFirstAdmin(sc, userList);
        }

        // Login first
        User currentUser = loginUser(sc, authService, userList);
        if (currentUser == null) {
            System.out.println("Login failed. Exiting...");
            return;
        }

        int choice = 0;

        do {
            // Display menu based on user role
            displayMenu(currentUser);

            System.out.print("Enter your choice: ");
            String input = sc.nextLine().trim();

            int maxChoice = getMaxChoice(currentUser);
            if (!input.matches("[1-" + maxChoice + "]")) {
                System.out.println("Please enter a number between 1 and " + maxChoice + ".");
                continue;
            }

            choice = Integer.parseInt(input);

            // Handle menu choice based on role
            if (!handleMenuChoice(choice, currentUser, sc, userList, userManager, authService)) {
                break;
            }

        } while (choice != getMaxChoice(currentUser)); // Exit is always the last option

        authService.logout();
        sc.close();
    }

    private static User loginUser(Scanner sc, AuthService authService, UserList userList) {
        System.out.println("\n=== TARUMT LIBRARY SERVICES - LOGIN ===");
        System.out.println("Please enter your User ID to login");

        int attempts = 0;
        while (attempts < 3) {
            System.out.print("User ID: ");
            String userId = sc.nextLine().trim();

            if (userId.isEmpty()) {
                System.out.println("User ID cannot be empty.");
                attempts++;
                continue;
            }

            User user = authService.login(userId);
            if (user != null) {
                return user;
            }

            attempts++;
            System.out.println("Login failed. Attempts remaining: " + (3 - attempts));
        }

        System.out.println("Too many failed attempts. Exiting...");
        return null;
    }

    private static void setupFirstAdmin(Scanner sc, UserList userList) {
        System.out.println("\n=== FIRST TIME SETUP ===");
        System.out.println("No users found. Creating initial administrator account.");
        System.out.println("Master Code: ADMIN2024");

        System.out.print("Enter Master Code: ");
        String masterCode = sc.nextLine().trim();

        if (masterCode.equals("ADMIN2024")) {
            System.out.print("Enter Admin User ID (A001): ");
            String id = sc.nextLine().trim();

            if (id.isEmpty() || !id.matches("A\\d{3}")) {
                System.out.println("Invalid ID format. Using A001");
                id = "A001";
            }

            System.out.print("Enter Admin Name: ");
            String name = sc.nextLine().trim();
            if (name.isEmpty()) {
                name = "System Administrator";
            }

            User admin = new User(id, name, "A");
            if (userList.addUser(admin)) {
                System.out.println("Administrator account created successfully!");
                System.out.println("Please login with User ID: " + id);
            } else {
                System.out.println("Failed to create admin account.");
            }
        } else {
            System.out.println("Invalid master code. Exiting...");
            System.exit(0);
        }
    }

    private static void displayMenu(User user) {
        System.out.println("\n=== TARUMT LIBRARY SERVICES ===");
        System.out.println("Logged in as: " + user.getName() + " (" + user.getRoleDisplayName() + ")");
        System.out.println("=".repeat(40));

        // Common menu items for all users
        System.out.println("1. View My Profile");
        System.out.println("2. Update My Profile");

        int menuNum = 3;

        // Role-specific menu items
        if (user.canViewAllUsers()) {
            System.out.println(menuNum++ + ". View All Users");
        }

        if (user.canAddUser()) {
            System.out.println(menuNum++ + ". Add New User");
        }

        if (user.canUpdateAnyUser()) {
            System.out.println(menuNum++ + ". Update Any User");
        }

        if (user.canDeleteUser()) {
            System.out.println(menuNum++ + ". Delete User");
        }

        if (user.canViewReports()) {
            System.out.println(menuNum++ + ". View Reports");
        }

        if (user.canManageBooks()) {
            System.out.println(menuNum++ + ". Manage Books");
        }

        // Logout option
        System.out.println(menuNum + ". Logout");
    }

    private static int getMaxChoice(User user) {
        int count = 2; // View Profile and Update Profile

        if (user.canViewAllUsers()) {
            count++;
        }
        if (user.canAddUser()) {
            count++;
        }
        if (user.canUpdateAnyUser()) {
            count++;
        }
        if (user.canDeleteUser()) {
            count++;
        }
        if (user.canViewReports()) {
            count++;
        }
        if (user.canManageBooks()) {
            count++;
        }

        return count + 1; // +1 for logout
    }

    private static boolean handleMenuChoice(int choice, User currentUser, Scanner sc,
            UserList userList, UserManager userManager,
            AuthService authService) {

        int menuIndex = 1;

        // Option 1: View My Profile
        if (choice == menuIndex++) {
            viewMyProfile(currentUser);
            return true;
        }

        // Option 2: Update My Profile
        if (choice == menuIndex++) {
            updateMyProfile(currentUser, sc, userList);
            return true;
        }

        // View All Users (if permitted)
        if (currentUser.canViewAllUsers() && choice == menuIndex++) {
            if (userList.isEmpty()) {
                System.out.println("No users found.");
            } else {
                userList.displayAllUsers();
            }
            return true;
        }

        // Add New User (if permitted)
        if (currentUser.canAddUser() && choice == menuIndex++) {
            addNewUser(sc, userList);
            return true;
        }

        // Update Any User (if permitted)
        if (currentUser.canUpdateAnyUser() && choice == menuIndex++) {
            updateAnyUser(sc, userList, currentUser);
            return true;
        }

        // Delete User (if permitted)
        if (currentUser.canDeleteUser() && choice == menuIndex++) {
            deleteUser(sc, userList, currentUser);
            return true;
        }

        // View Reports (if permitted)
        if (currentUser.canViewReports() && choice == menuIndex++) {
            showReportMenu(sc, userManager, userList, currentUser);
            return true;
        }

        // Manage Books (if permitted) - placeholder for future implementation
        if (currentUser.canManageBooks() && choice == menuIndex++) {
            System.out.println("Book management coming soon...");
            return true;
        }

        // Logout option
        if (choice == menuIndex) {
            System.out.println("Logging out...");
            return false;
        }

        return true;
    }

    private static void viewMyProfile(User user) {
        System.out.println("\n=== MY PROFILE ===");
        System.out.println("-".repeat(40));
        System.out.printf("%-15s: %s%n", "User ID", user.getUserId());
        System.out.printf("%-15s: %s%n", "Name", user.getName());
        System.out.printf("%-15s: %s%n", "Role", user.getRoleDisplayName());
        System.out.printf("%-15s: %s%n", "Permissions", user.getRoleDescription());
        System.out.printf("%-15s: %s%n", "Status", user.isActive() ? "Active" : "Inactive");
    }

    private static void updateMyProfile(User user, Scanner sc, UserList userList) {
        System.out.println("\n=== UPDATE MY PROFILE ===");
        System.out.println("Current Name: " + user.getName());
        System.out.print("Enter new name (press Enter to keep current): ");
        String newName = sc.nextLine().trim();

        System.out.println("\n(Note: Role changes require administrator)");

        if (!newName.isEmpty()) {
            if (userList.updateUser(user.getUserId(), newName, null)) {
                System.out.println("Profile updated successfully!");
            } else {
                System.out.println("Failed to update profile.");
            }
        } else {
            System.out.println("No changes made.");
        }
    }

    private static void addNewUser(Scanner sc, UserList userList) {
        System.out.println("\n=== ADD NEW USER ===");
        System.out.print("Enter User ID (S001 for Student, L001 for Librarian, A001 for Admin): ");
        String id = sc.nextLine().trim();

        if (id.isEmpty() || !id.matches("[SAL]\\d{3}")) {
            System.out.println("Invalid ID format. Must start with S, A, or L followed by 3 digits.");
            return;
        }

        String name;
        while (true) {
            System.out.print("Enter Name: ");
            name = sc.nextLine().trim();

            if (name.isEmpty() || !name.matches("[A-Za-z ]+")) {
                System.out.println("Name must contain alphabets only. Try again.");
            } else {
                break;
            }
        }

        System.out.print("Enter Role: (S for Student / L for Librarian / A for Admin): ");
        String role = sc.nextLine().trim().toUpperCase();

        if (role.isEmpty() || !role.matches("[SLA]")) {
            System.out.println("Role must be S (Student), L (Librarian), or A (Admin).");
            return;
        }

        // Optional fields
        System.out.print("Enter Email (optional): ");
        String email = sc.nextLine().trim();

        System.out.print("Enter Phone (optional): ");
        String phone = sc.nextLine().trim();

        User newUser;
        if (!email.isEmpty() || !phone.isEmpty()) {
            newUser = new User(id, name, role, email, phone);
        } else {
            newUser = new User(id, name, role);
        }

        if (userList.addUser(newUser)) {
            System.out.println("User added successfully.");
        } else {
            System.out.println("User ID already exists.");
        }
    }

    private static void updateAnyUser(Scanner sc, UserList userList, User currentUser) {
        System.out.println("\n=== UPDATE USER ===");
        System.out.print("Enter User ID to update: ");
        String updateId = sc.nextLine().trim();

        User user = userList.getUserById(updateId);

        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.println("\nCurrent User Details:");
        System.out.printf("%-15s: %s%n", "User ID", user.getUserId());
        System.out.printf("%-15s: %s%n", "Name", user.getName());
        System.out.printf("%-15s: %s%n", "Role", user.getRoleDisplayName());

        System.out.println("\nEnter new details (press Enter to keep current value):");

        System.out.print("Enter new name: ");
        String newName = sc.nextLine().trim();

        System.out.print("Enter new role (S/L/A): ");
        String newRole = sc.nextLine().trim().toUpperCase();

        // Validate role if provided
        if (!newRole.isEmpty() && !newRole.matches("[SLA]")) {
            System.out.println("Invalid role. Role must be S, L, or A.");
            return;
        }

        if (userList.updateUser(updateId, newName, newRole)) {
            System.out.println("User updated successfully.");
        } else {
            System.out.println("Failed to update user.");
        }
    }

    private static void deleteUser(Scanner sc, UserList userList, User currentUser) {
        System.out.println("\n=== DELETE USER ===");
        System.out.print("Enter User ID to delete: ");
        String deleteId = sc.nextLine().trim();

        User deleteUser = userList.getUserById(deleteId);
        if (deleteUser == null) {
            System.out.println("User not found.");
            return;
        }

        // Prevent deleting yourself
        if (deleteUser.getUserId().equals(currentUser.getUserId())) {
            System.out.println("You cannot delete your own account!");
            return;
        }

        System.out.println("\nUser to delete:");
        System.out.printf("%-15s: %s%n", "User ID", deleteUser.getUserId());
        System.out.printf("%-15s: %s%n", "Name", deleteUser.getName());
        System.out.printf("%-15s: %s%n", "Role", deleteUser.getRoleDisplayName());

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
    }

    private static void showReportMenu(Scanner sc, UserManager userManager, UserList userList, User currentUser) {
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
                    if (currentUser.canExportReports()) {
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
                    } else {
                        System.out.println("You don't have permission to export reports.");
                    }
                    break;

                case 7:
                    System.out.println("Returning to main menu...");
                    break;
            }

        } while (reportChoice != 7);
    }
}
