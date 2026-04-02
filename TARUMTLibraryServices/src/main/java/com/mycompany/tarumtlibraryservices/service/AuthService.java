package com.mycompany.tarumtlibraryservices.service;

import com.mycompany.tarumtlibraryservices.adt.UserList;
import com.mycompany.tarumtlibraryservices.model.User;

/**
 *
 * @author ch
 */
public class AuthService {

    private UserList userList;
    private User currentUser;

    public AuthService(UserList userList) {
        this.userList = userList;
        this.currentUser = null;
    }

    // Login method
    public User login(String userId) {
        User user = userList.getUserById(userId);
        if (user != null && user.isActive()) {
            currentUser = user;
            System.out.println("Welcome, " + user.getName() + "!");
            System.out.println("Logged in as: " + user.getRoleDisplayName());
            return user;
        } else if (user != null && !user.isActive()) {
            System.out.println("Account is deactivated. Please contact administrator.");
        } else {
            System.out.println("User ID not found.");
        }
        return null;
    }

    // Logout method
    public void logout() {
        if (currentUser != null) {
            System.out.println("Goodbye, " + currentUser.getName() + "!");
            currentUser = null;
        }
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    // Get current user
    public User getCurrentUser() {
        return currentUser;
    }

    // Permission check methods
    public boolean checkCanManageUsers() {
        if (!isLoggedIn()) {
            System.out.println("Please login first.");
            return false;
        }
        return currentUser.canManageUsers();
    }

    public boolean checkCanViewAllUsers() {
        if (!isLoggedIn()) {
            System.out.println("Please login first.");
            return false;
        }
        return currentUser.canViewAllUsers();
    }

    public boolean checkCanAddUser() {
        if (!isLoggedIn()) {
            System.out.println("Please login first.");
            return false;
        }
        return currentUser.canAddUser();
    }

    public boolean checkCanDeleteUser() {
        if (!isLoggedIn()) {
            System.out.println("Please login first.");
            return false;
        }
        return currentUser.canDeleteUser();
    }

    public boolean checkCanUpdateUser(String targetUserId) {
        if (!isLoggedIn()) {
            System.out.println("Please login first.");
            return false;
        }

        User targetUser = userList.getUserById(targetUserId);
        if (targetUser == null) {
            System.out.println("User not found.");
            return false;
        }

        return currentUser.canAccessUser(targetUser);
    }

    public boolean checkCanViewReports() {
        if (!isLoggedIn()) {
            System.out.println("Please login first.");
            return false;
        }
        return currentUser.canViewReports();
    }
}
