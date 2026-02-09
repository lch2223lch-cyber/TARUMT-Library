/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tarumtlibraryservices.app;

import com.mycompany.tarumtlibraryservices.adt.UserList;
import com.mycompany.tarumtlibraryservices.model.User;

/**
 *
 * @author ch
 */
public class MainApp {
    
    public static void main(String[] args) {

        UserList userList = new UserList();

        // hardcoded users
        userList.addUser(new User("S001", "Alice", "Student"));
        userList.addUser(new User("S002", "Bob", "Student"));
        userList.addUser(new User("A001", "Mr Tan", "Admin"));

        System.out.println("=== All Users ===");
        userList.displayAllUsers();

        System.out.println("\n=== Get User S001 ===");
        System.out.println(userList.getUserById("S001"));

        System.out.println("\n=== Remove User S001 ===");
        userList.removeUserById("S001");

        System.out.println("\n=== Users After Removal ===");
        userList.displayAllUsers();
    }
}
