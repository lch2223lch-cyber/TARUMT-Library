package com.mycompany.tarumtlibraryservices.service;

import com.mycompany.tarumtlibraryservices.adt.BookList;
import com.mycompany.tarumtlibraryservices.model.Book;
import com.mycompany.tarumtlibraryservices.model.BookSource;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BookManager {

    private BookList bookList;

    public BookManager(BookList bookList) {
        this.bookList = bookList;
    }

    // Generate full book report
    public void generateBookReport() {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("BOOK MANAGEMENT REPORT");
        System.out.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("=".repeat(100));

        int totalBooks = bookList.getSize();
        int newCount = countBooksBySource(BookSource.NEW);
        int donatedCount = countBooksBySource(BookSource.DONATED);
        int availableCount = countAvailableBooks();
        int borrowedCount = totalBooks - availableCount;

        System.out.println("\nSUMMARY STATISTICS:");
        System.out.println("-".repeat(50));
        System.out.printf("%-20s: %d%n", "Total Books", totalBooks);
        System.out.printf("%-20s: %d (%.1f%%)%n", "New Books", newCount,
                totalBooks > 0 ? (newCount * 100.0 / totalBooks) : 0);
        System.out.printf("%-20s: %d (%.1f%%)%n", "Donated Books", donatedCount,
                totalBooks > 0 ? (donatedCount * 100.0 / totalBooks) : 0);
        System.out.printf("%-20s: %d (%.1f%%)%n", "Available Books", availableCount,
                totalBooks > 0 ? (availableCount * 100.0 / totalBooks) : 0);
        System.out.printf("%-20s: %d (%.1f%%)%n", "Borrowed Books", borrowedCount,
                totalBooks > 0 ? (borrowedCount * 100.0 / totalBooks) : 0);

        System.out.println("\nDETAILED BOOK LIST:");
        displayAllBooksFormatted();

        displayAdditionalStats(newCount, donatedCount, availableCount, borrowedCount, totalBooks);
    }

    // Generate source-based report
    public void generateSourceBasedReport(BookSource source) {
        String sourceName = source.name();

        System.out.println("\n" + "=".repeat(90));
        System.out.println(sourceName + " BOOK REPORT");
        System.out.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("=".repeat(90));

        Book[] allBooks = bookList.toArray(Book[]::new);
        int totalBooks = bookList.getSize();
        int count = 0;

        System.out.printf("\n%-10s | %-30s | %-20s | %-10s | %-12s%n",
                "Book ID", "Title", "Author", "Source", "Status");
        System.out.println("-".repeat(90));

        for (Book book : allBooks) {
            if (book.getSource() == source) {
                System.out.printf("%-10s | %-30s | %-20s | %-10s | %-12s%n",
                        book.getBookId(),
                        truncateString(book.getTitle(), 30),
                        truncateString(book.getAuthor(), 20),
                        book.getSource(),
                        book.isAvailable() ? "Available" : "Borrowed");
                count++;
            }
        }

        System.out.println("-".repeat(90));
        System.out.printf("Total %s books: %d%n", sourceName, count);
        System.out.printf("Percentage of total: %.1f%%%n",
                totalBooks > 0 ? (count * 100.0 / totalBooks) : 0);
    }

    // Generate title search report
    public void generateTitleSearchReport(String keyword) {
        System.out.println("\n" + "=".repeat(90));
        System.out.println("BOOK SEARCH REPORT - Title contains: \"" + keyword + "\"");
        System.out.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("=".repeat(90));

        Book[] results = bookList.searchBooksByTitle(keyword);
        int totalBooks = bookList.getSize();

        displaySearchResults(results, totalBooks);
    }

    // Generate author search report
    public void generateAuthorSearchReport(String keyword) {
        System.out.println("\n" + "=".repeat(90));
        System.out.println("BOOK SEARCH REPORT - Author contains: \"" + keyword + "\"");
        System.out.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("=".repeat(90));

        Book[] results = bookList.searchBooksByAuthor(keyword);
        int totalBooks = bookList.getSize();

        displaySearchResults(results, totalBooks);
    }

    // Export report to file
    public boolean exportReportToFile(String reportType) {
        String filename = "BookReport_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=".repeat(100));
            writer.println("TARUMT LIBRARY SERVICES - BOOK MANAGEMENT REPORT");
            writer.println("Report Type: " + reportType);
            writer.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println("=".repeat(100));

            int totalBooks = bookList.getSize();
            int newCount = countBooksBySource(BookSource.NEW);
            int donatedCount = countBooksBySource(BookSource.DONATED);
            int availableCount = countAvailableBooks();
            int borrowedCount = totalBooks - availableCount;

            writer.println("\nSUMMARY STATISTICS:");
            writer.println("-".repeat(50));
            writer.printf("%-20s: %d%n", "Total Books", totalBooks);
            writer.printf("%-20s: %d%n", "New Books", newCount);
            writer.printf("%-20s: %d%n", "Donated Books", donatedCount);
            writer.printf("%-20s: %d%n", "Available Books", availableCount);
            writer.printf("%-20s: %d%n", "Borrowed Books", borrowedCount);

            writer.println("\nDETAILED BOOK LIST:");
            writer.printf("%-10s | %-30s | %-20s | %-10s | %-12s%n",
                    "Book ID", "Title", "Author", "Source", "Status");
            writer.println("-".repeat(90));

            Book[] allBooks = bookList.toArray(Book[]::new);
            for (Book book : allBooks) {
                writer.printf("%-10s | %-30s | %-20s | %-10s | %-12s%n",
                        book.getBookId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getSource(),
                        book.isAvailable() ? "Available" : "Borrowed");
            }

            writer.println("\n" + "=".repeat(100));
            writer.println("END OF REPORT");

            System.out.println("Report exported successfully to: " + filename);
            return true;

        } catch (IOException e) {
            System.err.println("Error exporting report: " + e.getMessage());
            return false;
        }
    }

    private int countBooksBySource(BookSource source) {
        return bookList.count(book -> book.getSource() == source);
    }

    private int countAvailableBooks() {
        return bookList.count(Book::isAvailable);
    }

    private void displayAllBooksFormatted() {
        if (bookList.isEmpty()) {
            System.out.println("No books available.");
            return;
        }

        System.out.printf("%-10s | %-30s | %-20s | %-10s | %-12s%n",
                "Book ID", "Title", "Author", "Source", "Status");
        System.out.println("-".repeat(90));

        Book[] allBooks = bookList.toArray(Book[]::new);
        for (Book book : allBooks) {
            System.out.printf("%-10s | %-30s | %-20s | %-10s | %-12s%n",
                    book.getBookId(),
                    truncateString(book.getTitle(), 30),
                    truncateString(book.getAuthor(), 20),
                    book.getSource(),
                    book.isAvailable() ? "Available" : "Borrowed");
        }
    }

    private void displaySearchResults(Book[] results, int totalBooks) {
        if (results.length == 0) {
            System.out.println("\nNo books found matching the search criteria.");
            return;
        }

        System.out.printf("\n%-10s | %-30s | %-20s | %-10s | %-12s%n",
                "Book ID", "Title", "Author", "Source", "Status");
        System.out.println("-".repeat(90));

        for (Book book : results) {
            System.out.printf("%-10s | %-30s | %-20s | %-10s | %-12s%n",
                    book.getBookId(),
                    truncateString(book.getTitle(), 30),
                    truncateString(book.getAuthor(), 20),
                    book.getSource(),
                    book.isAvailable() ? "Available" : "Borrowed");
        }

        System.out.println("-".repeat(90));
        System.out.printf("Total books found: %d%n", results.length);
        System.out.printf("Search matched: %.1f%% of total books%n",
                totalBooks > 0 ? (results.length * 100.0 / totalBooks) : 0);
    }

    private void displayAdditionalStats(int newCount, int donatedCount, int availableCount, int borrowedCount, int total) {
        if (total == 0) {
            return;
        }

        System.out.println("\nADDITIONAL STATISTICS:");
        System.out.println("-".repeat(50));

        String mostCommonSource = newCount >= donatedCount ? "NEW" : "DONATED";
        int maxSourceCount = Math.max(newCount, donatedCount);

        System.out.printf("%-20s: %s (%d books, %.1f%%)%n",
                "Most common source",
                mostCommonSource,
                maxSourceCount,
                maxSourceCount * 100.0 / total);

        System.out.printf("%-20s: %.1f%%%n",
                "Availability rate",
                availableCount * 100.0 / total);

        System.out.printf("%-20s: %.1f%%%n",
                "Borrowed rate",
                borrowedCount * 100.0 / total);

        System.out.printf("%-20s: %.1f books%n",
                "Average by source",
                total / 2.0);

        System.out.println("-".repeat(50));
    }

    private String truncateString(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}