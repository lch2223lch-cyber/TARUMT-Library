/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tarumtlibraryservices.adt;

import com.mycompany.tarumtlibraryservices.model.User;
import java.io.*;

/**
 *
 * @author ch
 */
public class UserList { //List ADT
    
    private UserNode head; //points to first node in list
    private int size; //track size for easier counting
    private static final String FILE_NAME = "users.txt";
    
    public UserList() {
        head = null;
        size = 0;
        loadFromFile(); // Load existing data from file if available
    }
    
    // Add user-unique userId
    public boolean addUser(User user) {
        if (getUserById(user.getUserId()) != null) {
            return false; // duplicate ID not allowed
        }

        UserNode newNode = new UserNode(user);

        if (head == null) {
            head = newNode;
        } else {
            UserNode current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
        saveToFile(); // Save to file after adding
        return true;
    }
    
    // Retrieve user by ID
    public User getUserById(String userId) {
        UserNode current = head;

        while (current != null) {
            if (current.data.getUserId().equalsIgnoreCase(userId)) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }
    
    // Remove user by ID
    public boolean removeUserById(String userId) {
        if (head == null) {
            return false;
        }

        if (head.data.getUserId().equalsIgnoreCase(userId)) {
            head = head.next;
            size--;
            saveToFile(); // Save to file after deletion
            return true;
        }

        UserNode current = head;
        while (current.next != null) {
            if (current.next.data.getUserId().equalsIgnoreCase(userId)) {
                current.next = current.next.next;
                size--;
                saveToFile(); // Save to file after deletion
                return true;
            }
            current = current.next;
        }
        return false;
    }
    
    // Update user
    public boolean updateUser(String userId, String newName, String newRole) {
        User user = getUserById(userId);
        if (user != null) {
            if (newName != null && !newName.trim().isEmpty()) {
                user.setName(newName);
            }
            if (newRole != null && !newRole.trim().isEmpty()) {
                // Validate role
                if (newRole.matches("[SLA]")) {
                    user.setRole(newRole);
                }
            }
            saveToFile(); // Save to file after update
            return true;
        }
        return false;
    }
    
    // Display all users
    public void displayAllUsers() {
        UserNode current = head;

        if (current == null) {
            System.out.println("No users available.");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.printf("%-10s | %-25s | %-15s | %-20s%n", 
            "User ID", "Name", "Role", "Role Description");
        System.out.println("=".repeat(80));
        
        while (current != null) {
            User user = current.data;
            System.out.printf("%-10s | %-25s | %-10s | %-20s%n",
                user.getUserId(),
                truncateString(user.getName(), 25),
                user.getRole(),
                user.getRoleName());
            current = current.next;
        }
        System.out.println("=".repeat(80));
        System.out.println("Total users: " + size);
    }
    
    // Get user at specific index (for iteration without collections)
    public User getUserAtIndex(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        
        UserNode current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }
    
    // Count users by role
    public int countUsersByRole(String role) {
        int count = 0;
        UserNode current = head;
        while (current != null) {
            if (current.data.getRole().equalsIgnoreCase(role)) {
                count++;
            }
            current = current.next;
        }
        return count;
    }
    
    // Search users by name (partial match) - returns array of users
    public User[] searchUsersByName(String keyword) {
        // First, count matches
        int matchCount = 0;
        UserNode current = head;
        while (current != null) {
            if (current.data.getName().toLowerCase().contains(keyword.toLowerCase())) {
                matchCount++;
            }
            current = current.next;
        }
        
        // Create array of matching users
        User[] results = new User[matchCount];
        if (matchCount == 0) {
            return results;
        }
        
        // Fill array with matches
        int index = 0;
        current = head;
        while (current != null) {
            if (current.data.getName().toLowerCase().contains(keyword.toLowerCase())) {
                results[index++] = current.data;
            }
            current = current.next;
        }
        
        return results;
    }
    
    // Get all users as array (for reporting)
    public User[] getAllUsers() {
        User[] users = new User[size];
        UserNode current = head;
        int index = 0;
        
        while (current != null) {
            users[index++] = current.data;
            current = current.next;
        }
        
        return users;
    }
    
    // Save to file
    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            UserNode current = head;
            while (current != null) {
                User user = current.data;
                // Format: userId|name|role
                writer.println(user.getUserId() + "|" + user.getName() + "|" + user.getRole());
                current = current.next;
            }
            System.out.println("Data saved to " + FILE_NAME); // Optional: confirm save
        } catch (IOException e) {
            System.err.println("Error saving to file: " + e.getMessage());
        }
    }
    
    // Load from file
    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("No existing data file found. Starting with empty list.");
            return; // No file yet, start with empty list
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int loadedCount = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    User user = new User(parts[0].trim(), parts[1].trim(), parts[2].trim());
                    addUserDirect(user);
                    loadedCount++;
                }
            }
            if (loadedCount > 0) {
                System.out.println("Loaded " + loadedCount + " users from " + FILE_NAME);
            }
        } catch (IOException e) {
            System.err.println("Error loading from file: " + e.getMessage());
        }
    }
    
    // Helper method to add user without saving to file (for loading only)
    private void addUserDirect(User user) {
        // Check for duplicate during load (avoid duplicates in file)
        if (getUserById(user.getUserId()) != null) {
            System.out.println("Warning: Duplicate user ID " + user.getUserId() + " found in file. Skipping.");
            return;
        }
        
        UserNode newNode = new UserNode(user);
        if (head == null) {
            head = newNode;
        } else {
            UserNode current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }
    
    public boolean isEmpty() {
        return head == null;
    }
    
    public int getSize() {
        return size;
    }
    
    private String truncateString(String str, int length) {
        if (str.length() <= length) return str;
        return str.substring(0, length - 3) + "...";
    }
}