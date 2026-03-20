package com.mycompany.tarumtlibraryservices.app;

import com.mycompany.tarumtlibraryservices.adt.UserList;
import com.mycompany.tarumtlibraryservices.model.User;
import com.mycompany.tarumtlibraryservices.service.UserManager;
import com.mycompany.tarumtlibraryservices.service.AuthService;
import java.util.Scanner;
import java.io.*;

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

        // Check if there's at least one admin user
        if (!hasAdminUser(userList)) {
            System.out.println("\n⚠️  No admin user found in the system!");
            System.out.println("You need to create an administrator account first.");

            // Offer to clear existing data or create admin
            showSetupMenu(sc, userList);
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

    /**
     * Check if there's at least one admin user in the system
     */
    private static boolean hasAdminUser(UserList userList) {
        User[] allUsers = userList.getAllUsers();
        for (User user : allUsers) {
            if (user.getRole().equals("A")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Show setup menu when no admin exists
     */
    private static void showSetupMenu(Scanner sc, UserList userList) {
        while (true) {
            System.out.println("\n=== SYSTEM SETUP ===");
            System.out.println("1. Create New Administrator Account");
            System.out.println("2. Clear All Users and Start Fresh");
            System.out.println("3. Exit Program");
            System.out.print("Enter your choice: ");

            String input = sc.nextLine().trim();

            if (input.equals("1")) {
                createAdminAccount(sc, userList);
                if (hasAdminUser(userList)) {
                    System.out.println("\n✅ Admin account created successfully!");
                    System.out.println("You can now login with your admin credentials.");
                    return;
                }
            } else if (input.equals("2")) {
                clearAllUsers(userList);
                System.out.println("\n✅ All users have been cleared.");
                System.out.println("Please create a new administrator account.");
                createAdminAccount(sc, userList);
                if (hasAdminUser(userList)) {
                    System.out.println("\n✅ Admin account created successfully!");
                    System.out.println("You can now login with your admin credentials.");
                    return;
                }
            } else if (input.equals("3")) {
                System.out.println("Exiting program...");
                System.exit(0);
            } else {
                System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }

    private static void createAdminAccount(Scanner sc, UserList userList) {
        System.out.println("\n=== CREATE ADMINISTRATOR ACCOUNT ===");

        String id;
        while (true) {
            System.out.print("Enter Admin User ID (must start with A, e.g., A001): ");
            id = sc.nextLine().trim().toUpperCase();

            if (id.isEmpty()) {
                System.out.println("User ID cannot be empty.");
                continue;
            }

            if (!id.matches("A\\d{3}")) {
                System.out.println("Invalid ID format. Admin ID must start with 'A' followed by 3 digits (e.g., A001)");
                continue;
            }

            // Check if ID already exists
            if (userList.getUserById(id) != null) {
                System.out.println("User ID already exists. Please use a different ID.");
                continue;
            }

            break;
        }

        String name;
        while (true) {
            System.out.print("Enter Admin Name: ");
            name = sc.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("Name cannot be empty.");
                continue;
            }

            if (!name.matches("[A-Za-z ]+")) {
                System.out.println("Name must contain only alphabets and spaces.");
                continue;
            }

            break;
        }

        // Email validation for admin
        String email;
        while (true) {
            System.out.print("Enter Email (must end with @tarc.edu.my): ");
            email = sc.nextLine().trim();

            if (email.isEmpty()) {
                System.out.println("Email is required for admin.");
                continue;
            }

            if (!User.isValidEmail(email)) {
                System.out.println("Invalid email format!");
                System.out.println("Email must end with @tarc.edu.my (e.g., admin@tarc.edu.my)");
                System.out.println("Please try again.");
            } else {
                break;
            }
        }

        // Phone validation for admin
        String phone;
        while (true) {
            System.out.print("Enter Phone Number (10 digits, e.g., 0123456789): ");
            phone = sc.nextLine().trim();

            if (phone.isEmpty()) {
                System.out.println("Phone number is required for admin.");
                continue;
            }

            if (!User.isValidPhone(phone)) {
                System.out.println("Invalid phone number format!");
                System.out.println("Phone number must be exactly 10 digits (e.g., 0123456789)");
                System.out.println("Please try again.");
            } else {
                phone = User.formatPhone(phone);
                break;
            }
        }

        User admin = new User(id, name, "A", email, phone);

        if (userList.addUser(admin)) {
            System.out.println("\n✅ Administrator account created successfully!");
            System.out.println("User ID: " + id);
            System.out.println("Name: " + name);
            System.out.println("Email: " + email);
            System.out.println("Phone: " + phone);
            System.out.println("Role: Administrator");
        } else {
            System.out.println("\n❌ Failed to create administrator account.");
        }
    }

    /**
     * Clear all users from the system
     */
    private static void clearAllUsers(UserList userList) {
        System.out.print("\n⚠️  Are you sure you want to delete ALL users? (Y/N): ");
        Scanner sc = new Scanner(System.in);
        String confirm = sc.nextLine().trim().toUpperCase();

        if (confirm.equals("Y")) {
            // Clear the list
            userList.clear();

            // Also delete the file to ensure fresh start
            File file = new File("users.txt");
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("users.txt file deleted.");
                } else {
                    System.out.println("Warning: Could not delete users.txt file.");
                }
            }

            System.out.println("All users have been cleared from the system.");
        } else {
            System.out.println("Clear operation cancelled.");
        }
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

        System.out.println("Current Email: " + (user.getEmail() != null ? user.getEmail() : "Not set"));
        System.out.print("Enter new email (must end with @tarc.edu.my, press Enter to keep current): ");
        String newEmail = sc.nextLine().trim();

        if (!newEmail.isEmpty()) {
            if (!User.isValidEmail(newEmail)) {
                System.out.println("Invalid email format! Email must end with @tarc.edu.my");
                System.out.println("Email not updated.");
                newEmail = "";
            } else {
                user.setEmail(newEmail);
            }
        }

        System.out.println("Current Phone: " + (user.getPhone() != null ? user.getPhone() : "Not set"));
        System.out.print("Enter new phone number (10 digits, press Enter to keep current): ");
        String newPhone = sc.nextLine().trim();

        if (!newPhone.isEmpty()) {
            if (!User.isValidPhone(newPhone)) {
                System.out.println("Invalid phone number! Must be exactly 10 digits.");
                System.out.println("Phone number not updated.");
                newPhone = "";
            } else {
                user.setPhone(User.formatPhone(newPhone));
            }
        }

        System.out.println("\n(Note: Role changes require administrator)");

        if (!newName.isEmpty()) {
            if (userList.updateUser(user.getUserId(), newName, null)) {
                System.out.println("Profile updated successfully!");
            } else {
                System.out.println("Failed to update profile.");
            }
        } else if (!newEmail.isEmpty() || !newPhone.isEmpty()) {
            userList.saveToFile();
            System.out.println("Profile updated successfully!");
        } else {
            System.out.println("No changes made.");
        }
    }

    private static void addNewUser(Scanner sc, UserList userList) {
        System.out.println("\n=== ADD NEW USER ===");
        System.out.print("Enter User ID (S001 for Student, L001 for Librarian, A001 for Admin): ");
        String id = sc.nextLine().trim().toUpperCase();

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

        // Email validation
        String email = "";
        while (true) {
            System.out.print("Enter Email (must end with @tarc.edu.my): ");
            email = sc.nextLine().trim();

            if (email.isEmpty()) {
                System.out.println("Email is required for all users.");
                continue;
            }

            if (!User.isValidEmail(email)) {
                System.out.println("Invalid email format!");
                System.out.println("Email must end with @tarc.edu.my (e.g., student@tarc.edu.my)");
                System.out.println("Please try again.");
            } else {
                break;
            }
        }

        // Phone validation
        String phone = "";
        while (true) {
            System.out.print("Enter Phone Number (10 digits, e.g., 0123456789): ");
            phone = sc.nextLine().trim();

            if (phone.isEmpty()) {
                System.out.println("Phone number is required for all users.");
                continue;
            }

            if (!User.isValidPhone(phone)) {
                System.out.println("Invalid phone number format!");
                System.out.println("Phone number must be exactly 10 digits (e.g., 0123456789)");
                System.out.println("Please try again.");
            } else {
                // Format the phone number for display
                phone = User.formatPhone(phone);
                break;
            }
        }

        User newUser = new User(id, name, role, email, phone);

        if (userList.addUser(newUser)) {
            System.out.println("User added successfully.");
            System.out.println("Email: " + email);
            System.out.println("Phone: " + phone);
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
        System.out.printf("%-15s: %s%n", "Email", user.getEmail());
        System.out.printf("%-15s: %s%n", "Phone", user.getPhone());

        System.out.println("\nEnter new details (press Enter to keep current value):");

        System.out.print("Enter new name: ");
        String newName = sc.nextLine().trim();

        System.out.print("Enter new role (S/L/A): ");
        String newRole = sc.nextLine().trim().toUpperCase();

        // Email update with validation
        String newEmail = "";
        System.out.print("Enter new email (must end with @tarc.edu.my): ");
        newEmail = sc.nextLine().trim();

        if (!newEmail.isEmpty()) {
            if (!User.isValidEmail(newEmail)) {
                System.out.println("Invalid email format! Email must end with @tarc.edu.my");
                System.out.println("Email not updated.");
                newEmail = "";
            }
        }

        // Phone update with validation
        String newPhone = "";
        System.out.print("Enter new phone number (10 digits): ");
        newPhone = sc.nextLine().trim();

        if (!newPhone.isEmpty()) {
            if (!User.isValidPhone(newPhone)) {
                System.out.println("Invalid phone number! Must be exactly 10 digits.");
                System.out.println("Phone number not updated.");
                newPhone = "";
            } else {
                newPhone = User.formatPhone(newPhone);
            }
        }

        // Validate role if provided
        if (!newRole.isEmpty() && !newRole.matches("[SLA]")) {
            System.out.println("Invalid role. Role must be S, L, or A.");
            return;
        }

        // Update user
        if (userList.updateUser(updateId, newName, newRole)) {
            // Update email and phone separately if provided
            if (!newEmail.isEmpty()) {
                user.setEmail(newEmail);
            }
            if (!newPhone.isEmpty()) {
                user.setPhone(newPhone);
            }
            userList.saveToFile(); // Save changes
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
