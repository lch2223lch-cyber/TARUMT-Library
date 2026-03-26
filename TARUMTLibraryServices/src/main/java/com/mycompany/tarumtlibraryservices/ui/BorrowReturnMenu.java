package com.mycompany.tarumtlibraryservices.ui;

import com.mycompany.tarumtlibraryservices.adt.Book.BookList;
import com.mycompany.tarumtlibraryservices.adt.TransactionList;
import com.mycompany.tarumtlibraryservices.adt.UserList;
import com.mycompany.tarumtlibraryservices.model.Book;
import com.mycompany.tarumtlibraryservices.model.Transaction;
import com.mycompany.tarumtlibraryservices.model.User;
import com.mycompany.tarumtlibraryservices.service.TransactionService;
import java.util.Scanner;

/**
 * BorrowReturnMenu - UI for the Borrow & Return module.
 * Handles borrowing, returning, transaction history, and reports.
 *
 * @author [Your Name]
 */
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
            // Build valid pattern dynamically
            if (!input.matches("[1-" + max + "]")) {
                System.out.println("Please enter a number between 1 and " + max + ".");
                continue;
            }
            choice = Integer.parseInt(input);
        } while (handleChoice(choice));
    }

    // ========== MENU DISPLAY ==========

    private void displayMenu() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         BORROW & RETURN               ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Logged in as: " + currentUser.getName()
                + " (" + currentUser.getRoleDisplayName() + ")");
        System.out.println("=".repeat(40));

        int n = 1;
        System.out.println(n++ + ". Borrow a Book");
        System.out.println(n++ + ". Return a Book");
        System.out.println(n++ + ". My Transaction History");

        // Librarians and Admins get extra options
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
        int base = 4; // Borrow, Return, My History, Back
        if (currentUser.canViewReports()) base += 4; // All Borrowed, Full Report, By User, Export
        return base;
    }

    // ========== CHOICE HANDLER ==========

    private boolean handleChoice(int choice) {
        int n = 1;

        // 1. Borrow a Book
        if (choice == n++) { borrowBook(); return true; }

        // 2. Return a Book
        if (choice == n++) { returnBook(); return true; }

        // 3. My Transaction History
        if (choice == n++) {
            transactionService.printUserHistory(currentUser.getUserId());
            pause();
            return true;
        }

        // Librarian/Admin-only options
        if (currentUser.canViewReports()) {
            // 4. View All Borrowed Books
            if (choice == n++) {
                transactionService.printActiveBorrows();
                pause();
                return true;
            }
            // 5. Full Transaction Report
            if (choice == n++) {
                transactionService.printFullReport();
                pause();
                return true;
            }
            // 6. View History by User
            if (choice == n++) {
                viewHistoryByUser();
                return true;
            }
            // 7. Export Report to File
            if (choice == n++) {
                transactionService.exportReportToFile();
                pause();
                return true;
            }
        }

        // Back to Main Menu (last option)
        System.out.println("Returning to main menu...");
        return false;
    }

    // ========== BORROW ==========

    private void borrowBook() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║             BORROW A BOOK             ║");
        System.out.println("╚════════════════════════════════════════╝");

        // Print available books first
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

        // Get Book ID
        System.out.print("\nEnter Book ID to borrow: ");
        String bookId = sc.nextLine().trim();
        if (bookId.isEmpty()) { System.out.println("Cancelled."); pause(); return; }

        // Determine which user is borrowing
        String userId = currentUser.getUserId();

        // Admins/Librarians can borrow on behalf of a student
        if (currentUser.canViewReports()) {
            System.out.print("Enter User ID (press Enter to borrow for yourself [" + userId + "]): ");
            String inputId = sc.nextLine().trim();
            if (!inputId.isEmpty()) userId = inputId;
        }

        String result = transactionService.borrowBook(userId, bookId);

        if (result.startsWith("SUCCESS")) {
            String txnId = result.split("\\|")[1];
            System.out.println("\n✅ Book borrowed successfully!");
            System.out.println("   Transaction ID : " + txnId);
            System.out.println("   User           : " + userId);
            System.out.println("   Book ID        : " + bookId);
        } else {
            System.out.println("\n❌ " + result);
        }
        pause();
    }

    // ========== RETURN ==========

    private void returnBook() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║             RETURN A BOOK             ║");
        System.out.println("╚════════════════════════════════════════╝");

        // Show the user's currently borrowed books
        Transaction[] myBorrows;
        if (currentUser.canViewReports()) {
            // Librarian/Admin sees all currently borrowed
            myBorrows = transactionList.getAllActiveBorrows();
        } else {
            // Student sees only their own active borrows
            myBorrows = transactionList.getTransactionsByUser(currentUser.getUserId());
            // Filter to only BORROWED
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
        String bookId = sc.nextLine().trim();
        if (bookId.isEmpty()) { System.out.println("Cancelled."); pause(); return; }

        String result = transactionService.returnBook(bookId);

        if (result.startsWith("SUCCESS")) {
            String txnId = result.split("\\|")[1];
            System.out.println("\n✅ Book returned successfully!");
            System.out.println("   Transaction ID : " + txnId);
            System.out.println("   Book ID        : " + bookId);
        } else {
            System.out.println("\n❌ " + result);
        }
        pause();
    }

    // ========== HISTORY BY USER ==========

    private void viewHistoryByUser() {
        System.out.print("Enter User ID: ");
        String uid = sc.nextLine().trim();
        if (uid.isEmpty()) { System.out.println("Cancelled."); return; }
        transactionService.printUserHistory(uid);
        pause();
    }

    // ========== HELPERS ==========

    /** Returns all books where isAvailable == true */
    private Book[] getAvailableBooks() {
        // Walk the BookList manually (BookList doesn't extend GenericList)
        int cnt = 0;
        com.mycompany.tarumtlibraryservices.adt.Book.BookNode cur = bookList.getHead();
        while (cur != null) { if (cur.getData().isAvailable()) cnt++; cur = cur.getNext(); }

        Book[] result = new Book[cnt];
        int i = 0;
        cur = bookList.getHead();
        while (cur != null) {
            if (cur.getData().isAvailable()) result[i++] = cur.getData();
            cur = cur.getNext();
        }
        return result;
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
