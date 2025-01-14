package com.example.javafundamentalsproject.Model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Ticket implements Serializable {
    private static final long serialVersionUID = 3L;
    private String customerName;
    private int seatNumber;
    private Showing showing;
    private LocalDateTime purchaseDateTime;


    public Ticket(LocalDateTime purchaseDateTime,String customerName, int seatNumber, Showing showing) {
        this.customerName = customerName;
        this.seatNumber = seatNumber;
        this.showing = showing;
        this.purchaseDateTime = purchaseDateTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public Showing getShowing() {
        return showing;
    }

    public LocalDateTime getPurchaseDateTime() {
        return purchaseDateTime;
    }
}
