package com.example.javafundamentalsproject.Model;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;

public class SalesRecord implements Serializable {
    private LocalDateTime purchaseDateTime;
    private int ticketCount;
    private String customerName;
    private String showingInfo;

    // Constructor
    public SalesRecord(LocalDateTime purchaseDateTime, int ticketCount, String customerName, String showingTitle, LocalDateTime showingStartTime) {
        this.purchaseDateTime = purchaseDateTime;
        this.ticketCount = ticketCount;
        this.customerName = customerName;

        // Format the showing's start time, combine title+time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String formattedStartTime = showingStartTime.format(formatter);
        this.showingInfo = showingTitle + " - " + formattedStartTime;
    }

    // Getters
    public LocalDateTime getPurchaseDateTime() {
        return purchaseDateTime;
    }


    public int getTicketCount() {
        return ticketCount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getShowingInfo() {
        return showingInfo;
    }

    // Optionally, you could override toString() for easier debugging
    @Override
    public String toString() {
        return "SalesRecord{" +
                "purchaseDateTime=" + purchaseDateTime +
                ", ticketCount=" + ticketCount +
                ", customerName='" + customerName + '\'' +
                ", showingInfo='" + showingInfo + '\'' +
                '}';
    }
}
