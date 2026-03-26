package com.mycompany.tarumtlibraryservices.adt;

import com.mycompany.tarumtlibraryservices.model.Transaction;
import java.time.LocalDate;

/**
 * TransactionList ADT - Manages Transaction objects.
 * Extends GenericList<Transaction> with transaction-specific operations.
 *
 * @author [Your Name]
 */
public class TransactionList extends GenericList<Transaction> {

    private static final String DEFAULT_FILE_NAME = "transactions.txt";

    public TransactionList() {
        super(DEFAULT_FILE_NAME);
    }

    public TransactionList(String fileName) {
        super(fileName);
    }

    // ========== TRANSACTION-SPECIFIC METHODS ==========

    /**
     * Add a new transaction.
     */
    public boolean addTransaction(Transaction t) {
        return add(t);
    }

    /**
     * Find a transaction by its ID.
     */
    public Transaction getTransactionById(String transactionId) {
        return findFirst(t -> t.getTransactionId().equalsIgnoreCase(transactionId));
    }

    /**
     * Generate the next transaction ID automatically (e.g., T001, T002…).
     */
    public String generateNextId() {
        int max = 0;
        Node<Transaction> current = head;
        while (current != null) {
            String id = current.data.getTransactionId();
            if (id != null && id.toUpperCase().startsWith("T")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {}
            }
            current = current.next;
        }
        return String.format("T%03d", max + 1);
    }

    /**
     * Check if a user currently has this book borrowed (not yet returned).
     */
    public boolean hasActiveBorrow(String userId, String bookId) {
        return contains(t ->
                t.getUserId().equalsIgnoreCase(userId) &&
                t.getBookId().equalsIgnoreCase(bookId) &&
                t.isBorrowed()
        );
    }

    /**
     * Find the active (BORROWED) transaction for a given book.
     */
    public Transaction getActiveBorrowByBook(String bookId) {
        return findFirst(t ->
                t.getBookId().equalsIgnoreCase(bookId) && t.isBorrowed()
        );
    }

    /**
     * Get all transactions for a specific user.
     */
    public Transaction[] getTransactionsByUser(String userId) {
        return findAll(
                t -> t.getUserId().equalsIgnoreCase(userId),
                Transaction[]::new
        );
    }

    /**
     * Get all currently borrowed (not returned) transactions.
     */
    public Transaction[] getAllActiveBorrows() {
        return findAll(Transaction::isBorrowed, Transaction[]::new);
    }

    /**
     * Get all returned transactions.
     */
    public Transaction[] getAllReturned() {
        return findAll(t -> !t.isBorrowed(), Transaction[]::new);
    }

    /**
     * Get all transactions as an array.
     */
    public Transaction[] getAllTransactions() {
        return toArray(Transaction[]::new);
    }

    /**
     * Count active borrows for a user.
     */
    public int countActiveBorrows(String userId) {
        return count(t -> t.getUserId().equalsIgnoreCase(userId) && t.isBorrowed());
    }

    // ========== DISPLAY ==========

    public void displayAll() {
        if (isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        printHeader();
        forEach(t -> System.out.println(t));
        System.out.println("=".repeat(72));
        System.out.println("Total transactions: " + getSize());
    }

    public void displayForUser(String userId) {
        Transaction[] txns = getTransactionsByUser(userId);
        if (txns.length == 0) {
            System.out.println("No transactions found for user: " + userId);
            return;
        }
        printHeader();
        for (Transaction t : txns) System.out.println(t);
        System.out.println("=".repeat(72));
        System.out.println("Total: " + txns.length);
    }

    private void printHeader() {
        System.out.println("\n" + "=".repeat(72));
        System.out.printf("%-8s | %-8s | %-8s | %-12s | %-12s | %s%n",
                "Txn ID", "User ID", "Book ID",
                "Borrow Date", "Return Date", "Status");
        System.out.println("=".repeat(72));
    }

    // ========== PERSISTENCE (saveElement / parseElement) ==========

    @Override
    protected String saveElement(Transaction t) {
        // Format: transactionId|userId|bookId|borrowDate|returnDate|status
        return t.getTransactionId() + "|"
                + t.getUserId() + "|"
                + t.getBookId() + "|"
                + t.getBorrowDateStr() + "|"
                + t.getReturnDateStr() + "|"
                + t.getStatus();
    }

    @Override
    protected Transaction parseElement(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] p = line.split("\\|");
        if (p.length < 6) return null;

        String id         = p[0].trim();
        String userId     = p[1].trim();
        String bookId     = p[2].trim();
        LocalDate borrow  = parseDate(p[3].trim());
        LocalDate ret     = parseDate(p[4].trim());
        String status     = p[5].trim();

        return new Transaction(id, userId, bookId, borrow, ret, status);
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.equals("-") || s.isEmpty()) return null;
        try { return LocalDate.parse(s); } catch (Exception e) { return null; }
    }
}
