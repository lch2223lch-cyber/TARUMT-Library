// Room.java (Entity Class)
package com.mycompany.tarumtlibraryservices.model;

public class Room {
    private String roomId;
    private String roomName;
    private int capacity;
    
    // Arrays to manage 4 distinct timeslots per room
    // Index 0: 09:00-11:00 | Index 1: 11:00-13:00 | Index 2: 13:00-15:00 | Index 3: 15:00-17:00
    private boolean[] slotAvailable; 
    private String[] bookedByUserId; 

    public Room(String roomId, String roomName, int capacity) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.capacity = capacity;
        
        // Initialize all 4 slots as available (true) and no owners (null)
        this.slotAvailable = new boolean[]{true, true, true, true};
        this.bookedByUserId = new String[4];
    }

    // Getters
    public String getRoomId() { 
        return roomId; 
    }
    
    public String getRoomName() { 
        return roomName; 
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isAvailable() {
        // Returns true if at least one timeslot is currently open
        for (boolean slot : slotAvailable) {
            if (slot) return true; 
        }
        return false; // All slots are booked
    }

    public void setAvailable(boolean available) {
        // Temporary dummy method to prevent errors in RoomList's file parser
        // We can update the file saving logic for timeslots later!
    }

    public String getBookedBy(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < bookedByUserId.length) {
            return bookedByUserId[slotIndex];
        }
        return null;
    }
    
    // Check if a specific slot is available
    public boolean isSlotAvailable(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < slotAvailable.length) {
            return slotAvailable[slotIndex];
        }
        return false;
    }

    // Setters for locking/unlocking
    public void lockSlot(int slotIndex, String userId) {
        if (slotIndex >= 0 && slotIndex < slotAvailable.length) {
            slotAvailable[slotIndex] = false;
            bookedByUserId[slotIndex] = userId;
        }
    }

    public void unlockSlot(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < slotAvailable.length) {
            slotAvailable[slotIndex] = true;
            bookedByUserId[slotIndex] = null;
        }
    }
    
    @Override
    public String toString() {
        return "Room ID: " + roomId + " | Name: " + roomName + " | Capacity: " + capacity + 
               " | Available: " + (isAvailable() ? "Yes" : "No");
    }
}