package com.mycompany.tarumtlibraryservices.ui;

import com.mycompany.tarumtlibraryservices.adt.BookList;
import com.mycompany.tarumtlibraryservices.adt.TransactionList;
import com.mycompany.tarumtlibraryservices.adt.UserList;
import com.mycompany.tarumtlibraryservices.model.Book;
import com.mycompany.tarumtlibraryservices.model.Transaction;
import com.mycompany.tarumtlibraryservices.model.User;
import com.mycompany.tarumtlibraryservices.service.TransactionService;
import java.util.Scanner;

public class BorrowReturnMenu {

    private final Scanner sc;
    private final User currentUser;
    private final TransactionService transactionService;
    private final BookList bookList;
    private final TransactionList transactionList;

    public BorrowReturnMenu(Scanner sc, User currentUser,
                            BookList bookList, UserList userList,
                            TransactionList transactionList) {
        this.sc = sc;
        this.currentUser = currentUser;
        this.bookList = bookList;
        this.transactionList = transactionList;
        this.transactionService = new TransactionService(transactionList, bookList, userList);
    }

    public void start() {
        int choice = 0;
        do {
            displayMenu();
            System.out.print("Enter your choice: ");
            String input = sc.nextLine().trim();

            int max = getMaxChoice();
            if (!input.matches("[1-" + max + "]")) {
                System.out.println("Please enter a number between 1 and " + max + ".");
                continue;
            }
            choice = Integer.parseInt(input);
        } while (handleChoice(choice));
    }

    private void displayMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         BORROW & RETURN");
        System.out.println("=".repeat(50));
        System.out.println("Logged in as: " + currentUser.getName()
                + " (" + currentUser.getRoleDisplayName() + ")");
        System.out.println("-".repeat(40));

        int n = 1;
        System.out.println(n++ + ". Borrow a Book");
        System.out.println(n++ + ". Return a Book");
        System.out.println(n++ + ". My Transaction History");

        if (currentUser.canViewReports()) {
            System.out.println(n++ + ". View All Borrowed Books");
            System.out.println(n++ + ". Full Transaction Report");
            System.out.println(n++ + ". View History by User");
            System.out.println(n++ + ". Export Report to File");
        }

        System.out.println(n + ". Back to Main Menu");
        System.out.println("=".repeat(40));
    }

    private int getMaxChoice() {
        int base = 4;
        if (currentUser.canViewReports()) base += 4;
        return base;
    }

    private boolean handleChoice(int choice) {
        int n = 1;

        if (choice == n++) { borrowBook(); return true; }
        if (choice == n++) { returnBook(); return true; }
        if (choice == n++) {
            transactionService.printUserHistory(currentUser.getUserId());
            pause();
            return true;
        }

        if (currentUser.canViewReports()) {
            if (choice == n++) {
                transactionService.printActiveBorrows();
                pause();
                return true;
            }
            if (choice == n++) {
                transactionService.printFullReport();
                pause();
                return true;
            }
            if (choice == n++) {
                viewHistoryByUser();
                return true;
            }
            if (choice == n++) {
                transactionService.exportReportToFile();
                pause();
                return true;
            }
        }

        System.out.println("Returning to main menu...");
        return false;
    }

    private void borrowBook() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("             BORROW A BOOK");
        System.out.println("=".repeat(50));

        Book[] available = getAvailableBooks();
        if (available.length == 0) {
            System.out.println("No books are currently available for borrowing.");
            pause();
            return;
        }

        System.out.println("\nAVAILABLE BOOKS:");
        System.out.println("-".repeat(50));
        System.out.printf("%-8s | %-30s | %-15s%n", "Book ID", "Title", "Author");
        System.out.println("-".repeat(50));
        for (Book b : available) {
            System.out.printf("%-8s | %-30s | %-15s%n",
                    b.getBookId(), trunc(b.getTitle(), 30), trunc(b.getAuthor(), 15));
        }
        System.out.println("-".repeat(50));

        System.out.print("\nEnter Book ID to borrow: ");
        String bookId = sc.nextLine().trim().toUpperCase();
        if (bookId.isEmpty()) { System.out.println("Cancelled."); pause(); return; }

        String userId = currentUser.getUserId();

        if (currentUser.canViewReports()) {
            System.out.print("Enter User ID (press Enter to borrow for yourself [" + userId + "]): ");
            String inputId = sc.nextLine().trim();
            if (!inputId.isEmpty()) userId = inputId;
        }

        String result = transactionService.borrowBook(userId, bookId);

        if (result.startsWith("SUCCESS")) {
            String txnId = result.split("\\|")[1];
            System.out.println("\n[SUCCESS] Book borrowed successfully!");
            System.out.println("   Transaction ID : " + txnId);
            System.out.println("   User           : " + userId);
            System.out.println("   Book ID        : " + bookId);
        } else {
            System.out.println("\n[ERROR] " + result);
        }
        pause();
    }

    private void returnBook() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("             RETURN A BOOK");
        System.out.println("=".repeat(50));

        Transaction[] myBorrows;
        if (currentUser.canViewReports()) {
            myBorrows = transactionList.getAllActiveBorrows();
        } else {
            myBorrows = transactionList.getTransactionsByUser(currentUser.getUserId());
            int cnt = 0;
            for (Transaction t : myBorrows) if (t.isBorrowed()) cnt++;
            Transaction[] filtered = new Transaction[cnt];
            int i = 0;
            for (Transaction t : myBorrows) if (t.isBorrowed()) filtered[i++] = t;
            myBorrows = filtered;
        }

        if (myBorrows.length == 0) {
            System.out.println("No books currently borrowed.");
            pause();
            return;
        }

        System.out.println("\nCURRENTLY BORROWED:");
        System.out.println("-".repeat(60));
        System.out.printf("%-8s | %-8s | %-8s | %-25s | %-12s%n",
                "Txn ID", "User ID", "Book ID", "Book Title", "Borrow Date");
        System.out.println("-".repeat(60));
        for (Transaction t : myBorrows) {
            Book b = bookList.getBookById(t.getBookId());
            String title = b != null ? b.getTitle() : "?";
            System.out.printf("%-8s | %-8s | %-8s | %-25s | %-12s%n",
                    t.getTransactionId(), t.getUserId(), t.getBookId(),
                    trunc(title, 25), t.getBorrowDateStr());
        }
        System.out.println("-".repeat(60));

        System.out.print("\nEnter Book ID to return: ");
        String bookId = sc.nextLine().trim().toUpperCase();
        if (bookId.isEmpty()) { System.out.println("Cancelled."); pause(); return; }

        String result = transactionService.returnBook(bookId);

        if (result.startsWith("SUCCESS")) {
            String txnId = result.split("\\|")[1];
            System.out.println("\n[SUCCESS] Book returned successfully!");
            System.out.println("   Transaction ID : " + txnId);
            System.out.println("   Book ID        : " + bookId);
        } else {
            System.out.println("\n[ERROR] " + result);
        }
        pause();
    }

    private void viewHistoryByUser() {
        System.out.print("Enter User ID: ");
        String uid = sc.nextLine().trim();
        if (uid.isEmpty()) { System.out.println("Cancelled."); return; }
        transactionService.printUserHistory(uid);
        pause();
    }

    private Book[] getAvailableBooks() {
        return bookList.findAll(Book::isAvailable, Book[]::new);
    }

    private void pause() {
        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }

    private String trunc(String s, int max) {
        if (s == null || s.length() <= max) return s != null ? s : "";
        return s.substring(0, max - 3) + "...";
    }
}