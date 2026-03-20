package com.mycompany.tarumtlibraryservices.adt;

import com.mycompany.tarumtlibraryservices.model.User;
import java.io.*;

/**
 * UserList ADT - Manages User objects Extends GenericList with User-specific
 * functionality
 *
 * @author [Lim Chuin Hao]
 */
public class UserList extends GenericList<User> {

    private static final String DEFAULT_FILE_NAME = "users.txt";

    public UserList() {
        super(DEFAULT_FILE_NAME);
    }

    public UserList(String fileName) {
        super(fileName);
    }

    // ========== USER-SPECIFIC METHODS ==========
    /**
     * Add user with duplicate ID checking
     */
    public boolean addUser(User user) {
        if (getUserById(user.getUserId()) != null) {
            return false; // Duplicate ID
        }
        return add(user);
    }

    /**
     * Get user by ID
     */
    public User getUserById(String userId) {
        return findFirst(user -> user.getUserId().equalsIgnoreCase(userId));
    }

    /**
     * Remove user by ID
     */
    public boolean removeUserById(String userId) {
        return removeIf(user -> user.getUserId().equalsIgnoreCase(userId));
    }

    /**
     * Update user information
     */
    public boolean updateUser(String userId, String newName, String newRole) {
        User user = getUserById(userId);
        if (user == null) {
            return false;
        }

        if (newName != null && !newName.trim().isEmpty()) {
            user.setName(newName);
        }

        if (newRole != null && !newRole.trim().isEmpty()) {
            if (newRole.matches("[SLA]")) {
                user.setRole(newRole);
            }
        }

        saveToFile(); // Persist changes
        return true;
    }

    /**
     * Display all users with formatted output
     */
    public void displayAllUsers() {
        if (isEmpty()) {
            System.out.println("No users available.");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.printf("%-10s | %-25s | %-15s | %-20s%n",
                "User ID", "Name", "Role", "Role Description");
        System.out.println("=".repeat(80));

        forEach(user -> {
            System.out.printf("%-10s | %-25s | %-10s | %-20s%n",
                    user.getUserId(),
                    truncateString(user.getName(), 25),
                    user.getRole(),
                    user.getRoleName());
        });

        System.out.println("=".repeat(80));
        System.out.println("Total users: " + getSize());
    }

    /**
     * Count users by role
     */
    public int countUsersByRole(String role) {
        return count(user -> user.getRole().equalsIgnoreCase(role));
    }

    /**
     * Search users by name (partial match)
     */
    public User[] searchUsersByName(String keyword) {
        return findAll(
                user -> user.getName().toLowerCase().contains(keyword.toLowerCase()),
                User[]::new
        );
    }

    /**
     * Get all users as array
     */
    public User[] getAllUsers() {
        return toArray(User[]::new);
    }

    /**
     * Get active users only
     */
    public User[] getActiveUsers() {
        return findAll(User::isActive, User[]::new);
    }

    /**
     * Get users by role
     */
    public User[] getUsersByRole(String role) {
        return findAll(user -> user.getRole().equalsIgnoreCase(role), User[]::new);
    }

    /**
     * Check if user exists
     */
    public boolean userExists(String userId) {
        return getUserById(userId) != null;
    }

    // ========== OVERRIDDEN METHODS FOR PERSISTENCE ==========
    @Override
    protected String saveElement(User user) {
        // Format: userId|name|role|email|phone|isActive
        return user.getUserId() + "|"
                + user.getName() + "|"
                + user.getRole() + "|"
                + (user.getEmail() != null ? user.getEmail() : "") + "|"
                + (user.getPhone() != null ? user.getPhone() : "") + "|"
                + user.isActive();
    }

    @Override
    protected User parseElement(String line) {
        // Skip empty lines
        if (line.trim().isEmpty()) {
            return null;
        }

        String[] parts = line.split("\\|");

        // Check if we have at least userId and name
        if (parts.length < 2) {
            return null;
        }

        String userId = parts[0].trim();
        String name = parts[1].trim();

        // Determine role
        String role = "";
        if (parts.length >= 3) {
            role = parts[2].trim();
        }

        // If role is empty or invalid, try to infer from userId
        if (role.isEmpty() || !role.matches("[SLA]")) {
            if (userId.toUpperCase().startsWith("A")) {
                role = "A";
            } else if (userId.toUpperCase().startsWith("L")) {
                role = "L";
            } else if (userId.toUpperCase().startsWith("S")) {
                role = "S";
            } else {
                role = "S"; // Default to student
            }
        }

        // Create user
        User user;

        // Check if we have email and phone
        if (parts.length >= 5) {
            String email = parts[3].trim();
            String phone = parts[4].trim();
            user = new User(userId, name, role, email, phone);
        } else if (parts.length >= 4) {
            String email = parts[3].trim();
            user = new User(userId, name, role, email, "");
        } else {
            user = new User(userId, name, role);
        }

        // Check if we have active status
        if (parts.length >= 6) {
            try {
                user.setActive(Boolean.parseBoolean(parts[5].trim()));
            } catch (Exception e) {
                // Keep default active status
            }
        }

        return user;
    }

    // ========== HELPER METHODS ==========
    private String truncateString(String str, int length) {
        if (str.length() <= length) {
            return str;
        }
        return str.substring(0, length - 3) + "...";
    }
}
