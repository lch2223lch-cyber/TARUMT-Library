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
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         USER MANAGEMENT");
        System.out.println("=".repeat(50));

        int menuNum = 1;

        System.out.println(menuNum++ + ". View My Profile");
        System.out.println(menuNum++ + ". Update My Profile");

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
        int count = 3;

        if (currentUser.canViewAllUsers()) count++;
        if (currentUser.canAddUser()) count++;
        if (currentUser.canUpdateAnyUser()) count++;
        if (currentUser.canDeleteUser()) count++;
        if (currentUser.canViewReports()) count++;

        return count;
    }

    private boolean handleMenuChoice(int choice) {
        int menuNum = 1;

        if (choice == menuNum++) {
            viewMyProfile();
            return true;
        }

        if (choice == menuNum++) {
            updateMyProfile();
            return true;
        }

        if (currentUser.canViewAllUsers() && choice == menuNum++) {
            viewAllUsers();
            return true;
        }

        if (currentUser.canAddUser() && choice == menuNum++) {
            addNewUser();
            return true;
        }

        if (currentUser.canUpdateAnyUser() && choice == menuNum++) {
            updateAnyUser();
            return true;
        }

        if (currentUser.canDeleteUser() && choice == menuNum++) {
            deleteUser();
            return true;
        }

        if (currentUser.canViewReports() && choice == menuNum++) {
            showReportMenu();
            return true;
        }

        if (choice == menuNum) {
            return false;
        }

        return true;
    }

    private String generateNextId(String role) {
        User[] allUsers = userList.getAllUsers();
        int maxNumber = 0;

        for (User user : allUsers) {
            String userId = user.getUserId();
            if (userId.startsWith(role)) {
                try {
                    int number = Integer.parseInt(userId.substring(1));
                    if (number > maxNumber) {
                        maxNumber = number;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }

        int nextNumber = maxNumber + 1;
        String formattedNumber = String.format("%03d", nextNumber);
        return role + formattedNumber;
    }

    private void viewMyProfile() {
        // Check if phone number is valid when viewing profile
        boolean phoneNeedsUpdate = false;
        String currentPhone = currentUser.getPhone();
        
        if (currentPhone != null && !currentPhone.isEmpty()) {
            String cleanPhone = currentPhone.replaceAll("[-\\s]", "");
            if (cleanPhone.length() != 10) {
                phoneNeedsUpdate = true;
            }
        } else {
            phoneNeedsUpdate = true;
        }
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           MY PROFILE");
        System.out.println("=".repeat(50));
        System.out.println("-".repeat(40));
        System.out.printf("%-15s: %s%n", "User ID", currentUser.getUserId());
        System.out.printf("%-15s: %s%n", "Name", currentUser.getName());
        System.out.printf("%-15s: %s%n", "Role", currentUser.getRoleDisplayName());
        System.out.printf("%-15s: %s%n", "Email", currentUser.getEmail() != null ? currentUser.getEmail() : "Not set");
        
        String displayPhone = currentPhone != null ? currentPhone : "Not set";
        System.out.printf("%-15s: %s%n", "Phone", displayPhone);
        
        if (phoneNeedsUpdate) {
            System.out.println("\n[WARNING] Your phone number is invalid or missing!");
            System.out.println("Phone number must be exactly 10 digits (e.g., 0123456789)");
        }
        
        System.out.printf("%-15s: %s%n", "Permissions", currentUser.getRoleDescription());
        System.out.printf("%-15s: %s%n", "Status", currentUser.isActive() ? "Active" : "Inactive");

        // Force user to update if phone number is invalid
        if (phoneNeedsUpdate) {
            System.out.println("\n[ACTION REQUIRED] Please update your phone number now.");
            System.out.print("Press Enter to continue to update...");
            sc.nextLine();
            updateMyProfile();
        } else {
            System.out.print("\nPress Enter to continue...");
            sc.nextLine();
        }
    }

    private void updateMyProfile() {
        System.out.println("\n=== UPDATE MY PROFILE ===");
        System.out.println("-".repeat(40));
        
        boolean updated = false;
        
        // Update Name
        System.out.println("Current Name: " + currentUser.getName());
        System.out.print("Enter new name (press Enter to keep current): ");
        String newName = sc.nextLine().trim();
        
        if (!newName.isEmpty()) {
            if (!newName.matches("[A-Za-z ]+")) {
                System.out.println("[ERROR] Name must contain only letters and spaces.");
                newName = "";
            }
        }

        // Update Email
        System.out.println("\nCurrent Email: " + (currentUser.getEmail() != null ? currentUser.getEmail() : "Not set"));
        System.out.print("Enter new email (must end with @tarc.edu.my, press Enter to keep current): ");
        String newEmail = sc.nextLine().trim();

        if (!newEmail.isEmpty()) {
            if (!User.isValidEmail(newEmail)) {
                System.out.println("[ERROR] Invalid email format! Email must end with @tarc.edu.my");
                System.out.println("Email not updated.");
                newEmail = "";
            }
        }

        // Update Phone Number with validation - FORCED if invalid
        System.out.println("\nCurrent Phone: " + (currentUser.getPhone() != null ? currentUser.getPhone() : "Not set"));
        System.out.println("Phone number must be exactly 10 digits (e.g., 0123456789)");
        
        // Check if current phone is valid
        String currentPhone = currentUser.getPhone();
        boolean isCurrentPhoneValid = false;
        
        if (currentPhone != null && !currentPhone.isEmpty()) {
            String cleanPhone = currentPhone.replaceAll("[-\\s]", "");
            if (cleanPhone.length() == 10 && cleanPhone.matches("\\d{10}")) {
                isCurrentPhoneValid = true;
            }
        }
        
        String newPhone = "";
        boolean phoneUpdated = false;
        
        while (true) {
            if (!isCurrentPhoneValid) {
                System.out.print("[REQUIRED] Enter valid phone number (10 digits): ");
            } else {
                System.out.print("Enter new phone number (press Enter to keep current): ");
            }
            
            newPhone = sc.nextLine().trim();
            
            if (newPhone.isEmpty()) {
                if (!isCurrentPhoneValid) {
                    System.out.println("[ERROR] Phone number is required! Please enter a valid 10-digit number.");
                    continue;
                } else {
                    break;
                }
            }
            
            // Remove any existing hyphens or spaces for validation
            String cleanPhone = newPhone.replaceAll("[-\\s]", "");
            
            if (cleanPhone.length() != 10) {
                System.out.println("[ERROR] Phone number must be exactly 10 digits. You entered " + cleanPhone.length() + " digits.");
                continue;
            }
            
            if (!cleanPhone.matches("\\d{10}")) {
                System.out.println("[ERROR] Phone number must contain only digits 0-9.");
                continue;
            }
            
            // Valid phone number - format it
            newPhone = cleanPhone.substring(0, 3) + "-" + cleanPhone.substring(3);
            phoneUpdated = true;
            break;
        }

        System.out.println("\n" + "-".repeat(40));
        System.out.println("Note: Role changes require administrator");
        System.out.println("-".repeat(40));

        // Apply changes
        if (!newName.isEmpty()) {
            if (userList.updateUser(currentUser.getUserId(), newName, null)) {
                System.out.println("[SUCCESS] Name updated successfully!");
                updated = true;
            } else {
                System.out.println("[ERROR] Failed to update name.");
            }
        }
        
        if (!newEmail.isEmpty()) {
            currentUser.setEmail(newEmail);
            System.out.println("[SUCCESS] Email updated successfully!");
            updated = true;
        }
        
        if (phoneUpdated) {
            currentUser.setPhone(newPhone);
            System.out.println("[SUCCESS] Phone updated successfully!");
            updated = true;
        }
        
        if (!updated) {
            System.out.println("No changes made.");
        }
        
        // Save to file if any changes were made
        if (updated) {
            userList.saveToFile();
            System.out.println("\n[SUCCESS] Profile saved to file.");
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
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           ADD NEW USER");
        System.out.println("=".repeat(50));

        String role = "";
        while (true) {
            System.out.print("Select Role (S for Student / L for Librarian / A for Admin): ");
            role = sc.nextLine().trim().toUpperCase();

            if (role.isEmpty() || !role.matches("[SLA]")) {
                System.out.println("[ERROR] Invalid role. Please enter S, L, or A.");
                continue;
            }
            break;
        }

        String id = generateNextId(role);
        System.out.println("\nAuto-generated User ID: " + id);

        String name;
        while (true) {
            System.out.print("Enter Name: ");
            name = sc.nextLine().trim();

            if (name.isEmpty() || !name.matches("[A-Za-z ]+")) {
                System.out.println("[ERROR] Name must contain alphabets only. Try again.");
            } else {
                break;
            }
        }

        String email;
        while (true) {
            System.out.print("Enter Email (must end with @tarc.edu.my): ");
            email = sc.nextLine().trim();

            if (email.isEmpty()) {
                System.out.println("[ERROR] Email is required for all users.");
                continue;
            }

            if (!User.isValidEmail(email)) {
                System.out.println("[ERROR] Invalid email format! Email must end with @tarc.edu.my");
                continue;
            }
            break;
        }

        String phone;
        while (true) {
            System.out.print("Enter Phone Number (10 digits, e.g., 0123456789): ");
            phone = sc.nextLine().trim();

            if (phone.isEmpty()) {
                System.out.println("[ERROR] Phone number is required for all users.");
                continue;
            }

            String cleanPhone = phone.replaceAll("[-\\s]", "");
            
            if (cleanPhone.length() != 10) {
                System.out.println("[ERROR] Phone number must be exactly 10 digits. You entered " + cleanPhone.length() + " digits.");
                continue;
            }
            
            if (!cleanPhone.matches("\\d{10}")) {
                System.out.println("[ERROR] Phone number must contain only digits 0-9.");
                continue;
            }
            
            phone = cleanPhone.substring(0, 3) + "-" + cleanPhone.substring(3);
            break;
        }

        User newUser = new User(id, name, role, email, phone);

        if (userList.addUser(newUser)) {
            System.out.println("\n[SUCCESS] User added successfully!");
            System.out.println("   User ID: " + id);
            System.out.println("   Name: " + name);
            System.out.println("   Role: " + getRoleName(role));
            System.out.println("   Email: " + email);
            System.out.println("   Phone: " + phone);
        } else {
            System.out.println("\n[ERROR] Failed to add user. User ID may already exist.");
        }

        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }

    private String getRoleName(String roleCode) {
        switch (roleCode) {
            case "S": return "Student";
            case "L": return "Librarian";
            case "A": return "Administrator";
            default: return "Unknown";
        }
    }

    private void updateAnyUser() {
        System.out.println("\n=== UPDATE USER ===");
        System.out.print("Enter User ID to update: ");
        String updateId = sc.nextLine().trim().toUpperCase();

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
            String cleanPhone = newPhone.replaceAll("[-\\s]", "");
            if (cleanPhone.length() != 10) {
                System.out.println("Invalid phone number! Phone not updated.");
                newPhone = "";
            } else if (!cleanPhone.matches("\\d{10}")) {
                System.out.println("Invalid phone number! Phone not updated.");
                newPhone = "";
            } else {
                newPhone = cleanPhone.substring(0, 3) + "-" + cleanPhone.substring(3);
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
            System.out.println("[SUCCESS] User updated successfully.");
        } else {
            System.out.println("[ERROR] Failed to update user.");
        }

        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }

    private void deleteUser() {
        System.out.println("\n=== DELETE USER ===");
        System.out.print("Enter User ID to delete: ");
        String deleteId = sc.nextLine().trim().toUpperCase();

        User deleteUser = userList.getUserById(deleteId);
        if (deleteUser == null) {
            System.out.println("User not found.");
            System.out.print("\nPress Enter to continue...");
            sc.nextLine();
            return;
        }

        if (deleteUser.getUserId().equals(currentUser.getUserId())) {
            System.out.println("[ERROR] You cannot delete your own account!");
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
                System.out.println("[SUCCESS] User deleted successfully.");
            } else {
                System.out.println("[ERROR] Failed to delete user.");
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
                                case "1": reportType = "Full Report"; break;
                                case "2": reportType = "Student Report"; break;
                                case "3": reportType = "Librarian Report"; break;
                                case "4": reportType = "Admin Report"; break;
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
    
    private void pause() {
        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }
}