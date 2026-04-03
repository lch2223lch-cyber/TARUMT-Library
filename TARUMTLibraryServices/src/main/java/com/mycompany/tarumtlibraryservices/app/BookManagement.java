package com.mycompany.tarumtlibraryservices.app;

import com.mycompany.tarumtlibraryservices.adt.BookList;
import com.mycompany.tarumtlibraryservices.model.Book;
import com.mycompany.tarumtlibraryservices.model.BookSource;
import java.util.Scanner;

public class BookManagement {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        BookList bookList = new BookList(); // ✅ auto load from books.txt
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
                System.out.println("Please enter 1–6.");
                continue;
            }

            choice = Integer.parseInt(input);

            switch (choice) {

                // ===== ADD =====
                case 1:
                    String id;
                    while (true) {
                        System.out.print("Enter Book ID (B001): ");
                        id = sc.nextLine().trim();

                        if (!id.matches("B\\d{3}")) {
                            System.out.println("Invalid ID format.");
                            continue;
                        }

                        if (bookList.bookExists(id)) {
                            System.out.println("Book ID already exists.");
                            continue;
                        }

                        break;
                    }

                    System.out.print("Enter Title: ");
                    String title = sc.nextLine();

                    System.out.print("Enter Author: ");
                    String author = sc.nextLine();

                    BookSource source;
                    while (true) {
                        System.out.print("Source (1=NEW, 2=DONATED): ");
                        String s = sc.nextLine();

                        if (s.equals("1")) {
                            source = BookSource.NEW;
                            break;
                        } else if (s.equals("2")) {
                            source = BookSource.DONATED;
                            break;
                        }
                        System.out.println("Invalid input.");
                    }

                    Book newBook = new Book(id, title, author, source, true);

                    if (bookList.addBook(newBook)) {
                        bookList.saveToFile(); // ✅ NEW WAY
                        System.out.println("Book added.");
                    } else {
                        System.out.println("Duplicate ID.");
                    }
                    break;

                // ===== VIEW =====
                case 2:
                    bookList.displayAllBooks();
                    break;

                // ===== SEARCH =====
                case 3:
                    System.out.println("1. ID  2. Title  3. Author");

                    String opt;
                    while (true) {
                        System.out.print("Choose: ");
                        opt = sc.nextLine();
                        if (opt.matches("[1-3]")) break;
                        System.out.println("Invalid choice.");
                    }

                    System.out.print("Keyword: ");
                    String keyword = sc.nextLine();
                    final String key = keyword.toLowerCase();

                    switch (opt) {
                        case "1":
                            Book found = bookList.getBookById(keyword);
                            System.out.println(found != null ? found : "Not found");
                            break;

                        case "2":
                            Book[] t = bookList.searchBooksByTitle(keyword);
                            for (Book b : t) System.out.println(b);
                            break;

                        case "3":
                            Book[] a = bookList.searchBooksByAuthor(keyword);
                            for (Book b : a) System.out.println(b);
                            break;
                    }
                    break;

                // ===== UPDATE =====
                case 4:
                    System.out.print("Enter Book ID: ");
                    String uid = sc.nextLine();

                    Book book = bookList.getBookById(uid);

                    if (book == null) {
                        System.out.println("Not found.");
                        break;
                    }

                    System.out.print("New title: ");
                    String nt = sc.nextLine();
                    if (!nt.isEmpty()) book.setTitle(nt);

                    System.out.print("New author: ");
                    String na = sc.nextLine();
                    if (!na.isEmpty()) book.setAuthor(na);

                    bookList.saveToFile(); // ✅
                    System.out.println("Updated.");
                    break;

                // ===== DELETE =====
                case 5:
                    System.out.print("Enter ID: ");
                    String did = sc.nextLine();

                    if (bookList.removeBookById(did)) {
                        bookList.saveToFile(); // ✅
                        System.out.println("Deleted.");
                    } else {
                        System.out.println("Not found.");
                    }
                    break;

                case 6:
                    System.out.println("Exit.");
                    break;
            }

        } while (choice != 6);

        sc.close();
    }
}