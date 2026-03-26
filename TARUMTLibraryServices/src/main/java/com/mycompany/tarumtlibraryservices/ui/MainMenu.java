package com.mycompany.tarumtlibraryservices.ui;

import com.mycompany.tarumtlibraryservices.adt.Book.BookList;
import com.mycompany.tarumtlibraryservices.adt.TransactionList;
import com.mycompany.tarumtlibraryservices.adt.UserList;
import com.mycompany.tarumtlibraryservices.model.User;
import java.util.Scanner;

public class MainMenu {

    private final Scanner sc;
    private final UserList userList;
    private final BookList bookList;
    private final TransactionList transactionList;
    private final User currentUser;

    public MainMenu(Scanner sc, UserList userList, BookList bookList,
                    TransactionList transactionList, User currentUser) {
        this.sc = sc;
        this.userList = userList;
        this.bookList = bookList;
        this.transactionList = transactionList;
        this.currentUser = currentUser;
    }

    public void start() {
        int choice = 0;

        do {
            displayMainMenu();

            System.out.print("Enter your choice: ");
            String input = sc.nextLine().trim();

            int maxChoice = getMaxChoice();
            if (!input.matches("[1-" + maxChoice + "]")) {
                System.out.println("Please enter a number between 1 and " + maxChoice + ".");
                continue;
            }

            choice = Integer.parseInt(input);

            if (!handleMenuChoice(choice)) {
                break;
            }

        } while (choice != getMaxChoice());
    }

    private void displayMainMenu() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║   TARUMT LIBRARY MANAGEMENT SYSTEM     ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Welcome, " + currentUser.getName()
                + " (" + currentUser.getRoleDisplayName() + ")");
        System.out.println("=".repeat(40));

        int menuNum = 1;

        System.out.println("\nMAIN MENU:");
        System.out.println("-".repeat(40));

        // Module 1: User Management
        System.out.println(menuNum++ + ". User Management");

        // Module 2: Book Management
        System.out.println(menuNum++ + ". Book Management");

        // Module 3: Borrow & Return
        System.out.println(menuNum++ + ". Borrow & Return");

        // Module 4: Room Booking
        System.out.println(menuNum++ + ". Room Booking");

        // Logout
        System.out.println(menuNum + ". Logout");

        System.out.println("=".repeat(40));
    }

    private int getMaxChoice() {
        return 5; // 4 modules + 1 logout = 5
    }

    private boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                // Module 1: User Management
                UserMenu userMenu = new UserMenu(sc, userList, currentUser);
                userMenu.start();
                return true;

            case 2:
                // Module 2: Book Management
                BookMenu bookMenu = new BookMenu(sc, currentUser);
                bookMenu.start();
                return true;

            case 3:
                // Module 3: Borrow & Return
                BorrowReturnMenu borrowMenu = new BorrowReturnMenu(
                        sc, currentUser, bookList, userList, transactionList);
                borrowMenu.start();
                return true;

            case 4:
                // Module 4: Room Booking
                RoomMenu roomMenu = new RoomMenu(sc, currentUser);
                roomMenu.start();
                return true;

            case 5:
                // Logout
                System.out.println("\nGoodbye, " + currentUser.getName() + "!");
                return false;

            default:
                return true;
        }
    }
}
