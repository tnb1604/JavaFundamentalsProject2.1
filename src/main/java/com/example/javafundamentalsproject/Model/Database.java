package com.example.javafundamentalsproject.Model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Database implements Serializable {
    private static final long serialVersionUID = 1L;
    // Lists to hold users, showings, and ticket sales
    private List<User> users;
    private List<Showing> showings;
    private List<Ticket> ticketSales;
    private List<SalesRecord> salesRecords;
    public final String DATABASE_FILE = "src/main/resources/movie_theater_db.ser";

    public Database() {
        users = new ArrayList<>();
        showings = new ArrayList<>();
        ticketSales = new ArrayList<>();
        salesRecords = new ArrayList<>();

        // adding some users to test
        users.add(new User("dnb", "dnb", "Management"));
        users.add(new User("mishi", "mishi", "Sales"));
    }


    // Method to retrieve a user by username
    public User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null; // Return null if no user is found
    }


    public void updateShowing(Showing updatedShowing) {
        for (int i = 0; i < showings.size(); i++) {
            Showing showing = showings.get(i);
            if (showing.getTitle().equals(updatedShowing.getTitle()) &&
                    showing.getStartTime().equals(updatedShowing.getStartTime())) {
                showings.set(i, updatedShowing);
                break;
            }
        }
        saveSelf();  // Save the database after updating
    }

    public List<Showing> getShowings() {
        return showings;
    }

    public void saveTicket(Ticket ticket) {
        int seatNumber = ticket.getSeatNumber();

        if (ticket.getShowing().isSeatAvailable(seatNumber)) {
            System.out.println("Seat number: " + seatNumber + " is available. Proceeding with the sale.");

            if (ticket.getShowing().sellTicket(ticket)) {
                ticketSales.add(ticket);
                updateShowing(ticket.getShowing());
                System.out.println("Ticket sold for seat number: " + seatNumber);
            }
        }
    }


    // Methods to manage showings (CRUD)
    public void addShowing(Showing showing) {
        showings.add(showing);
        saveSelf();  // Save the database after adding a showing
    }

    public void deleteShowing(Showing showing) {
        showings.remove(showing);
        saveSelf();  // Save the database after deleting a showing
    }

    public void saveSalesRecord(SalesRecord salesRecord) {
        if (salesRecords == null) {
            salesRecords = new ArrayList<>(); // Initialize if necessary
        }
        salesRecords.add(salesRecord); // Add the sales record to the list
        System.out.println("SalesRecord saved: " + salesRecord);
    }

    public List<SalesRecord> loadSalesRecords() {
        return salesRecords;
    }

    // Serialize the database to a file
    public static void saveDatabase(Database database, String fileName) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(database);
        }
    }

    // Instance method to save the current instance of the database
    public void saveSelf() {
        try {
            saveDatabase(this, DATABASE_FILE);  // 'this' refers to the current instance of Database
        } catch (IOException e) {
            System.err.println("Error saving the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Deserialize the database from a file
    public static Database loadDatabase(String fileName) throws IOException, ClassNotFoundException {
        File dbFile = new File(fileName);
        if (!dbFile.exists()) {
            System.out.println("Database file not found: " + fileName + ". Creating a new database instance.");
            return new Database(); // Return a new Database instance
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(dbFile))) {
            return (Database) in.readObject();
        }
    }

    @Override
    public String toString() {
        return "Database{" +
                "users=" + users +
                ", showings=" + showings +
                ", ticketSales=" + ticketSales +
                '}';
    }
}
