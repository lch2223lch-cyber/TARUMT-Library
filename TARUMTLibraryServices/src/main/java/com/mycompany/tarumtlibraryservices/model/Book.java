/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tarumtlibraryservices.model;

/**
 *
 * @author junji
 */
public class Book {
    private String bookId;
    private String title;
    private String author;
    private BookSource source; //NEW or DONATED
    private boolean isAvailable; //Avaiable / Borrowed / Lost
    
    public Book(String bookId, String title, String author, BookSource source){
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.source = source;
        this.isAvailable = true; //default when created
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setSource(BookSource source) {
        this.source = source;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public BookSource getSource() {
        return source;
    }

    public boolean isIsAvailable() {
        return isAvailable;
    }

    @Override
    public String toString() {
        return "Book{" + "bookId=" + bookId + ", title=" + title + ", author=" + author + ", source=" + source + ", isAvailable=" + isAvailable + '}';
    }
    
}
