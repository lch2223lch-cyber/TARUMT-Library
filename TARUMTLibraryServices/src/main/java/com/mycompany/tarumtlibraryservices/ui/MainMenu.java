package com.mycompany.tarumtlibraryservices.ui;

import com.mycompany.tarumtlibraryservices.adt.UserList;
import com.mycompany.tarumtlibraryservices.model.User;
import java.util.Scanner;

public class MainMenu {

    private Scanner sc;
    private UserList userList;
    private User currentUser;

    public MainMenu(Scanner sc, UserList userList, User currentUser) {
        this.sc = sc;
        this.userList = userList;
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
                // Module 2: Book Management - Team Member 2
                BookMenu bookMenu = new BookMenu(sc, currentUser);
                bookMenu.start();
                return true;

            case 3:
                // Module 3: Borrow & Return - Team Member 3
                BorrowReturnMenu borrowMenu = new BorrowReturnMenu(sc, currentUser);
                borrowMenu.start();
                return true;

            case 4:
                // Module 4: Room Booking - Team Member 4
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
