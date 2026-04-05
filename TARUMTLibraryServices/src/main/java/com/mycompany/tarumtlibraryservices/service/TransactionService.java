package com.mycompany.tarumtlibraryservices.service;

import com.mycompany.tarumtlibraryservices.adt.TransactionList;
import com.mycompany.tarumtlibraryservices.adt.UserList;
import com.mycompany.tarumtlibraryservices.adt.BookList;
import com.mycompany.tarumtlibraryservices.model.Book;
import com.mycompany.tarumtlibraryservices.model.Transaction;
import com.mycompany.tarumtlibraryservices.model.User;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TransactionService - Business logic for borrow and return operations.
 *
 * @author Jeffery Lee Jia Hao
 */
public class TransactionService {

    private final TransactionList transactionList;
    private final BookList bookList;
    private final UserList userList;

    public TransactionService(TransactionList transactionList,
                              BookList bookList,
                              UserList userList) {
        this.transactionList = transactionList;
        this.bookList = bookList;
        this.userList = userList;
    }

    // ========== BORROW ==========

    /**
     * Borrow a book for a user. Returns a result message.
     */
    public String borrowBook(String userId, String bookId) {
        // Validate user
        User user = userList.getUserById(userId);
        if (user == null) return "User not found: " + userId;
        if (!user.canBorrowBooks()) return "User account is inactive.";

        // Validate book
        Book book = bookList.getBookById(bookId);
        if (book == null) return "Book not found: " + bookId;
        if (!book.isAvailable()) return "Book is currently not available (already borrowed).";

        // Check the user doesn't already have this book
        if (transactionList.hasActiveBorrow(userId, bookId)) {
            return "User already has this book borrowed.";
        }

        // Create transaction
        String newId = transactionList.generateNextId();
        Transaction txn = new Transaction(newId, userId, bookId);
        transactionList.addTransaction(txn);

        // Mark book as unavailable
        book.setIsAvailable(false);
        bookList.saveToFile();

        return "SUCCESS|" + newId;
    }

    // ========== RETURN ==========

    /**
     * Return a book. Finds the active borrow record and marks it returned.
     */
    public String returnBook(String bookId) {
        // Find active borrow for this book
        Transaction txn = transactionList.getActiveBorrowByBook(bookId);
        if (txn == null) return "No active borrow record found for book: " + bookId;

        // Check book exists
        Book book = bookList.getBookById(bookId);
        if (book == null) return "Book not found: " + bookId;

        // Mark returned
        txn.markReturned();
        transactionList.saveToFile();

        // Mark book as available again
        book.setIsAvailable(true);
        bookList.saveToFile();

        return "SUCCESS|" + txn.getTransactionId();
    }

    // ========== REPORT GENERATION ==========

    /**
     * Print full transaction history to console.
     */
    public void printFullReport() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("TRANSACTION REPORT - ALL RECORDS");
        System.out.println("Generated: " + now());
        System.out.println("=".repeat(80));

        Transaction[] all = transactionList.getAllTransactions();
        int borrowed = 0, returned = 0;
        for (Transaction t : all) {
            if (t.isBorrowed()) borrowed++;
            else returned++;
        }

        System.out.println("\nSUMMARY:");
        System.out.printf("  %-20s: %d%n", "Total Transactions", all.length);
        System.out.printf("  %-20s: %d%n", "Currently Borrowed", borrowed);
        System.out.printf("  %-20s: %d%n", "Returned", returned);

        System.out.println("\nDETAILS:");
        transactionList.displayAll();
    }

    /**
     * Print transaction history for one user.
     */
    public void printUserHistory(String userId) {
        User user = userList.getUserById(userId);
        String name = user != null ? user.getName() : "Unknown";

        System.out.println("\n" + "=".repeat(80));
        System.out.println("TRANSACTION HISTORY - " + userId + " (" + name + ")");
        System.out.println("Generated: " + now());
        System.out.println("=".repeat(80));

        Transaction[] txns = transactionList.getTransactionsByUser(userId);
        if (txns.length == 0) {
            System.out.println("No transactions found for this user.");
            return;
        }

        int borrowed = 0, returned = 0;
        for (Transaction t : txns) {
            if (t.isBorrowed()) borrowed++;
            else returned++;
        }

        System.out.printf("  Active borrows: %d  |  Returned: %d%n%n", borrowed, returned);
        transactionList.displayForUser(userId);
    }

    /**
     * Print all currently borrowed (not returned) books.
     */
    public void printActiveBorrows() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("CURRENTLY BORROWED BOOKS");
        System.out.println("Generated: " + now());
        System.out.println("=".repeat(80));

        Transaction[] active = transactionList.getAllActiveBorrows();
        if (active.length == 0) {
            System.out.println("No books currently borrowed.");
            return;
        }

        System.out.printf("%-8s | %-8s | %-8s | %-12s | %-25s | %-20s%n",
                "Txn ID", "User ID", "Book ID", "Borrow Date", "Book Title", "Borrower");
        System.out.println("-".repeat(80));

        for (Transaction t : active) {
            Book book = bookList.getBookById(t.getBookId());
            User user = userList.getUserById(t.getUserId());
            String title = book != null ? book.getTitle() : "?";
            String userName = user != null ? user.getName() : "?";

            System.out.printf("%-8s | %-8s | %-8s | %-12s | %-25s | %-20s%n",
                    t.getTransactionId(), t.getUserId(), t.getBookId(),
                    t.getBorrowDateStr(), trunc(title, 25), trunc(userName, 20));
        }

        System.out.println("=".repeat(80));
        System.out.println("Total currently borrowed: " + active.length);
    }

    /**
     * Export the full report to a dated .txt file.
     */
    public void exportReportToFile() {
        String filename = "TransactionReport_" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";

        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("=".repeat(80));
            pw.println("TARUMT LIBRARY SERVICES - TRANSACTION REPORT");
            pw.println("Generated: " + now());
            pw.println("=".repeat(80));

            Transaction[] all = transactionList.getAllTransactions();
            pw.printf("Total Transactions : %d%n", all.length);
            pw.printf("Currently Borrowed : %d%n",
                    transactionList.getAllActiveBorrows().length);
            pw.printf("Returned           : %d%n",
                    transactionList.getAllReturned().length);
            pw.println();

            pw.printf("%-8s | %-8s | %-8s | %-12s | %-12s | %s%n",
                    "Txn ID", "User ID", "Book ID",
                    "Borrow Date", "Return Date", "Status");
            pw.println("-".repeat(72));

            for (Transaction t : all) {
                pw.println(t);
            }

            pw.println("=".repeat(80));
            pw.println("END OF REPORT");

            System.out.println("Report exported to: " + filename);
        } catch (IOException e) {
            System.err.println("Error exporting report: " + e.getMessage());
        }
    }

    // ========== HELPERS ==========
    private String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String trunc(String s, int max) {
        if (s == null || s.length() <= max) return s != null ? s : "";
        return s.substring(0, max - 3) + "...";
    }
}