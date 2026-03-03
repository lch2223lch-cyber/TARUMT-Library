/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tarumtlibraryservices.adt.Book;
import com.mycompany.tarumtlibraryservices.model.Book;

/**
 *
 * @author junji
 */
public class BookNode {
    Book data; //element in list adt
    BookNode next; //link to next element
    
    public BookNode(Book data) {
        this.data = data;
        this.next = null; // by default, next is null
    }
}
