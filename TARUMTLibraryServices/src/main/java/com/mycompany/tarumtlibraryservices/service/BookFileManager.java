/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tarumtlibraryservices.service;

import com.mycompany.tarumtlibraryservices.adt.Book.BookList;
import com.mycompany.tarumtlibraryservices.adt.Book.BookNode;
import com.mycompany.tarumtlibraryservices.model.Book;
import com.mycompany.tarumtlibraryservices.model.BookSource;
import java.io.*;

/**
 *
 * @author junji
 */
public class BookFileManager {
    
    private static final String Books = "books.txt";
    
    //Save Books
    public static void addBook(Book book) {
        try(FileWriter writer = new FileWriter(Books, true)){

            writer.write(
                book.getBookId() + "," +
                book.getTitle() + "," +
                book.getAuthor() + "," +
                book.getSource() + "," +
                book.isAvailable() + "\n"
            );
        }catch(IOException e){
            System.out.println("Error adding book data.");
       }
    }
    
    //Load Books
    public static void loadBooks(BookList bookList) {
        File file = new File(Books);
        
        if(!file.exists()) {
            return;
        }
        
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            
            while ((line = reader.readLine()) !=null){
                String[] data = line.split(",");
                String id = data[0];
                String title = data[1];
                String author = data[2];
                BookSource source = BookSource.valueOf(data[3]);
                boolean isAvailable = Boolean.parseBoolean(data[4]);
                
                Book book = new Book(id, title, author, source, isAvailable);
                bookList.addBook(book);
            }
        }catch(IOException e){
            System.out.println("Error loading books.");
        }
    }
    
    //Update / Delete Books
    public static void updateBooks(BookList bookList) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(Books))) {
            BookNode current = bookList.getHead();
            
            while (current != null) {
                Book book = current.getData();
                
                writer.println(
                    book.getBookId() + "," +
                    book.getTitle() + "," +
                    book.getAuthor() + "," +
                    book.getSource() + "," +
                    book.isAvailable() + "\n"
                );
                current = current.getNext();
            }
        }catch(IOException e) {
            System.out.println("Error updating books file: " + e.getMessage());
        }
    }
}
