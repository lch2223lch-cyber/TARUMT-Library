package com.mycompany.tarumtlibraryservices.adt;

/**
 * Generic List ADT Interface Defines the contract for all list implementations
 *
 * @author Your Team
 * @param <T> the type of elements in this list
 */
public interface ListInterface<T> {

    boolean add(T newEntry);

    boolean add(int position, T newEntry);

    T remove(int position);

    boolean remove(T anEntry);

    boolean replace(int position, T newEntry);

    T getEntry(int position);

    boolean contains(T anEntry);

    int getNumberOfEntries();

    boolean isEmpty();

    void clear();
}
