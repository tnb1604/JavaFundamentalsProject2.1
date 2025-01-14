package com.example.javafundamentalsproject.Model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Showing implements Serializable {
    private String title;
    private LocalDateTime startTime;  // Combine both date and time
    private LocalDateTime endTime;    // Combine both date and time
    private int totalSeats;            // Total number of seats available
    private int availableSeats;
    private List<Ticket> soldTickets;  // List of sold tickets
    private static final long serialVersionUID = 2L;

    // Constructor
    public Showing(String title, LocalDateTime startTime, LocalDateTime endTime, int totalSeats) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalSeats = totalSeats;
        this.soldTickets = new ArrayList<>();
        this.availableSeats = totalSeats;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public List<Ticket> getSoldTickets() {
        return soldTickets;
    }

    // New methods to get LocalDate and LocalTime
    public LocalDate getStartDate() {
        return startTime.toLocalDate();
    }

    public LocalTime getStartTimeOnly() {
        return startTime.toLocalTime();
    }

    public LocalDate getEndDate() {
        return endTime.toLocalDate();
    }

    public LocalTime getEndTimeOnly() {
        return endTime.toLocalTime();
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    // Calculate available seats
    public int getAvailableSeats() {
        return availableSeats;
    }

    public boolean sellTicket(Ticket ticket) {
        int seatNumber = ticket.getSeatNumber();
        if (isSeatAvailable(seatNumber)) {
            soldTickets.add(ticket);
            availableSeats--;  // Decrement available seats
            System.out.println("Ticket sold successfully for seat number: " + seatNumber);
            return true;
        } else {
            System.out.println("Failed to sell ticket. Seat number: " + seatNumber + " is unavailable.");
            return false;
        }
    }



    public boolean isSeatAvailable(int seatNumber) {
        System.out.println("Checking availability for seat number: " + seatNumber);
        for (Ticket soldTicket : soldTickets) {
            if (soldTicket.getSeatNumber() == seatNumber) {
                return false;
            }
        }
        System.out.println("Seat number: " + seatNumber + " is available.");
        return true;  // Seat is available
    }


    // Check if the showing has started
    //public boolean hasStarted() {
   //     return LocalDateTime.now().isAfter(startTime);
    //}

    // Check if the showing has ended
   // public boolean hasEnded() {
    //    return LocalDateTime.now().isAfter(endTime);
    //}

    // Update showing details
    public void updateShowingDetails(String newTitle, LocalDateTime newStartTime, LocalDateTime newEndTime, int newTotalSeats) {
        setTitle(newTitle);
        setStartTime(newStartTime);
        setEndTime(newEndTime);
        this.totalSeats = newTotalSeats;
    }

    // Check if tickets have been sold
    public boolean hasTicketsSold() {
        return !soldTickets.isEmpty();
    }

    public boolean isSeatSold(int seatNumber) {
        for (Ticket ticket : soldTickets) {
            if (ticket.getSeatNumber() == seatNumber) {
                return true; // The seat is sold
            }
        }
        return false; // The seat is not sold
    }



    // Return all sold seat numbers
    public List<Integer> getSoldSeatNumbers() {
        List<Integer> soldSeatNumbers = new ArrayList<>();
        for (Ticket ticket : soldTickets) {
            soldSeatNumbers.add(ticket.getSeatNumber());
        }
        return soldSeatNumbers;
    }
}
