package com.mycompany.tarumtlibraryservices.ui;

import com.mycompany.tarumtlibraryservices.adt.UserList;
import com.mycompany.tarumtlibraryservices.model.User;
import com.mycompany.tarumtlibraryservices.service.UserManager;
import java.util.Scanner;
import java.io.File;

public class UserMenu {

    private Scanner sc;
    private UserList userList;
    private User currentUser;
    private UserManager userManager;

    public UserMenu(Scanner sc, UserList userList, User currentUser) {
        this.sc = sc;
        this.userList = userList;
        this.currentUser = currentUser;
        this.userManager = new UserManager(userList);
    }

    public void start() {
        int choice = 0;

        do {
            displayUserMenu();

            System.out.print("Enter your choice: ");
            String input = sc.nextLine().trim();

            int maxChoice = getMaxChoice();
            if (!input.matches("[1-" + maxChoice + "]")) {
                System.out.println("Please enter a number between 1 and " + maxChoice + ".");
                continue;
            }

            choice = Integer.parseInt(input);

        } while (handleMenuChoice(choice));
    }

    private void displayUserMenu() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         USER MANAGEMENT               ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("=".repeat(40));

        int menuNum = 1;

        // Common menu items for all users
        System.out.println(menuNum++ + ". View My Profile");
        System.out.println(menuNum++ + ". Update My Profile");

        // Role-specific menu items
        if (currentUser.canViewAllUsers()) {
            System.out.println(menuNum++ + ". View All Users");
        }

        if (currentUser.canAddUser()) {
            System.out.println(menuNum++ + ". Add New User");
        }

        if (currentUser.canUpdateAnyUser()) {
            System.out.println(menuNum++ + ". Update Any User");
        }

        if (currentUser.canDeleteUser()) {
            System.out.println(menuNum++ + ". Delete User");
        }

        if (currentUser.canViewReports()) {
            System.out.println(menuNum++ + ". View Reports");
        }

