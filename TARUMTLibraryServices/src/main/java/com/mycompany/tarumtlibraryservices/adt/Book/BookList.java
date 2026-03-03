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
public class BookList { //List ADT
    
    private BookNode head; //points to first node in list
    
    public BookList() {
        head = null;
    }
    
    // Add unique bookId
    public boolean addBook(Book book) {
        if (getBookById(book.getBookId()) != null) {
            return false; // duplicate ID not allowed
        }

        BookNode newNode = new BookNode(book);

        if (head == null) {
            head = newNode;
        } else {
            BookNode current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        return true;
    }
    // Retrieve book by ID
    public Book getBookById(String bookId) {
        BookNode current = head;

        while (current != null) {
            if (current.data.getBookId().equals(bookId)) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }
    // Remove book by ID
    public boolean removeBookById(String bookId) {
        if (head == null) {
            return false;
        }

        if (head.data.getBookId().equals(bookId)) {
            head = head.next;
            return true;
        }

        BookNode current = head;
        while (current.next != null) {
            if (current.next.data.getBookId().equals(bookId)) {
                current.next = current.next.next;
                return true;
            }
            current = current.next;
        }
        return false;
    }
    // Display all books
    public void displayAllBooks() {
        BookNode current = head;

        if (current == null) {
            System.out.println("No books available.");
            return;
        }

        while (current != null) {
            System.out.println(current.data);
            current = current.next;
        }
    }
    
    // Search by Title (partial match, case-insensitive)
    public void searchByTitle(String titleKeyword) {
        if (titleKeyword == null || titleKeyword.trim().isEmpty()) {
            System.out.println("Title keyword cannot be empty.");
            return;
        }

        String k = titleKeyword.trim().toLowerCase();
        BookNode current = head;
        boolean found = false;

        while (current != null) {
            String title = current.data.getTitle();
            if (title != null && title.toLowerCase().contains(k)) {
                System.out.println(current.data);
                found = true;
            }
            current = current.next;
        }

        if (!found) {
            System.out.println("No book found with that title.");
        }
    }

    // Search by Author (partial match, case-insensitive)
    public void searchByAuthor(String authorKeyword) {
        if (authorKeyword == null || authorKeyword.trim().isEmpty()) {
            System.out.println("Author keyword cannot be empty.");
            return;
        }

        String k = authorKeyword.trim().toLowerCase();
        BookNode current = head;
        boolean found = false;

        while (current != null) {
            String author = current.data.getAuthor();
            if (author != null && author.toLowerCase().contains(k)) {
                System.out.println(current.data);
                found = true;
            }
            current = current.next;
        }

        if (!found) {
            System.out.println("No book found with that author.");
        }
    }

    // Search by Book ID (exact match). You already have getBookById, this is just for printing.
    public void searchByBookId(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            System.out.println("Book ID cannot be empty.");
            return;
        }

        Book found = getBookById(bookId.trim());
        System.out.println(found != null ? found : "Book not found.");
    }
    
    public boolean isEmpty() {
        return head == null;
    }

}
