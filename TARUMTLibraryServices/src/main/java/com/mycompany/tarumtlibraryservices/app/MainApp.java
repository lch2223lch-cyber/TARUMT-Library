package com.mycompany.tarumtlibraryservices.app;

import com.mycompany.tarumtlibraryservices.adt.BookList;
import com.mycompany.tarumtlibraryservices.adt.TransactionList;
import com.mycompany.tarumtlibraryservices.adt.UserList;
import com.mycompany.tarumtlibraryservices.model.User;
import com.mycompany.tarumtlibraryservices.service.AuthService;
import com.mycompany.tarumtlibraryservices.ui.MainMenu;
import java.util.Scanner;
import java.io.File;

public class MainApp {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UserList userList = new UserList();
        AuthService authService = new AuthService(userList);

        // Initialise shared data stores
        BookList bookList = new BookList();
        TransactionList transactionList = new TransactionList();

        // Check if there's at least one admin user
        if (!hasAdminUser(userList)) {
            showSetupMenu(sc, userList);
        }

        // Login
        User currentUser = loginUser(sc, authService);
        if (currentUser == null) {
            System.out.println("Login failed. Exiting...");
            return;
        }

        // Launch main menu with all 4 modules
        MainMenu mainMenu = new MainMenu(sc, userList, bookList, transactionList, currentUser);
        mainMenu.start();

        authService.logout();
        sc.close();
    }

    private static boolean hasAdminUser(UserList userList) {
        User[] users = userList.getAllUsers();
        for (User user : users) {
            if (user.getRole().equals("A")) {
                return true;
            }
        }
        return false;
    }

    private static void showSetupMenu(Scanner sc, UserList userList) {
        while (true) {
            System.out.println("\n================================");
            System.out.println("=         SYSTEM SETUP                   =");
            System.out.println("=================================");
            System.out.println("1. Create New Administrator Account");
            System.out.println("2. Clear All Users and Start Fresh");
            System.out.println("3. Exit Program");
            System.out.print("Enter your choice: ");

            String input = sc.nextLine().trim();

            if (input.equals("1")) {
                createAdminAccount(sc, userList);
                if (hasAdminUser(userList)) {
                    System.out.println("\n✅ Admin account created successfully!");
                    return;
                }
            } else if (input.equals("2")) {
                clearAllUsers(userList);
                System.out.println("\n✅ All users have been cleared.");
                createAdminAccount(sc, userList);
                if (hasAdminUser(userList)) {
                    System.out.println("\n✅ Admin account created successfully!");
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

        String email;
        while (true) {
            System.out.print("Enter Email (must end with @tarc.edu.my): ");
            email = sc.nextLine().trim();

            if (email.isEmpty()) {
                System.out.println("Email is required for admin.");
                continue;
            }

            if (!User.isValidEmail(email)) {
                System.out.println("Invalid email format! Email must end with @tarc.edu.my");
                continue;
            }
            break;
        }

        String phone;
        while (true) {
            System.out.print("Enter Phone Number (10 digits, e.g., 0123456789): ");
            phone = sc.nextLine().trim();

            if (phone.isEmpty()) {
                System.out.println("Phone number is required for admin.");
                continue;
            }

            if (!User.isValidPhone(phone)) {
                System.out.println("Invalid phone number! Must be exactly 10 digits.");
                continue;
            }
            break;
        }

        User admin = new User(id, name, "A", email, User.formatPhone(phone));

        if (userList.addUser(admin)) {
            System.out.println("\n✅ Administrator account created successfully!");
        } else {
            System.out.println("\n❌ Failed to create administrator account.");
        }
    }

    private static void clearAllUsers(UserList userList) {
        System.out.print("\n⚠️  Are you sure you want to delete ALL users? (Y/N): ");
        Scanner sc = new Scanner(System.in);
        String confirm = sc.nextLine().trim().toUpperCase();

        if (confirm.equals("Y")) {
            userList.clear();
            File file = new File("users.txt");
            if (file.exists()) {
                file.delete();
            }
            System.out.println("All users have been cleared from the system.");
        } else {
            System.out.println("Clear operation cancelled.");
        }
    }

    private static User loginUser(Scanner sc, AuthService authService) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("        TARUMT LIBRARY SERVICES");
        System.out.println("               LOGIN");
        System.out.println("=".repeat(50));

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

        return null;
    }
}
