package com.mycompany.tarumtlibraryservices.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single borrow/return transaction in the library system.
 *
 * @author [Your Name]
 */
public class Transaction {

    public static final String STATUS_BORROWED = "BORROWED";
    public static final String STATUS_RETURNED = "RETURNED";

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String transactionId;  // e.g. T001
    private String userId;
    private String bookId;
    private LocalDate borrowDate;
    private LocalDate returnDate;  // null until returned
    private String status;         // BORROWED or RETURNED

    // Constructor for a new borrow
    public Transaction(String transactionId, String userId, String bookId) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = LocalDate.now();
        this.returnDate = null;
        this.status = STATUS_BORROWED;
    }

    // Full constructor (used when loading from file)
    public Transaction(String transactionId, String userId, String bookId,
                       LocalDate borrowDate, LocalDate returnDate, String status) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    // ========== GETTERS ==========
    public String getTransactionId() { return transactionId; }
    public String getUserId()        { return userId; }
    public String getBookId()        { return bookId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public String getStatus()        { return status; }

    public boolean isBorrowed() {
        return STATUS_BORROWED.equals(status);
    }

    // ========== SETTERS ==========
    public void markReturned() {
        this.status = STATUS_RETURNED;
        this.returnDate = LocalDate.now();
    }

    // ========== HELPERS ==========
    public String getBorrowDateStr() {
        return borrowDate != null ? borrowDate.format(DATE_FMT) : "-";
    }

    public String getReturnDateStr() {
        return returnDate != null ? returnDate.format(DATE_FMT) : "-";
    }

    @Override
    public String toString() {
        return String.format("%-8s | %-8s | %-8s | %-12s | %-12s | %s",
                transactionId, userId, bookId,
                getBorrowDateStr(), getReturnDateStr(), status);
    }
}
