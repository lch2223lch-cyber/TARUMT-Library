/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tarumtlibraryservices.app;

import com.mycompany.tarumtlibraryservices.adt.UserList;
import com.mycompany.tarumtlibraryservices.model.User;
import java.util.Scanner;

/**
 *
 * @author ch
 */

public class MainApp {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        UserList userList = new UserList();
        int choice = 0;

        do {
            System.out.println("\n=== User Management System ===");
            System.out.println("1. Add User");
            System.out.println("2. View All Users");
            System.out.println("3. Search User");
            System.out.println("4. Update User");
            System.out.println("5. Delete User");
            System.out.println("6. Exit");
            
            System.out.print("Enter your choice: ");
            String input = sc.nextLine().trim();
            
            if (!input.matches("[1-6]")) {
                System.out.println("Please enter a number between 1 and 6.");
                continue;
            }

            choice = Integer.parseInt(input);

            switch (choice) {

                case 1: // CREATE
                    System.out.print("Enter User ID (S001 / A001): ");
                    String id = sc.nextLine().trim();

                    if (id.isEmpty() || !id.matches("[SA]\\d{3}")) {
                        System.out.println("Invalid ID format.");
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


                    System.out.print("Enter Role: (S/L/A) ");
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
                    System.out.println(found != null ? found : "User not found.");
                    break;

                case 4: // UPDATE
                    System.out.print("Enter User ID to update: ");
                    String updateId = sc.nextLine().trim();

                    User user = userList.getUserById(updateId);

                    if (user == null) {
                        System.out.println("User not found.");
                        break;
                    }

                    System.out.print("Enter new name: ");
                    String newName = sc.nextLine().trim();

                    if (!newName.isEmpty()) {
                        user.setName(newName);
                    }

                    System.out.print("Enter new role: ");
                    String newRole = sc.nextLine().trim();

                    if (!newRole.isEmpty()) {
                        user.setRole(newRole);
                    }

                    System.out.println("User updated successfully.");
                    break;

                case 5: // DELETE
                    System.out.print("Enter User ID to delete: ");
                    String deleteId = sc.nextLine().trim();

                    System.out.print("Confirm delete? (Y/N): ");
                    String confirm = sc.nextLine();

                    if (confirm.equalsIgnoreCase("Y")) {
                        if (userList.removeUserById(deleteId)) {
                            System.out.println("User deleted.");
                        } else {
                            System.out.println("User not found.");
                        }
                    } else {
                        System.out.println("Delete cancelled.");
                    }
                    break;

                case 6:
                    System.out.println("Exiting system...");
                    break;

                default:
                    System.out.println("Invalid choice. Enter 1–6.");
            }

        } while (choice != 6);

        sc.close();
    }
}