        System.out.println(menuNum + ". Back to Main Menu");
        System.out.println("=".repeat(40));
    }

    private int getMaxChoice() {
        int count = 3; // View My Profile, Update My Profile, Back to Main Menu

        if (currentUser.canViewAllUsers()) {
            count++;
        }
        if (currentUser.canAddUser()) {
            count++;
        }
        if (currentUser.canUpdateAnyUser()) {
            count++;
        }
        if (currentUser.canDeleteUser()) {
            count++;
        }
        if (currentUser.canViewReports()) {
            count++;
        }

        return count;
    }

    private boolean handleMenuChoice(int choice) {
        int menuNum = 1;

        // View My Profile
        if (choice == menuNum++) {
            viewMyProfile();
            return true;
        }

        // Update My Profile
        if (choice == menuNum++) {
            updateMyProfile();
            return true;
        }

        // View All Users
        if (currentUser.canViewAllUsers() && choice == menuNum++) {
            viewAllUsers();
            return true;
        }

        // Add New User
        if (currentUser.canAddUser() && choice == menuNum++) {
            addNewUser();
            return true;
        }

        // Update Any User
        if (currentUser.canUpdateAnyUser() && choice == menuNum++) {
            updateAnyUser();
            return true;
        }

        // Delete User
        if (currentUser.canDeleteUser() && choice == menuNum++) {
            deleteUser();
            return true;
        }

        // View Reports
        if (currentUser.canViewReports() && choice == menuNum++) {
            showReportMenu();
            return true;
        }

        // Back to Main Menu
        if (choice == menuNum) {
            return false;
        }

        return true;
    }

    /**
     * Generate next available ID for a specific role
     *
     * @param role The role prefix (S, L, A)
     * @return Next available ID (e.g., S003, L005, A002)
     */
    private String generateNextId(String role) {
        User[] allUsers = userList.getAllUsers();
        int maxNumber = 0;

        // Find the highest number for this role
        for (User user : allUsers) {
            String userId = user.getUserId();
            if (userId.startsWith(role)) {
                try {
                    int number = Integer.parseInt(userId.substring(1));
                    if (number > maxNumber) {
                        maxNumber = number;
                    }
                } catch (NumberFormatException e) {
                    // Skip if format is invalid
                }
            }
        }

        int nextNumber = maxNumber + 1;
        String formattedNumber = String.format("%03d", nextNumber);
        return role + formattedNumber;
    }

    private void viewMyProfile() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║           MY PROFILE                   ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("-".repeat(40));
        System.out.printf("%-15s: %s%n", "User ID", currentUser.getUserId());
        System.out.printf("%-15s: %s%n", "Name", currentUser.getName());
        System.out.printf("%-15s: %s%n", "Role", currentUser.getRoleDisplayName());
        System.out.printf("%-15s: %s%n", "Email", currentUser.getEmail() != null ? currentUser.getEmail() : "Not set");
        System.out.printf("%-15s: %s%n", "Phone", currentUser.getPhone() != null ? currentUser.getPhone() : "Not set");
        System.out.printf("%-15s: %s%n", "Permissions", currentUser.getRoleDescription());
        System.out.printf("%-15s: %s%n", "Status", currentUser.isActive() ? "Active" : "Inactive");

        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }

    private void updateMyProfile() {
        System.out.println("\n=== UPDATE MY PROFILE ===");
        System.out.println("Current Name: " + currentUser.getName());
        System.out.print("Enter new name (press Enter to keep current): ");
        String newName = sc.nextLine().trim();

        System.out.println("Current Email: " + (currentUser.getEmail() != null ? currentUser.getEmail() : "Not set"));
        System.out.print("Enter new email (must end with @tarc.edu.my, press Enter to keep current): ");
        String newEmail = sc.nextLine().trim();

        if (!newEmail.isEmpty()) {
            if (!User.isValidEmail(newEmail)) {
                System.out.println("Invalid email format! Email must end with @tarc.edu.my");
                System.out.println("Email not updated.");
                newEmail = "";
            } else {
                currentUser.setEmail(newEmail);
            }
        }

        System.out.println("Current Phone: " + (currentUser.getPhone() != null ? currentUser.getPhone() : "Not set"));
        System.out.print("Enter new phone number (10 digits, press Enter to keep current): ");
        String newPhone = sc.nextLine().trim();

        if (!newPhone.isEmpty()) {
            if (!User.isValidPhone(newPhone)) {
                System.out.println("Invalid phone number! Must be exactly 10 digits.");
                System.out.println("Phone number not updated.");
                newPhone = "";
            } else {
                currentUser.setPhone(User.formatPhone(newPhone));
            }
        }

        System.out.println("\n(Note: Role changes require administrator)");

        if (!newName.isEmpty()) {
            if (userList.updateUser(currentUser.getUserId(), newName, null)) {
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

        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }

    private void viewAllUsers() {
        if (userList.isEmpty()) {
            System.out.println("No users found.");
        } else {
            userList.displayAllUsers();
        }
        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }

    private void addNewUser() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║           ADD NEW USER                 ║");
        System.out.println("╚════════════════════════════════════════╝");

        // First ask for role
        String role = "";
        while (true) {
            System.out.print("Select Role (S for Student / L for Librarian / A for Admin): ");
            role = sc.nextLine().trim().toUpperCase();

            if (role.isEmpty() || !role.matches("[SLA]")) {
                System.out.println("❌ Invalid role. Please enter S, L, or A.");
                continue;
            }
            break;
        }

        // Auto-generate ID based on role
        String id = generateNextId(role);
        System.out.println("\n📝 Auto-generated User ID: " + id);

        // Get name
        String name;
        while (true) {
            System.out.print("Enter Name: ");
            name = sc.nextLine().trim();

            if (name.isEmpty() || !name.matches("[A-Za-z ]+")) {
                System.out.println("❌ Name must contain alphabets only. Try again.");
            } else {
                break;
            }
        }

        // Get email
        String email;
        while (true) {
            System.out.print("Enter Email (must end with @tarc.edu.my): ");
            email = sc.nextLine().trim();

            if (email.isEmpty()) {
                System.out.println("❌ Email is required for all users.");
                continue;
            }

            if (!User.isValidEmail(email)) {
                System.out.println("❌ Invalid email format! Email must end with @tarc.edu.my");
                continue;
            }
            break;
        }

        // Get phone
        String phone;
        while (true) {
            System.out.print("Enter Phone Number (10 digits, e.g., 0123456789): ");
            phone = sc.nextLine().trim();

            if (phone.isEmpty()) {
                System.out.println("❌ Phone number is required for all users.");
                continue;
            }

            if (!User.isValidPhone(phone)) {
                System.out.println("❌ Invalid phone number format! Must be exactly 10 digits.");
                continue;
            }
            break;
        }

        // Create and add user
        User newUser = new User(id, name, role, email, User.formatPhone(phone));

        if (userList.addUser(newUser)) {
            System.out.println("\n✅ User added successfully!");
            System.out.println("   User ID: " + id);
            System.out.println("   Name: " + name);
            System.out.println("   Role: " + getRoleName(role));
            System.out.println("   Email: " + email);
            System.out.println("   Phone: " + User.formatPhone(phone));
        } else {
            System.out.println("\n❌ Failed to add user. User ID may already exist.");
        }

        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }

    private String getRoleName(String roleCode) {
        switch (roleCode) {
            case "S":
                return "Student";
            case "L":
                return "Librarian";
            case "A":
                return "Administrator";
            default:
                return "Unknown";
        }
    }

    private void updateAnyUser() {
        System.out.println("\n=== UPDATE USER ===");
        System.out.print("Enter User ID to update: ");
        String updateId = sc.nextLine().trim();

        User user = userList.getUserById(updateId);

        if (user == null) {
            System.out.println("User not found.");
            System.out.print("\nPress Enter to continue...");
            sc.nextLine();
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

        String newEmail = "";
        System.out.print("Enter new email (must end with @tarc.edu.my): ");
        newEmail = sc.nextLine().trim();

        if (!newEmail.isEmpty()) {
            if (!User.isValidEmail(newEmail)) {
                System.out.println("Invalid email format! Email not updated.");
                newEmail = "";
            }
        }

        String newPhone = "";
        System.out.print("Enter new phone number (10 digits): ");
        newPhone = sc.nextLine().trim();

        if (!newPhone.isEmpty()) {
            if (!User.isValidPhone(newPhone)) {
                System.out.println("Invalid phone number! Phone not updated.");
                newPhone = "";
            } else {
                newPhone = User.formatPhone(newPhone);
            }
        }

        if (!newRole.isEmpty() && !newRole.matches("[SLA]")) {
            System.out.println("Invalid role. Role must be S, L, or A.");
            System.out.print("\nPress Enter to continue...");
            sc.nextLine();
            return;
        }

        if (userList.updateUser(updateId, newName, newRole)) {
            if (!newEmail.isEmpty()) {
                user.setEmail(newEmail);
            }
            if (!newPhone.isEmpty()) {
                user.setPhone(newPhone);
            }
            userList.saveToFile();
            System.out.println("✅ User updated successfully.");
        } else {
            System.out.println("❌ Failed to update user.");
        }

        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }

    private void deleteUser() {
        System.out.println("\n=== DELETE USER ===");
        System.out.print("Enter User ID to delete: ");
        String deleteId = sc.nextLine().trim();

        User deleteUser = userList.getUserById(deleteId);
        if (deleteUser == null) {
            System.out.println("User not found.");
            System.out.print("\nPress Enter to continue...");
            sc.nextLine();
            return;
        }

        if (deleteUser.getUserId().equals(currentUser.getUserId())) {
            System.out.println("❌ You cannot delete your own account!");
            System.out.print("\nPress Enter to continue...");
            sc.nextLine();
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
                System.out.println("✅ User deleted successfully.");
            } else {
                System.out.println("❌ Failed to delete user.");
            }
        } else {
            System.out.println("Delete cancelled.");
        }

        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }

    private void showReportMenu() {
        int reportChoice = 0;

        do {
            System.out.println("\n=== REPORTS MENU ===");
            System.out.println("1. Full User Report");
            System.out.println("2. Student Report");
            System.out.println("3. Librarian Report");
            System.out.println("4. Admin Report");
            System.out.println("5. Search Users by Name");
            System.out.println("6. Export Report to File");
            System.out.println("7. Back to User Menu");

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
                    System.out.println("Returning to User Menu...");
                    break;
            }

        } while (reportChoice != 7);
    }
}
