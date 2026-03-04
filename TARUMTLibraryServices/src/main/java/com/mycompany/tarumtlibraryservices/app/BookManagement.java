/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.mycompany.tarumtlibraryservices.app;

import com.mycompany.tarumtlibraryservices.adt.Book.BookList;
import com.mycompany.tarumtlibraryservices.model.Book;
import com.mycompany.tarumtlibraryservices.model.BookSource;
import com.mycompany.tarumtlibraryservices.service.BookFileManager;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author junji
 */
public class BookManagement {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        BookList bookList = new BookList();
        BookFileManager.loadBooks(bookList);
        int choice = 0;

        do {
            System.out.println("\n=== Book Management ===");
            System.out.println("1. Add Book");
            System.out.println("2. View All Books");
            System.out.println("3. Search Book");
            System.out.println("4. Update Book");
            System.out.println("5. Delete Book");
            System.out.println("6. Exit");
            
            System.out.print("Enter your choice: ");
            String input = sc.nextLine().trim();
            
            if (!input.matches("[1-6]")) {
                System.out.println("Please enter a number between 1 and 6.");
                continue;
            }

            choice = Integer.parseInt(input);

            switch (choice) {

                case 1: // CREATE
                    String id;
                    while (true) {
                        System.out.print("Enter Book ID (B001): ");
                        id = sc.nextLine().trim();

                        if (id.isEmpty() || !id.matches("B\\d{3}")) {
                            System.out.println("Invalid ID format. Example: B001");
                            continue;
                        }

                        if (bookList.getBookById(id) != null) {
                            System.out.println("Book ID already exists. Please enter another ID.");
                            continue;
                        }

                        break; // valid and unique ID
                    }

                    String title;
                    while (true) {
                        System.out.print("Enter Book Title: ");
                        title = sc.nextLine().trim();

                        if (title.isEmpty() || !title.matches("[A-Za-z0-9 ,:'\\-\\.]+")) {
                            System.out.println("Invalid book title. Try again.");
                        } else {
                            break; // valid input, exit loop
                        }
                    }
                    

                    String author;
                    while (true) {
                        System.out.print("Enter Author: ");
                        author = sc.nextLine().trim();

                        if (author.isEmpty()) {
                            System.out.println("Author name cannot be empty. Try again.");
                        } 
                        else if (!author.matches("[A-Za-z .'-]+")) {
                            System.out.println("Author name contains invalid characters. Try again.");
                        } 
                        else {
                            break; // valid input
                        }
                    }
                    
                    BookSource source;
                    while (true) {
                        System.out.print("Enter Source (1 = NEW, 2 = DONATED): ");
                        String sourceInput = sc.nextLine().trim();

                        if (sourceInput.equals("1")) {
                            source = BookSource.NEW;
                            break;
                        } 
                        else if (sourceInput.equals("2")) {
                            source = BookSource.DONATED;
                            break;
                        } 
                        else {
                            System.out.println("Invalid choice. Please enter 1 or 2.");
                        }
                    }
                    
                    Book newBook = new Book(id, title, author, source, true);
                    
                    if(bookList.addBook(newBook)){
                        System.out.println("Book added successful"); 
                        BookFileManager.addBook(newBook);
                        
                    } else{
                        System.out.println("Error: Book ID already exists. Cannot be duplicate");
                    }
                    
                    break;

                case 2: // READ ALL
                    if (bookList.isEmpty()) {
                        System.out.println("No books found.");
                    } else {
                        bookList.displayAllBooks();
                    }
                    break;

                case 3: // SEARCH
                    System.out.println("Search Book By: ");
                    System.out.println("1. Book Id");
                    System.out.println("2. Title");
                    System.out.println("3. Author");
                    System.out.print("Choose: ");
                    String searchChoice = sc.nextLine().trim();
                    
                    System.out.print("Enter Keyword: ");
                    String keyword = sc.nextLine().trim();
                    
                    switch(searchChoice){
                        case "1":
                            bookList.searchByBookId(keyword);
                            break;
                            
                        case "2":
                            bookList.searchByTitle(keyword);
                            break;
                            
                        case "3":
                            bookList.searchByAuthor(keyword);
                            break;
                            
                        default:
                            System.out.println("Invalid search option.");
                    }
                    
                    break;  

                case 4: // UPDATE
                    System.out.print("Enter Book ID: ");
                    String updateId = sc.nextLine().trim();

                    Book bookToUpdate = bookList.getBookById(updateId);

                    if (bookToUpdate == null) {
                        System.out.println("Book not found.");
                        break;
                    }
                    
                    System.out.println("Current details: " + bookToUpdate);

                    System.out.print("Enter new title (leave blank to keep current): ");
                    String newTitle = sc.nextLine().trim();

                    if (!newTitle.isEmpty()) {
                        bookToUpdate.setTitle(newTitle);
                    }

                    System.out.print("Enter new role(leave blank to keep current): ");
                    String newAuthor = sc.nextLine().trim();

                    if (!newAuthor.isEmpty()) {
                        bookToUpdate.setAuthor(newAuthor);
                    }
                    
                    BookFileManager.updateBooks(bookList); //to update the txt

                    System.out.println("Book updated successfully.");
                    break;

                case 5: // DELETE
                    System.out.print("Enter Book ID to delete: ");
                    String deleteId = sc.nextLine().trim();

                    System.out.print("Confirm delete? (Y/N): ");
                    String confirm = sc.nextLine();

                    if (confirm.equalsIgnoreCase("Y")) {
                        if (bookList.removeBookById(deleteId)) {
                            System.out.println( "This book is deleted.");
                            
                            //after delete the data, also need update to txt
                            BookFileManager.updateBooks(bookList);
                        } else {
                            System.out.println("Book not found.");
                        }
                    } else {
                        System.out.println("Delete cancelled.");
                    }
                    
                    break;

                case 6:
                    System.out.println("Exiting system...");
                    break;

                default:
                    System.out.println("Invalid choice. Enter 1–6.");
            }

        } while (choice != 6);

        sc.close();
    }
    
}
