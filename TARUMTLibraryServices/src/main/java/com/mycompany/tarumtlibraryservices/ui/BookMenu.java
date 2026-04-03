package com.mycompany.tarumtlibraryservices.ui;

import com.mycompany.tarumtlibraryservices.adt.BookList;
import com.mycompany.tarumtlibraryservices.model.Book;
import com.mycompany.tarumtlibraryservices.model.BookSource;
import com.mycompany.tarumtlibraryservices.model.User;
import com.mycompany.tarumtlibraryservices.service.BookManager;
import java.util.Scanner;

public class BookMenu {

    private final Scanner sc;
    private final User currentUser;
    private final BookList bookList;
    private final BookManager bookManager;

    public BookMenu(Scanner sc, User currentUser, BookList bookList) {
        this.sc = sc;
        this.currentUser = currentUser;
        this.bookList = bookList;
        this.bookManager = new BookManager(bookList);
    }

    public void start() {
        int choice = 0;

        do {
            displayMenu();
            System.out.print("Enter your choice: ");
            String input = sc.nextLine().trim();

            if (!input.matches("[1-7]")) {
                System.out.println("Please enter a number between 1 and 7.");
                continue;
            }

            choice = Integer.parseInt(input);

        } while (handleChoice(choice));
    }

    private void displayMenu() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         BOOK MANAGEMENT               ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Logged in as: " + currentUser.getName()
                + " (" + currentUser.getRoleDisplayName() + ")");
        System.out.println("=".repeat(40));
        System.out.println("1. Add Book");
        System.out.println("2. View All Books");
        System.out.println("3. Search Book");
        System.out.println("4. Update Book");
        System.out.println("5. Delete Book");
        System.out.println("6. View Reports");
        System.out.println("7. Back to Main Menu");
        System.out.println("=".repeat(40));
    }

    private boolean handleChoice(int choice) {
        switch (choice) {
            case 1:
                addBook();
                return true;
            case 2:
                viewAllBooks();
                return true;
            case 3:
                searchBook();
                return true;
            case 4:
                updateBook();
                return true;
            case 5:
                deleteBook();
                return true;
            case 6:
                showReportMenu();
                return true;
            case 7:
                System.out.println("Returning to main menu...");
                return false;
            default:
                return true;
        }
    }

    private void addBook() {
        String id;
        while (true) {
            System.out.print("Enter Book ID (B001): ");
            id = sc.nextLine().trim().toUpperCase();

            if (id.isEmpty() || !id.matches("B\\d{3}")) {
                System.out.println("Invalid ID format. Example: B001");
                continue;
            }

            if (bookList.getBookById(id) != null) {
                System.out.println("Book ID already exists. Please enter another ID.");
                continue;
            }

            break;
        }

        String title;
        while (true) {
            System.out.print("Enter Book Title: ");
            title = sc.nextLine().trim();

            if (title.isEmpty() || !title.matches("[A-Za-z0-9 ,:'\\-\\.]+")) {
                System.out.println("Invalid book title. Try again.");
            } else {
                break;
            }
        }

        String author;
        while (true) {
            System.out.print("Enter Author: ");
            author = sc.nextLine().trim();

            if (author.isEmpty()) {
                System.out.println("Author name cannot be empty. Try again.");
            } else if (!author.matches("[A-Za-z .'-]+")) {
                System.out.println("Author name contains invalid characters. Try again.");
            } else {
                break;
            }
        }

        BookSource source;
        while (true) {
            System.out.print("Enter Source (1 = NEW, 2 = DONATED): ");
            String sourceInput = sc.nextLine().trim();

            if (sourceInput.equals("1")) {
                source = BookSource.NEW;
                break;
            } else if (sourceInput.equals("2")) {
                source = BookSource.DONATED;
                break;
            } else {
                System.out.println("Invalid choice. Please enter 1 or 2.");
            }
        }

        Book newBook = new Book(id, title, author, source, true);

        if (bookList.addBook(newBook)) {
            bookList.saveToFile();
            System.out.println("Book added successfully.");
        } else {
            System.out.println("Error: Book ID already exists. Cannot be duplicate.");
        }

        pause();
    }

    private void viewAllBooks() {
        if (bookList.isEmpty()) {
            System.out.println("No books found.");
        } else {
            bookList.displayAllBooks();
        }
        pause();
    }

    private void searchBook() {
        System.out.println("Search Book By:");
        System.out.println("1. Book ID");
        System.out.println("2. Title");
        System.out.println("3. Author");

        String searchChoice;
        while (true) {
            System.out.print("Choose: ");
            searchChoice = sc.nextLine().trim();

            if (searchChoice.matches("[1-3]")) {
                break;
            } else {
                System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }

        String keyword;
        while (true) {
            System.out.print("Enter Keyword: ");
            keyword = sc.nextLine().trim();

            if (!keyword.isEmpty()) {
                break;
            } else {
                System.out.println("Keyword cannot be empty.");
            }
        }

        final String key = keyword.toLowerCase();

        switch (searchChoice) {
            case "1":
                Book found = bookList.getBookById(keyword);
                System.out.println(found != null ? found : "Book not found.");
                break;

            case "2":
                Book[] titleResults = bookList.findAll(
                    book -> book.getTitle() != null &&
                            book.getTitle().toLowerCase().contains(key),
                    Book[]::new
                );

                if (titleResults.length == 0) {
                    System.out.println("No book found.");
                } else {
                    for (Book b : titleResults) {
                        System.out.println(b);
                    }
                }
                break;

            case "3":
                Book[] authorResults = bookList.findAll(
                    book -> book.getAuthor() != null &&
                            book.getAuthor().toLowerCase().contains(key),
                    Book[]::new
                );

                if (authorResults.length == 0) {
                    System.out.println("No book found.");
                } else {
                    for (Book b : authorResults) {
                        System.out.println(b);
                    }
                }
                break;
        }

        pause();
    }

    private void updateBook() {
        System.out.print("Enter Book ID: ");
        String updateId = sc.nextLine().trim();

        Book bookToUpdate = bookList.getBookById(updateId);

        if (bookToUpdate == null) {
            System.out.println("Book not found.");
            pause();
            return;
        }

        System.out.println("Current details: " + bookToUpdate);

        System.out.print("Enter new title (leave blank to keep current): ");
        String newTitle = sc.nextLine().trim();
        if (!newTitle.isEmpty()) {
            bookToUpdate.setTitle(newTitle);
        }

        System.out.print("Enter new author (leave blank to keep current): ");
        String newAuthor = sc.nextLine().trim();
        if (!newAuthor.isEmpty()) {
            bookToUpdate.setAuthor(newAuthor);
        }

        bookList.saveToFile();
        System.out.println("Book updated successfully.");
        pause();
    }

    private void deleteBook() {
        System.out.print("Enter Book ID to delete: ");
        String deleteId = sc.nextLine().trim();

        System.out.print("Confirm delete? (Y/N): ");
        String confirm = sc.nextLine().trim();

        if (confirm.equalsIgnoreCase("Y")) {
            if (bookList.removeBookById(deleteId)) {
                bookList.saveToFile();
                System.out.println("This book is deleted.");
            } else {
                System.out.println("Book not found.");
            }
        } else {
            System.out.println("Delete cancelled.");
        }

        pause();
    }

    private void showReportMenu() {
        int reportChoice = 0;

        do {
            System.out.println("\n=== BOOK REPORT MENU ===");
            System.out.println("1. Full Book Report");
            System.out.println("2. NEW Book Report");
            System.out.println("3. DONATED Book Report");
            System.out.println("4. Search by Title Report");
            System.out.println("5. Search by Author Report");
            System.out.println("6. Export Report to File");
            System.out.println("7. Back to Book Menu");

            System.out.print("Enter your choice: ");
            String input = sc.nextLine().trim();

            if (!input.matches("[1-7]")) {
                System.out.println("Please enter a number between 1 and 7.");
                continue;
            }

            reportChoice = Integer.parseInt(input);

            switch (reportChoice) {
                case 1:
                    bookManager.generateBookReport();
                    pause();
                    break;

                case 2:
                    bookManager.generateSourceBasedReport(BookSource.NEW);
                    pause();
                    break;

                case 3:
                    bookManager.generateSourceBasedReport(BookSource.DONATED);
                    pause();
                    break;

                case 4:
                    String titleKeyword;
                    while (true) {
                        System.out.print("Enter title keyword: ");
                        titleKeyword = sc.nextLine().trim();
                        if (!titleKeyword.isEmpty()) {
                            break;
                        }
                        System.out.println("Keyword cannot be empty.");
                    }
                    bookManager.generateTitleSearchReport(titleKeyword);
                    pause();
                    break;

                case 5:
                    String authorKeyword;
                    while (true) {
                        System.out.print("Enter author keyword: ");
                        authorKeyword = sc.nextLine().trim();
                        if (!authorKeyword.isEmpty()) {
                            break;
                        }
                        System.out.println("Keyword cannot be empty.");
                    }
                    bookManager.generateAuthorSearchReport(authorKeyword);
                    pause();
                    break;

                case 6:
                    bookManager.exportReportToFile("Full Book Report");
                    pause();
                    break;

                case 7:
                    System.out.println("Returning to Book Menu...");
                    break;
            }

        } while (reportChoice != 7);
    }

    private void pause() {
        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }
}