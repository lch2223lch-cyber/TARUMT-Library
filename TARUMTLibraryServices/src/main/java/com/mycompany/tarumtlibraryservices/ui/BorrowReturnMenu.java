package com.mycompany.tarumtlibraryservices.ui;

import com.mycompany.tarumtlibraryservices.model.User;
import java.util.Scanner;

public class BorrowReturnMenu {

    private Scanner sc;
    private User currentUser;

    public BorrowReturnMenu(Scanner sc, User currentUser) {
        this.sc = sc;
        this.currentUser = currentUser;
    }

    public void start() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         BORROW & RETURN               ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("This feature is under development.");
        System.out.print("\nPress Enter to return to main menu...");
        sc.nextLine();
    }
}
