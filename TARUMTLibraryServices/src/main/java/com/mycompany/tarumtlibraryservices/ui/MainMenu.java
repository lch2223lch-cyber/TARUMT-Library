package com.mycompany.tarumtlibraryservices.ui;

import com.mycompany.tarumtlibraryservices.adt.BookList;
import com.mycompany.tarumtlibraryservices.adt.RoomList;
import com.mycompany.tarumtlibraryservices.adt.TransactionList;
import com.mycompany.tarumtlibraryservices.adt.UserList;
import com.mycompany.tarumtlibraryservices.model.User;
import com.mycompany.tarumtlibraryservices.service.RoomManager;
import java.util.Scanner;

public class MainMenu {

    private final Scanner sc;
    private final UserList userList;
    private final BookList bookList;
    private final TransactionList transactionList;
    private final User currentUser;
    private final RoomManager roomManager;

    public MainMenu(Scanner sc, UserList userList, BookList bookList,
                    TransactionList transactionList, User currentUser) {
        this.sc = sc;
        this.userList = userList;
        this.bookList = bookList;
        this.transactionList = transactionList;
        this.currentUser = currentUser;
        
        // Initialize RoomManager
        RoomList roomList = new RoomList("rooms.txt");
        this.roomManager = new RoomManager(roomList);
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
        System.out.println("\n" + "=".repeat(50));
        System.out.println("     TARUMT LIBRARY MANAGEMENT SYSTEM");
        System.out.println("=".repeat(50));
        System.out.println("Welcome, " + currentUser.getName()
                + " (" + currentUser.getRoleDisplayName() + ")");
        System.out.println("-".repeat(40));

        int menuNum = 1;

        System.out.println("\nMAIN MENU:");
        System.out.println("-".repeat(40));

        System.out.println(menuNum++ + ". User Management");
        System.out.println(menuNum++ + ". Book Management");
        System.out.println(menuNum++ + ". Borrow & Return");
        System.out.println(menuNum++ + ". Room Booking");
        System.out.println(menuNum + ". Logout");

        System.out.println("=".repeat(40));
    }

    private int getMaxChoice() {
        return 5;
    }

    private boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                UserMenu userMenu = new UserMenu(sc, userList, currentUser);
                userMenu.start();
                return true;

            case 2:
                BookMenu bookMenu = new BookMenu(sc, currentUser, bookList);
                bookMenu.start();
                return true;

            case 3:
                BorrowReturnMenu borrowMenu = new BorrowReturnMenu(
                        sc, currentUser, bookList, userList, transactionList);
                borrowMenu.start();
                return true;

            case 4:
                // Module 4: Room Booking/Management
                if (currentUser.getRole().equals(User.ROLE_ADMIN)) {
                    // Admin gets full menu
                    RoomMenu adminMenu = new RoomMenu(roomManager, sc, currentUser);
                    adminMenu.displayMenu();
                } else if (currentUser.getRole().equals(User.ROLE_LIBRARIAN)) {
                    // Librarian gets limited menu (no add/delete rooms)
                    RoomMenu librarianMenu = new RoomMenu(roomManager, sc, currentUser);
                    librarianMenu.displayMenu();
                } else if (currentUser.getRole().equals(User.ROLE_STUDENT)) {
                    // Student gets booking menu only
                    StudentRoomMenu studentMenu = new StudentRoomMenu(roomManager, currentUser);
                    studentMenu.displayMenu();
                } else {
                    System.out.println("\n[ACCESS DENIED] You don't have permission for this module.");
                }
                return true;

            

            default:
                return true;
        }
    }
}