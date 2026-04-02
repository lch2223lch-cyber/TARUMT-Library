/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tarumtlibraryservices.service;

import com.mycompany.tarumtlibraryservices.adt.UserList;
import com.mycompany.tarumtlibraryservices.model.User;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author ch
 */
public class UserManager {
    
    private UserList userList;
    
    public UserManager(UserList userList) {
        this.userList = userList;
    }
    
    // Generate comprehensive user report
    public void generateUserReport() {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("USER MANAGEMENT REPORT");
        System.out.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("=".repeat(100));
        
        // Summary statistics
        int totalUsers = userList.getSize();
        int studentCount = userList.countUsersByRole("S");
        int librarianCount = userList.countUsersByRole("L");
        int adminCount = userList.countUsersByRole("A");
        
        System.out.println("\nSUMMARY STATISTICS:");
        System.out.println("-".repeat(50));
        System.out.printf("%-20s: %d%n", "Total Users", totalUsers);
        System.out.printf("%-20s: %d (%.1f%%)%n", "Students", studentCount, 
            totalUsers > 0 ? (studentCount * 100.0 / totalUsers) : 0);
        System.out.printf("%-20s: %d (%.1f%%)%n", "Librarians", librarianCount,
            totalUsers > 0 ? (librarianCount * 100.0 / totalUsers) : 0);
        System.out.printf("%-20s: %d (%.1f%%)%n", "Admins", adminCount,
            totalUsers > 0 ? (adminCount * 100.0 / totalUsers) : 0);
        
        // Detailed user list
        System.out.println("\nDETAILED USER LIST:");
        userList.displayAllUsers();
        
        // Additional statistics only (no chart)
        displayAdditionalStats(studentCount, librarianCount, adminCount, totalUsers);
    }
    
    // Generate role-based report
    public void generateRoleBasedReport(String role) {
        String roleName = "";
        String roleCode = role.toUpperCase();
        
        switch (roleCode) {
            case "S": roleName = "STUDENT"; break;
            case "L": roleName = "LIBRARIAN"; break;
            case "A": roleName = "ADMIN"; break;
            default: 
                System.out.println("Invalid role specified.");
                return;
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println(roleName + " REPORT");
        System.out.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("=".repeat(80));
        
        User[] allUsers = userList.getAllUsers();
        int totalUsers = userList.getSize();
        int count = 0;
        
        System.out.printf("\n%-10s | %-30s | %-15s | %-10s%n", "User ID", "Name", "Role", "Code");
        System.out.println("-".repeat(70));
        
        for (int i = 0; i < allUsers.length; i++) {
            User user = allUsers[i];
            if (user.getRole().equalsIgnoreCase(roleCode)) {
                System.out.printf("%-10s | %-30s | %-15s | %-10s%n", 
                    user.getUserId(), 
                    truncateString(user.getName(), 30), 
                    user.getRoleName(),
                    user.getRole());
                count++;
            }
        }
        
        System.out.println("-".repeat(70));
        System.out.printf("Total %s users: %d%n", roleName, count);
        System.out.printf("Percentage of total: %.1f%%%n", 
            totalUsers > 0 ? (count * 100.0 / totalUsers) : 0);
    }
    
    // Generate name search report
    public void generateNameSearchReport(String keyword) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("USER SEARCH REPORT - Name contains: \"" + keyword + "\"");
        System.out.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("=".repeat(80));
        
        User[] searchResults = userList.searchUsersByName(keyword);
        int totalUsers = userList.getSize();
        
        if (searchResults.length == 0) {
            System.out.println("\nNo users found matching the search criteria.");
        } else {
            System.out.printf("\n%-10s | %-30s | %-15s | %-10s%n", "User ID", "Name", "Role", "Code");
            System.out.println("-".repeat(70));
            
            for (int i = 0; i < searchResults.length; i++) {
                User user = searchResults[i];
                System.out.printf("%-10s | %-30s | %-15s | %-10s%n", 
                    user.getUserId(), 
                    truncateString(user.getName(), 30), 
                    user.getRoleName(),
                    user.getRole());
            }
            
            System.out.println("-".repeat(70));
            System.out.printf("Total users found: %d%n", searchResults.length);
            System.out.printf("Search matched: %.1f%% of total users%n", 
                totalUsers > 0 ? (searchResults.length * 100.0 / totalUsers) : 0);
        }
    }
    
