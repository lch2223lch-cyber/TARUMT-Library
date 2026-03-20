/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tarumtlibraryservices.model;

/**
 *
 * @author ch
 */
public class User {
    private String userId;
    private String name;
    private String role; // S: Student, L: Librarian, A: Admin
    private String email;
    private String phone;
    private boolean isActive;
    
    // Permission constants
    public static final String ROLE_STUDENT = "S";
    public static final String ROLE_LIBRARIAN = "L";
    public static final String ROLE_ADMIN = "A";
    
    public User(String userId, String name, String role) {
        this.userId = userId;
        this.name = name;
        this.role = role;
        this.isActive = true;
    }
    
    // Constructor with additional fields
    public User(String userId, String name, String role, String email, String phone) {
        this.userId = userId;
        this.name = name;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.isActive = true;
    }
    
    // Permission check methods
    
    public boolean canManageUsers() {
        return role.equals(ROLE_ADMIN);
    }
    
    public boolean canViewAllUsers() {
        return role.equals(ROLE_ADMIN) || role.equals(ROLE_LIBRARIAN);
    }
    
    public boolean canAddUser() {
        return role.equals(ROLE_ADMIN);
    }
    
    public boolean canDeleteUser() {
        return role.equals(ROLE_ADMIN);
    }
    
    public boolean canUpdateAnyUser() {
        return role.equals(ROLE_ADMIN);
    }
    
    public boolean canViewReports() {
        return role.equals(ROLE_ADMIN) || role.equals(ROLE_LIBRARIAN);
    }
    
    public boolean canExportReports() {
        return role.equals(ROLE_ADMIN) || role.equals(ROLE_LIBRARIAN);
    }
    
    public boolean canManageBooks() {
        return role.equals(ROLE_ADMIN) || role.equals(ROLE_LIBRARIAN);
    }
    
    public boolean canChangeSystemConfig() {
        return role.equals(ROLE_ADMIN);
    }
    
    public boolean canViewLogs() {
        return role.equals(ROLE_ADMIN) || role.equals(ROLE_LIBRARIAN);
    }
    
    public boolean canBorrowBooks() {
        return isActive; // All active users can borrow
    }
    
    // Check if user can access a specific user's data
    public boolean canAccessUser(User targetUser) {
        if (role.equals(ROLE_ADMIN)) {
            return true; // Admin can access anyone
        }
        if (role.equals(ROLE_LIBRARIAN)) {
            return true; // Librarian can access anyone for viewing
        }
        // Students can only access their own data
        return this.userId.equals(targetUser.getUserId());
    }
    
    // Get role display name
    public String getRoleDisplayName() {
        switch (role) {
            case ROLE_STUDENT:
                return "Student";
            case ROLE_LIBRARIAN:
                return "Librarian";
            case ROLE_ADMIN:
                return "Administrator";
            default:
                return "Unknown";
        }
    }
    
    // Get role description
    public String getRoleDescription() {
        switch (role) {
            case ROLE_STUDENT:
                return "Can borrow books and manage own profile";
            case ROLE_LIBRARIAN:
                return "Can manage books and view reports";
            case ROLE_ADMIN:
                return "Full system access and user management";
            default:
                return "No special permissions";
        }
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public String getRoleName() {
        return getRoleDisplayName();
    }
    
    @Override
    public String toString() {
        return userId + " | " + name + " | " + getRoleDisplayName() + " | " + (isActive ? "Active" : "Inactive");
    }
}