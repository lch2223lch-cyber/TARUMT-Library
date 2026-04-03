package com.mycompany.tarumtlibraryservices.adt;

import com.mycompany.tarumtlibraryservices.model.Book;
import com.mycompany.tarumtlibraryservices.model.BookSource;

public class BookList extends GenericList<Book> {

    private static final String DEFAULT_FILE_NAME = "books.txt";

    public BookList() {
        super(DEFAULT_FILE_NAME);
    }

    public BookList(String fileName) {
        super(fileName);
    }

    public boolean addBook(Book book) {
        if (getBookById(book.getBookId()) != null) {
            return false;
        }
        return add(book);
    }

    public Book getBookById(String bookId) {
        return findFirst(book -> book.getBookId().equalsIgnoreCase(bookId));
    }

    public boolean removeBookById(String bookId) {
        return removeIf(book -> book.getBookId().equalsIgnoreCase(bookId));
    }

    public void displayAllBooks() {
        if (isEmpty()) {
            System.out.println("No books available.");
            return;
        }

        forEach(book -> System.out.println(book));
    }

    public Book[] searchBooksByTitle(String titleKeyword) {
        return findAll(
            book -> book.getTitle() != null &&
                    book.getTitle().toLowerCase().contains(titleKeyword.toLowerCase()),
            Book[]::new
        );
    }

    public Book[] searchBooksByAuthor(String authorKeyword) {
        return findAll(
            book -> book.getAuthor() != null &&
                    book.getAuthor().toLowerCase().contains(authorKeyword.toLowerCase()),
            Book[]::new
        );
    }

    public Book[] getAvailableBooks() {
        return findAll(Book::isAvailable, Book[]::new);
    }

    public boolean bookExists(String bookId) {
        return getBookById(bookId) != null;
    }

    @Override
    protected String saveElement(Book book) {
        return book.getBookId() + "|"
                + book.getTitle() + "|"
                + book.getAuthor() + "|"
                + book.getSource() + "|"
                + book.isAvailable();
    }

    @Override
    protected Book parseElement(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] parts = line.split("\\|");
        if (parts.length < 5) {
            return null;
        }

        String bookId = parts[0].trim();
        String title = parts[1].trim();
        String author = parts[2].trim();
        BookSource source = BookSource.valueOf(parts[3].trim());
        boolean isAvailable = Boolean.parseBoolean(parts[4].trim());

        return new Book(bookId, title, author, source, isAvailable);
    }
}