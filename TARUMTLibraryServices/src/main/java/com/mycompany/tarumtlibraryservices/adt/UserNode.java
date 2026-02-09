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
public class UserNode { //linked list node
    
    User data; //element in list adt
    UserNode next; //link to next element
    
    public UserNode(User data) {
        this.data = data;
        this.next = null; // by default, next is null
    }
}