    // Export report to file
    public boolean exportReportToFile(String reportType) {
        String filename = "UserReport_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=".repeat(100));
            writer.println("TARUMT LIBRARY SERVICES - USER MANAGEMENT REPORT");
            writer.println("Report Type: " + reportType);
            writer.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println("=".repeat(100));
            
            // Summary statistics
            int totalUsers = userList.getSize();
            int studentCount = userList.countUsersByRole("S");
            int librarianCount = userList.countUsersByRole("L");
            int adminCount = userList.countUsersByRole("A");
            
            writer.println("\nSUMMARY STATISTICS:");
            writer.println("-".repeat(50));
            writer.printf("%-20s: %d%n", "Total Users", totalUsers);
            writer.printf("%-20s: %d%n", "Students", studentCount);
            writer.printf("%-20s: %d%n", "Librarians", librarianCount);
            writer.printf("%-20s: %d%n", "Admins", adminCount);
            
            // Percentage breakdown
            writer.println("\nPERCENTAGE BREAKDOWN:");
            writer.println("-".repeat(50));
            writer.printf("%-20s: %.1f%%%n", "Students", 
                totalUsers > 0 ? (studentCount * 100.0 / totalUsers) : 0);
            writer.printf("%-20s: %.1f%%%n", "Librarians", 
                totalUsers > 0 ? (librarianCount * 100.0 / totalUsers) : 0);
            writer.printf("%-20s: %.1f%%%n", "Admins", 
                totalUsers > 0 ? (adminCount * 100.0 / totalUsers) : 0);
            
            // Detailed user list
            writer.println("\nDETAILED USER LIST:");
            writer.printf("%-10s | %-30s | %-15s | %-10s%n", "User ID", "Name", "Role", "Code");
            writer.println("-".repeat(70));
            
            User[] allUsers = userList.getAllUsers();
            for (int i = 0; i < allUsers.length; i++) {
                User user = allUsers[i];
                writer.printf("%-10s | %-30s | %-15s | %-10s%n", 
                    user.getUserId(), 
                    user.getName(), 
                    user.getRoleName(),
                    user.getRole());
            }
            
            writer.println("\n" + "=".repeat(100));
            writer.println("END OF REPORT");
            
            System.out.println("Report exported successfully to: " + filename);
            return true;
            
        } catch (IOException e) {
            System.err.println("Error exporting report: " + e.getMessage());
            return false;
        }
    }
    
    // Display additional statistics (no chart)
    private void displayAdditionalStats(int studentCount, int librarianCount, int adminCount, int total) {
        if (total == 0) {
            return;
        }
        
        System.out.println("\nADDITIONAL STATISTICS:");
        System.out.println("-".repeat(50));
        
        // Find most common role
        String mostCommonRole = "Students";
        int maxCount = studentCount;
        
        if (librarianCount > maxCount) {
            mostCommonRole = "Librarians";
            maxCount = librarianCount;
        }
        if (adminCount > maxCount) {
            mostCommonRole = "Admins";
            maxCount = adminCount;
        }
        
        System.out.printf("%-20s: %s (%d users, %.1f%%)%n", 
            "Most common role", 
            mostCommonRole, 
            maxCount,
            (maxCount * 100.0 / total));
        
        // Find least common role
        String leastCommonRole = "Students";
        int minCount = studentCount;
        
        if (librarianCount < minCount) {
            leastCommonRole = "Librarians";
            minCount = librarianCount;
        }
        if (adminCount < minCount) {
            leastCommonRole = "Admins";
            minCount = adminCount;
        }
        
        System.out.printf("%-20s: %s (%d users, %.1f%%)%n", 
            "Least common role", 
            leastCommonRole, 
            minCount,
            (minCount * 100.0 / total));
        
        // Average users per role
        System.out.printf("%-20s: %.1f users%n", "Average per role", (total / 3.0));
        
        // Role ratio (if all roles have users)
        if (studentCount > 0 && librarianCount > 0 && adminCount > 0) {
            // Simplify ratio
            int gcd = findGCD(findGCD(studentCount, librarianCount), adminCount);
            if (gcd > 0) {
                System.out.printf("%-20s: %d : %d : %d (S:L:A)%n", 
                    "Role ratio", 
                    studentCount / gcd, 
                    librarianCount / gcd, 
                    adminCount / gcd);
            }
        }
        
        System.out.println("-".repeat(50));
    }
    
    // Helper method to find GCD for ratio simplification
    private int findGCD(int a, int b) {
        if (b == 0) return a;
        return findGCD(b, a % b);
    }
    
    // Helper method to truncate long strings
    private String truncateString(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}