/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tarumtlibraryservices.adt;

import com.mycompany.tarumtlibraryservices.model.User;

/**
 *
 * @author ch
 */
public class UserList { //List ADT
    
    private UserNode head; //points to first node in list
    
    public UserList() {
        head = null;
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
        return true;
    }
    // Retrieve user by ID
    public User getUserById(String userId) {
        UserNode current = head;

        while (current != null) {
            if (current.data.getUserId().equals(userId)) {
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

        if (head.data.getUserId().equals(userId)) {
            head = head.next;
            return true;
        }

        UserNode current = head;
        while (current.next != null) {
            if (current.next.data.getUserId().equals(userId)) {
                current.next = current.next.next;
                return true;
            }
            current = current.next;
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

        while (current != null) {
            System.out.println(current.data);
            current = current.next;
        }
    }
    public boolean isEmpty() {
        return head == null;
    }

}
