package com.mycompany.tarumtlibraryservices.adt;

import java.io.*;
import java.util.function.Predicate;
import java.util.function.Consumer;

/**
 * Generic Linked List ADT (Abstract Data Type) Provides basic list operations
 * for any type T
 *
 * @param <T> the type of elements in this list
 * @author [Lim Chuin Hao]
 */
public class GenericList<T> {

    protected Node<T> head;    // protected so subclasses can access
    protected int size;
    protected String fileName;  // for file persistence (optional)

    /**
     * Constructor with default filename
     */
    public GenericList() {
        this.head = null;
        this.size = 0;
        this.fileName = null;
    }

    /**
     * Constructor with custom filename for persistence
     */
    public GenericList(String fileName) {
        this.head = null;
        this.size = 0;
        this.fileName = fileName;
        if (fileName != null) {
            loadFromFile();
        }
    }

    //LIST OPERATIONS
    /**
     * Add element to the end of the list
     *
     * @param element element to add
     * @return true if added successfully
     */
    public boolean add(T element) {
        if (element == null) {
            return false;
        }

        Node<T> newNode = new Node<>(element);

        if (head == null) {
            head = newNode;
        } else {
            Node<T> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;

        if (fileName != null && !isLoading) {
            saveToFile();
        }
        return true;
    }

    /**
     * Internal add method for loading (does not trigger save)
     */
    private boolean addInternal(T element) {
        if (element == null) {
            return false;
        }

        Node<T> newNode = new Node<>(element);

        if (head == null) {
            head = newNode;
        } else {
            Node<T> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
        return true;
    }

    /**
     * Remove element at specified index
     *
     * @param index position to remove (0-based)
     * @return removed element, or null if index invalid
     */
    public T remove(int index) {
        if (index < 0 || index >= size) {
            return null;
        }

        T removedData;

        if (index == 0) {
            removedData = head.data;
            head = head.next;
        } else {
            Node<T> current = head;
            for (int i = 0; i < index - 1; i++) {
                current = current.next;
            }
            removedData = current.next.data;
            current.next = current.next.next;
        }

        size--;

        if (fileName != null) {
            saveToFile();
        }

        return removedData;
    }

    /**
     * Remove element by predicate
     *
     * @param predicate condition to match
     * @return true if element was removed
     */
    public boolean removeIf(Predicate<T> predicate) {
        if (head == null) {
            return false;
        }

        // Check head
        if (predicate.test(head.data)) {
            head = head.next;
            size--;
            if (fileName != null) {
                saveToFile();
            }
            return true;
        }

        // Check rest of list
        Node<T> current = head;
        while (current.next != null) {
            if (predicate.test(current.next.data)) {
                current.next = current.next.next;
                size--;
                if (fileName != null) {
                    saveToFile();
                }
                return true;
            }
            current = current.next;
        }

        return false;
    }

    /**
     * Get element at index
     *
     * @param index position (0-based)
     * @return element or null if invalid
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }

        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    /**
     * Find first element matching predicate
     *
     * @param predicate condition to match
     * @return first matching element or null
     */
    public T findFirst(Predicate<T> predicate) {
        Node<T> current = head;
        while (current != null) {
            if (predicate.test(current.data)) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }

    /**
     * Get all elements matching predicate
     *
     * @param predicate condition to match
     * @return array of matching elements
     */
    public T[] findAll(Predicate<T> predicate, java.util.function.IntFunction<T[]> arrayCreator) {
        // Count matches
        int matchCount = 0;
        Node<T> current = head;
        while (current != null) {
            if (predicate.test(current.data)) {
                matchCount++;
            }
            current = current.next;
        }

        // Create result array
        T[] results = arrayCreator.apply(matchCount);

        // Fill array
        int index = 0;
        current = head;
        while (current != null) {
            if (predicate.test(current.data)) {
                results[index++] = current.data;
            }
            current = current.next;
        }

        return results;
    }

    /**
     * Count elements matching predicate
     */
    public int count(Predicate<T> predicate) {
        int count = 0;
        Node<T> current = head;
        while (current != null) {
            if (predicate.test(current.data)) {
                count++;
            }
            current = current.next;
        }
        return count;
    }

    /**
     * Update element at index
     *
     * @param index position to update
     * @param newValue new value
     * @return true if updated successfully
     */
    public boolean update(int index, T newValue) {
        if (index < 0 || index >= size || newValue == null) {
            return false;
        }

        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        current.data = newValue;

        if (fileName != null) {
            saveToFile();
        }

        return true;
    }

    /**
     * Apply function to all elements (for display/processing)
     */
    public void forEach(Consumer<T> action) {
        Node<T> current = head;
        while (current != null) {
            action.accept(current.data);
            current = current.next;
        }
    }

    /**
     * Convert list to array
     */
    public T[] toArray(java.util.function.IntFunction<T[]> arrayCreator) {
        T[] array = arrayCreator.apply(size);
        Node<T> current = head;
        int index = 0;
        while (current != null) {
            array[index++] = current.data;
            current = current.next;
        }
        return array;
    }

    // ========== UTILITY METHODS ==========
    public boolean isEmpty() {
        return head == null;
    }

    /**
     * Get the size of the list
     *
     * @return number of elements in the list
     */
    public int getSize() {
        return size;
    }

    /**
     * Alternative method name for consistency with original code
     */
    public int size() {
        return size;
    }

    public void clear() {
        head = null;
        size = 0;
        if (fileName != null) {
            saveToFile();
        }
    }

    // ========== FILE PERSISTENCE ==========
    private boolean isLoading = false; // Flag to prevent recursive saving

    /**
     * Set filename for persistence
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Save element to string format (override in subclass)
     */
    protected String saveElement(T element) {
        return element.toString(); // Default implementation
    }

    /**
     * Parse element from string (override in subclass)
     */
    protected T parseElement(String line) {
        return null; // Must be overridden
    }

    /**
     * Save entire list to file
     */
    public void saveToFile() {
        if (fileName == null) {
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            Node<T> current = head;
            while (current != null) {
                writer.println(saveElement(current.data));
                current = current.next;
            }
        } catch (IOException e) {
            System.err.println("Error saving to file: " + e.getMessage());
        }
    }

    /**
     * Load list from file
     */
    public void loadFromFile() {
        if (fileName == null) {
            return;
        }

        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("No existing data file found. Starting with empty list.");
            return;
        }

        isLoading = true; // Set flag to prevent recursive saving

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int loadedCount = 0;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                T element = parseElement(line);
                if (element != null) {
                    addInternal(element); // Use internal add that doesn't trigger save
                    loadedCount++;
                }
            }
            if (loadedCount > 0) {
                System.out.println("Loaded " + loadedCount + " elements from " + fileName);
            }
        } catch (IOException e) {
            System.err.println("Error loading from file: " + e.getMessage());
        } finally {
            isLoading = false; // Reset flag
        }
    }

    // ========== ADDITIONAL LIST OPERATIONS ==========
    /**
     * Check if list contains element matching predicate
     */
    public boolean contains(Predicate<T> predicate) {
        return findFirst(predicate) != null;
    }

    /**
     * Get all elements (simplified version)
     */
    public Object[] getAll() {
        Object[] result = new Object[size];
        Node<T> current = head;
        int index = 0;
        while (current != null) {
            result[index++] = current.data;
            current = current.next;
        }
        return result;
    }

    /**
     * Add all elements from array
     */
    public void addAll(T[] elements) {
        for (T element : elements) {
            add(element);
        }
    }
}
