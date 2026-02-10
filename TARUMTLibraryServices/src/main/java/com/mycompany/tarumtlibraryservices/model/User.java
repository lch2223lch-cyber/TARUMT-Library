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
    private String role; // Student, Staff, Admin

    public User(String userId, String name, String role) {
        this.userId = userId;
        this.name = name;
        this.role = role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
    
    @Override
    public String toString() {
        return userId + " | " + name + " | " + getRoleName();
    }
    private String getRoleName() {
        switch (role) {
            case "S":
                return "Student";
            case "A":
                return "Admin";
            case "L":
                return "Librarian";
            default:
                return "Unknown";
        }
    }
}
